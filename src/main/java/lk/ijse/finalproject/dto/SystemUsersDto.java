package lk.ijse.finalproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class SystemUsersDto {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
}