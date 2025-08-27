package com.fluxion.sote.musiclp.dto;

import java.time.LocalDate;

public class MusicLPDto {
    private Long id;
    private String title;
    private String artist;
    private LocalDate releaseDate;

    public MusicLPDto(Long id, String title, String artist, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.releaseDate = releaseDate;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public LocalDate getReleaseDate() { return releaseDate; }
}
