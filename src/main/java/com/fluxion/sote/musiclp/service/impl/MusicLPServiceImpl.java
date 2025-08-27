package com.fluxion.sote.musiclp.service.impl;

import com.fluxion.sote.musiclp.dto.MusicLPDto;
import com.fluxion.sote.musiclp.entity.MusicLP;
import com.fluxion.sote.musiclp.repository.MusicLPRepository;
import com.fluxion.sote.musiclp.service.MusicLPService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicLPServiceImpl implements MusicLPService {
    private final MusicLPRepository repo;

    public MusicLPServiceImpl(MusicLPRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<MusicLPDto> getWeeklyLPs() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        return repo.findByReleaseDateBetween(weekAgo, today)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MusicLPDto> getMonthlyLPs() {
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusMonths(1);
        return repo.findByReleaseDateBetween(monthAgo, today)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MusicLPDto getLPDetail(Long id) {
        MusicLP lp = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "MusicLP not found with id " + id));
        return toDto(lp);
    }

    private MusicLPDto toDto(MusicLP lp) {
        return new MusicLPDto(
                lp.getId(),
                lp.getTitle(),
                lp.getArtist(),
                lp.getReleaseDate()
        );
    }
}
