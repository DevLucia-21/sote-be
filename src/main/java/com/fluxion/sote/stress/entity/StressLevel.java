// com.fluxion.sote.stress.entity.StressLevel.java
package com.fluxion.sote.stress.entity;

public enum StressLevel {
    LOW, MEDIUM, HIGH;

    public static StressLevel fromHrv(Double hrv) {
        if (hrv == null) return MEDIUM;
        if (hrv >= 60) return LOW;
        if (hrv >= 40) return MEDIUM;
        return HIGH;
    }
}
