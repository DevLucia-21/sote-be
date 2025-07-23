package com.fluxion.sote.user.service;

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

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public UserServiceImpl(
            UserRepository userRepo,
            BCryptPasswordEncoder passwordEncoder,
            JavaMailSender mailSender
    ) {
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender      = mailSender;
    }

    @Override
    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest req) {
        String email = userRepo.findByNicknameAndSecurityAnswer(
                        req.getNickname(), req.getSecurityAnswer()
                )
                .map(User::getEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));
        return new FindEmailResponse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public FindPwdResponse findPassword(FindPwdRequest req) {
        String pwd = userRepo.findByEmailAndSecurityAnswer(
                        req.getEmail(), req.getSecurityAnswer()
                )
                .map(User::getPassword)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));
        return new FindPwdResponse(pwd);
    }

    /**
     * 이메일과 보안 답변이 일치할 때
     * 임시 비밀번호를 생성·저장하고 이메일로 전송합니다.
     */
    @Override
    @Transactional
    public void resetPasswordWithTemp(FindPwdRequest req) {
        User user = userRepo.findByEmailAndSecurityAnswer(
                        req.getEmail(), req.getSecurityAnswer()
                )
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));

        // 1) 임시 비밀번호 생성 (8문자)
        String temp = UUID.randomUUID().toString().substring(0, 8);

        // 2) DB에 해시 저장
        user.setPassword(passwordEncoder.encode(temp));
        userRepo.save(user);

        // 3) 이메일 발송
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Sote 임시 비밀번호 안내");
        msg.setText(
                "안녕하세요, Sote입니다.\n\n" +
                        "귀하의 임시 비밀번호는 다음과 같습니다:\n" +
                        temp + "\n\n" +
                        "로그인 후 반드시 비밀번호를 변경해 주세요.\n\n" +
                        "감사합니다."
        );
        mailSender.send(msg);
    }
}
