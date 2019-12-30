package org.gassman.admin.client;

import org.gassman.admin.dto.UserCreditDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("gassman-payment-service/internal-credit")
public interface UserCreditResourceClient {
    @GetMapping("/all")
    List<UserCreditDTO> findAll();

    @GetMapping("/{userId}")
    UserCreditDTO findById(@PathVariable("userId") Long userId);

    @PutMapping("/{userId}/{additionalCredit}")
    UserCreditDTO newCredit(@PathVariable("userId") Long userId, @PathVariable("additionalCredit") BigDecimal additionalCredit);
}
