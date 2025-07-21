package com.fluxion.sote.musiclp.entity;

import jakarta.persistence.*;
import com.fluxion.sote.musiclp.entity.MusicLP;

@Entity
@Table(name = "music_track")
public class MusicTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "play_url", nullable = false)
    private String playUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lp_id")
    private MusicLP lp;

    protected MusicTrack() {}

    public MusicTrack(String title, String playUrl, MusicLP lp) {
        this.title = title;
        this.playUrl = playUrl;
        this.lp = lp;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPlayUrl() { return playUrl; }
    public MusicLP getLp() { return lp; }
}
