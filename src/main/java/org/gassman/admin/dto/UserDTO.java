package org.gassman.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String mail;
    private Boolean active = Boolean.TRUE;

    @Override
    public String toString() {
        return name + " " + surname + " : " + mail;
    }
}
