package org.gassman.admin.client;

import org.gassman.admin.dto.HeartbeatDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("gassman-discovery-service/heartbeats")
public interface HeartbeatResourceClient {
    @GetMapping
    List<HeartbeatDTO> getHeartbeats();
}
