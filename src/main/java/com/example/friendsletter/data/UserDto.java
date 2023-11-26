package com.example.friendsletter.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    @NotNull(message = "{registration.error.not_empty_username}")
    @NotEmpty(message = "{registration.error.not_empty_username}")
    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{2,40}$)(?!.*[_.]{2})[^_.].*[^_.]$",
            message = "{registration.error.username_error}")
    private String username;
    private String password;
    @NotNull(message = "{registration.error.not_empty_email}")
    @NotEmpty(message = "{registration.error.not_empty_email}")
    @Email(message = "{registration.error.email_incorrect}")
    private String email;

}
