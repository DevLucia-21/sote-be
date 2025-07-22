package com.fluxion.sote.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "security_answer", nullable = false, length = 100)
    private String securityAnswer;

    /**
     * 유저 ↔ 장르 다대다 매핑
     * user_genres 조인 테이블을 통해 Genre 엔티티와 연결
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_genres",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> musicPreferences = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 기본 생성자
    public User() {}

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public Set<Genre> getMusicPreferences() {
        return musicPreferences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public void setMusicPreferences(Set<Genre> musicPreferences) {
        this.musicPreferences = musicPreferences;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
