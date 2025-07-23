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
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest req) {
        User user = findUserByNicknameAndAnswer(req.getNickname(), req.getSecurityAnswer());
        return new FindEmailResponse(user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public FindPwdResponse findPassword(FindPwdRequest req) {
        User user = findUserByEmailAndAnswer(req.getEmail(), req.getSecurityAnswer());
        return new FindPwdResponse(user.getPassword());
    }

    @Override
    @Transactional
    public void resetPasswordWithTemp(FindPwdRequest req) {
        User user = findUserByEmailAndAnswer(req.getEmail(), req.getSecurityAnswer());

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepo.save(user);

        sendTemporaryPasswordEmail(user.getEmail(), tempPassword);
    }

    private User findUserByNicknameAndAnswer(String nickname, String answer) {
        return userRepo.findByNicknameAndSecurityAnswer(nickname, answer)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));
    }

    private User findUserByEmailAndAnswer(String email, String answer) {
        return userRepo.findByEmailAndSecurityAnswer(email, answer)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));
    }

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