package com.fluxion.sote.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum InstrumentType {
    PIANO, GUITAR, MARIMBA, VIOLIN, FLUTE;

    @JsonCreator
    public static InstrumentType from(Object v) {
        if (v == null) return null;
        return InstrumentType.valueOf(v.toString().trim().toUpperCase());
    }
}
