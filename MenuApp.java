package uas;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MenuApp extends Application {

    private final MenuService service = new MenuService();
    private final ObservableList<Menu> data = FXCollections.observableArrayList();
    private final TableView<Menu> table = new TableView<>();
    private final TextField tfNama = new TextField();
    private final TextField tfHarga = new TextField();
    private final ComboBox<String> cbKategori = new ComboBox<>();
    private final ComboBox<String> cbKetersediaan = new ComboBox<>();
    private Menu selectedMenu = null;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("🍽️ Menu Manager - JavaFX");

        Label labelTitle = new Label("🍽️ Manajemen Menu");
        labelTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        labelTitle.setAlignment(Pos.CENTER);

        // Table columns
        TableColumn<Menu, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(50);

        TableColumn<Menu, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setMinWidth(150);

        TableColumn<Menu, String> colKategori = new TableColumn<>("Kategori");
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colKategori.setMinWidth(120);

        TableColumn<Menu, Integer> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colHarga.setMinWidth(100);
        colHarga.setCellFactory(tc -> new TableCell<Menu, Integer>() {
            @Override
            protected void updateItem(Integer harga, boolean empty) {
                super.updateItem(harga, empty);
                if (empty || harga == null) {
                    setText(null);
                } else {
                    setText("Rp " + String.format("%,d", harga));
                }
            }
        });

        TableColumn<Menu, String> colKetersediaan = new TableColumn<>("Ketersediaan");
        colKetersediaan.setCellValueFactory(new PropertyValueFactory<>("ketersediaan"));
        colKetersediaan.setMinWidth(120);

        table.getColumns().addAll(colId, colNama, colKategori, colHarga, colKetersediaan);
        table.setItems(data);

        // Form inputs
        tfNama.setPromptText("Nama Menu");
        tfHarga.setPromptText("Harga");

        cbKategori.getItems().addAll("Makanan", "Minuman", "Cemilan", "Dessert");
        cbKategori.setPromptText("Pilih Kategori");

        cbKetersediaan.getItems().addAll("Tersedia", "Habis");
        cbKetersediaan.setPromptText("Pilih Ketersediaan");

        // Table selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedMenu = newVal;
            if (newVal != null) {
                tfNama.setText(newVal.getNama());
                tfHarga.setText(String.valueOf(newVal.getHarga()));
                cbKategori.setValue(newVal.getKategori());
                cbKetersediaan.setValue(newVal.getKetersediaan());
            }
        });

        // Buttons
        Button btnTambah = new Button("Tambah");
        Button btnUpdate = new Button("Update");
        Button btnHapus = new Button("Hapus");

        btnTambah.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnHapus.setMaxWidth(Double.MAX_VALUE);

        btnTambah.setOnAction(e -> tambahMenu());
        btnUpdate.setOnAction(e -> updateMenu());
        btnHapus.setOnAction(e -> hapusMenu());

        VBox formBox = new VBox(10,
                new Label("Nama:"), tfNama,
                new Label("Kategori:"), cbKategori,
                new Label("Harga:"), tfHarga,
                new Label("Ketersediaan:"), cbKetersediaan,
                btnTambah, btnUpdate, btnHapus
        );
        formBox.setPadding(new Insets(10));
        formBox.setAlignment(Pos.TOP_LEFT);
        formBox.setPrefWidth(250);

        VBox.setVgrow(tfNama, Priority.NEVER);
        VBox.setVgrow(cbKategori, Priority.NEVER);
        VBox.setVgrow(tfHarga, Priority.NEVER);
        VBox.setVgrow(cbKetersediaan, Priority.NEVER);

        VBox.setVgrow(table, Priority.ALWAYS);

        VBox tableBox = new VBox(10, table);
        tableBox.setPadding(new Insets(10));
        tableBox.setAlignment(Pos.TOP_CENTER);

        HBox contentBox = new HBox(20, formBox, tableBox);
        contentBox.setPadding(new Insets(10));

        VBox root = new VBox(15, labelTitle, contentBox);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void tambahMenu() {
        String nama = tfNama.getText().trim();
        String kategori = cbKategori.getValue();
        String hargaText = tfHarga.getText().trim();
        String ketersediaan = cbKetersediaan.getValue();

        if (nama.isEmpty() || kategori == null || hargaText.isEmpty() || ketersediaan == null) {
            showError("Semua field wajib diisi & dipilih.");
            return;
        }

        int harga;
        try {
            harga = Integer.parseInt(hargaText);
        } catch (NumberFormatException e) {
            showError("Harga harus berupa angka.");
            return;
        }

        service.tambahMenu(nama, kategori, harga, ketersediaan);
        refreshTable();
        clearForm();
    }

    private void updateMenu() {
        if (selectedMenu != null) {
            String namaBaru = tfNama.getText().trim();
            String kategoriBaru = cbKategori.getValue();
            String hargaText = tfHarga.getText().trim();
            String ketersediaanBaru = cbKetersediaan.getValue();

            if (namaBaru.isEmpty() || kategoriBaru == null || hargaText.isEmpty() || ketersediaanBaru == null) {
                showError("Semua field wajib diisi & dipilih.");
                return;
            }

            int hargaBaru;
            try {
                hargaBaru = Integer.parseInt(hargaText);
            } catch (NumberFormatException e) {
                showError("Harga harus berupa angka.");
                return;
            }

            service.updateMenu(selectedMenu.getId(), namaBaru, kategoriBaru, hargaBaru, ketersediaanBaru);
            refreshTable();
            clearForm();
        }
    }

    private void hapusMenu() {
        if (selectedMenu != null) {
            service.hapusMenu(selectedMenu.getId());
            refreshTable();
            clearForm();
        }
    }

    private void refreshTable() {
        data.setAll(service.getAll());
    }

    private void clearForm() {
        tfNama.clear();
        tfHarga.clear();
        cbKategori.setValue(null);
        cbKetersediaan.setValue(null);
        table.getSelectionModel().clearSelection();
        selectedMenu = null;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
