package com.fluxion.sote.calendar.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.calendar.dto.CalendarNoteDto;
import com.fluxion.sote.calendar.service.CalendarNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-notes")
@RequiredArgsConstructor
public class CalendarNoteController {

    private final CalendarNoteService calendarNoteService;

    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<CalendarNoteDto>> getMonthlyNotes(
            @RequestAttribute("user") User user,
            @PathVariable int year,
            @PathVariable int month
    ) {
        List<CalendarNoteDto> notes = calendarNoteService.getMonthlyNotes(user.getId(), year, month);
        return ResponseEntity.ok(notes);
    }
}
