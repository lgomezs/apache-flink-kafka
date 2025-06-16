package com.example.apiviewer.controller;

import com.example.apiviewer.entity.MovieEntity;
import com.example.apiviewer.repository.MovieRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

	private final MovieRepository movieRepository;

	public MovieController(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	@GetMapping
	public List<MovieEntity> getViews() {
		return this.movieRepository.findAll();
	}

}
