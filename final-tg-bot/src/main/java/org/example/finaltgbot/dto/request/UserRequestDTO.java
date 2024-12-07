package org.example.finaltgbot.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.finaltgbot.enums.Role;

@Getter
@Setter
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
//    private String password;
    private Role role;
    private Long companyId;
    private int chatId;

}
