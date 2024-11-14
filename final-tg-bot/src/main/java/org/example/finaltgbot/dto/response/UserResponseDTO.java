package org.example.finaltgbot.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.finaltgbot.enums.OrderStep;
import org.example.finaltgbot.enums.RegistrationStep;
import org.example.finaltgbot.enums.Role;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Long companyId;
    private RegistrationStep registrationStep;
    private OrderStep orderStep;
}
