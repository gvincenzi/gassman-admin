package org.gassman.admin.client;

import org.gassman.admin.dto.OrderDTO;
import org.gassman.admin.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("gassman-order-service/orders")
public interface OrderResourceClient {
    @GetMapping("/{id}")
    OrderDTO findById(@PathVariable("id") Long id);

    @PutMapping("/{id}")
    OrderDTO updateOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO);

    @DeleteMapping("/{id}")
    void deleteOrder(@PathVariable("id") Long id);
}
