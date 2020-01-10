package org.gassman.admin.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MQBinding {
    String USER_REGISTRATION = "userRegistrationChannel";
    String ORDER_PAYMENT = "orderPaymentChannel";
    String ORDER_PAYMENT_CONFIRMATION = "orderPaymentConfirmationChannel";

    @Input(USER_REGISTRATION)
    SubscribableChannel userRegistrationChannel();

    @Input(ORDER_PAYMENT)
    SubscribableChannel orderPaymentChannel();

    @Input(ORDER_PAYMENT_CONFIRMATION)
    SubscribableChannel userOrderPaymentConfirmationChannel();
}
