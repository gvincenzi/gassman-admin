package org.gassman.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DiscoveryProductDTO extends ProductDTO {
    private String gassmanName;
    private List<UserDTO> administrators;

    public DiscoveryProductDTO(ProductDTO productDTO){
        this.setActive(productDTO.getActive());
        this.setAvailableQuantity(productDTO.getAvailableQuantity());
        this.setDeliveryDateTime(productDTO.getDeliveryDateTime());
        this.setDescription(productDTO.getDescription());
        this.setName(productDTO.getName());
        this.setPricePerUnit(productDTO.getPricePerUnit());
        this.setProductId(productDTO.getProductId());
        this.setUnitOfMeasure(productDTO.getUnitOfMeasure());
    }
}
