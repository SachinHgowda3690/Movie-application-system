package com.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarMovieDto {
    private String id;
    private String title;
    private String year;
    private String type;
    private String poster;


}