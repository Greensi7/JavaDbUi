package org.example;

import cz.fi.muni.pb162.sqlike.data.database.Database;
import cz.fi.muni.pb162.sqlike.query.Query;
import cz.fi.muni.pb162.sqlike.query.builder.*;
import cz.fi.muni.pb162.sqlike.query.builder.field.Condition;
import cz.fi.muni.pb162.sqlike.query.builder.field.Field;
import cz.fi.muni.pb162.sqlike.query.builder.field.OrderedField;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardField;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardFieldClass;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardOrderedField;
import cz.fi.muni.pb162.sqlike.query.impl.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Window for running SQL queries inside a selected database.
 * Now enhanced with query builder drop boxes for standard query types.
 */
public class DatabaseWindow {

    private final Stage stage;
    private final Database database;

    // Dropdowns
    private final ComboBox<String> queryTypeBox = new ComboBox<>();
    private final ComboBox<String> tableBox = new ComboBox<>();
    private final ComboBox<String> columnBox = new ComboBox<>();
    private final ComboBox<String> indexBox = new ComboBox<>();
    private final TextField valueField = new TextField();
    private final TextField conditionField = new TextField();
    private final TextField newValueField = new TextField();

    private final TextArea resultArea = new TextArea();

    public DatabaseWindow(Database database) {
        this.database = database;
        stage = new Stage();
        stage.setTitle("Database: " + database.getName());

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // Result area
        resultArea.setEditable(false);
        resultArea.setPromptText("Query results...");
        layout.setBottom(resultArea);

        // Top bar buttons
        Button executeButton = new Button("Execute");
        Button backButton = new Button("Back");

        HBox topBar = new HBox(10, executeButton, backButton);
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(-10, 0, 10, 0));
        layout.setTop(topBar);

        backButton.setOnAction(e -> {
            new MainAppWindowAdmin().show();
            stage.close();
        });

        // Query Type drop-down
        queryTypeBox.setPromptText("Query Type");
        queryTypeBox.setItems(FXCollections.observableArrayList(
                "SELECT", "INSERT", "UPDATE", "DELETE", "CREATE TABLE", "DROP TABLE", "ALTER TABLE", "CREATE INDEX"
        ));

        // Table name drop-down (dummy; you can enhance with live DB metadata)
        tableBox.setPromptText("Table");
        tableBox.setItems(FXCollections.observableArrayList(
                // TODO: Replace with actual DB table names if available
                "users", "orders", "products"
        ));

        // Column drop-down (dummy)
        columnBox.setPromptText("Column");
        columnBox.setItems(FXCollections.observableArrayList(
                "id", "name", "price"
        ));

        // Index name (for CREATE INDEX)
        indexBox.setPromptText("Index Name");
        indexBox.setItems(FXCollections.observableArrayList(
                "idx_id", "idx_name"
        ));

        valueField.setPromptText("Value (for INSERT/UPDATE)");
        conditionField.setPromptText("Condition (for WHERE, e.g. id=1)");
        newValueField.setPromptText("New Value (for UPDATE)");

        VBox builderBox = new VBox(10, queryTypeBox, tableBox, columnBox, valueField, newValueField, conditionField, indexBox);
        builderBox.setPadding(new Insets(10));
        layout.setCenter(builderBox);

        // -- Query building and execution logic
        executeButton.setOnAction(e -> {
            try {
                Query query = buildQueryFromInputs();
                if (query != null) {
                    // Here, you would execute the query on the database, e.g.:
                    // String result = database.executeQuery(query);
                    // resultArea.setText(result);
                    // For demo, just display the built SQL:
                    resultArea.setText("Executed on database: " + database.getName() +
                            "\nQuery type: " + queryTypeBox.getValue() +
                            "\nQuery:\n" + query.toSqlString());
                } else {
                    resultArea.setText("Could not build query. Please check your selections/inputs.");
                }
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        // Dynamic fields visibility
        queryTypeBox.setOnAction(e -> updateFieldVisibility());

        updateFieldVisibility();

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
    }

    /**
     * Dynamically show/hide input fields based on query type.
     */
    private void updateFieldVisibility() {
        String type = queryTypeBox.getValue();
        // Hide all by default
        tableBox.setVisible(false);
        columnBox.setVisible(false);
        valueField.setVisible(false);
        newValueField.setVisible(false);
        conditionField.setVisible(false);
        indexBox.setVisible(false);

        if (type == null) return;

        switch (type) {
            case "SELECT":
                tableBox.setVisible(true);
                columnBox.setVisible(true);
                conditionField.setVisible(true);
                break;
            case "INSERT":
                tableBox.setVisible(true);
                columnBox.setVisible(true);
                valueField.setVisible(true);
                break;
            case "UPDATE":
                tableBox.setVisible(true);
                columnBox.setVisible(true);
                newValueField.setVisible(true);
                conditionField.setVisible(true);
                break;
            case "DELETE":
                tableBox.setVisible(true);
                conditionField.setVisible(true);
                break;
            case "CREATE TABLE":
                tableBox.setVisible(true);
                // In a real app, you'd add column/type fields
                break;
            case "DROP TABLE":
                tableBox.setVisible(true);
                break;
            case "ALTER TABLE":
                tableBox.setVisible(true);
                // Could add more controls for add/drop/rename column, etc.
                break;
            case "CREATE INDEX":
                indexBox.setVisible(true);
                tableBox.setVisible(true);
                columnBox.setVisible(true);
                break;
        }
    }

    /**
     * Constructs a Query object based on user input in the drop boxes.
     */
    private Query buildQueryFromInputs() {
        String type = queryTypeBox.getValue();
        String table = tableBox.getValue();
        String column = columnBox.getValue();
        String value = valueField.getText();
        String newValue = newValueField.getText();
        String condition = conditionField.getText();
        String index = indexBox.getValue();

        switch (type) {
            case "SELECT": {
                SelectTableQuery.Builder builder = new SelectTableQuery.Builder();
                if (table == null || table.isEmpty()) return null;
                // Default to * if no column selected
                if (column != null && !column.isEmpty()) {
                    builder.select(new StandardField(column, StandardFieldClass.COLUMN));
                } else {
                    builder.selectFrom(new StandardField(table, StandardFieldClass.TABLE));
                }
                builder.from(new StandardField(table, StandardFieldClass.TABLE));
                if (condition != null && !condition.isEmpty()) {
                    //Todo
                }
                return builder.build();
            }
            case "INSERT": {
                InsertTableQuery.Builder builder = new InsertTableQuery.Builder();
                if (table != null && column != null && value != null) {
                    builder.insertInto(table, column).values(value);
                    return builder.build();
                }
                break;
            }
            case "UPDATE": {
                UpdateTableQuery.Builder builder = new UpdateTableQuery.Builder();
                if (table != null && column != null && newValue != null) {
                    builder.update(new StandardField(table, StandardFieldClass.TABLE)).set(new StandardField(column, StandardFieldClass.COLUMN), newValue);
                    if (condition != null && !condition.isEmpty()) {
                        //todo
                    }
                    return builder.build();
                }
                break;
            }
            case "DELETE": {
                DeleteTableQuery.Builder builder = new DeleteTableQuery.Builder();
                if (table != null) {
                    builder.deleteFrom(new StandardField(table, StandardFieldClass.TABLE));
                    if (condition != null && !condition.isEmpty()) {
                        //todo
                    }
                    return builder.build();
                }
                break;
            }
            case "CREATE TABLE": {
                // For demo, just create a table with a single column
                if (table != null) {
                    CreateTableQuery.Builder builder = new CreateTableQuery.Builder();
                    builder.createTable(table)
                            .column("id", "INTEGER")
                            .primaryKey("id");
                    return builder.build();
                }
                break;
            }
            case "DROP TABLE": {
                if (table != null) {
                    DropTableQuery.Builder builder = new DropTableQuery.Builder();
                    builder.dropTable(table);
                    return builder.build();
                }
                break;
            }
            case "ALTER TABLE": {
                // For demo, just add a column
                if (table != null) {
                    AlterTableQuery.Builder builder = new AlterTableQuery.Builder();
                    builder.alterTable(table).addColumn("new_col", "TEXT");
                    return builder.build();
                }
                break;
            }
            case "CREATE INDEX": {
                if (index != null && table != null && column != null) {
                    CreateIndexQuery.Builder builder = new CreateIndexQuery.Builder();
                    builder.createIndex(index).on(table, new StandardOrderedField(column, StandardFieldClass.COLUMN));
                    return builder.build();
                }
                break;
            }
        }
        return null;
    }

    public void show() {
        stage.show();
    }
}