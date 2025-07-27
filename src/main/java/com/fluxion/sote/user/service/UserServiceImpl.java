package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import com.fluxion.sote.auth.repository.UserSecurityAnswerRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.user.dto.FindEmailRequest;
import com.fluxion.sote.user.dto.FindEmailResponse;
import com.fluxion.sote.user.dto.FindPwdRequest;
import com.fluxion.sote.user.dto.FindPwdResponse;
import com.fluxion.sote.user.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserSecurityAnswerRepository userSecurityAnswerRepository;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public UserServiceImpl(
            UserSecurityAnswerRepository userSecurityAnswerRepository,
            UserRepository userRepo,
            BCryptPasswordEncoder passwordEncoder,
            JavaMailSender mailSender
    ) {
        this.userSecurityAnswerRepository = userSecurityAnswerRepository;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest req) {
        // 1) 보안 질문 일치 여부 확인
        UserSecurityAnswer usa = userSecurityAnswerRepository
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        // 2) 이메일 조회
        User user = userRepo.findById(usa.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자가 없습니다."));
        return new FindEmailResponse(user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public FindPwdResponse findPassword(FindPwdRequest req) {
        // 1) 보안 질문 일치 여부 확인
        UserSecurityAnswer usa = userSecurityAnswerRepository
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        // 2) 해시된 비밀번호 반환 (혹은 보안상 토큰 발급 로직으로 변경)
        User user = userRepo.findById(usa.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자가 없습니다."));
        return new FindPwdResponse(user.getPassword());
    }

    @Override
    @Transactional
    public void resetPasswordWithTemp(FindPwdRequest req) {
        // 1) 보안 질문 일치 여부 확인
        UserSecurityAnswer usa = userSecurityAnswerRepository
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        // 2) 임시 비밀번호 생성·저장
        User user = usa.getUser();
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepo.save(user);

        // 3) 이메일 발송
        sendTemporaryPasswordEmail(user.getEmail(), tempPassword);
    }

    @Override
    public boolean checkSecurity(Long userId, Integer questionId, String answer) {
        return userSecurityAnswerRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .map(usa -> passwordEncoder.matches(answer, usa.getAnswerEncrypted()))
                .orElse(false);
    }

    // ----------------------------------------------------------------
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void sendTemporaryPasswordEmail(String to, String temp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Sote 임시 비밀번호 안내");
        message.setText(
                "안녕하세요, Sote입니다.\n\n" +
                        "귀하의 임시 비밀번호는 다음과 같습니다:\n" +
                        temp +
                        "\n\n로그인 후 반드시 비밀번호를 변경해 주세요.\n\n감사합니다."
        );
        mailSender.send(message);
    }
}
