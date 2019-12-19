package org.gassman.admin.view.product;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.gassman.admin.client.ProductResourceClient;
import org.gassman.admin.dto.ProductDTO;

@Route
public class ProductsView extends VerticalLayout implements KeyNotifier {

    private final ProductResourceClient productResourceClient;
    private final ProductEditor productEditor;
    final Grid<ProductDTO> grid;
    private final Button addNewBtn, usersBtn;

    public ProductsView(ProductResourceClient productResourceClient, ProductEditor productEditor) {
        add(new Text("GasSMan - List of products"));
        this.grid = new Grid<>(ProductDTO.class);
        this.productEditor = productEditor;
        this.productResourceClient = productResourceClient;
        this.addNewBtn = new Button("New product", VaadinIcon.PLUS.create());
        this.usersBtn = new Button("Users management", VaadinIcon.USERS.create());
        usersBtn.addClickListener(e ->
                usersBtn.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        // build layout
        HorizontalLayout actions = new HorizontalLayout(addNewBtn, usersBtn);
        add(actions, grid, productEditor);

        grid.setItems(productResourceClient.findAll());
        grid.setHeight("300px");

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            productEditor.editProduct(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> productEditor.editProduct(new ProductDTO()));

        // Listen changes made by the editor, refresh data from backend
        productEditor.setChangeHandler(() -> {
            productEditor.setVisible(false);
            grid.setItems(productResourceClient.findAll());
        });
    }
}
