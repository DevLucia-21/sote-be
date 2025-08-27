// src/main/java/com/fluxion/sote/user/service/UserServiceImpl.java
package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import com.fluxion.sote.auth.repository.UserSecurityAnswerRepository;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.setting.repository.NotificationSettingRepository;
import com.fluxion.sote.user.dto.*;
import com.fluxion.sote.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.fluxion.sote.global.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserSecurityAnswerRepository securityAnswerRepo;
    private final NotificationSettingRepository notificationSettingRepo;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Override
    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest req) {
        UserSecurityAnswer usa = securityAnswerRepo
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));

        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        User user = userRepo.findById(usa.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자가 없습니다."));

        return new FindEmailResponse(user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public FindPwdResponse findPassword(FindPwdRequest req) {
        UserSecurityAnswer usa = securityAnswerRepo
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));

        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        User user = userRepo.findById(usa.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자가 없습니다."));

        return new FindPwdResponse(user.getPassword());
    }

    @Override
    @Transactional
    public void resetPasswordWithTemp(FindPwdRequest req) {
        UserSecurityAnswer usa = securityAnswerRepo
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));

        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        User user = usa.getUser();
        String tempPwd = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPwd));
        userRepo.save(user);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Sote 임시 비밀번호 안내");
        msg.setText("안녕하세요, Sote입니다.\n임시 비밀번호: " + tempPwd + "\n로그인 후 변경해 주세요.");
        mailSender.send(msg);
    }

    @Override
    public boolean checkSecurity(Long userId, Integer questionId, String answer) {
        return securityAnswerRepo
                .findByUserIdAndQuestionId(userId, questionId)
                .map(usa -> passwordEncoder.matches(answer, usa.getAnswerEncrypted()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 틀립니다.");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void deleteCurrentUser() {
        User user = getCurrentUser();

        securityAnswerRepo.deleteByUserId(user.getId());
        notificationSettingRepo.deleteByUser(user);
        userRepo.deleteById(user.getId());
    }
}
