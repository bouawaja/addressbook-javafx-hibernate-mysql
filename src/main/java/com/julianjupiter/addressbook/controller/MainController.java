package com.julianjupiter.addressbook.controller;

import com.julianjupiter.addressbook.dao.ContactDao;
import com.julianjupiter.addressbook.service.ContactService;
import com.julianjupiter.addressbook.util.ContactMapper;
import com.julianjupiter.addressbook.util.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainController implements Controller, Initializable {
    @FXML
    private BorderPane headerBorderPane;
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private FontIcon closeWindowFontIcon;
    @FXML
    private FontIcon minimizeWindowFontIcon;
    @FXML
    private Label applicationTitle;

    @FXML
    private TableView<ContactProperty> contactTableView;
    @FXML
    private TableColumn<ContactProperty, String> firstNameTableColumn;
    @FXML
    private TableColumn<ContactProperty, String> lastNameTableColumn;
    @FXML
    private BorderPane contactBorderPane;
    @FXML
    private BorderPane contactActionBorderPane;
    @FXML
    private Label contactActionLabel;
    @FXML
    private BorderPane firstActionBorderPane;
    @FXML
    private BorderPane secondActionBorderPane;
    @FXML
    private FontIcon newContactFontIcon;
    //    @FXML
    private FontIcon editFontIcon;
    //    @FXML
    private FontIcon deleteFontIcon;
    //    @FXML
    private FontIcon cancelFontIcon;
    //    @FXML
    private FontIcon saveFontIcon;

    private ObservableList<ContactProperty> contactPropertiesObservable = FXCollections.observableArrayList();

    private ResourceBundle resourceBundle;
    private Stage primaryStage;
    private ContactProperty selectedContactProperty;

    private final ContactService contactService;
    private final ContactMapper contactMapper;
    private Validator validator;

    public MainController() {
        this.contactService = ContactService.create(ContactDao.create());
        this.contactMapper = new ContactMapper();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.initWindowEvents();

        this.initContactAction();
        this.initContactActionFontIcons();

        this.listContacts();
        this.addContact();
        this.editContact();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    private void initWindowEvents() {
        headerBorderPane.setOnMousePressed(mouseEvent -> {
            if (!primaryStage.isMaximized()) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });

        headerBorderPane.setOnMouseDragged(mouseEvent -> {
            if (!primaryStage.isMaximized()) {
                primaryStage.setX(mouseEvent.getScreenX() - xOffset);
                primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            }
        });

        closeWindowFontIcon.setOnMouseClicked(mouseEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close Application");
            alert.setHeaderText("You are about to close the application.");
            alert.setContentText("Do you want to continue?");

            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(primaryStage);

            alert.showAndWait()
                    .filter(buttonType -> buttonType == ButtonType.OK)
                    .ifPresent(buttonType -> Platform.exit());
        });

        minimizeWindowFontIcon.setOnMouseClicked(mouseEvent -> {
            primaryStage.setIconified(true);
        });
    }

    private void initContactAction() {
        this.contactActionLabel.setVisible(false);
    }

    private void initContactActionFontIcons() {
        this.editFontIcon = new FontIcon();
        this.editFontIcon.setIconColor(Paint.valueOf("#3F51B5"));
        this.editFontIcon.setIconLiteral("mdi-pencil-box-outline");
        this.editFontIcon.setIconSize(18);

        this.deleteFontIcon = new FontIcon();
        this.deleteFontIcon.setIconColor(Paint.valueOf("#3F51B5"));
        this.deleteFontIcon.setIconLiteral("mdi-delete");
        this.deleteFontIcon.setIconSize(18);

        this.cancelFontIcon = new FontIcon();
        this.cancelFontIcon.setIconColor(Paint.valueOf("#3F51B5"));
        this.cancelFontIcon.setIconLiteral("mdi-close");
        this.cancelFontIcon.setIconSize(18);

        this.saveFontIcon = new FontIcon();
        this.saveFontIcon.setIconColor(Paint.valueOf("#3F51B5"));
        this.saveFontIcon.setIconLiteral("mdi-content-save");
        this.saveFontIcon.setIconSize(18);
    }

    private void listContacts() {
        this.firstNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        this.lastNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        List<ContactProperty> contactProperties = this.contactService.findAll().stream()
                .map(contactMapper::fromEntityToProperty)
                .collect(Collectors.toUnmodifiableList());
        this.contactPropertiesObservable.addAll(contactProperties);
        this.contactTableView.setItems(this.contactPropertiesObservable);

        this.contactTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> this.viewContact(newValue));
    }

    private void addContact() {
        this.newContactFontIcon.setOnMouseClicked(mouseEvent -> {
            this.contactTableView.getSelectionModel().clearSelection();
            var newContactView = View.of(NewContactController.class, AnchorPane.class);
            var newContactController = newContactView.controller();
            var anchorPane = newContactView.component();
            this.contactBorderPane.setCenter(anchorPane);
            this.contactActionBorderPane.setVisible(true);
            this.contactActionBorderPane.setStyle("-fx-background-color: #CCCCCC");
            this.contactActionLabel.setStyle("-fx-text-fill: #3F51B5");
            this.contactActionLabel.setText("New Contact");
            this.contactActionLabel.setVisible(true);
            this.firstActionBorderPane.setCenter(this.cancelFontIcon);
            this.secondActionBorderPane.setCenter(this.saveFontIcon);

            this.cancelFontIcon.setOnMouseClicked(null);
            this.cancelFontIcon.setOnMouseClicked(mouseEvent1 -> {
                this.contactActionBorderPane.setVisible(false);
                this.contactActionLabel.setVisible(false);
                this.contactActionLabel.setText(null);
                this.contactBorderPane.setCenter(null);
                if (this.selectedContactProperty != null) {
                    this.viewContact(this.selectedContactProperty);
                }
            });

            this.saveFontIcon.setOnMouseClicked(null);
            this.saveFontIcon.setOnMouseClicked(mouseEvent1 -> {
                System.out.println("New Save");
                ContactProperty contactProperty = newContactController.contactProperty();
                Set<ConstraintViolation<ContactProperty>> contactConstraintViolations = this.validator.validate(contactProperty);
                contactConstraintViolations.stream()
                        .forEach(violation -> {
                            System.out.println(violation.getMessage());
                        });
            });
        });
    }

    private void viewContact(ContactProperty contactProperty) {
        this.selectedContactProperty = contactProperty;
        var viewContactView = View.of(ViewContactController.class, AnchorPane.class);
        var viewContactController = viewContactView.controller();
        viewContactController.setContactProperty(contactProperty);
        var anchorPane = viewContactView.component();
        this.contactBorderPane.setCenter(anchorPane);
        this.contactActionBorderPane.setVisible(true);
        this.contactActionBorderPane.setStyle("-fx-background-color: #CCCCCC");
        this.contactActionLabel.setStyle("-fx-text-fill: #3F51B5");
        this.contactActionLabel.setText("View Contact");
        this.contactActionLabel.setVisible(true);
        this.firstActionBorderPane.setCenter(this.editFontIcon);
        this.secondActionBorderPane.setCenter(this.deleteFontIcon);
    }

    private void editContact() {
        this.editFontIcon.setOnMouseClicked(mouseEvent -> {
            this.contactActionLabel.setText("Edit Contact");
            var editContactView = View.of(EditContactController.class, AnchorPane.class);
            var editContactController = editContactView.controller();
            editContactController.setContactProperty(this.selectedContactProperty);
            var anchorPane = editContactView.component();
            this.contactBorderPane.setCenter(anchorPane);
            this.contactActionBorderPane.setVisible(true);
            this.contactActionLabel.setText("Edit Contact");
            this.firstActionBorderPane.setCenter(this.cancelFontIcon);
            this.secondActionBorderPane.setCenter(this.saveFontIcon);

            this.cancelFontIcon.setOnMouseClicked(null);
            this.cancelFontIcon.setOnMouseClicked(mouseEvent1 -> {
                if (this.selectedContactProperty != null) {
                    this.viewContact(this.selectedContactProperty);
                }
            });

            this.saveFontIcon.setOnMouseClicked(null);
            this.saveFontIcon.setOnMouseClicked(mouseEvent1 -> {
                System.out.println("Update Save");
            });
        });
    }

}
