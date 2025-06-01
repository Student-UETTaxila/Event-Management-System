package com.example.eventmanagement;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.text.*;

import javafx.scene.control.Alert;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.shape.Rectangle;
import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class Eventora extends Application {

   private BorderPane mainLayout = new BorderPane();
    private final String ADMIN_SECURITY_KEY = "admin123";
    private final String USERS_FILE = "users.txt";
    private final String BOOKINGS_FILE = "booking_data.txt";

    @Override
    public void start(Stage primaryStage) {
        showLoginWindow(primaryStage);
    }

    // ----------------------- LOGIN + REGISTER -----------------------

    private void showLoginWindow(Stage primaryStage) {
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#6A1B9A")),
                new Stop(0.5, Color.web("#BA68C8")),
                new Stop(1, Color.web("#B39DDB")));

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, null)));

        Rectangle loginFormBackground = new Rectangle(350, 300);
        loginFormBackground.setArcWidth(15);
        loginFormBackground.setArcHeight(15);
        loginFormBackground.setFill(Color.web("#D1C4E9"));

        GridPane loginForm = new GridPane();
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setVgap(10);
        loginForm.setHgap(10);
        loginForm.setStyle("-fx-padding: 30;");

        Label titleLabel = new Label("LOGIN FORM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginForm.add(titleLabel, 0, 0, 2, 1);

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        usernameField.setPromptText("Enter your username");
        passwordField.setPromptText("Enter your password");

        loginForm.add(new Label("Username:"), 0, 1);
        loginForm.add(usernameField, 1, 1);
        loginForm.add(new Label("Password:"), 0, 2);
        loginForm.add(passwordField, 1, 2);

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        loginBtn.setStyle("-fx-background-color: #6A1B9A; -fx-text-fill: white;");
        registerBtn.setStyle("-fx-background-color: #6A1B9A; -fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (validateUserLogin(username, password)) {
                showDashboardButtons(primaryStage, username);
            } else {
                showAlert("Login Failed", "Invalid credentials.");
            }
        });

        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Fields cannot be empty.");
            } else if (isUsernameTaken(username)) {
                showAlert("Error", "Username already exists.");
            } else {
                registerNewUser(username, password);
                showAlert("Success", "Registration successful.");
            }
        });

        loginForm.add(loginBtn, 0, 3);
        loginForm.add(registerBtn, 1, 3);

        root.getChildren().addAll(loginFormBackground, loginForm);
        primaryStage.setScene(new Scene(root, 400, 350));
        primaryStage.setTitle("Eventora - Login");
        primaryStage.show();
    }

    private boolean validateUserLogin(String username, String password) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            return lines.stream().anyMatch(line -> line.equals(username + "," + password));
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isUsernameTaken(String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            return lines.stream().anyMatch(line -> line.split(",")[0].equals(username));
        } catch (IOException e) {
            return false;
        }
    }

    private void registerNewUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + "," + password);
            writer.newLine();
        } catch (IOException e) {
            showAlert("Error", "Could not save user.");
        }
    }

    // ----------------------- DASHBOARD SELECTOR -----------------------

    private void showDashboardButtons(Stage stage, String username) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#E1BEE7")),
                new Stop(1, Color.web("#D1C4E9")));
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        Label welcome = new Label("Welcome, " + username + "!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Button adminBtn = new Button("Admin");
        Button userBtn = new Button("User");

        adminBtn.setStyle("-fx-background-color: #6A1B9A; -fx-text-fill: white;");
        userBtn.setStyle("-fx-background-color: #6A1B9A; -fx-text-fill: white;");

        adminBtn.setOnAction(e -> {
            PasswordField keyField = new PasswordField();
            keyField.setPromptText("Enter Admin Security Key");

            Button submit = new Button("Submit");
            submit.setOnAction(ev -> {
                if (keyField.getText().equals(ADMIN_SECURITY_KEY)) {
                    showViewRegistrations(stage);
                } else {
                    showAlert("Access Denied", "Invalid Security Key.");
                }
            });

            VBox secBox = new VBox(10, keyField, submit);
            secBox.setAlignment(Pos.CENTER);
            stage.setScene(new Scene(secBox, 400, 200));
        });

        userBtn.setOnAction(e -> showUserDashboard(stage));

        root.getChildren().addAll(welcome, adminBtn, userBtn);
        stage.setScene(new Scene(root, 400, 300));
    }

    // ----------------------- ADMIN DASHBOARD -----------------------

    private void showViewRegistrations(Stage stage) {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setLeft(createSidebar(stage));

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#E1BEE7")),
                new Stop(1, Color.web("#D1C4E9")));
        layout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        TableView<String[]> table = new TableView<>();
        TableColumn<String[], String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[0]));
        TableColumn<String[], String> passCol = new TableColumn<>("Password");
        passCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue()[1]));
        table.getColumns().addAll(userCol, passCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try {
            List<String> users = Files.readAllLines(Paths.get(USERS_FILE));
            for (String line : users) {
                String[] parts = line.split(",");
                if (parts.length >= 2) table.getItems().add(parts);
            }
        } catch (IOException e) {
            showAlert("Error", "Could not load user data.");
        }

        VBox content = new VBox(10, new Label("Registered Users"), table);
        content.setPadding(new Insets(10));
        layout.setCenter(content);

        stage.setScene(new Scene(layout, 800, 600));
        stage.setTitle("Admin Dashboard");
    }

    private VBox createSidebar(Stage stage) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        Button viewBtn = new Button("View Registrations");
        Button manageBtn = new Button("Manage Events");

        viewBtn.setOnAction(e -> showViewRegistrations(stage));
        manageBtn.setOnAction(e -> showManageEvents(stage));

        box.getChildren().addAll(viewBtn, manageBtn);
        return box;
    }

    private void showManageEvents(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        TextArea area = new TextArea();
        area.setEditable(false);
        loadBookingsToArea(area);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(e -> loadBookingsToArea(area));

        Button update = new Button("Update");
        update.setOnAction(e -> handleEventUpdate(area));

        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> handleEventCancel(area));

        Button budget = new Button("Budget");
        budget.setOnAction(e -> showTotalBudget());

        root.getChildren().addAll(new HBox(10, refresh, update, cancel, budget), area);
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Manage Events");
    }

    private void loadBookingsToArea(TextArea area) {
        try {
            String text = Files.readString(Paths.get(BOOKINGS_FILE));
            area.setText(text.isEmpty() ? "No bookings found." : text);
        } catch (IOException e) {
            area.setText("Error loading bookings.");
        }
    }

    private void handleEventUpdate(TextArea area) {
        TextInputDialog oldDialog = new TextInputDialog();
        oldDialog.setHeaderText("Enter the old event name:");
        oldDialog.showAndWait().ifPresent(oldEvent -> {
            TextInputDialog newDialog = new TextInputDialog();
            newDialog.setHeaderText("Enter the new event name:");
            newDialog.showAndWait().ifPresent(newEvent -> {
                try {
                    List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
                    boolean updated = false;
                    for (int i = 0; i < lines.size(); i++) {
                        if (lines.get(i).contains("Event: " + oldEvent)) {
                            lines.set(i, lines.get(i).replace("Event: " + oldEvent, "Event: " + newEvent));
                            updated = true;
                        }
                    }
                    Files.write(Paths.get(BOOKINGS_FILE), lines);
                    area.setText(String.join("\n", lines));
                    showAlert("Update", updated ? "Event updated." : "Event not found.");
                } catch (IOException e) {
                    showAlert("Error", "Update failed.");
                }
            });
        });
    }

    private void handleEventCancel(TextArea area) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter event to cancel:");
        dialog.showAndWait().ifPresent(eventName -> {
            try {
                List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
                int start = -1, end = -1;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("Event: " + eventName)) start = i;
                    if (start != -1 && lines.get(i).contains("-----------------------------")) {
                        end = i;
                        break;
                    }
                }
                if (start != -1 && end != -1) {
                    lines.subList(start, end + 1).clear();
                    Files.write(Paths.get(BOOKINGS_FILE), lines);
                    area.setText(String.join("\n", lines));
                    showAlert("Cancelled", "Event cancelled.");
                } else {
                    showAlert("Not Found", "Event not found.");
                }
            } catch (IOException e) {
                showAlert("Error", "Cancellation failed.");
            }
        });
    }

    private void showTotalBudget() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(BOOKINGS_FILE));
            int sum = lines.stream()
                    .filter(l -> l.startsWith("Total Cost:"))
                    .mapToInt(l -> Integer.parseInt(l.replace("Total Cost: ", "").replace(" PKR", "")))
                    .sum();
            showAlert("Budget Summary", "Total Budget: " + sum + " PKR");
        } catch (IOException e) {
            showAlert("Error", "Could not calculate budget.");
        }
    }
// ----------------------- USER DASHBOARD -----------------------

    //HOME TAB
    private void showUserDashboard(Stage primaryStage) {

        Label welcomeLabel = new Label("WELCOME TO EVENTORA");
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(Font.font("Lucida Calligraphy", FontWeight.BOLD, 36));
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);
        welcomeLabel.setPadding(new Insets(20));
        welcomeLabel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#201823")),
                        new Stop(0.5, Color.web("#6A1B9A")),
                        new Stop(1, Color.web("#E1BEE7"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        mainLayout.setTop(welcomeLabel);

        VBox sidePanel = new VBox(20);
        sidePanel.setPadding(new Insets(20));
        sidePanel.setAlignment(Pos.TOP_CENTER);
        sidePanel.setPrefWidth(180);
        sidePanel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#B39DDB")),
                        new Stop(0.5, Color.web("#BA68C8")),
                        new Stop(1, Color.web("#F3E5F5"))),
                CornerRadii.EMPTY, Insets.EMPTY)));


// Common button style
        String buttonStyle = "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(#6A1B9A, #B39DDB);" +
                "-fx-background-radius: 15;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10;";

// Hover effect (scale on hover)
        EventHandler<MouseEvent> hoverEffect = event -> {
            Button btn = (Button) event.getSource();
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        };
        EventHandler<MouseEvent> exitEffect = event -> {
            Button btn = (Button) event.getSource();
            btn.setScaleX(1);
            btn.setScaleY(1);
        };

// Home Button
        Button homeButton = new Button("Home");
        homeButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        homeButton.setStyle(buttonStyle);
        homeButton.setOnMouseEntered(hoverEffect);
        homeButton.setOnMouseExited(exitEffect);

// Booking Form Button
        Button bookingFormButton = new Button("Booking Form");
        bookingFormButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        bookingFormButton.setStyle(buttonStyle);
        bookingFormButton.setOnMouseEntered(hoverEffect);
        bookingFormButton.setOnMouseExited(exitEffect);

// Booking Status Button
        Button bookingStatusBtn = new Button("Booking Status");
        bookingStatusBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        bookingStatusBtn.setStyle(buttonStyle);
        bookingStatusBtn.setOnMouseEntered(hoverEffect);
        bookingStatusBtn.setOnMouseExited(exitEffect);
        bookingStatusBtn.setOnAction(e -> openBookingStatusWindow());

// Add buttons to side panel
        sidePanel.getChildren().addAll(homeButton, bookingFormButton, bookingStatusBtn);


        mainLayout.setLeft(sidePanel);

        VBox contentArea = new VBox();
        contentArea.setPadding(new Insets(20));
        contentArea.setSpacing(20);
        contentArea.setAlignment(Pos.TOP_CENTER);
        setHomeContent(contentArea);

        mainLayout.setCenter(contentArea);

        homeButton.setOnAction(e -> setHomeContent(contentArea));
        bookingFormButton.setOnAction(e -> showBookingForm());

        Scene scene = new Scene(mainLayout, 1200, 750);
        primaryStage.setTitle("User Dashboard - Events Forum");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setHomeContent(VBox contentArea) {
        contentArea.getChildren().clear();
        contentArea.setBackground(new Background(new BackgroundFill(
                Color.web("#D1C4E9"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label browseLabel = new Label("Our Latest Events");
        browseLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        browseLabel.setTextFill(Color.WHITE);
        browseLabel.setAlignment(Pos.CENTER);
        browseLabel.setMaxWidth(400);
        browseLabel.setPadding(new Insets(10));
        browseLabel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#201823")),
                        new Stop(0.5, Color.web("#BA68CB")),
                        new Stop(1, Color.web("#E1BEE7"))),
                new CornerRadii(15), Insets.EMPTY)));

        contentArea.getChildren().add(browseLabel);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox scrollContent = new VBox();
        scrollContent.setPadding(new Insets(10));
        scrollContent.setSpacing(10);

        GridPane eventGrid = new GridPane();
        eventGrid.setHgap(30);
        eventGrid.setVgap(30);
        eventGrid.setAlignment(Pos.CENTER);

        // Event titles and image file names (match exactly with filenames)
        String[][] events = {

                {"Wedding Boho Theme", "/images/images1/Wedding_Boho_Theme.PNG"},
                {"Wedding Classic Theme", "/images/images1/Wedding_classic_theme.PNG"},
                {"Wedding Modern Glam", "/images/images1/Wedding_modern_glam_theme.PNG"},
                {"Birthday Parties", "/images/images1/Birthday_Parties.PNG"},
                {"Product Launches", "/images/images1/Product_Launches.PNG"},
                {"Catering Facility", "/images/images1/Catering_Facility.PNG"},
                {"Corporate Retreats", "/images/images1/Corporate_Meetings.PNG"},
                {"Cultural Festivals", "/images/images1/Cultural_Festivals.PNG"},
                {"Music Concerts", "/images/images1/Music_concerts.PNG"},
                {"Art Exhibitions", "/images/images1/Art_Exhibitions.PNG"},
                {"Business Summits", "/images/images1/Business_Summits.PNG"},
                {"Tech Conferences", "/images/images1/Tech_Conferences.PNG"},
        };

        for (int i = 0; i < events.length; i++) {
            VBox card = createEventCard(events[i][0], events[i][1]);
            eventGrid.add(card, i % 4, i / 4);
        }

        scrollContent.getChildren().add(eventGrid);
        scrollPane.setContent(scrollContent);
        contentArea.getChildren().add(scrollPane);
    }

    private VBox createEventCard(String title, String imagePath) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefSize(220, 280);
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));
        card.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Error loading image for: " + title + " -> " + e.getMessage());
        }

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(180);
        titleLabel.setPadding(new Insets(8, 12, 8, 12));
        titleLabel.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#201823")),
                        new Stop(0.5, Color.web("#BA68CB")),
                        new Stop(1, Color.web("#E1BEE7"))),
                new CornerRadii(10), Insets.EMPTY)));

        card.getChildren().addAll(imageView, titleLabel);
        return card;
    }
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    //new window of booking form
    private void showBookingForm() {
        Stage formStage = new Stage();
        formStage.setTitle("Booking Form");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label header = new Label("Booking Form");
        header.setFont(Font.font("Lucida Calligraphy", FontWeight.BOLD, 36));
        header.setTextFill(Color.WHITE);
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#201823"))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        header.setPadding(new Insets(20));

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#201824")),
                        new Stop(0.5, Color.web("#E1BEE7")),
                        new Stop(1, Color.web("#B39DDB"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        // TextFields for User Information
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Event Name");
        TextField locationField = new TextField();
        locationField.setPromptText("Event Location");
        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("Event Time");

        // Number of People Field
        TextField numPeopleField = new TextField();
        numPeopleField.setPromptText("Number of Guests");

        // Event Pricing Information
        Label pricingLabel = new Label("Event Pricing: \n- Wedding: 50,000 PKR (up to 100 people), 500 PKR per extra person\n- Corporate Conference: 30,000 PKR (up to 50 people), 1,000 PKR per extra person\n- Birthday Party: 20,000 PKR (up to 50 people), 300 PKR per extra person\n- Conference: 10,000 PKR (up to 30 people), 1,500 PKR per extra person");
        pricingLabel.setTextFill(Color.WHITE);
        pricingLabel.setFont(Font.font("Arial", 14));

        // Food, Dessert, Drinks, Photography
        TitledPane foodPane = new TitledPane();
        foodPane.setText("Food Dishes");
        VBox foodBox = new VBox(5);
        String[] foods = {"Chicken Parmesan", "Grilled Salmon", "Beef Wellington", "Stuffed Chicken", "Lamb Chops", "Chicken Biryani", "Pasta Alfredo", "Garlic Prawns", "Stuffed Peppers", "Veg Lasagna"};
        List<CheckBox> foodCheckboxes = new ArrayList<>();
        for (String food : foods) {
            CheckBox cb = new CheckBox(food);
            foodBox.getChildren().add(cb);
            foodCheckboxes.add(cb);
        }
        foodPane.setContent(foodBox);

        // Dessert
        TitledPane dessertPane = new TitledPane();
        dessertPane.setText("Desserts");
        VBox dessertBox = new VBox(5);
        String[] desserts = {"Tiramisu", "Lava Cake", "Cheesecake", "Macarons", "Red Velvet Cupcakes", "Crème Brûlée", "Fruit Trifle", "Mousse", "Gulab Jamun", "Éclairs"};
        List<CheckBox> dessertCheckboxes = new ArrayList<>();
        for (String dessert : desserts) {
            CheckBox cb = new CheckBox(dessert);
            dessertBox.getChildren().add(cb);
            dessertCheckboxes.add(cb);
        }
        dessertPane.setContent(dessertBox);

        // Drinks
        TitledPane drinksPane = new TitledPane();
        drinksPane.setText("Drinks");
        VBox drinksBox = new VBox(5);
        String[] drinks = {"Mint Margarita", "Lime Soda", "Mojito", "Blue Lagoon", "Strawberry Lemonade", "Iced Tea", "Cold Coffee", "Fresh Juice", "Milkshake", "Sparkling Water"};
        List<CheckBox> drinkCheckboxes = new ArrayList<>();
        for (String drink : drinks) {
            CheckBox cb = new CheckBox(drink);
            drinksBox.getChildren().add(cb);
            drinkCheckboxes.add(cb);
        }
        drinksPane.setContent(drinksBox);

        // Photography
        TitledPane photographyPane = new TitledPane();
        photographyPane.setText("Photography Options");
        VBox photographyBox = new VBox(5);
        String[] photographyOptions = {"Portraits", "Candid", "Drone Shots", "Traditional", "Cinematic", "Photo Booth"};
        List<CheckBox> photographyCheckboxes = new ArrayList<>();
        for (String photo : photographyOptions) {
            CheckBox cb = new CheckBox(photo);
            photographyBox.getChildren().add(cb);
            photographyCheckboxes.add(cb);
        }
        photographyPane.setContent(photographyBox);

        // Calculate Total Expenditure
        Label totalCostLabel = new Label("Total Expenditure: 0 PKR");
        totalCostLabel.setFont(Font.font("Arial", 16));
        totalCostLabel.setTextFill(Color.YELLOW);

        // Update Total Cost
        Button calculateBtn = new Button("Calculate Total");
        calculateBtn.setOnAction(e -> {
            int totalCost = 0;

            // Event Price based on number of guests
            int numGuests = Integer.parseInt(numPeopleField.getText());
            String event = eventNameField.getText();
            if (event.equalsIgnoreCase("Wedding")) {
                totalCost += 50000 + (numGuests - 100) * 500;
            } else if (event.equalsIgnoreCase("Corporate Conference")) {
                totalCost += 30000 + (numGuests - 50) * 1000;
            } else if (event.equalsIgnoreCase("Birthday Party")) {
                totalCost += 20000 + (numGuests - 50) * 300;
            } else if (event.equalsIgnoreCase("Conference")) {
                totalCost += 10000 + (numGuests - 30) * 1500;
            }

            // Food, Dessert, Drinks, Photography prices
            totalCost += foodCheckboxes.stream().filter(CheckBox::isSelected).count() * 1000;  // Assuming 1000 PKR per selected food
            totalCost += dessertCheckboxes.stream().filter(CheckBox::isSelected).count() * 500;  // Assuming 500 PKR per selected dessert
            totalCost += drinkCheckboxes.stream().filter(CheckBox::isSelected).count() * 300;  // Assuming 300 PKR per selected drink
            totalCost += photographyCheckboxes.stream().filter(CheckBox::isSelected).count() * 5000;  // Assuming 5000 PKR per photography service

            totalCostLabel.setText("Total Expenditure: " + totalCost + " PKR");
        });

        // Submit Button
        Button submitBtn = new Button("Submit Booking");
        submitBtn.setStyle("-fx-background-radius: 10; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: linear-gradient(#201823, #6A1B9A, #E1BEE7);");
        submitBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String eventName = eventNameField.getText();
            String location = locationField.getText();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
            String time = timeField.getText();

            List<String> selectedFoods = foodCheckboxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();
            List<String> selectedDesserts = dessertCheckboxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();
            List<String> selectedDrinks = drinkCheckboxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();
            List<String> selectedPhotography = photographyCheckboxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("booking_data.txt", true))) {
                writer.write("Name: " + name + "\n");
                writer.write("Email: " + email + "\n");
                writer.write("Phone: " + phone + "\n");
                writer.write("Event: " + eventName + "\n");
                writer.write("Location: " + location + "\n");
                writer.write("Date: " + date + "\n");
                writer.write("Time: " + time + "\n");
                writer.write("Foods: " + selectedFoods + "\n");
                writer.write("Desserts: " + selectedDesserts + "\n");
                writer.write("Drinks: " + selectedDrinks + "\n");
                writer.write("Photography: " + selectedPhotography + "\n");
                writer.write("Total Cost: " + totalCostLabel.getText() + "\n");
                writer.write("-----------------------------\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Booking Submitted Successfully!");
            alert.showAndWait();
            formStage.close();
        });

        form.getChildren().addAll(nameField, emailField, phoneField, eventNameField, locationField, datePicker, timeField, numPeopleField,
                pricingLabel, foodPane, dessertPane, drinksPane, photographyPane, calculateBtn, totalCostLabel, submitBtn);

        root.getChildren().addAll(header, form);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);  // Makes sure it expands horizontally
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(new Insets(10));

        Scene scene = new Scene(scrollPane, 800, 900);
        formStage.setScene(scene);

        formStage.show();
    }


    //Booking status window

    private void openBookingStatusWindow() {
        Stage statusStage = new Stage();
        statusStage.setTitle("Booking Status");

        // Outer VBox with light gradient background
        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #F3E5F5, #BA68CB, #B39DDB);");

        // Title with gradient background
        Label title = new Label("Event Booking Status");
        title.setFont(Font.font("Lucida Calligraphy", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

// Setting a gradient background
        title.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#201823")),
                        new Stop(0.5, Color.web("#6A1B9A")),
                        new Stop(1, Color.web("#E1BEE7"))
                ),
                CornerRadii.EMPTY, Insets.EMPTY
        )));

// Padding for the title
        title.setPadding(new Insets(20, 0, 20, 0));


        // Table Grid with gradient background
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(20);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);
        grid.setBackground(new Background(new BackgroundFill(
                Color.web("#B39DDB"), new CornerRadii(15), Insets.EMPTY)));

        // Labels
        Label[] fieldLabels = {
                new Label("Name:"), new Label("Event:"), new Label("Date:"), new Label("Time:"),
                new Label("Location:"), new Label("Foods:"), new Label("Desserts:"),
                new Label("Drinks:"), new Label("Photography:"), new Label("Guests:")
        };

        for (Label label : fieldLabels) {
            label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            label.setTextFill(Color.web("#E1BEE7"));
        }

        // Booking Data
        List<String> lines;
        Map<String, String> booking = new HashMap<>();
        try {
            lines = Files.readAllLines(Paths.get("booking_data.txt"));
            Map<String, String> current = new HashMap<>();
            for (String line : lines) {
                if (line.equals("-----------------------------")) {
                    if (!current.isEmpty()) {
                        booking = new HashMap<>(current);  // Save last booking
                    }
                    current.clear();
                } else if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    current.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String[] fieldKeys = {
                "Name", "Event", "Date", "Time", "Location",
                "Foods", "Desserts", "Drinks", "Photography", "Guests"
        };

        for (int i = 0; i < fieldKeys.length; i++) {
            grid.add(fieldLabels[i], 0, i);
            Label value = new Label(booking.getOrDefault(fieldKeys[i], "Not Provided"));
            value.setTextFill(Color.web("#FFFDE7")); // light yellowish
            value.setFont(Font.font("Arial", 16));
            grid.add(value, 1, i);
        }

        // Pricing Logic
        Map<String, Integer> baseEventPrices = Map.of(
                "Wedding", 50000,
                "Corporate Conference", 30000,
                "Birthday", 20000,
                "Conference", 10000
        );

        Map<String, Integer> extraPeopleCosts = Map.of(
                "Wedding", 500,
                "Corporate Conference", 1000,
                "Birthday", 300,
                "Conference", 1500
        );

        Map<String, Integer> featurePrices = Map.of(
                "Foods", 1000,
                "Desserts", 500,
                "Drinks", 300,
                "Photography", 5000
        );

        // Total Cost Calculation
        int guestCount = Integer.parseInt(booking.getOrDefault("Guests", "100"));
        String selectedEvent = booking.getOrDefault("Event", "Wedding");

        int totalCost = baseEventPrices.getOrDefault(selectedEvent, 0);
        int extraCost = (guestCount > 100) ?
                (guestCount - 100) * extraPeopleCosts.getOrDefault(selectedEvent, 0) : 0;
        totalCost += extraCost;

        if (!booking.getOrDefault("Foods", "").equals("Not Provided"))
            totalCost += guestCount * featurePrices.get("Foods");
        if (!booking.getOrDefault("Desserts", "").equals("Not Provided"))
            totalCost += guestCount * featurePrices.get("Desserts");
        if (!booking.getOrDefault("Drinks", "").equals("Not Provided"))
            totalCost += guestCount * featurePrices.get("Drinks");
        if (!booking.getOrDefault("Photography", "").equals("Not Provided"))
            totalCost += featurePrices.get("Photography");

        Label costLabel = new Label("Total Cost: PKR " + totalCost);
        costLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        costLabel.setTextFill(Color.web("#FFEB3B"));
        costLabel.setAlignment(Pos.CENTER);

        // Payment Section with gradient background
        VBox paymentSection = new VBox(10);
        paymentSection.setAlignment(Pos.CENTER);
        paymentSection.setStyle("-fx-background-color: linear-gradient(to bottom, #B39DDB, #BA68CB, #F3E5F5);");

        Label paymentLabel = new Label("Enter reference code sent on your registered contact no:");
        paymentLabel.setTextFill(Color.WHITE);
        paymentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField paymentField = new TextField();
        paymentField.setPromptText("e.g. TXN-54892");

        Button verifyPaymentButton = new Button("Verify Payment");
        verifyPaymentButton.setStyle("-fx-background-radius: 20; -fx-background-color: #00E676; -fx-text-fill: black; -fx-font-weight: bold;");

        verifyPaymentButton.setOnAction(e -> {
            String refCode = paymentField.getText().trim();
            Alert alert = new Alert(refCode.matches("TXN-\\d{5}") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(refCode.matches("TXN-\\d{5}") ? "Payment Verified" : "Invalid Reference Code");
            alert.setHeaderText(null);
            alert.setContentText(refCode.matches("TXN-\\d{5}") ?
                    "Your payment has been successfully verified.\nThank you for booking with Eventora!" :
                    "The payment reference code is invalid. Please try again.");
            alert.showAndWait();
        });

        paymentSection.getChildren().addAll(paymentLabel, paymentField, verifyPaymentButton);

        // Dummy Contact Info with gradient background
        Label accountInfo = new Label(
                "\nCompany Account Number: 123-4567890-001\n" +
                        "For payment queries, contact us:\nsupport@eventora.com | +92-123-4567890"
        );
        accountInfo.setFont(Font.font("Georgia", FontPosture.ITALIC, 14));
        accountInfo.setTextFill(Color.LIGHTGRAY);
        accountInfo.setAlignment(Pos.CENTER);
        accountInfo.setWrapText(true);
        accountInfo.setStyle("-fx-background-color: linear-gradient(to bottom, #B39DDB, #BA68CB, #F3E5F5);");

        VBox contentBox = new VBox(20, title, grid, costLabel, paymentSection, accountInfo);
        contentBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");

        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root, 700, 750);
        statusStage.setScene(scene);
        statusStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
