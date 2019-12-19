package org.gassman.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private UserDTO user;
    private Double quantity;
    private BigDecimal totalToPay;
    private ProductDTO product;
    private Boolean payed = Boolean.FALSE;
}
