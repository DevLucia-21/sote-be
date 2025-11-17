package com.fluxion.sote.user.entity;

import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "keywords")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 기본 생성자
    protected Keyword() {}

    public Keyword(String content, User user) {
        this.content = content;
        this.user = user;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    // Setter
    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
