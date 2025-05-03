package org.example;
import cz.fi.muni.pb162.sqlike.data.table.Table;
import cz.fi.muni.pb162.sqlike.query.builder.field.Field;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import cz.fi.muni.pb162.sqlike.data.database.Database;
import cz.fi.muni.pb162.sqlike.data.type.DataType;
import cz.fi.muni.pb162.sqlike.data.type.StandardDataType;
import cz.fi.muni.pb162.sqlike.query.Query;
import cz.fi.muni.pb162.sqlike.query.StandardQueryType;
import cz.fi.muni.pb162.sqlike.query.builder.*;
import cz.fi.muni.pb162.sqlike.query.builder.field.Condition;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardField;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardFieldClass;
import cz.fi.muni.pb162.sqlike.query.impl.*;
import cz.fi.muni.pb162.sqlike.query.result.QueryResult;
import cz.fi.muni.pb162.sqlike.query.result.QueryResultStatus;
import cz.fi.muni.pb162.sqlike.query.result.ResultRow;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.Data.DatabaseList;
import org.example.User.UserRecord;
import org.example.User.UserType;
import org.example.parser.ConditionBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static cz.fi.muni.pb162.sqlike.data.type.StandardDataType.INTEGER;
import static cz.fi.muni.pb162.sqlike.data.type.StandardDataType.REAL;
import static cz.fi.muni.pb162.sqlike.data.type.StandardDataType.TEXT;

public class DatabaseWindow {

    private final Stage stage;
    private final Database database;

    private final ComboBox<StandardQueryType> queryTypeBox = new ComboBox<>();
    private final ComboBox<String> tableBox = new ComboBox<>();
    private final ComboBox<String> columnBox = new ComboBox<>();
    private final TextField valueField = new TextField();
    private final TextField tableField = new TextField();
    private final TextField newValueField = new TextField();
    private final TextField conditionField = new TextField();
    private final VBox columnInputsBox = new VBox(5);
    private final VBox selectColumnBox = new VBox(5);
    private final Button addColumnButton = new Button("Add Column");
    private final Button addSelectColumnButton = new Button("Add Column");
    private final CheckBox selectAllColumnsCheckBox = new CheckBox("Select All Columns");
    private final CheckBox whereConditionCheckBox = new CheckBox("Add WHERE condition");
    private final SimpleBooleanProperty isWhereConditionEnabled = new SimpleBooleanProperty(false);

    private final Label createTableLabel = new Label("Select Table Name:");
    private final VBox columnSection = new VBox(5);
    private final TextArea resultArea = new TextArea();
    private final Label typeLabel = new Label();

    private final VBox insertFieldsBox = new VBox(5);

    public DatabaseWindow(Database database, DatabaseList databaseList, UserRecord user) {
        this.database = database;
        this.stage = new Stage();
        stage.setTitle("Database: " + database.getName());

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        resultArea.setEditable(false);
        layout.setBottom(resultArea);



        Button executeButton = new Button("Execute");
        Button backButton = new Button("Back");
        HBox topBar = new HBox(10, executeButton, backButton);
        topBar.setAlignment(Pos.CENTER);
        layout.setTop(topBar);

        backButton.setOnAction(_ -> {
            if(user.getUserType() == UserType.ADMIN) {
                new MainAppWindowAdmin(user).show();
            }
            if(user.getUserType() == UserType.VIEWER){
                new MainAppWindowViewer(user).show();
            }
            stage.close();
        });

        queryTypeBox.setPromptText("Query Type");
        if(user.getUserType() == UserType.ADMIN) {
            queryTypeBox.getItems().clear();
            queryTypeBox.getItems().addAll(StandardQueryType.values());
        }
        if(user.getUserType() == UserType.VIEWER){
            queryTypeBox.getItems().clear();
            queryTypeBox.getItems().add(StandardQueryType.SELECT);
        }

        tableBox.setPromptText("Table");
        tableBox.setItems(FXCollections.observableArrayList(database.getTables().keySet()));

        columnBox.setPromptText("Column");
        columnBox.setOnAction(_ ->{
            if (queryTypeBox.getValue() == StandardQueryType.UPDATE) {
                String selectedColumn = columnBox.getValue();
                String selectedTable = tableBox.getValue();

                if (selectedTable != null && !selectedTable.isBlank() && selectedColumn != null) {
                    Table table = database.getTables().get(selectedTable);
                    String[] columnNames = table.getColumnNames();
                    int index = IntStream.range(0, columnNames.length)
                            .filter(i -> columnNames[i].equals(selectedColumn))
                            .findFirst()
                            .orElse(0);
                    DataType type = table.getColumnTypes()[index];
                    typeLabel.setText(type.getSignature());
                }}});

        valueField.setPromptText("Value (for INSERT/UPDATE)");
        tableField.setPromptText("Select table name");
        newValueField.setPromptText("New value");
        conditionField.setPromptText("Condition (for WHERE, e.g. id=1)");

        addColumnButton.setOnAction(_ -> addColumnInput());
        addSelectColumnButton.setOnAction(_ -> addSelectColumnInput());

        whereConditionCheckBox.selectedProperty().bindBidirectional(isWhereConditionEnabled);
        isWhereConditionEnabled.addListener((_, _, newVal) -> conditionField.setVisible(newVal));

        columnSection.getChildren().addAll(addColumnButton, columnInputsBox);
        selectColumnBox.getChildren().add(addSelectColumnButton);

        tableBox.setOnAction(_ -> {
            if (queryTypeBox.getValue() == StandardQueryType.INSERT) {
                updateFieldVisibility();
                populateInsertFields(tableBox.getValue());
            }
            if (queryTypeBox.getValue() == StandardQueryType.UPDATE || queryTypeBox.getValue() == StandardQueryType.ALTER_TABLE_DROP_COLUMN) {
                String selectedTable = tableBox.getValue();

                if (selectedTable != null && !selectedTable.isBlank()) {
                    Table tableDef = database.getTables().get(selectedTable);
                    if (tableDef != null) {
                        columnBox.setItems(FXCollections.observableArrayList(tableDef.getColumnNames()));
                    }
                }
            }
            if (queryTypeBox.getValue() == StandardQueryType.SELECT){
                String selectedTable = tableBox.getValue();
                if (selectedTable != null && !selectedTable.isBlank() && database.getTables().containsKey(selectedTable)) {
                    String[] columnNames = database.getTables().get(selectedTable).getColumnNames();
                    for (Node node : selectColumnBox.getChildren()) {
                        if (node instanceof HBox) {
                            HBox row = (HBox) node;
                            for (Node child : row.getChildren()) {
                                if (child instanceof ComboBox) {
                                    ComboBox<String> columnComboBox = (ComboBox<String>) child;
                                    columnComboBox.setItems(FXCollections.observableArrayList(columnNames));
                                }
                            }
                        }
                    }
                }
            }
        });


        StackPane tableStack = new StackPane(tableBox, tableField);
        StackPane.setAlignment(tableBox, Pos.CENTER_LEFT);
        StackPane.setAlignment(tableField, Pos.CENTER_LEFT);

        VBox builderBox = new VBox(10,
                queryTypeBox,
                tableStack,
                columnSection,
                new VBox(5, selectAllColumnsCheckBox, whereConditionCheckBox, conditionField, selectColumnBox),
                columnBox,
                typeLabel,
                valueField,
                newValueField,
                insertFieldsBox
        );
        builderBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(builderBox);
        scrollPane.setFitToWidth(true);
        layout.setCenter(scrollPane);

        executeButton.setOnAction(_ -> {
            try {
                Query query = buildQueryFromInputs();
                if (query != null) {
                    QueryResult result = database.execute(query);
                    String error = result.status() == QueryResultStatus.ERROR ? result.errorMessage().toString() : "";

                    if (result.payload().isPresent() && result.payload().isPresent() && query instanceof SelectTableQuery) {

                        Optional<Iterable<ResultRow>> pay = result.payload();

                        SelectTableQuery q = (SelectTableQuery) query;
                        var tableDef = database.getTables().get(q.getTableName().getFieldName());
                        String[] columnNames = tableDef.getColumnNames();
                        DataType[] columnTypes = tableDef.getColumnTypes();

                        TableView<List<String>> tableView = new TableView<>();

                        Set<Integer> validColumnIndexes = new HashSet<>();
                        for (ResultRow r : pay.get()) {
                            for (int i = 0; i < columnNames.length; i++) {
                                String colName = columnNames[i];
                                DataType type = columnTypes[i];
                                boolean hasValue = switch (type) {
                                    case INTEGER -> r.getInt(colName).isPresent();
                                    case REAL    -> r.getDouble(colName).isPresent();
                                    case TEXT    -> r.getString(colName).isPresent();
                                    default      -> false;
                                };
                                if (hasValue) {
                                    validColumnIndexes.add(i);
                                }
                            }
                        }

                        List<Integer> displayIndexes = validColumnIndexes.stream().sorted().toList();

                        for (int i : displayIndexes) {
                            final int colIndex = i;
                            TableColumn<List<String>, String> col = new TableColumn<>(columnNames[i]);
                            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(displayIndexes.indexOf(colIndex))));
                            tableView.getColumns().add(col);
                        }

                        for (ResultRow r : pay.get()) {
                            List<String> row = new ArrayList<>();
                            for (int i : displayIndexes) {
                                String colName = columnNames[i];
                                DataType type = columnTypes[i];
                                String value = switch (type) {
                                    case INTEGER -> r.getInt(colName).map(Object::toString).orElse(null);
                                    case REAL    -> r.getDouble(colName).map(Object::toString).orElse(null);
                                    case TEXT    -> r.getString(colName).orElse(null);
                                    default      -> null;
                                };
                                row.add(value);
                            }
                            tableView.getItems().add(row);
                        }


                        Stage tableStage = new Stage();
                        tableStage.initModality(Modality.APPLICATION_MODAL);
                        tableStage.setTitle("Query Result Table");
                        VBox vbox = new VBox(tableView);
                        vbox.setPadding(new Insets(10));
                        Scene scene = new Scene(vbox, 600, 400);
                        tableStage.setScene(scene);
                        tableStage.show();
                    }

                    if (result.status() == QueryResultStatus.OK) {
                        databaseList.save();
                        tableBox.setItems(FXCollections.observableArrayList(database.getTables().keySet()));
                    }

                    resultArea.setText("Executed on database: " + database.getName() +
                            "\nQuery type: " + queryTypeBox.getValue() +
                            "\nQuery:\n" + query.toSqlString() +
                            "\nRESULT STATUS: " + result.status() + "\n" + error + "\n" + result.rowsAffected());

                } else {
                    resultArea.setText("Could not build query. Please check your selections/inputs.");
                }
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
            finally {
                columnInputsBox.getChildren().clear();
                primaryColumnRow = null;
                if (selectColumnBox.getChildren().size() > 1) {
                    selectColumnBox.getChildren().subList(1, selectColumnBox.getChildren().size()).clear();
                }
            }
        });

        queryTypeBox.setOnAction(_ -> {
            updateFieldVisibility();
            columnInputsBox.getChildren().clear();
            primaryColumnRow = null;
            if (selectColumnBox.getChildren().size() > 1) {
                selectColumnBox.getChildren().subList(1, selectColumnBox.getChildren().size()).clear();
            }});
        queryTypeBox.setValue(StandardQueryType.SELECT);
        updateFieldVisibility();

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
    }

    private void updateFieldVisibility() {
        StandardQueryType type = queryTypeBox.getValue();
        if (type == null) return;

        setDefault();


        switch (type) {
            case SELECT -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
                selectColumnBox.setVisible(true);
                selectColumnBox.setManaged(true);
                addSelectColumnButton.setVisible(true);
                addSelectColumnButton.setManaged(true);
                selectAllColumnsCheckBox.setVisible(true);
                selectAllColumnsCheckBox.setManaged(true);
                whereConditionCheckBox.setVisible(true);
                whereConditionCheckBox.setManaged(true);
                conditionField.setVisible(isWhereConditionEnabled.get());
                conditionField.setManaged(true);
            }
            case INSERT -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
                insertFieldsBox.setVisible(true);
                insertFieldsBox.setManaged(true);
                String selectedTable = tableBox.getValue();
                populateInsertFields(selectedTable);
            }
            case UPDATE -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
                columnBox.setVisible(true);
                columnBox.setManaged(true);
                newValueField.setVisible(true);
                newValueField.setManaged(true);
                whereConditionCheckBox.setVisible(true);
                whereConditionCheckBox.setManaged(true);
                conditionField.setVisible(isWhereConditionEnabled.get());
                conditionField.setManaged(true);
                typeLabel.setVisible(true);
                typeLabel.setManaged(true);
                String selectedTable = tableBox.getValue();
                if (selectedTable != null && !selectedTable.isBlank()) {
                    Table tableDef = database.getTables().get(selectedTable);
                    if (tableDef != null) {
                        columnBox.setItems(FXCollections.observableArrayList(tableDef.getColumnNames()));
                    }
                }
            }
            case DELETE -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
                whereConditionCheckBox.setVisible(true);
                whereConditionCheckBox.setManaged(true);
                conditionField.setVisible(isWhereConditionEnabled.get());
                conditionField.setManaged(true);
            }
            case CREATE_TABLE -> {
                setDefault();
                tableField.setVisible(true);
                tableField.setManaged(true);
                columnSection.setVisible(true);
                columnSection.setManaged(true);
            }
            case DROP_TABLE -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
            }
            case ALTER_TABLE_RENAME_TABLE -> {
                setDefault();
                newValueField.setVisible(true);
                newValueField.setManaged(true);
                tableBox.setVisible(true);
                tableBox.setManaged(true);
            }
            case ALTER_TABLE_ADD_COLUMN -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
                columnSection.setVisible(true);
                columnSection.setManaged(true);
            }
            case ALTER_TABLE_DROP_COLUMN -> {
                setDefault();
                tableBox.setVisible(true);
                tableBox.setManaged(true);
                columnBox.setVisible(true);
                columnBox.setManaged(true);
            }
        }
    }

    private final boolean isFirstColumn = true;  // Flag to track if it's the first column

    private void setDefault(){
        createTableLabel.setVisible(false);
        createTableLabel.setManaged(false);
        tableField.setVisible(false);
        tableField.setManaged(false);
        tableBox.setVisible(false);
        tableBox.setManaged(false);
        columnBox.setVisible(false);
        columnBox.setManaged(false);
        valueField.setVisible(false);
        valueField.setManaged(false);
        newValueField.setVisible(false);
        newValueField.setManaged(false);
        conditionField.setVisible(false);
        conditionField.setManaged(false);
        columnSection.setVisible(false);
        columnSection.setManaged(false);
        selectColumnBox.setVisible(false);
        selectColumnBox.setManaged(false);
        addSelectColumnButton.setVisible(false);
        addSelectColumnButton.setManaged(false);
        selectAllColumnsCheckBox.setVisible(false);
        selectAllColumnsCheckBox.setManaged(false);
        whereConditionCheckBox.setVisible(false);
        whereConditionCheckBox.setManaged(false);
        insertFieldsBox.setVisible(false);
        insertFieldsBox.setManaged(false);
        typeLabel.setVisible(false);
        typeLabel.setManaged(false);
        insertFieldsBox.getChildren().clear();
    }


    private HBox primaryColumnRow = null;

    private void addColumnInput() {
        TextField columnNameField = new TextField();
        columnNameField.setPromptText("Column Name");

        ComboBox<StandardDataType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(StandardDataType.values());
        typeComboBox.setPromptText("Data Type");

        Button deleteButton = new Button("X");
        deleteButton.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Label primaryLabel = new Label();
        primaryLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        HBox row = new HBox(10, columnNameField, typeComboBox, deleteButton, primaryLabel);
        row.setAlignment(Pos.CENTER_LEFT);

        if (primaryColumnRow == null && queryTypeBox.getValue() == StandardQueryType.CREATE_TABLE) {
            primaryLabel.setText("Primary Column");
            primaryColumnRow = row;
        }

        deleteButton.setOnAction(_ -> {
            columnInputsBox.getChildren().remove(row);

            if (row == primaryColumnRow) {
                primaryColumnRow = null;

                if (!columnInputsBox.getChildren().isEmpty()) {
                    HBox newPrimary = (HBox) columnInputsBox.getChildren().getFirst();
                    Label newLabel = (Label) newPrimary.getChildren().get(3);
                    newLabel.setText("Primary Column");
                    primaryColumnRow = newPrimary;
                }
            }
        });

        columnInputsBox.getChildren().add(row);
    }

    private void addSelectColumnInput() {
        String selectedTable = tableBox.getValue();
        if (selectedTable == null || selectedTable.isBlank()) {
            return;
        }
        var tableDef = database.getTables().get(selectedTable);
        if (tableDef == null) {
            return;
        }

        ComboBox<String> ccolumnComboBox = new ComboBox<>(FXCollections.observableArrayList(tableDef.getColumnNames()));
        ccolumnComboBox.setPromptText("Select Column");
        ccolumnComboBox.setAccessibleText("columnField");

        Button deleteButton = new Button("X");
        deleteButton.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        HBox row = new HBox(10, ccolumnComboBox, deleteButton);
        row.setAlignment(Pos.CENTER_LEFT);

        deleteButton.setOnAction(_ -> selectColumnBox.getChildren().remove(row));

        selectColumnBox.getChildren().add(row);
    }





    private Query buildQueryFromInputs() {
        StandardQueryType type = queryTypeBox.getValue();
        String table = tableBox.getValue();
        String column = columnBox.getValue();
        String value = valueField.getText();
        String newValue = newValueField.getText();
        String tableNameField = tableField.getText();
        Condition condition = isWhereConditionEnabled.get() ? ConditionBuilder.parse(conditionField.getText()) : null;
        boolean pkSet = false;

        switch (type) {
            case INSERT -> {
                if (table != null && !insertFieldsBox.getChildren().isEmpty()) {
                    List<String> columnNames = new ArrayList<>();
                    List<Object> values = new ArrayList<>();

                    for (var node : insertFieldsBox.getChildren()) {
                        if (node instanceof HBox row && row.getChildren().size() == 2) {
                            Label label = (Label) row.getChildren().get(0);
                            TextField input = (TextField) row.getChildren().get(1);

                            String colName = label.getText().split(" ")[0].trim();
                            String valueText = input.getText();

                            columnNames.add(colName);
                            values.add(parseValue(valueText));
                        }
                    }

                    if (!columnNames.isEmpty() && columnNames.size() == values.size()) {
                        return new InsertTableQuery.Builder()
                                .insertInto(table, columnNames.toArray(new String[0]))
                                .values(values.toArray())
                                .build();
                    }
                }
            }
            case SELECT -> {
                SelectQueryBuilder builder = new SelectTableQuery.Builder();
                if (selectAllColumnsCheckBox.isSelected()) {
                    builder.selectFrom(new StandardField(table, StandardFieldClass.TABLE));
                } else {
                    builder.from(new StandardField(table, StandardFieldClass.TABLE));
                    List<Field> f = new ArrayList<>();
                    for (var node : selectColumnBox.getChildren()) {
                        if (node instanceof HBox row) {
                            for (var child : row.getChildren()) {
                                if (child instanceof TextField field) {
                                    String colName = field.getText();
                                    if (!colName.isBlank()) {
                                        f.add(new StandardField(colName, StandardFieldClass.COLUMN));
                                    }
                                }
                            }
                        }
                    }

                    builder.select(f.toArray(Field[]::new));
                }
                if (condition != null) builder.where(condition);
                return builder.build();
            }
            case UPDATE -> {
                if (table != null && column != null && newValue != null) {
                    UpdateQueryBuilder builder = new UpdateTableQuery.Builder()
                            .update(new StandardField(table, StandardFieldClass.TABLE))
                            .set(new StandardField(column, StandardFieldClass.COLUMN), newValue);
                    if (condition != null) builder.where(condition);
                    return builder.build();
                }
            }
            case DELETE -> {
                if (table != null) {
                    DeleteQueryBuilder builder = new DeleteTableQuery.Builder()
                            .deleteFrom(new StandardField(table, StandardFieldClass.TABLE));
                    if (condition != null) builder.where(condition);
                    return builder.build();
                }
            }
            case CREATE_TABLE -> {
                if (tableNameField != null && !tableNameField.isBlank()) {
                    CreateTableQueryBuilder builder = new CreateTableQuery.Builder().createTable(tableNameField);
                    for (var node : columnInputsBox.getChildren()) {
                        if (node instanceof HBox row) {
                            TextField nameField = (TextField) row.getChildren().get(0);
                            ComboBox<StandardDataType> typeBox = (ComboBox<StandardDataType>) row.getChildren().get(1);
                            String colName = nameField.getText();
                            StandardDataType dataType = typeBox.getValue();
                            if (colName != null && dataType != null) {
                                if (!pkSet) {
                                    builder.primaryKey(colName);
                                    pkSet = true;
                                }
                                builder.column(colName, dataType.name());
                            }
                        }
                    }
                    return builder.build();
                }
            }
            case DROP_TABLE -> {
                if (table != null) {
                    return new DropTableQuery.Builder().dropTable(table).build();
                }
            }
            case ALTER_TABLE_RENAME_TABLE -> {
                if (table != null && tableNameField != null) {
                    return new AlterTableQuery.Builder().alterTable(table).renameTo(newValue).build();
                }
            }
            case ALTER_TABLE_ADD_COLUMN -> {
                if (table != null && !columnInputsBox.getChildren().isEmpty()) {
                    AlterTableQueryBuilder builder = new AlterTableQuery.Builder().alterTable(table);
                    for (var node : columnInputsBox.getChildren()) {
                        if (node instanceof HBox row && row.getChildren().size() >= 2) {
                            TextField nameField = (TextField) row.getChildren().get(0);
                            ComboBox<StandardDataType> typeBox = (ComboBox<StandardDataType>) row.getChildren().get(1);
                            String colName = nameField.getText();
                            StandardDataType dataType = typeBox.getValue();
                            if (colName != null && !colName.isBlank() && dataType != null) {
                                builder = builder.addColumn(colName, dataType.name());
                            }
                        }
                    }
                    return builder.build();
                }
            }
            case ALTER_TABLE_DROP_COLUMN-> {
                if (table != null && column != null) {
                    return new AlterTableQuery.Builder().alterTable(table).dropColumn(column).build();
                }
            }
        }
        return null;
    }

    private Object parseValue(String val) {
        if (val == null) return null;
        try {
            if (val.contains(".")) return Double.parseDouble(val);
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return val;
        }
    }

    private void populateInsertFields(String selectedTable) {
        insertFieldsBox.getChildren().clear();

        if (selectedTable != null && !selectedTable.isBlank()) {
            var tableDef = database.getTables().get(selectedTable);
            if (tableDef != null) {
                String[] columnNames = tableDef.getColumnNames();
                DataType[] columnTypes = tableDef.getColumnTypes();
                for (int i = 0; i < columnNames.length; i++) {
                    Label label = new Label(columnNames[i] + " (" + columnTypes[i] + ")");
                    TextField input = new TextField();
                    input.setPromptText("Enter value");
                    HBox row = new HBox(10, label, input);
                    row.setAlignment(Pos.CENTER_LEFT);
                    insertFieldsBox.getChildren().add(row);
                }
            }
        }
    }

    public void show() {
        stage.show();
    }
}