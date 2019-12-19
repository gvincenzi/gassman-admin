package org.gassman.admin.client;

import org.gassman.admin.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("gassman-order-service/users")
public interface UserResourceClient {
    @GetMapping("/all")
    List<UserDTO> findAll();

    @GetMapping("/{id}")
    UserDTO findById(@PathVariable("id") Long id);

    @PostMapping()
    UserDTO addUser(@RequestBody UserDTO userDTO);

    @PutMapping("/{id}")
    UserDTO updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO);

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
