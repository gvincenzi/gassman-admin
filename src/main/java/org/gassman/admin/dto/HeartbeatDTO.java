package org.gassman.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class HeartbeatDTO {
    private String name;
    private List<ProductDTO> products;
    private List<UserDTO> administrators;
    private LocalDateTime lastBeatDateTime;
}
