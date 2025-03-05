package com.example;

import java.util.Random;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class SimpleReactiveAdderWithHistory extends Application {
    private static Random random = new Random();

    private final StringProperty valueA = new SimpleStringProperty("0");
    private final StringProperty valueB = new SimpleStringProperty("0");
    private final IntegerProperty outputValue = new SimpleIntegerProperty(0);
    private final BooleanProperty isValidInput = new SimpleBooleanProperty(true);

    private final ObservableList<IntegerPair> history = FXCollections.observableArrayList();
    private ListView<IntegerPair> historyView;

    private class IntegerPair {
        private int a, b;

        public IntegerPair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() { return a; }
        public int getB() { return b; }

        @Override
        public String toString() {
            return String.format("A: %d, B: %d", a, b);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var scene = new Scene(createMainView(), 600, 200);

        // Adding a ChangeListener
        valueA.addListener((obs, oldValue, newValue) -> updateOutput());

        // Subscription, added in JavaFX 21
        valueB.subscribe(() -> updateOutput());

        stage.setScene(scene);
        stage.setTitle("Simple Adder");
        stage.show();
    }

    private void updateOutput() {
        try {
            outputValue.set(Integer.parseInt(valueA.get()) + Integer.parseInt(valueB.get()));
            isValidInput.set(true);
        } catch (NumberFormatException e) {
            isValidInput.set(false);
        }
    }

    private Region createMainView() {
        var view = new BorderPane();
        view.getStylesheets().add(getClass().getResource("/css/simple-adder.css").toExternalForm());
        view.setTop(createHeading());
        view.setCenter(createCenterContent());
        view.setRight(createHistoryPane());
        return view;
    }

    private Node createHeading() {
        var heading = new Label("Simple Adder");
        HBox.setHgrow(heading, Priority.ALWAYS);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);
        heading.getStyleClass().add("heading-label");
        return heading;
    }

    private Node createCenterContent() {
        var inputRow = createInputRow();
        var outputPane = createOutputPane();

        var inputOutputPane = new VBox(20, inputRow, outputPane);
        inputOutputPane.setPadding(new Insets(20));
        inputOutputPane.setAlignment(Pos.CENTER);

        var centerContent = new BorderPane();
        centerContent.setCenter(inputOutputPane);
        centerContent.setBottom(createButtonRow());

        return centerContent;
    }

    private Node createInputRow() {
        var textFieldA = new TextField();
        textFieldA.textProperty().bindBidirectional(valueA);

        var textFieldB = new TextField();
        textFieldB.textProperty().bindBidirectional(valueB);

        var inputRow = new HBox(20, new Label("A:"), textFieldA, new Label("B:"), textFieldB);
        inputRow.setAlignment(Pos.CENTER);
        return inputRow;
    }

    private Node createOutputPane() {
        var outputRow = createOutputRow();
        var warningLabel = createWarningLabel();
        var outputPane = new StackPane(outputRow, warningLabel);
        return outputPane;
    }

    private Node createOutputRow() {
        var labelA = new Label("0");
        labelA.textProperty().bind(valueA);

        var labelB = new Label("0");
        labelB.textProperty().bind(valueB);

        var outputLabel = new Label("0");
        outputLabel.textProperty().bind(outputValue.asString());

        var outputRow = new HBox(10, labelA, new Label("+"), labelB, new Label("="), outputLabel);
        outputRow.visibleProperty().bind(isValidInput);
        outputRow.setAlignment(Pos.CENTER);
        return outputRow;
    }

    private Node createWarningLabel() {
        var warningLabel = new Label("Invalid input format.");
        warningLabel.visibleProperty().bind(isValidInput.not());
        warningLabel.getStyleClass().add("warning");
        return warningLabel;
    }

    private Node createButtonRow() {
        var buttonRow = new HBox(20, createRandomizeButton(), createRecordButton());
        buttonRow.setPadding(new Insets(0, 0, 20, 0));
        buttonRow.setAlignment(Pos.CENTER);
        return buttonRow;
    }

    private Node createRandomizeButton() {
        var randomizeButton = new Button("Randomize");
        randomizeButton.setOnAction(evt -> {
            valueA.set(String.valueOf(random.nextInt(-1000, 1000)));
            valueB.set(String.valueOf(random.nextInt(-1000, 1000)));
        });
        return randomizeButton;
    }

    private Node createRecordButton() {
        var recordButton = new Button("Record");
        recordButton.disableProperty().bind(isValidInput.not());
        recordButton.setOnAction(evt -> {
            var a = Integer.parseInt(valueA.get());
            var b = Integer.parseInt(valueB.get());
            history.add(new IntegerPair(a, b));
            historyView.scrollTo(history.size() - 1);
        });
        return recordButton;
    }

    private Node createHistoryPane() {
        historyView = new ListView<>(history);
        historyView.setCellFactory(lv -> createCell());
        historyView.getSelectionModel().selectedItemProperty().subscribe(item -> {
            if (item != null) {
                valueA.set(String.valueOf(item.getA()));
                valueB.set(String.valueOf(item.getB()));
            }
        });

        var historyPane = new VBox(historyView);
        historyPane.setPadding(new Insets(10));
        historyPane.setMaxWidth(160);

        return historyPane;
    }

    private ListCell<IntegerPair> createCell() {
        return new ListCell<>() {
            private final Label labelA = new Label("A:");
            private final Label displayA = new Label();
            private final Label labelB = new Label("B:");
            private final Label displayB = new Label();
            private final HBox layout = new HBox(10, labelA, displayA, labelB, displayB);

            {
                labelA.setTextFill(Color.BLUE);
                labelB.setTextFill(Color.RED);
            }

            @Override
            public void updateItem(IntegerPair item, boolean isEmpty) {
                super.updateItem(item, isEmpty);
                if (isEmpty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    displayA.setText(String.valueOf(item.getA()));
                    displayB.setText(String.valueOf(item.getB()));
                    setGraphic(layout);
                }
            }
        };
    }
}
