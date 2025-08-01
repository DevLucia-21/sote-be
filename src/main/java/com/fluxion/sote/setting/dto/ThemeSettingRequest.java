package com.fluxion.sote.setting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThemeSettingRequest {

    @NotNull
    private boolean darkMode;
}
