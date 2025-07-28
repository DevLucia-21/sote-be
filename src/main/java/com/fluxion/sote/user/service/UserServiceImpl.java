package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import com.fluxion.sote.auth.repository.UserSecurityAnswerRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.dto.*;
import com.fluxion.sote.user.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
        UserSecurityAnswer usa = userSecurityAnswerRepository
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
        UserSecurityAnswer usa = userSecurityAnswerRepository
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
        UserSecurityAnswer usa = userSecurityAnswerRepository
                .findByUserIdAndQuestionId(req.getUserId(), req.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("질문–답이 일치하지 않습니다."));
        if (!passwordEncoder.matches(req.getSecurityAnswer(), usa.getAnswerEncrypted())) {
            throw new ResourceNotFoundException("질문–답이 일치하지 않습니다.");
        }
        User user = usa.getUser();
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepo.save(user);
        sendTemporaryPasswordEmail(user.getEmail(), tempPassword);
    }

    @Override
    public boolean checkSecurity(Long userId, Integer questionId, String answer) {
        return userSecurityAnswerRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .map(usa -> passwordEncoder.matches(answer, usa.getAnswerEncrypted()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        User user = getCurrentUser();
        int totalDiaryCount = 0; // TODO: 일기 수 연동 시 교체
        List<String> savedImages = List.of(); // TODO: 이미지 연동 시 교체

        return new UserProfileResponse(
                user.getNickname(),
                user.getCharacter(),
                user.getProfileImageUrl(),
                totalDiaryCount,
                savedImages
        );
    }

    @Override
    public void updateMyProfile(UserProfileUpdateRequest request) {
        User user = getCurrentUser();
        user.setNickname(request.getNickname());
        user.setCharacter(request.getCharacter());
        user.setProfileImageUrl(request.getProfileImageUrl());
        userRepo.save(user);
    }

    private User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("로그인된 사용자를 찾을 수 없습니다."));
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    @Transactional
    public void updateProfileImage(MultipartFile image) {
        User user = getCurrentUser();
        try {
            byte[] imageBytes = image.getBytes();
            user.setProfileImage(imageBytes);
            userRepo.save(user);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 중 오류 발생", e);
        }
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

    @Override
    @Transactional
    public void deleteProfileImage() {
        User user = getCurrentUser();
        user.setProfileImage(null);
        userRepo.save(user);
    }
}
