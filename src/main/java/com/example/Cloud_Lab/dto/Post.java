package com.example.Cloud_Lab.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    String Id;
    String title;
    String communityId;
    String creatorNickname;
    Date timeOfCreation;
    String message;
    String linkToImage;
    String linkToParentPost;
    Integer numberOfLikes;
}
