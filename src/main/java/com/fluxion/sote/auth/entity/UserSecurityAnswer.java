package com.fluxion.sote.auth.entity;

import com.fluxion.sote.auth.entity.SecurityQuestion;
import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;

@Entity
@Table(
        name = "user_security_answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "question_id"})
)
public class UserSecurityAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SecurityQuestion question;

    @Column(name = "answer_encrypted", nullable = false)
    private String answerEncrypted;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SecurityQuestion getQuestion() { return question; }
    public void setQuestion(SecurityQuestion question) { this.question = question; }

    public String getAnswerEncrypted() { return answerEncrypted; }
    public void setAnswerEncrypted(String answerEncrypted) { this.answerEncrypted = answerEncrypted; }
}
