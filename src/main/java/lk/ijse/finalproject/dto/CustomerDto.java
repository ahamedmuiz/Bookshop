package lk.ijse.finalproject.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerDto {
    private int cId;
    private String name;
    private String email;
    private String contact;
}