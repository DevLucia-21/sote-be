// src/main/java/com/fluxion/sote/auth/service/SecurityQuestionService.java
package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.SecurityQuestion;
import java.util.List;

public interface SecurityQuestionService {
    List<SecurityQuestion> findAll();
}
