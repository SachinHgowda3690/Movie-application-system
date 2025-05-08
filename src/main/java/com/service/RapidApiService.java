package com.service;



import com.dto.ApiResponseDto;
import com.dto.SimilarMovieDto;
import com.model.SearchHistoryItem;
import com.model.User;
import com.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for calling external APIs (RapidAPI/OMDB) and managing user search history.
 */
@Service
@RequiredArgsConstructor
public class RapidApiService {


    private final UserRepository userRepository;

    private final WebClient webClient; // Autowired from config



    public ApiResponseDto<?> getTrendingMovies() {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/movies/upcoming")
                    .header("X-RapidAPI-Key", "your-rapid-api-key-here")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("results")) {
                return new ApiResponseDto<>(false, "No trending movies found", null);
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            Map<String, Object> randomMovie = results.get(new Random().nextInt(results.size()));

            return new ApiResponseDto<>(true, "Success", randomMovie);

        } catch (Exception e) {
            return new ApiResponseDto<>(false, "Internal server error", null);
        }
    }

    public ApiResponseDto<?> getTrendingTv() {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/shows/popular")
                    .header("X-RapidAPI-Key", "your-rapid-api-key-here")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return new ApiResponseDto<>(true, "Success", response);

        } catch (Exception e) {
            return new ApiResponseDto<>(false, "Failed to fetch TV shows", null);
        }
    }

    public ApiResponseDto<List<?>> getSimilarMovies(String name) {
        try {
            WebClient omdbClient = WebClient.create("http://www.omdbapi.com");

            Map<String, Object> response = omdbClient.get()
                    .uri("?s={name}&apikey={key}", name, "your-omdb-api-key-here")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if ("False".equals(response.get("Response"))) {
                return new ApiResponseDto<>(false, (String) response.get("Error"), null);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> searchResults = (List<Map<String, Object>>) response.get("Search");

            List<SimilarMovieDto> filteredResults = searchResults.stream()
                    .filter(item -> "movie".equalsIgnoreCase((String) item.get("Type")))
                    .map(item -> new SimilarMovieDto(
                            (String) item.get("imdbID"),
                            (String) item.get("Title"),
                            (String) item.get("Year"),
                            (String) item.get("Type"),
                            (String) item.get("Poster")
                    ))
                    .collect(Collectors.toList());

            return new ApiResponseDto<>(true, "Success", filteredResults);

        } catch (Exception e) {
            return new ApiResponseDto<>(false, "Failed to fetch similar movies", null);
        }
    }

    public ApiResponseDto<?> getMoviesByCategory(String category) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/movies/" + category)
                    .header("X-RapidAPI-Key", "your-rapid-api-key-here")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("results")) {
                @SuppressWarnings("unchecked")
                List<Object> results = (List<Object>) response.get("results");
                return new ApiResponseDto<>(true, "Success", results);
            }

            return new ApiResponseDto<>(false, "No movies found in this category", null);

        } catch (Exception e) {
            return new ApiResponseDto<>(false, "Internal server error", null);
        }
    }

    public void addToSearchHistory(String userId, Map<String, Object> item, String searchType) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        SearchHistoryItem historyItem = new SearchHistoryItem();
        historyItem.setId((String) item.get("id"));
        historyItem.setTitle((String) item.get("title"));
        historyItem.setPoster((String) item.get("poster"));
        historyItem.setSearchType(searchType);
        historyItem.setCreatedAt(new Date());

        user.getSearchHistory().add(historyItem);
        userRepository.save(user);
    }

    public List<SearchHistoryItem> getSearchHistory(String userId) {
        return userRepository.findById(userId)
                .map(User::getSearchHistory)
                .orElse(Collections.emptyList());
    }

    public boolean removeItemFromSearchHistory(String userId, String itemId) {
        return userRepository.findById(userId)
                .map(user -> {
                    boolean removed = user.getSearchHistory().removeIf(item -> item.getId().equals(itemId));
                    if (removed) userRepository.save(user);
                    return removed;
                })
                .orElse(false);
    }
}