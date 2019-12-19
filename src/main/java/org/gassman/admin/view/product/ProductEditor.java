package org.gassman.admin.view.product;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.gassman.admin.client.ProductResourceClient;
import org.gassman.admin.component.DateTimePicker;
import org.gassman.admin.dto.OrderDTO;
import org.gassman.admin.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringComponent
@UIScope
public class ProductEditor extends HorizontalLayout implements KeyNotifier {

    @Autowired
    private final ProductResourceClient productResourceClient;

    private ProductDTO productDTO;

    private Grid<OrderDTO> grid;

    private OrderEditor orderEditor;

    /* Fields to edit properties in Customer entity */
    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    TextField unitOfMeasure = new TextField("Unit Of Measure (ex. KG)");
    NumberField pricePerUnit = new NumberField("Price Per Unit");
    NumberField availableQuantity = new NumberField("Available Quantity");
    DateTimePicker deliveryDateTime = new DateTimePicker("Delivery Date");
    Checkbox active = new Checkbox("is Active");

    /* Action buttons */
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());

    Binder<ProductDTO> binder = new Binder<>(ProductDTO.class);
    private ChangeHandler changeHandler;

    public ProductEditor(ProductResourceClient productResourceClient, OrderEditor orderEditor) {
        this.productResourceClient = productResourceClient;
        this.orderEditor = orderEditor;
        this.grid = new Grid<>(OrderDTO.class);

        HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
        VerticalLayout editorFields = new VerticalLayout(name, description, unitOfMeasure, pricePerUnit, availableQuantity, deliveryDateTime, active, actions);
        editorFields.setWidth("30%");
        grid.setColumns("user","quantity","totalToPay","payed");
        grid.setWidth("70%");

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            orderEditor.editOrder(e.getValue());
        });

        VerticalLayout gridOrders = new VerticalLayout();
        gridOrders.add(grid,orderEditor);
        gridOrders.setHeightFull();

        // Listen changes made by the editor, refresh data from backend
        orderEditor.setChangeHandler(() -> {
            orderEditor.setVisible(false);
            grid.setItems(productResourceClient.findProductOrders(productDTO.getProductId()));
            changeHandler.onChange();
        });

        add(editorFields, gridOrders);

        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);
        setWidthFull();

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editProduct(productDTO));
        setVisible(false);
    }

    void delete() {
        productResourceClient.deleteProduct(productDTO.getProductId());
        changeHandler.onChange();
    }

    void save() {
        if(productDTO.getProductId() != null){
            productResourceClient.updateProduct(productDTO.getProductId(), productDTO);
        } else {
            productResourceClient.addProduct(productDTO);
        }
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = productDTO.getProductId() != null;
        if (persisted) {
            // Find fresh entity for editing
            this.productDTO = productResourceClient.findById(productDTO.getProductId());
            grid.setItems(productResourceClient.findProductOrders(productDTO.getProductId()));
        }
        else {
            grid.setItems(new ArrayList(0));
            this.productDTO = productDTO;
        }
        cancel.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.productDTO);

        setVisible(true);

        // Focus first name initially
        name.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }
}
