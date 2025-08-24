package com.fluxion.sote.global.enums;

public enum InstrumentType {
    PIANO,
    GUITAR,
    DRUM,
    VIOLIN,
    FLUTE;

    public static InstrumentType fromString(String value) {
        if (value == null) return null;
        return InstrumentType.valueOf(value.trim().toUpperCase());
    }

    public String toLower() {
        return name().toLowerCase();
    }
}
