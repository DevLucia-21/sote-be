package com.fluxion.sote.auth.entity;

import com.fluxion.sote.notification.entity.FcmToken;
import com.fluxion.sote.notification.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "character", nullable = false, length = 20)
    private String character = "piano";

    @Column(name = "profile_image", columnDefinition = "bytea")
    private byte[] profileImage;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_notifications", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private Set<NotificationType> enabledNotifications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getCharacter() {
        return character;
    }

    public Set<NotificationType> getEnabledNotifications() {
        return enabledNotifications;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public List<FcmToken> getFcmTokens() {
        return fcmTokens;
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

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setEnabledNotifications(Set<NotificationType> enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public void setFcmTokens(List<FcmToken> fcmTokens) {
        this.fcmTokens = fcmTokens;
    }

    // 편의 메서드

    // 중복 방지 토큰 추가
    public void addFcmToken(FcmToken token) {
        boolean exists = fcmTokens.stream()
                .anyMatch(t -> t.getToken().equals(token.getToken()));
        if (!exists) {
            fcmTokens.add(token);
            token.setUser(this);
        }
    }

    // 토큰 제거
    public void removeFcmToken(FcmToken token) {
        fcmTokens.remove(token);
        token.setUser(null);
    }
}
