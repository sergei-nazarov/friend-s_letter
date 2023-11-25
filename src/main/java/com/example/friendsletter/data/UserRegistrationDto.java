package com.example.friendsletter.data;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegistrationDto {

    private Long id;

    @NotNull(message = "{registration.error.not_empty_username}")
    @NotEmpty(message = "{registration.error.not_empty_username}")
    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{2,40}$)(?!.*[_.]{2})[^_.].*[^_.]$",
            message = "{registration.error.username_error}")
    private String username;

    @Size(min = 1, max = 99999, message = "{registration.error.empty_password}")
    private String password;

    @NotNull(message = "{registration.error.not_empty_email}")
    @NotEmpty(message = "{registration.error.not_empty_email}")
    @Email(message = "{registration.error.email_incorrect}")
    private String email;

}
