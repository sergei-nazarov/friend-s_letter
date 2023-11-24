package com.example.friendsletter.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegistrationDto {

    private Long id;

    @NotNull(message = "Field username cannot be empty")
    @NotEmpty(message = "Field username cannot be empty")
    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{2,40}$)(?!.*[_.]{2})[^_.].*[^_.]$",
            message = "Username must contain only alphabetical characters (include digits), underscore and dot")
    private String username;

    @NotNull(message = "Field password cannot be empty")
    @Size(min = 1, max = 99999, message = "Password must include at least 1 symbol")
    private String password;

    @NotNull(message = "Field email cannot be empty")
    @NotEmpty(message = "Field email cannot be empty")
    @NotEmpty(message = "Email must be email")
    private String email;

}
