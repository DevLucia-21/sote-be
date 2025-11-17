package com.fluxion.sote.lpmusic.service;

import com.fluxion.sote.lpmusic.config.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Spotify API 토큰 발급
     */
    private String getAccessToken() {
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

        return (String) response.getBody().get("access_token");
    }

    /**
     * 곡 검색 (제목 + 아티스트 기반)
     */
    public Map<String, String> searchTrack(String title, String artist) {
        String token = getAccessToken();

        String query = title + " " + artist;
        String url = "https://api.spotify.com/v1/search?q=" + query + "&type=track&limit=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null || body.isEmpty()) {
            return Map.of("title", title, "artist", artist);
        }

        Map<String, Object> tracks = (Map<String, Object>) body.get("tracks");
        var items = (java.util.List<Map<String, Object>>) tracks.get("items");

        if (items.isEmpty()) {
            return Map.of("title", title, "artist", artist);
        }

        Map<String, Object> track = items.get(0);

        String trackTitle = (String) track.get("name");
        String trackArtist = (String) ((Map<String, Object>) ((java.util.List<?>) track.get("artists")).get(0)).get("name");
        String albumImageUrl = (String) ((Map<String, Object>) ((java.util.List<?>) ((Map<String, Object>) track.get("album")).get("images")).get(0)).get("url");
        String playUrl = (String) ((Map<String, Object>) track.get("external_urls")).get("spotify");

        return Map.of(
                "title", trackTitle,
                "artist", trackArtist,
                "albumImageUrl", albumImageUrl,
                "playUrl", playUrl
        );
    }
}
