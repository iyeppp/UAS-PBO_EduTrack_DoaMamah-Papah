package com.doamamah.edutrack.fe.controller;

import com.doamamah.edutrack.fe.model.CourseMaterial;
import com.doamamah.edutrack.fe.model.TextMaterial;
import com.doamamah.edutrack.fe.model.VideoMaterial;
import com.doamamah.edutrack.fe.model.Teacher;
import com.doamamah.edutrack.fe.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class MaterialView {

    private final DashboardController controller;

    public MaterialView(DashboardController controller) {
        this.controller = controller;
    }

    public Node buildListContent(List<CourseMaterial> materials) {
        VBox root = new VBox(14);
        root.setMaxWidth(Double.MAX_VALUE);

        if (materials.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada materi yang tersedia.");
            emptyLabel.getStyleClass().add("placeholder-text");
            root.getChildren().add(emptyLabel);
            return root;
        }

        // Header section
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label sectionLabel = new Label("Daftar Materi Tersedia");
        sectionLabel.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label countLabel = new Label(materials.size() + " materi");
        countLabel.getStyleClass().add("material-count");
        header.getChildren().addAll(sectionLabel, sp, countLabel);
        root.getChildren().add(header);

        // FlowPane for responsive wrapping columns
        FlowPane cardsContainer = new FlowPane();
        cardsContainer.setHgap(16);
        cardsContainer.setVgap(16);
        cardsContainer.setMaxWidth(Double.MAX_VALUE);
        cardsContainer.setPrefWrapLength(750);

        for (CourseMaterial material : materials) {
            VBox card = buildMaterialCard(material);
            card.setPrefWidth(340);
            card.setMinWidth(300);
            cardsContainer.getChildren().add(card);
        }

        root.getChildren().add(cardsContainer);
        return root;
    }

    private VBox buildMaterialCard(CourseMaterial material) {
        VBox card = new VBox(10);
        card.getStyleClass().add("material-card");
        card.setPadding(new Insets(18));

        // Top row: badge + actions
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        boolean isVideo = "VIDEO".equals(material.getMaterialType());
        Label typeBadge = new Label(isVideo ? "Video" : "Teks");
        typeBadge.getStyleClass().addAll("badge", isVideo ? "badge-video" : "badge-text");

        // Colored dot
        Circle dot = new Circle(4);
        dot.setFill(Color.web(isVideo ? "#FF7A00" : "#059669"));

        Label typeDetail = new Label(isVideo ? "Materi Video" : "Materi Teks");
        typeDetail.getStyleClass().add("card-type-detail");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        topRow.getChildren().addAll(typeBadge, dot, typeDetail, sp);

        // Title
        Label titleLabel = new Label(material.getTitle());
        titleLabel.getStyleClass().add("card-title");

        // Description
        Label descLabel = new Label(material.getDescription());
        descLabel.getStyleClass().add("card-description");
        descLabel.setWrapText(true);

        // Divider
        javafx.scene.control.Separator divider = new javafx.scene.control.Separator();

        // Button row
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = new Button("Lihat Materi");
        viewBtn.getStyleClass().addAll("btn-primary", "btn-small");
        viewBtn.setOnAction(e -> {
            Node materialUI = material.getUIComponent();
            if (materialUI instanceof Pane pane) {
                HBox backWrapper = new HBox(buildBackButton());
                backWrapper.setAlignment(Pos.CENTER_LEFT);
                backWrapper.setMaxWidth(Double.MAX_VALUE);
                backWrapper.setPadding(new Insets(0, 0, 10, 0));
                pane.getChildren().add(0, backWrapper);
            }
            controller.getContentArea().getChildren().clear();
            controller.getContentArea().getChildren().add(materialUI);
            controller.getContentTitleLabel().setText(material.getTitle());
        });

        actions.getChildren().add(viewBtn);

        User currentUser = controller.getCurrentUser();
        if (currentUser instanceof Teacher) {
            Button editBtn = new Button("Edit");
            editBtn.getStyleClass().addAll("btn-secondary", "btn-small");
            editBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white;");
            editBtn.setOnAction(e -> {
                controller.getContentArea().getChildren().clear();
                controller.getContentArea().getChildren().add(buildForm(material));
                controller.getContentTitleLabel().setText("Edit Materi: " + material.getTitle());
            });

            Button deleteBtn = new Button("Hapus");
            deleteBtn.getStyleClass().addAll("btn-ghost", "btn-small");
            deleteBtn.setStyle("-fx-text-fill: #DC2626; -fx-border-color: #DC2626; -fx-border-radius: 4px;");
            deleteBtn.setOnAction(e -> {
                ButtonType response = controller.showCustomAlert(
                    Alert.AlertType.CONFIRMATION,
                    "Konfirmasi Hapus",
                    "Hapus Materi?",
                    "Apakah Anda yakin ingin menghapus materi '" + material.getTitle() + "'?"
                );
                if (response == ButtonType.OK) {
                    controller.getMaterialService().deleteMaterial(material.getId());
                    controller.showMaterialsContent();
                }
            });

            actions.getChildren().addAll(editBtn, deleteBtn);
        }

        card.getChildren().addAll(topRow, titleLabel, descLabel, divider, actions);
        return card;
    }

    private Button buildBackButton() {
        Button backBtn = new Button();
        backBtn.getStyleClass().add("btn-back-round");

        // Custom SVG back arrow icon (attractive layout)
        javafx.scene.shape.SVGPath arrow = new javafx.scene.shape.SVGPath();
        arrow.setContent("M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z");
        arrow.setStyle("-fx-fill: #FF7A00;");

        backBtn.setGraphic(arrow);
        backBtn.setTooltip(new Tooltip("Kembali ke Daftar Materi"));

        // Style the button as a premium circular button
        backBtn.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 20px; " +
            "-fx-border-color: #E5E0D8; " +
            "-fx-border-radius: 20px; " +
            "-fx-border-width: 1.5px; " +
            "-fx-padding: 8px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 4, 0, 0, 1);"
        );

        // Add dynamic micro-animations/hover changes
        backBtn.setOnMouseEntered(e -> {
            backBtn.setStyle(
                "-fx-background-color: #FFF0E0; " +
                "-fx-background-radius: 20px; " +
                "-fx-border-color: #FF7A00; " +
                "-fx-border-radius: 20px; " +
                "-fx-border-width: 1.5px; " +
                "-fx-padding: 8px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 122, 0, 0.15), 6, 0, 0, 2);"
            );
            arrow.setStyle("-fx-fill: #E66E00;");
        });
        backBtn.setOnMouseExited(e -> {
            backBtn.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 20px; " +
                "-fx-border-color: #E5E0D8; " +
                "-fx-border-radius: 20px; " +
                "-fx-border-width: 1.5px; " +
                "-fx-padding: 8px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 4, 0, 0, 1);"
            );
            arrow.setStyle("-fx-fill: #FF7A00;");
        });

        backBtn.setOnAction(e -> controller.showMaterialsContent());
        return backBtn;
    }

    public Node buildForm(CourseMaterial materialToEdit) {
        boolean isEdit = (materialToEdit != null);
        VBox root = new VBox(20);
        root.setMaxWidth(Double.MAX_VALUE);

        // Header Box
        VBox headerBox = new VBox(6);
        headerBox.getStyleClass().add("section-box");
        headerBox.setPadding(new Insets(20));

        Label iconLabel = new Label("✨");
        iconLabel.setStyle("-fx-font-size: 32px;");
        Label titleLabel = new Label(isEdit ? "Edit Materi E-Learning" : "Buat Materi E-Learning Baru");
        titleLabel.getStyleClass().add("section-title");
        Label subLabel = new Label(isEdit ? "Ubah formulir di bawah ini untuk memperbarui materi pembelajaran." : "Lengkapi formulir di bawah ini untuk membagikan materi baru bagi siswa.");
        subLabel.getStyleClass().add("card-description");
        headerBox.getChildren().addAll(iconLabel, titleLabel, subLabel);

        // Form Card
        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("material-card");
        formCard.setPadding(new Insets(24));

        // Judul Input
        TextField txtTitle = new TextField(isEdit ? materialToEdit.getTitle() : "");
        txtTitle.setPromptText("Contoh: Pengenalan Array di Java");
        VBox titleBox = controller.createInputField("Judul Materi", txtTitle);

        // Deskripsi Input
        TextArea txtDesc = new TextArea(isEdit ? materialToEdit.getDescription() : "");
        txtDesc.setPrefHeight(70);
        txtDesc.setWrapText(true);
        txtDesc.setPromptText("Tuliskan deskripsi singkat mengenai materi ini...");
        VBox descBox = controller.createInputField("Deskripsi Singkat", txtDesc);

        // Tipe Materi ComboBox
        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Video", "Teks");
        boolean isVideo = !isEdit || "VIDEO".equals(materialToEdit.getMaterialType());
        cmbType.setValue(isVideo ? "Video" : "Teks");
        cmbType.setMaxWidth(Double.MAX_VALUE);
        if (isEdit) {
            cmbType.setDisable(true);
        }
        VBox typeBox = controller.createInputField("Tipe Materi", cmbType);

        // Dynamic Form Fields Container
        VBox dynamicContainer = new VBox(16);

        // Dynamic Sub-Form Video
        VBox videoForm = new VBox(16);
        String initialUrl = "";
        String initialDuration = "";
        if (isEdit && materialToEdit instanceof VideoMaterial vm) {
            initialUrl = vm.getVideoUrl();
            initialDuration = String.valueOf(vm.getDurationMinutes());
        }
        TextField txtUrl = new TextField(initialUrl);
        txtUrl.setPromptText("Contoh: https://www.youtube.com/watch?v=pTB0EiLXUC8");
        VBox urlBox = controller.createInputField("URL Video YouTube", txtUrl);

        TextField txtDuration = new TextField(initialDuration);
        txtDuration.setPromptText("Contoh: 15");
        VBox durationBox = controller.createInputField("Durasi (Menit)", txtDuration);
        videoForm.getChildren().addAll(urlBox, durationBox);

        // Dynamic Sub-Form Teks
        VBox textForm = new VBox(16);
        String initialContent = "";
        if (isEdit && materialToEdit instanceof TextMaterial tm) {
            initialContent = tm.getTextContent();
        }
        TextArea txtContent = new TextArea(initialContent);
        txtContent.setPrefHeight(250);
        txtContent.setWrapText(true);
        txtContent.setPromptText("Tuliskan seluruh materi pembelajaran di sini...");
        VBox contentBox = controller.createInputField("Konten Teks Materi", txtContent);
        textForm.getChildren().add(contentBox);

        // Set default dynamic form
        dynamicContainer.getChildren().add(isVideo ? videoForm : textForm);

        // Listener to change dynamic fields on ComboBox change
        if (!isEdit) {
            cmbType.valueProperty().addListener((obs, oldVal, newVal) -> {
                dynamicContainer.getChildren().clear();
                dynamicContainer.getChildren().add("Video".equals(newVal) ? videoForm : textForm);
            });
        }

        // Error message label
        Label errorMsg = new Label();
        errorMsg.getStyleClass().add("error-label");
        errorMsg.setVisible(false);
        errorMsg.setManaged(false);

        // Action Buttons Row
        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancel = new Button("Batal");
        btnCancel.getStyleClass().addAll("btn-ghost", "btn-medium");
        btnCancel.setOnAction(e -> controller.showMaterialsContent());

        Button btnSave = new Button(isEdit ? "Simpan Perubahan" : "Simpan Materi");
        btnSave.getStyleClass().addAll("btn-primary", "btn-medium");
        btnSave.setStyle("-fx-background-color: #059669;"); // colorful success green
        btnSave.setOnAction(e -> {
            String title = txtTitle.getText().trim();
            String desc = txtDesc.getText().trim();
            String type = cmbType.getValue();

            if (title.isEmpty()) {
                showFormError(errorMsg, "Judul materi tidak boleh kosong!");
                return;
            }
            if (desc.isEmpty()) {
                showFormError(errorMsg, "Deskripsi materi tidak boleh kosong!");
                return;
            }

            CourseMaterial material = null;
            if ("Video".equals(type)) {
                String url = txtUrl.getText().trim();
                String durationStr = txtDuration.getText().trim();

                if (url.isEmpty()) {
                    showFormError(errorMsg, "URL Video YouTube tidak boleh kosong!");
                    return;
                }
                if (durationStr.isEmpty()) {
                    showFormError(errorMsg, "Durasi video tidak boleh kosong!");
                    return;
                }

                int duration;
                try {
                    duration = Integer.parseInt(durationStr);
                    if (duration <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    showFormError(errorMsg, "Durasi harus berupa angka bulat positif!");
                    return;
                }

                material = new VideoMaterial(isEdit ? materialToEdit.getId() : null, title, desc, url, duration);
            } else {
                String content = txtContent.getText().trim();
                if (content.isEmpty()) {
                    showFormError(errorMsg, "Konten teks materi tidak boleh kosong!");
                    return;
                }
                material = new TextMaterial(isEdit ? materialToEdit.getId() : null, title, desc, content);
            }

            // Save via service
            if (isEdit) {
                controller.getMaterialService().updateMaterial(material);
            } else {
                controller.getMaterialService().addMaterial(material);
            }

            // Success notification alert
            controller.showCustomAlert(
                Alert.AlertType.INFORMATION,
                "Berhasil",
                isEdit ? "Materi Berhasil Diperbarui!" : "Materi Baru Berhasil Disimpan!",
                isEdit ? "Perubahan pada materi '" + title + "' kini sudah tersimpan."
                       : "Materi '" + title + "' kini sudah dapat diakses oleh seluruh siswa."
            );

            controller.showMaterialsContent();
        });

        buttonRow.getChildren().addAll(btnCancel, btnSave);
        formCard.getChildren().addAll(titleBox, descBox, typeBox, dynamicContainer, errorMsg, buttonRow);
        root.getChildren().addAll(headerBox, formCard);
        return root;
    }

    private void showFormError(Label lbl, String msg) {
        lbl.setText("⚠ " + msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }
}
