package com.fluxion.sote.global.validation;

import com.fluxion.sote.global.validation.NoProfanity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NoProfanityValidator implements ConstraintValidator<NoProfanity, String> {

    private final List<String> profanityWords;

    public NoProfanityValidator() throws Exception {
        try (var in = new ClassPathResource("profanity.txt").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            profanityWords = reader.lines()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        String lower = value.toLowerCase();
        return profanityWords.stream()
                .noneMatch(lower::contains);
    }
}
