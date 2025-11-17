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

    /**
     * 월별 조회
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<CalendarNoteDto>> getMonthlyNotes(
            @RequestAttribute("user") User user,
            @PathVariable int year,
            @PathVariable int month
    ) {
        List<CalendarNoteDto> notes = calendarNoteService.getMonthlyNotes(user.getId(), year, month);
        return ResponseEntity.ok(notes);
    }

    /**
     * 하루 조회
     */
    @GetMapping("/{year}/{month}/{day}")
    public ResponseEntity<CalendarNoteDto> getDailyNote(
            @RequestAttribute("user") User user,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int day
    ) {
        CalendarNoteDto note = calendarNoteService.getDailyNote(user.getId(), year, month, day);
        return ResponseEntity.ok(note);
    }

    /**
     * 주간 조회
     */
    @GetMapping("/{year}/{month}/{day}/week")
    public ResponseEntity<List<CalendarNoteDto>> getWeeklyNotes(
            @RequestAttribute("user") User user,
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int day
    ) {
        List<CalendarNoteDto> notes = calendarNoteService.getWeeklyNotes(user.getId(), year, month, day);
        return ResponseEntity.ok(notes);
    }
}
