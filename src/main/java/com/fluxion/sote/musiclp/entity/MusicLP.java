package com.fluxion.sote.musiclp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "music_lp")
public class MusicLP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    // JPA용 기본 생성자
    protected MusicLP() {}

    // 편의 생성자
    public MusicLP(String title, String artist, LocalDate releaseDate) {
        this.title = title;
        this.artist = artist;
        this.releaseDate = releaseDate;
    }

    // Getter/Setter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
}
