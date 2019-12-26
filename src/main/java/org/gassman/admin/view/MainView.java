package org.gassman.admin.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.InputStream;

@Route
public class MainView extends VerticalLayout {
    public MainView() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "GasSMan Logo");
        logo.setMaxWidth("370px");
        add(logo);
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

        // build layout
        HorizontalLayout actions = new HorizontalLayout(buttonUser, buttonProduct);
        add(actions);
    }
}
