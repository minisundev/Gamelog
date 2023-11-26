package com.best11.gamelog.user.dto;

import com.best11.gamelog.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDto {
    private String username;
    private String description;

//    private List<Post> postList;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.description = user.getDescription();
    }
}
