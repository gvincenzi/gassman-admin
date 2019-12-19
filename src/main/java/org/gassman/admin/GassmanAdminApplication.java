package org.gassman.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GassmanAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GassmanAdminApplication.class, args);
    }

}
