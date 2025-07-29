package com.fluxion.sote.challenge.service;

import com.fluxion.sote.challenge.dto.ChallengeDefinitionRequestDto;
import com.fluxion.sote.challenge.dto.ChallengeDefinitionResponseDto;
import com.fluxion.sote.challenge.entity.ChallengeDefinition;
import com.fluxion.sote.challenge.repository.ChallengeDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeDefinitionService {

    private final ChallengeDefinitionRepository challengeRepository;

    // 챌린지 등록
    @Transactional
    public Long create(ChallengeDefinitionRequestDto dto) {
        ChallengeDefinition challenge = ChallengeDefinition.builder()
                .content(dto.content())
                .emotionType(dto.emotionType())
                .category(dto.category())
                .isDeleted(false)
                .build();

        return challengeRepository.save(challenge).getId();
    }

    // 챌린지 수정
    @Transactional
    public void update(Long id, ChallengeDefinitionRequestDto dto) {
        ChallengeDefinition challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챌린지입니다."));

        challenge.update(dto.content(), dto.emotionType(), dto.category());
    }

    // 챌린지 삭제 (soft delete)
    @Transactional
    public void delete(Long id) {
        ChallengeDefinition challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챌린지입니다."));

        challenge.softDelete();
    }

    // 전체 조회
    public List<ChallengeDefinitionResponseDto> findAll() {
        return challengeRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .map(c -> ChallengeDefinitionResponseDto.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .emotionType(c.getEmotionType())
                        .category(c.getCategory())
                        .build())
                .toList();
    }
}
