package com.fluxion.sote.calendar.controller;

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

    @GetMapping("/{userId}/{year}/{month}")
    public ResponseEntity<List<CalendarNoteDto>> getMonthlyNotes(
            @PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        List<CalendarNoteDto> notes = calendarNoteService.getMonthlyNotes(userId, year, month);
        return ResponseEntity.ok(notes);
    }
}
