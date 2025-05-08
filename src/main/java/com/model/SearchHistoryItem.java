package com.model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class SearchHistoryItem {

    @Field(name = "id")
    private String id;

    @Field(name = "title")
    private String title;

    @Field(name = "poster")
    private String poster;

    @Field(name = "searchType")
    private String searchType;

    @Field(name = "createdAt")
    private Date createdAt = new Date();
}
