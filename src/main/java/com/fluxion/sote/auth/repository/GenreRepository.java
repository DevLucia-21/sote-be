package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findAllByIdIn(List<Integer> ids);
}
