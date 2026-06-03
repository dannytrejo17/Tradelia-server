package com.tradelia.Dto;

import com.tradelia.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long id;
    private String email;
    private String username;
    private String profileImageUrl;

    public static UserProfileDto from(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                null
        );
    }
}
