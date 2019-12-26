package org.gassman.admin.view.user;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.gassman.admin.client.UserResourceClient;
import org.gassman.admin.dto.UserDTO;
import org.gassman.admin.view.ButtonLabelConfig;

@SpringComponent
@UIScope
public class UserEditor extends VerticalLayout implements KeyNotifier {
    private final UserResourceClient userResourceClient;
    private final UserLabelConfig userLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    private UserDTO userDTO;

    Binder<UserDTO> binder = new Binder<>(UserDTO.class);
    private ChangeHandler changeHandler;

    private TextField name,surname,mail;
    private Checkbox active;
    private Button save,reset,delete;

    public UserEditor(UserResourceClient userResourceClient, UserLabelConfig userLabelConfig, ButtonLabelConfig buttonLabelConfig) {
        this.userResourceClient = userResourceClient;
        this.userLabelConfig = userLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;

        /* Fields to edit properties in User entity */
        name = new TextField(userLabelConfig.getFirstname());
        surname = new TextField(userLabelConfig.getLastname());
        mail  = new TextField(userLabelConfig.getMail());
        active = new Checkbox(userLabelConfig.getActive());

        /* Action buttons */
        save = new Button(buttonLabelConfig.getSave(), VaadinIcon.CHECK.create());
        reset = new Button(buttonLabelConfig.getReset());
        delete = new Button(buttonLabelConfig.getDelete(), VaadinIcon.TRASH.create());
        HorizontalLayout actions = new HorizontalLayout(save, reset, delete);

        HorizontalLayout data = new HorizontalLayout(name, surname, mail);

        add(data, active, actions);

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
        reset.addClickListener(e -> editUser(userDTO));
        setVisible(false);
    }

    void delete() {
        userResourceClient.deleteUser(userDTO.getId());
        changeHandler.onChange();
    }

    void save() {
        final boolean persisted = userDTO.getId() != null;
        if (persisted) {
            userResourceClient.updateUser(userDTO.getId(), userDTO);
        } else {
            userResourceClient.addUser(userDTO);
        }
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editUser(UserDTO userDTO) {
        if (userDTO == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = userDTO.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            this.userDTO = userResourceClient.findById(userDTO.getId());
        }
        else {
            this.userDTO = userDTO;
        }
        reset.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.userDTO);

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
