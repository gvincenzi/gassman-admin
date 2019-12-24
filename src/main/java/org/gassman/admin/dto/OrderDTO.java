package org.gassman.admin.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private UserDTO user;
    private Double quantity;
    @Getter(AccessLevel.NONE)
    private String totalToPay;
    private ProductDTO product;
    private Boolean payed = Boolean.FALSE;
    private String paymentExternalReference;
    private LocalDateTime paymentExternalDateTime;

    public String getTotalToPay(){
        return this.getQuantity() != null && this.getProduct() != null
        && this.getProduct().getPricePerUnit() != null ? this.getQuantity() * (this.getProduct().getPricePerUnit()) + " €" : null;
    }
}
