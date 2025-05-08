package com.controller;


import com.dto.ApiResponseDto;
import com.service.RapidApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movie")

public class MovieController {

    private final RapidApiService rapidApiService;

    public MovieController(RapidApiService rapidApiService) {
        this.rapidApiService = rapidApiService;
    }

    @GetMapping("/trending")
    public ApiResponseDto<?> getTrendingMovies() {
        return rapidApiService.getTrendingMovies();
    }

    @GetMapping("/{category}")
    public ApiResponseDto<?> getMoviesByCategory(@PathVariable String category) {
        return rapidApiService.getMoviesByCategory(category);
    }
}