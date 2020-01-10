package org.gassman.admin.listener;

import com.vaadin.flow.component.UI;
import org.gassman.admin.binding.MQBinding;
import org.gassman.admin.dto.OrderDTO;
import org.gassman.admin.dto.UserDTO;
import org.gassman.admin.view.product.ProductsView;
import org.gassman.admin.view.user.UsersView;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(MQBinding.class)
public class MQListener {
    UI ui;
    UsersView usersView;
    ProductsView productsView;

    public void setUIAndUsersViewToUpdate(UI ui, UsersView usersView){
        this.ui = ui;
        this.usersView = usersView;
    }

    @StreamListener(target = MQBinding.USER_REGISTRATION)
    public void processUserRegistration(UserDTO msg) {
        ui.access(()->usersView.refreshUserGrid());
    }

    @StreamListener(target = MQBinding.USER_ORDER)
    public void processUserOrderRegistration(OrderDTO msg) {
        ui.access(()->productsView.refreshProductGrid());
        ui.access(()->productsView.refreshProductOrdersGrid(msg.getProduct().getProductId()));
    }

    @StreamListener(target = MQBinding.ORDER_PAYMENT_CONFIRMATION)
    public void processOrderPaymentConfirmation(OrderDTO msg) {
        ui.access(()->usersView.refreshUserGrid());
        ui.access(()->productsView.refreshProductOrdersGrid(msg.getProduct().getProductId()));
    }

    public void setUIAndProductsViewToUpdate(UI ui, ProductsView productsView) {
        this.ui = ui;
        this.productsView = productsView;
    }
}
