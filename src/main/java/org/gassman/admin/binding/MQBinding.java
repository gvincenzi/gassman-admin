package org.gassman.admin.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MQBinding {
    String USER_REGISTRATION = "userRegistrationChannel";

    @Input(USER_REGISTRATION)
    SubscribableChannel userRegistrationChannel();
}
