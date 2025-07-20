package com.fluxion.sote.musiclp.service;

import com.fluxion.sote.musiclp.dto.MusicLPDto;
import java.util.List;

public interface MusicLPService {
    List<MusicLPDto> getWeeklyLPs();
    List<MusicLPDto> getMonthlyLPs();
    MusicLPDto getLPDetail(Long id);
}
