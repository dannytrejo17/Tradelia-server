package com.tradelia.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoUserSeed {

    private String username;
    private String email;
    private String password;
    private String role;
}
