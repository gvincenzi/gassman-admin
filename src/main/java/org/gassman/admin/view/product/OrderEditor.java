package org.gassman.admin.view.product;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.gassman.admin.client.OrderResourceClient;
import org.gassman.admin.dto.OrderDTO;
import org.gassman.admin.view.ButtonLabelConfig;

@SpringComponent
@UIScope
public class OrderEditor extends VerticalLayout implements KeyNotifier {
    private final OrderResourceClient orderResourceClient;
    private final OrderLabelConfig orderLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;
    private OrderDTO orderDTO;

    /* Fields to edit properties in Order entity */
    NumberField quantity;
    Checkbox paid;

    /* Action buttons */
    Button save,delete;

    Binder<OrderDTO> binder = new Binder<>(OrderDTO.class);
    private ChangeHandler changeHandler;

    public OrderEditor(OrderResourceClient orderResourceClient, OrderLabelConfig orderLabelConfig, ButtonLabelConfig buttonLabelConfig) {
        this.orderResourceClient = orderResourceClient;
        this.orderLabelConfig = orderLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;

        quantity = new NumberField(orderLabelConfig.getQuantity());
        paid = new Checkbox(orderLabelConfig.getPaid());
        save = new Button(buttonLabelConfig.getSave(), VaadinIcon.CHECK.create());
        delete = new Button(buttonLabelConfig.getDelete(), VaadinIcon.TRASH.create());

        HorizontalLayout actions = new HorizontalLayout(save, delete);

        add(quantity, paid, actions);

        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        setVisible(false);
    }

    void delete() {
        orderResourceClient.deleteOrder(orderDTO.getOrderId());
        changeHandler.onChange();
    }

    void save() {
        orderResourceClient.updateOrder(orderDTO.getOrderId(), orderDTO);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = orderDTO.getOrderId() != null;
        this.orderDTO = orderDTO;

        // Bind order properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.orderDTO);

        setVisible(true);

        // Focus first name initially
        quantity.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }
}
