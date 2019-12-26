package org.gassman.admin.view.user;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.gassman.admin.client.UserResourceClient;
import org.gassman.admin.dto.UserDTO;
import org.gassman.admin.view.ButtonLabelConfig;

import java.io.InputStream;

@Route
public class UsersView extends VerticalLayout implements KeyNotifier {
    private final UserResourceClient userResourceClient;
    private final UserEditor userEditor;
    private final UserLabelConfig userLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    final Grid<UserDTO> grid;
    private final Button addNewBtn, productBtn;

    public UsersView(UserResourceClient userResourceClient, UserEditor userEditor, UserLabelConfig userLabelConfig, ButtonLabelConfig buttonLabelConfig) {
        this.userEditor = userEditor;
        this.userResourceClient = userResourceClient;
        this.userLabelConfig = userLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "GasSMan Logo");
        logo.setMaxWidth("370px");
        add(logo);

        this.grid = new Grid<>(UserDTO.class);
        this.addNewBtn = new Button(buttonLabelConfig.getUserNew(), VaadinIcon.PLUS.create());
        this.productBtn = new Button(buttonLabelConfig.getProductManagement(), VaadinIcon.BOOK_DOLLAR.create());
        productBtn.addClickListener(e ->
                productBtn.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );

        // build layout
        HorizontalLayout actions = new HorizontalLayout(addNewBtn, productBtn);
        add(actions, grid, userEditor);

        grid.setItems(userResourceClient.findAll());
        grid.setHeight("300px");

        grid.setColumns("name","surname","mail","active");
        grid.getColumnByKey("name").setHeader(userLabelConfig.getFirstname());
        grid.getColumnByKey("surname").setHeader(userLabelConfig.getLastname());
        grid.getColumnByKey("mail").setHeader(userLabelConfig.getMail());
        grid.getColumnByKey("active").setHeader(userLabelConfig.getActive());

        // Connect selected User to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            userEditor.editUser(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> userEditor.editUser(new UserDTO()));

        // Listen changes made by the editor, refresh data from backend
        userEditor.setChangeHandler(() -> {
            userEditor.setVisible(false);
            grid.setItems(userResourceClient.findAll());
        });
    }
}
