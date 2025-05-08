package com.controller;


import com.dto.ApiResponseDto;
import com.model.SearchHistoryItem;
import com.repository.UserRepository;
import com.service.RapidApiService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final RapidApiService rapidApiService;
    private final UserRepository userRepository;

    public SearchController(RapidApiService rapidApiService, UserRepository userRepository) {
        this.rapidApiService = rapidApiService;
        this.userRepository = userRepository;
    }

    @GetMapping("/person/{query}")
    public ApiResponseDto<?> searchPerson(@PathVariable String query, @RequestHeader("X-User-ID") String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", "tt1234567");
        result.put("title", "Sample Person");
        result.put("type", "person");
        result.put("poster", "/sample.jpg");

        rapidApiService.addToSearchHistory(userId, result, "person");
        return new ApiResponseDto<>(true, "Success", result);
    }

    @GetMapping("/movies_and_shows/{query}")
    public ApiResponseDto<?> searchMoviesAndTvShows(@PathVariable String query, @RequestHeader("X-User-ID") String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", "tt7654321");
        result.put("title", "Sample Movie");
        result.put("type", "movie");
        result.put("poster", "/sample.jpg");

        rapidApiService.addToSearchHistory(userId, result, "movie and tv shows");
        return new ApiResponseDto<>(true, "Success", result);
    }

    @GetMapping("/history")
    public ApiResponseDto<List<SearchHistoryItem>> getSearchHistory(@RequestHeader("X-User-ID") String userId) {
        return new ApiResponseDto<>(true, "Success", rapidApiService.getSearchHistory(userId));
    }

    @DeleteMapping("/history/{id}")
    public ApiResponseDto<String> removeItemFromSearchHistory(@PathVariable String id, @RequestHeader("X-User-ID") String userId) {
        boolean removed = rapidApiService.removeItemFromSearchHistory(userId, id);
        return new ApiResponseDto<>(removed, removed ? "Removed" : "Not found", null);
    }
}