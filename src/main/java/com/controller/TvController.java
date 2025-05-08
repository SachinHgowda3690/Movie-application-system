package com.controller;


import com.dto.ApiResponseDto;
import com.service.RapidApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tv")
public class TvController {

    private final RapidApiService rapidApiService;

    public TvController(RapidApiService rapidApiService) {
        this.rapidApiService = rapidApiService;
    }

    @GetMapping("/trending")
    public ApiResponseDto<?> getTrendingTv() {
        return rapidApiService.getTrendingTv();
    }

    @GetMapping("/{category}")
    public ApiResponseDto<?> getTvsByCategory(@PathVariable String category) {
        return rapidApiService.getMoviesByCategory(category);
    }
}