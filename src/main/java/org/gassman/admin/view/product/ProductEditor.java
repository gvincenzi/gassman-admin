package org.gassman.admin.view.product;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
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
import org.gassman.admin.view.ButtonLabelConfig;
import org.vaadin.olli.ClipboardHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@UIScope
public class ProductEditor extends HorizontalLayout implements KeyNotifier {
    private final ProductResourceClient productResourceClient;
    private final ProductLabelConfig productLabelConfig;
    private final OrderLabelConfig orderLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    private ProductDTO productDTO;

    private Grid<OrderDTO> grid;

    private OrderEditor orderEditor;

    private TextField name, description, unitOfMeasure;
    private NumberField pricePerUnit, availableQuantity;
    private DateTimePicker deliveryDateTime;
    private Checkbox active;
    private Button save, reset, delete, supplierBtn;
    private Text text;
    private ClipboardHelper clipboardHelper;

    private Binder<ProductDTO> binder = new Binder<>(ProductDTO.class);
    private ChangeHandler changeHandler;

    public ProductEditor(ProductResourceClient productResourceClient, OrderEditor orderEditor, ProductLabelConfig productLabelConfig, ButtonLabelConfig buttonLabelConfig, OrderLabelConfig orderLabelConfig) {
        this.productResourceClient = productResourceClient;
        this.buttonLabelConfig = buttonLabelConfig;
        this.productLabelConfig = productLabelConfig;
        this.orderLabelConfig = orderLabelConfig;
        this.orderEditor = orderEditor;
        this.grid = new Grid<>(OrderDTO.class);
        text = new Text("");

        /* Fields to edit properties in Product entity */
        name = new TextField(productLabelConfig.getName());
        description = new TextField(productLabelConfig.getDescription());
        unitOfMeasure = new TextField(productLabelConfig.getUnitOfMeasure());
        pricePerUnit = new NumberField(productLabelConfig.getPricePerUnit());
        availableQuantity = new NumberField(productLabelConfig.getAvailableQuantity());
        deliveryDateTime = new DateTimePicker(productLabelConfig.getDeliveryDateTime());
        active = new Checkbox(productLabelConfig.getActive());

        /* Action buttons */
        save = new Button(buttonLabelConfig.getSave(), VaadinIcon.CHECK.create());
        reset = new Button(buttonLabelConfig.getReset());
        delete = new Button(buttonLabelConfig.getDelete(), VaadinIcon.TRASH.create());
        supplierBtn = new Button(buttonLabelConfig.getSupplier(), VaadinIcon.COPY.create());
        clipboardHelper = new ClipboardHelper("", this.supplierBtn);

        HorizontalLayout actions = new HorizontalLayout(save, reset, delete);
        VerticalLayout editorFields = new VerticalLayout(clipboardHelper, name, description, unitOfMeasure, pricePerUnit, availableQuantity, deliveryDateTime, active, actions);
        editorFields.setWidth("30%");
        grid.setColumns("user","quantity","totalToPay","paid","paymentExternalReference","paymentExternalDateTime");
        grid.getColumnByKey("user").setHeader(orderLabelConfig.getUser());
        grid.getColumnByKey("quantity").setHeader(orderLabelConfig.getQuantity());
        grid.getColumnByKey("totalToPay").setHeader(orderLabelConfig.getTotalToPay());
        grid.getColumnByKey("paid").setHeader(orderLabelConfig.getPaid());
        grid.getColumnByKey("paymentExternalReference").setHeader(orderLabelConfig.getPaymentExternalReference());
        grid.getColumnByKey("paymentExternalDateTime").setHeader(orderLabelConfig.getPaymentExternalDateTime());
        grid.setWidth("100%");

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            orderEditor.editOrder(e.getValue());
        });

        VerticalLayout gridOrders = new VerticalLayout();
        gridOrders.add(grid,text,orderEditor);
        gridOrders.setHeightFull();

        // Listen changes made by the editor, refresh data from backend
        orderEditor.setChangeHandler(() -> {
            orderEditor.setVisible(false);
            grid.setItems(productResourceClient.findProductOrders(productDTO.getProductId()));
            text.setText(String.format("%s : %s €",orderLabelConfig.getTotalAmountSupplier(),computeTotalAmountSupplier().toString()));

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
        reset.addClickListener(e -> editProduct(productDTO));
        setVisible(false);
    }

    private BigDecimal computeTotalAmountSupplier() {
        BigDecimal total = BigDecimal.ZERO;
        if(productResourceClient != null && productDTO != null && productDTO.getProductId() != null) {
            List<OrderDTO> productOrders = productResourceClient.findProductOrders(productDTO.getProductId());
            for (OrderDTO order : productOrders) {
                total = total.add(BigDecimal.valueOf(order.getQuantity() * (order.getProduct().getPricePerUnit())));
            }
        }
        return total;
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

        text.setText(String.format("%s : %s €",orderLabelConfig.getTotalAmountSupplier(),computeTotalAmountSupplier().toString()));

        reset.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.productDTO);

        if(clipboardHelper != null && this.productDTO != null && this.productDTO.getProductId() != null){
            clipboardHelper.setContent("http://localhost:8881/gassman-order-service/public/products/"+this.productDTO.getProductId());
        }

        setVisible(true);

        // Focus first name initially
        name.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

    public void refreshProductOrdersGrid(Long productId){
        if(productId.equals(productDTO.getProductId())){
            grid.setItems(productResourceClient.findProductOrders(productDTO.getProductId()));
        }
    }


}
