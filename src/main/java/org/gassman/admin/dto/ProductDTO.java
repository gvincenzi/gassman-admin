package org.gassman.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private String unitOfMeasure;
    private Double pricePerUnit;
    private Double availableQuantity;
    private LocalDateTime deliveryDateTime;
    private Boolean active = Boolean.TRUE;
}
