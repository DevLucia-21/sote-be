package com.fluxion.sote.lpmusic.service;

import com.fluxion.sote.lpmusic.config.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {

    private final SpotifyProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Spotify API 토큰 발급
     */
    private String getAccessToken() {
        try {
            String auth = props.getClientId() + ":" + props.getClientSecret();
            String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encoded);

            HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://accounts.spotify.com/api/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || body.get("access_token") == null) {
                throw new IllegalStateException("Spotify access token 발급에 실패했습니다.");
            }

            return (String) body.get("access_token");
        } catch (Exception e) {
            log.warn("Spotify access token 발급 실패: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 곡 검색 (제목 + 아티스트 기반)
     * 실패해도 fallback 값 반환
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> searchTrack(String title, String artist) {
        try {
            String token = getAccessToken();
            if (token == null || token.isBlank()) {
                return fallback(title, artist, null);
            }

            String query = URLEncoder.encode(title + " " + artist, StandardCharsets.UTF_8);
            String url = "https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body == null || body.isEmpty()) {
                return fallback(title, artist, null);
            }

            Map<String, Object> tracks = (Map<String, Object>) body.get("tracks");
            if (tracks == null) {
                return fallback(title, artist, null);
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) tracks.get("items");
            if (items == null || items.isEmpty()) {
                return fallback(title, artist, null);
            }

            Map<String, Object> track = items.get(0);

            String trackTitle = (String) track.getOrDefault("name", title);

            List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
            String trackArtist = artist;
            if (artists != null && !artists.isEmpty()) {
                trackArtist = (String) artists.get(0).getOrDefault("name", artist);
            }

            Map<String, Object> album = (Map<String, Object>) track.get("album");
            String albumName = "";
            String albumImageUrl = "";
            if (album != null) {
                albumName = (String) album.getOrDefault("name", "");

                List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
                if (images != null && !images.isEmpty()) {
                    albumImageUrl = (String) images.get(0).getOrDefault("url", "");
                }
            }

            Map<String, Object> externalUrls = (Map<String, Object>) track.get("external_urls");
            String playUrl = "";
            if (externalUrls != null) {
                playUrl = (String) externalUrls.getOrDefault("spotify", "");
            }

            return Map.of(
                    "title", trackTitle != null ? trackTitle : title,
                    "artist", trackArtist != null ? trackArtist : artist,
                    "album", albumName != null ? albumName : "",
                    "albumImageUrl", albumImageUrl != null ? albumImageUrl : "",
                    "playUrl", playUrl != null ? playUrl : ""
            );
        } catch (RestClientException e) {
            log.warn("Spotify track search 실패: title={}, artist={}, error={}", title, artist, e.getMessage());
            return fallback(title, artist, null);
        } catch (Exception e) {
            log.warn("Spotify 처리 중 예외: title={}, artist={}, error={}", title, artist, e.getMessage());
            return fallback(title, artist, null);
        }
    }

    private Map<String, String> fallback(String title, String artist, String album) {
        return Map.of(
                "title", title != null ? title : "",
                "artist", artist != null ? artist : "",
                "album", album != null ? album : "",
                "albumImageUrl", "",
                "playUrl", ""
        );
    }
}