package org.gassman.admin.view.discovery;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.gassman.admin.client.HeartbeatResourceClient;
import org.gassman.admin.dto.DiscoveryProductDTO;
import org.gassman.admin.dto.HeartbeatDTO;
import org.gassman.admin.dto.ProductDTO;
import org.gassman.admin.dto.UserDTO;
import org.gassman.admin.listener.MQListener;
import org.gassman.admin.view.ButtonLabelConfig;
import org.gassman.admin.view.product.ProductLabelConfig;
import org.gassman.admin.view.user.UserLabelConfig;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Push
@Route
@PageTitle("GasSMan Discovery - Product List")
public class DiscoveryProductsView extends VerticalLayout implements KeyNotifier {
    private final HeartbeatResourceClient heartbeatResourceClient;
    private final ProductLabelConfig productLabelConfig;
    private final UserLabelConfig userLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    final Grid<DiscoveryProductDTO> grid;
    final Grid<UserDTO> administrators;
    private final Button productsBtn, usersBtn, logoutBtn;

    public DiscoveryProductsView(HeartbeatResourceClient heartbeatResourceClient, ProductLabelConfig productLabelConfig, UserLabelConfig userLabelConfig, ButtonLabelConfig buttonLabelConfig, MQListener mqListener) {
        this.heartbeatResourceClient = heartbeatResourceClient;
        this.buttonLabelConfig = buttonLabelConfig;
        this.productLabelConfig = productLabelConfig;
        this.userLabelConfig = userLabelConfig;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "GasSMan Logo");
        logo.setMaxWidth("370px");
        add(logo);

        this.grid = new Grid<>(DiscoveryProductDTO.class);
        this.administrators = new Grid<>(UserDTO.class);
        this.administrators.setVisible(Boolean.FALSE);

        administrators.setColumns("name","surname","mail");
        administrators.getColumnByKey("name").setHeader(userLabelConfig.getFirstname());
        administrators.getColumnByKey("surname").setHeader(userLabelConfig.getLastname());
        administrators.getColumnByKey("mail").setHeader(userLabelConfig.getMail());

        Renderer renderer = TemplateRenderer
                .of("<span on-dblclick='doubleClicked'>Telegram</span>")
                .withEventHandler("doubleClicked", item -> openTelegram((UserDTO)item));
        administrators.addColumn(renderer);

        administrators.setSelectionMode(Grid.SelectionMode.NONE);

        this.usersBtn = new Button(buttonLabelConfig.getUserManagement(), VaadinIcon.USERS.create());
        usersBtn.addClickListener(e ->
                usersBtn.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        this.productsBtn = new Button(buttonLabelConfig.getProductManagement(), VaadinIcon.BOOK_DOLLAR.create());
        productsBtn.addClickListener(e ->
                productsBtn.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );

        this.logoutBtn = new Button("Logout", VaadinIcon.EXIT.create());
        this.logoutBtn.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().getPage().setLocation("logout");
        });

        // build layout
        HorizontalLayout actions = new HorizontalLayout(usersBtn, productsBtn, logoutBtn);
        add(actions, grid, administrators);

        refreshProductGrid(heartbeatResourceClient);
        grid.setHeight("300px");

        grid.setColumns("name","description","unitOfMeasure","pricePerUnit","availableQuantity","deliveryDateTime","gassmanName");
        grid.getColumnByKey("name").setHeader(productLabelConfig.getName());
        grid.getColumnByKey("description").setHeader(productLabelConfig.getDescription());
        grid.getColumnByKey("unitOfMeasure").setHeader(productLabelConfig.getUnitOfMeasure());
        grid.getColumnByKey("pricePerUnit").setHeader(productLabelConfig.getPricePerUnit());
        grid.getColumnByKey("availableQuantity").setHeader(productLabelConfig.getAvailableQuantity());
        grid.getColumnByKey("deliveryDateTime").setHeader(productLabelConfig.getDeliveryDateTime());
        grid.getColumnByKey("gassmanName").setHeader("GasSMan");

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            showAdministrators(e.getValue());
        });
    }

    private void openTelegram(UserDTO item) {
        UI.getCurrent().getPage().executeJs("window.open(\"https://web.telegram.org/#/im?p=u"+item.getTelegramUserId()+"\", \"_blank\");");
    }

    private void showAdministrators(DiscoveryProductDTO value) {
        this.administrators.setItems(value.getAdministrators());
        this.administrators.setVisible(Boolean.TRUE);
    }

    private void refreshProductGrid(HeartbeatResourceClient heartbeatResourceClient) {
        List<DiscoveryProductDTO> discoveryProductDTOS = new ArrayList<>();
        List<HeartbeatDTO> heartbeats = heartbeatResourceClient.getHeartbeats();
        for(HeartbeatDTO heartbeatDTO : heartbeats) {
            for (ProductDTO productDTO : heartbeatDTO.getProducts()) {
                DiscoveryProductDTO discoveryProductDTO = new DiscoveryProductDTO(productDTO);
                discoveryProductDTO.setGassmanName(heartbeatDTO.getName());
                discoveryProductDTO.setAdministrators(heartbeatDTO.getAdministrators());
                discoveryProductDTOS.add(discoveryProductDTO);
            }
        }
        grid.setItems(discoveryProductDTOS);
    }
}
