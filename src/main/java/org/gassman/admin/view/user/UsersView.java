package org.gassman.admin.view.user;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.gassman.admin.client.UserResourceClient;
import org.gassman.admin.dto.UserDTO;

@Route
public class UsersView extends VerticalLayout implements KeyNotifier {

    private final UserResourceClient userResourceClient;
    private final UserEditor userEditor;
    final Grid<UserDTO> grid;
    private final Button addNewBtn, productBtn;

    public UsersView(UserResourceClient userResourceClient, UserEditor userEditor) {
        add(new Text("GasSMan - List of users"));
        this.grid = new Grid<>(UserDTO.class);
        this.userEditor = userEditor;
        this.userResourceClient = userResourceClient;
        this.addNewBtn = new Button("New user", VaadinIcon.PLUS.create());
        this.productBtn = new Button("Products management", VaadinIcon.BOOK_DOLLAR.create());
        productBtn.addClickListener(e ->
                productBtn.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );

        // build layout
        HorizontalLayout actions = new HorizontalLayout(addNewBtn, productBtn);
        add(actions, grid, userEditor);

        grid.setItems(userResourceClient.findAll());
        grid.setHeight("300px");

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
