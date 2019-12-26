package org.gassman.admin.view.product;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.gassman.admin.client.ProductResourceClient;
import org.gassman.admin.dto.ProductDTO;
import org.gassman.admin.view.ButtonLabelConfig;

import java.io.InputStream;

@Route
public class ProductsView extends VerticalLayout implements KeyNotifier {
    private final ProductResourceClient productResourceClient;
    private final ProductEditor productEditor;
    private final ProductLabelConfig productLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    final Grid<ProductDTO> grid;
    private final Button addNewBtn, usersBtn;

    public ProductsView(ProductResourceClient productResourceClient, ProductEditor productEditor, ProductLabelConfig productLabelConfig, ButtonLabelConfig buttonLabelConfig) {
        this.productEditor = productEditor;
        this.productResourceClient = productResourceClient;
        this.buttonLabelConfig = buttonLabelConfig;
        this.productLabelConfig = productLabelConfig;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "GasSMan Logo");
        logo.setMaxWidth("370px");
        add(logo);

        this.grid = new Grid<>(ProductDTO.class);

        this.addNewBtn = new Button(buttonLabelConfig.getProductNew(), VaadinIcon.PLUS.create());
        this.usersBtn = new Button(buttonLabelConfig.getUserManagement(), VaadinIcon.USERS.create());
        usersBtn.addClickListener(e ->
                usersBtn.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        // build layout
        HorizontalLayout actions = new HorizontalLayout(addNewBtn, usersBtn);
        add(actions, grid, productEditor);

        grid.setItems(productResourceClient.findAll());
        grid.setHeight("300px");

        grid.setColumns("name","description","unitOfMeasure","pricePerUnit","availableQuantity","deliveryDateTime","active");
        grid.getColumnByKey("name").setHeader(productLabelConfig.getName());
        grid.getColumnByKey("description").setHeader(productLabelConfig.getDescription());
        grid.getColumnByKey("unitOfMeasure").setHeader(productLabelConfig.getUnitOfMeasure());
        grid.getColumnByKey("pricePerUnit").setHeader(productLabelConfig.getPricePerUnit());
        grid.getColumnByKey("availableQuantity").setHeader(productLabelConfig.getAvailableQuantity());
        grid.getColumnByKey("deliveryDateTime").setHeader(productLabelConfig.getDeliveryDateTime());
        grid.getColumnByKey("active").setHeader(productLabelConfig.getActive());

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
