package org.example.finaltgbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.finaltgbot.enums.Language;
import org.example.finaltgbot.enums.OrderStep;
import org.example.finaltgbot.enums.RegistrationStep;
import org.example.finaltgbot.enums.Role;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String password;

    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private int chatId;

    @Enumerated(EnumType.STRING)
    private RegistrationStep registrationStep;

    @Enumerated(EnumType.STRING)
    private OrderStep orderStep = OrderStep.INITIATE_ORDER;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Getter
    private boolean active;


    private String language;

    public Language getLanguage() {
        return language != null ? Language.valueOf(language) : Language.EN;
    }


}
