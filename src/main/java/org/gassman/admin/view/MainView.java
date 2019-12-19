package org.gassman.admin.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {
    public MainView() {
        Button buttonUser = new Button(
                "Users management", VaadinIcon.USERS.create());
        buttonUser.addClickListener(e ->
                buttonUser.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        Button buttonProduct = new Button(
                "Products management", VaadinIcon.BOOK_DOLLAR.create());
        buttonProduct.addClickListener(e ->
                buttonProduct.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );

        add(new Text("GasSMan - GAS Sales Management Open Software"),buttonUser,buttonProduct);

    }
}
