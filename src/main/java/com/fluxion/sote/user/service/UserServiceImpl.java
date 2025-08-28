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
        // 닉네임 + 생년월일로 User 조회
        User user = userRepo.findByNicknameAndBirthDate(req.getNickname(), req.getBirthDate())
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자 없음"));

        // User + QuestionId 로 Answer 조회
        UserSecurityAnswer usa = securityAnswerRepo.findByUserAndQuestionId(user, req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("보안 질문 없음"));

        // 보안 답변 검증 (BCrypt)
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("보안 질문 답변이 일치하지 않습니다.");
        }

        return new FindEmailResponse(user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public FindPwdResponse findPassword(FindPwdRequest req) {
        // 이메일 기반으로 보안 질문/답변 조회
        UserSecurityAnswer usa = securityAnswerRepo
                .findByUserEmailAndQuestionId(req.getEmail(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));

        // 보안 답변 검증
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        // 사용자 정보 확인
        User user = usa.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("해당 사용자가 없습니다.");
        }

        // 보안상 실제 비밀번호를 그대로 주면 안 되므로,
        // 여기서는 단순 성공 여부만 알려주는 식으로 수정
        return new FindPwdResponse("VALID");
    }

    @Override
    @Transactional
    public void resetPasswordWithTemp(FindPwdRequest req) {
        // 이메일 + 질문 ID로 UserSecurityAnswer 조회
        UserSecurityAnswer usa = securityAnswerRepo
                .findByUserEmailAndQuestionId(req.getEmail(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));

        // 보안 답변 검증
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }

        // 사용자 계정 조회
        User user = usa.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("해당 사용자가 없습니다.");
        }

        // 임시 비밀번호 생성 및 저장
        String tempPwd = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPwd));
        userRepo.save(user);

        // 임시 비밀번호 메일 발송
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Sote 임시 비밀번호 안내");
        msg.setText("안녕하세요, Sote입니다.\n\n임시 비밀번호: " + tempPwd + "\n\n로그인 후 반드시 변경해 주세요.");
        mailSender.send(msg);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkSecurity(String email, Integer questionId, String answer) {
        return securityAnswerRepo
                .findByUserEmailAndQuestionId(email, questionId)
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
