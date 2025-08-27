package com.fluxion.sote.auth.controller;

import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.repository.GenreRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreRepository genreRepo;
    public GenreController(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    @GetMapping
    public List<Genre> list() {
        return genreRepo.findAll();
    }
}
