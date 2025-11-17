// com.fluxion.sote.stress.dto.StressDto.java
package com.fluxion.sote.stress.dto;

import com.fluxion.sote.stress.entity.StressLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StressDto {
    private Long id;
    private Double hrv;
    private StressLevel stressLevel;
    private LocalDateTime measuredAt;
    private String date; // yyyy-MM-dd
    private Double averageHrv;
}
