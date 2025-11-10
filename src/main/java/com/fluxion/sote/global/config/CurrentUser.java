// com.fluxion.sote.global.security.CurrentUser.java
package com.fluxion.sote.global.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {}
    public static Long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        Object p = auth.getPrincipal();
        return (p instanceof Long) ? (Long) p : Long.valueOf(p.toString());
    }
}
