package com.fluxion.sote.musiclp.service.impl;

import com.fluxion.sote.musiclp.entity.MusicTrack;
import com.fluxion.sote.musiclp.repository.MusicTrackRepository;
import com.fluxion.sote.musiclp.service.MusicTrackService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class MusicTrackServiceImpl implements MusicTrackService {
    private final MusicTrackRepository repo;

    public MusicTrackServiceImpl(MusicTrackRepository repo) {
        this.repo = repo;
    }

    @Override
    public String getPlayUrl(Long trackId) {
        MusicTrack track = repo.findById(trackId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Track not found with id " + trackId));
        return track.getPlayUrl();
    }
}
