package com.doamamah.edutrack.fe.controller;

import com.doamamah.edutrack.fe.model.Student;
import com.doamamah.edutrack.fe.model.Teacher;
import com.doamamah.edutrack.fe.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class QuizView {

    private final DashboardController controller;

    // =====================================================================
    //  QUIZ MODEL & IN-MEMORY CACHE
    // =====================================================================

    public static class QuizData {
        private final int id;
        private final String title;
        private final String desc;
        private final String difficulty;
        private final String color;
        private final List<String> questions;

        public QuizData(int id, String title, String desc, String difficulty, String color, List<String> questions) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.difficulty = difficulty;
            this.color = color;
            this.questions = questions;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getDesc() { return desc; }
        public String getDifficulty() { return difficulty; }
        public String getColor() { return color; }
        public List<String> getQuestions() { return questions; }
    }

    private static int nextQuizId = 1;
    public static final List<QuizData> customQuizzes = new java.util.ArrayList<>();
    static {
        customQuizzes.add(new QuizData(
            nextQuizId++,
            "Kuis: Dasar OOP",
            "3 soal pilihan ganda tentang konsep OOP dasar.",
            "Mudah",
            "#059669",
            java.util.Arrays.asList(
                "Manakah yang merupakan pilar utama dalam Object-Oriented Programming (OOP)?",
                "Apakah fungsi utama dari Encapsulation (Pewadahan) dalam OOP?",
                "Pilar OOP yang memungkinkan objek baru mewarisi sifat dari objek induknya adalah..."
            )
        ));
        customQuizzes.add(new QuizData(
            nextQuizId++,
            "Kuis: Inheritance & Polymorphism",
            "3 soal pilihan ganda tentang pewarisan dan polimorfisme di Java.",
            "Sedang",
            "#D97706",
            java.util.Arrays.asList(
                "Kata kunci (keyword) yang digunakan di Java untuk menerapkan pewarisan kelas (inheritance) adalah...",
                "Apa yang dimaksud dengan Polymorphism (Polimorfisme) dalam OOP?",
                "Keyword 'super' di Java digunakan untuk..."
            )
        ));
    }

    // =====================================================================
    //  QUIZ GAMEPLAY ENGINE STATE
    // =====================================================================

    private String activeQuizTitle;
    private List<QuizQuestion> activeQuestions;
    private int currentQuestionIndex;
    private int correctAnswersCount;
    private int selectedOptionIndex;
    private int[] userAnswers;
    private javafx.animation.Timeline quizTimer;
    private int secondsRemaining;
    private Label timerLabel;

    private static class QuizQuestion {
        String question;
        String[] options;
        int correctIndex;

        QuizQuestion(String question, String[] options, int correctIndex) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    public QuizView(DashboardController controller) {
        this.controller = controller;
    }

    public Node buildQuizContent() {
        User currentUser = controller.getCurrentUser();
        if (currentUser instanceof Teacher) {
            return buildTeacherQuizContent();
        } else {
            return buildStudentQuizContent();
        }
    }

    private Node buildStudentQuizContent() {
        HBox mainLayout = new HBox(20);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        VBox leftColumn = new VBox(20);
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // Header (Simple text layout, no card box)
        VBox headerBox = new VBox(4);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label quizIcon = new Label("🎯");
        quizIcon.setStyle("-fx-font-size: 24px;");
        Label quizTitle = new Label("Kuis & Evaluasi");
        quizTitle.getStyleClass().add("section-title");
        quizTitle.setStyle("-fx-font-size: 20px;");
        titleRow.getChildren().addAll(quizIcon, quizTitle);

        Label quizSub = new Label("Uji pemahamanmu setelah mempelajari materi.");
        quizSub.getStyleClass().add("card-description");

        headerBox.getChildren().addAll(titleRow, quizSub);

        // FlowPane for responsive wrapping quiz cards
        FlowPane quizContainer = new FlowPane();
        quizContainer.setHgap(20);
        quizContainer.setVgap(20);
        quizContainer.setMaxWidth(Double.MAX_VALUE);
        quizContainer.setPrefWrapLength(750);

        for (QuizData q : customQuizzes) {
            VBox card = buildQuizCard(q.getTitle(), q.getDesc(), q.getDifficulty(), q.getColor(), q.getQuestions().size());
            card.setPrefWidth(380);
            card.setMinWidth(320);
            quizContainer.getChildren().add(card);
        }

        leftColumn.getChildren().addAll(headerBox, quizContainer);

        // Right side: Illustration Panel
        VBox rightColumn = new VBox(16);
        rightColumn.setPadding(new Insets(24));
        rightColumn.setAlignment(Pos.CENTER);
        rightColumn.setPrefWidth(320);
        rightColumn.setMaxWidth(320);

        try {
            ImageView quizImg = new ImageView(
                new Image(getClass().getResourceAsStream("/com/doamamah/edutrack/fe/images/quiz_illustration.png"))
            );
            quizImg.setFitWidth(240);
            quizImg.setPreserveRatio(true);
            quizImg.setSmooth(true);

            Label promoTitle = new Label("Siap Menghadapi Kuis?");
            promoTitle.getStyleClass().add("section-title");
            promoTitle.setStyle("-fx-font-size: 15px;");
            Label promoText = new Label("Dapatkan skor tinggi dan buka lencana baru! Kuis dirancang interaktif untuk menguji sejauh mana kamu memahami konsep Java.");
            promoText.getStyleClass().add("card-description");
            promoText.setWrapText(true);
            promoText.setAlignment(Pos.CENTER);

            rightColumn.getChildren().addAll(quizImg, promoTitle, promoText);
        } catch (Exception e) {
            System.err.println("Gagal memuat ilustrasi kuis: " + e.getMessage());
        }

        mainLayout.getChildren().addAll(leftColumn, rightColumn);
        return mainLayout;
    }

    private Node buildTeacherQuizContent() {
        HBox mainLayout = new HBox(20);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        VBox leftColumn = new VBox(20);
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // Header with "Buat Kuis Baru" button
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        VBox titleBox = new VBox(4);
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label quizTitle = new Label("Kelola Kuis & Evaluasi");
        quizTitle.getStyleClass().add("section-title");
        quizTitle.setStyle("-fx-font-size: 20px;");
        titleRow.getChildren().add(quizTitle);
        Label quizSub = new Label("Lihat hasil pengerjaan kuis siswa dan buat kuis baru.");
        quizSub.getStyleClass().add("card-description");
        titleBox.getChildren().addAll(titleRow, quizSub);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button btnCreateQuiz = new Button();
        btnCreateQuiz.getStyleClass().add("btn-create-quiz-round");

        javafx.scene.shape.SVGPath plusIcon = new javafx.scene.shape.SVGPath();
        plusIcon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        plusIcon.setStyle("-fx-fill: #FFFFFF;");

        btnCreateQuiz.setGraphic(plusIcon);
        btnCreateQuiz.setTooltip(new Tooltip("Buat Kuis Baru"));

        btnCreateQuiz.setStyle(
            "-fx-background-color: #059669; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 40px; " +
            "-fx-min-height: 40px; " +
            "-fx-max-width: 40px; " +
            "-fx-max-height: 40px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(5, 150, 105, 0.2), 6, 0, 0, 2);"
        );

        btnCreateQuiz.setOnMouseEntered(e -> {
            btnCreateQuiz.setStyle(
                "-fx-background-color: #047857; " +
                "-fx-background-radius: 50%; " +
                "-fx-min-width: 40px; " +
                "-fx-min-height: 40px; " +
                "-fx-max-width: 40px; " +
                "-fx-max-height: 40px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(4, 120, 87, 0.4), 8, 0, 0, 3);"
            );
        });

        btnCreateQuiz.setOnMouseExited(e -> {
            btnCreateQuiz.setStyle(
                "-fx-background-color: #059669; " +
                "-fx-background-radius: 50%; " +
                "-fx-min-width: 40px; " +
                "-fx-min-height: 40px; " +
                "-fx-max-width: 40px; " +
                "-fx-max-height: 40px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(5, 150, 105, 0.2), 6, 0, 0, 2);"
            );
        });

        btnCreateQuiz.setOnAction(e -> {
            controller.getContentArea().getChildren().clear();
            controller.getContentArea().getChildren().add(buildQuizForm(null));
        });

        headerBox.getChildren().addAll(titleBox, btnCreateQuiz);

        // Daftar Kuis Tersedia
        Label quizzesTitle = new Label("Daftar Kuis Tersedia");
        quizzesTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A1A1A; -fx-font-size: 15px;");
        
        VBox quizzesListContainer = new VBox(10);
        quizzesListContainer.setMaxWidth(Double.MAX_VALUE);
        
        for (QuizData q : customQuizzes) {
            quizzesListContainer.getChildren().add(buildTeacherQuizRow(q));
        }
        
        if (customQuizzes.isEmpty()) {
            Label emptyLbl = new Label("Belum ada kuis yang dibuat.");
            emptyLbl.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic;");
            quizzesListContainer.getChildren().add(emptyLbl);
        }

        // Student Results Container
        Label resultsTitle = new Label("Hasil Pengerjaan Siswa");
        resultsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A1A1A; -fx-font-size: 15px; -fx-padding: 10px 0 0 0;");
        
        VBox resultsContainer = new VBox(12);
        resultsContainer.setMaxWidth(Double.MAX_VALUE);

        resultsContainer.getChildren().addAll(
            buildStudentResultItem("Budi Santoso", "Kuis: Dasar OOP", 100, "Hari ini"),
            buildStudentResultItem("Siti Aminah", "Kuis: Dasar OOP", 67, "Hari ini"),
            buildStudentResultItem("Ahmad Dhani", "Kuis: Inheritance & Polymorphism", 100, "Kemarin"),
            buildStudentResultItem("Rian Subiakto", "Kuis: Dasar OOP", 33, "2 hari lalu"),
            buildStudentResultItem("Dewi Lestari", "Kuis: Inheritance & Polymorphism", 67, "3 hari lalu")
        );

        leftColumn.getChildren().addAll(headerBox, quizzesTitle, quizzesListContainer, resultsTitle, resultsContainer);

        // Right side: Quiz Summary Stats
        VBox rightColumn = new VBox(16);
        rightColumn.setPadding(new Insets(20));
        rightColumn.getStyleClass().add("section-box");
        rightColumn.setPrefWidth(320);
        rightColumn.setMaxWidth(320);

        Label statsTitle = new Label("Statistik Kelas");
        statsTitle.getStyleClass().add("section-title");
        statsTitle.setStyle("-fx-font-size: 15px;");

        rightColumn.getChildren().addAll(
            statsTitle,
            buildMiniStatRow("Rata-rata Nilai", "73.4", "%", "#FF7A00"),
            buildMiniStatRow("Partisipasi Siswa", "92", "%", "#059669"),
            buildMiniStatRow("Total Percobaan", "12", "kali", "#D97706")
        );

        mainLayout.getChildren().addAll(leftColumn, rightColumn);
        return mainLayout;
    }

    private HBox buildTeacherQuizRow(QuizData q) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("material-card");
        row.setPadding(new Insets(12, 16, 12, 16));

        Label icon = new Label("📝");
        icon.setStyle("-fx-font-size: 18px;");

        VBox quizInfo = new VBox(4);
        Label titleLbl = new Label(q.getTitle());
        titleLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A1A1A; -fx-font-size: 14px;");
        Label descLbl = new Label(q.getQuestions().size() + " soal  ·  Tingkat: " + q.getDifficulty());
        descLbl.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        quizInfo.getChildren().addAll(titleLbl, descLbl);
        HBox.setHgrow(quizInfo, Priority.ALWAYS);

        Button btnEdit = new Button("Edit");
        btnEdit.getStyleClass().addAll("btn-secondary", "btn-small");
        btnEdit.setStyle("-fx-background-color: #D97706; -fx-text-fill: white;");
        btnEdit.setOnAction(e -> {
            controller.getContentArea().getChildren().clear();
            controller.getContentArea().getChildren().add(buildQuizForm(q));
        });

        Button btnDelete = new Button("Hapus");
        btnDelete.getStyleClass().addAll("btn-ghost", "btn-small");
        btnDelete.setStyle("-fx-text-fill: #DC2626; -fx-border-color: #DC2626; -fx-border-radius: 4px;");
        btnDelete.setOnAction(e -> {
            ButtonType response = controller.showCustomAlert(
                Alert.AlertType.CONFIRMATION,
                "Konfirmasi Hapus Kuis",
                "Hapus Kuis?",
                "Apakah Anda yakin ingin menghapus kuis '" + q.getTitle() + "'?"
            );
            if (response == ButtonType.OK) {
                customQuizzes.remove(q);
                controller.showQuizContent();
            }
        });

        row.getChildren().addAll(icon, quizInfo, btnEdit, btnDelete);
        return row;
    }

    private VBox buildStudentResultItem(String studentName, String quizName, int score, String date) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        
        String initial = String.valueOf(studentName.charAt(0)).toUpperCase();
        Label avatar = new Label(initial);
        avatar.setStyle(
            "-fx-background-color: #FFF0E0; -fx-text-fill: #FF7A00; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-min-width: 38px; -fx-min-height: 38px; " +
            "-fx-max-width: 38px; -fx-max-height: 38px; -fx-alignment: center; -fx-background-radius: 50%;"
        );

        VBox studentInfo = new VBox(4);
        Label nameLbl = new Label(studentName);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A1A1A; -fx-font-size: 14px;");
        Label quizLbl = new Label(quizName + "  ·  " + date);
        quizLbl.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        studentInfo.getChildren().addAll(nameLbl, quizLbl);
        HBox.setHgrow(studentInfo, Priority.ALWAYS);

        Label scoreBadge = new Label(score + " / 100");
        String badgeColor = score >= 80 ? "#059669" : (score >= 60 ? "#D97706" : "#DC2626");
        String badgeBg = score >= 80 ? "#ECFDF5" : (score >= 60 ? "#FFFBEA" : "#FEF2F2");
        scoreBadge.setStyle(
            "-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeColor + "; " +
            "-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 6px 14px; -fx-background-radius: 20px;"
        );

        row.getChildren().addAll(avatar, studentInfo, scoreBadge);

        VBox card = new VBox(row);
        card.getStyleClass().add("material-card");
        card.setPadding(new Insets(14, 18, 14, 18));
        return card;
    }

    private VBox buildMiniStatRow(String label, String value, String unit, String color) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setStyle(
            "-fx-background-color: #FAF8F3; -fx-background-radius: 8px; " +
            "-fx-border-color: #E5E0D8; -fx-border-radius: 8px; -fx-border-width: 1px;"
        );

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");

        HBox valBox = new HBox(4);
        valBox.setAlignment(Pos.BASELINE_LEFT);
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label ut = new Label(unit);
        ut.setStyle("-fx-font-size: 12px; -fx-text-fill: #9CA3AF;");
        valBox.getChildren().addAll(val, ut);

        box.getChildren().addAll(lbl, valBox);
        return box;
    }

    public Node buildQuizForm(QuizData quizToEdit) {
        boolean isEdit = (quizToEdit != null);
        VBox root = new VBox(20);
        root.setMaxWidth(Double.MAX_VALUE);

        // Header Box
        VBox headerBox = new VBox(6);
        headerBox.getStyleClass().add("section-box");
        headerBox.setPadding(new Insets(20));

        Label iconLabel = new Label("✨");
        iconLabel.setStyle("-fx-font-size: 32px;");
        Label titleLabel = new Label(isEdit ? "Edit Kuis" : "Buat Kuis Baru");
        titleLabel.getStyleClass().add("section-title");
        Label subLabel = new Label(isEdit 
            ? "Ubah formulir di bawah ini untuk memperbarui kuis bagi siswa." 
            : "Lengkapi formulir di bawah ini untuk membuat kuis baru bagi siswa.");
        subLabel.getStyleClass().add("card-description");
        headerBox.getChildren().addAll(iconLabel, titleLabel, subLabel);

        // Form Card
        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("material-card");
        formCard.setPadding(new Insets(24));

        // Judul Input
        TextField txtTitle = new TextField(isEdit ? quizToEdit.getTitle() : "");
        txtTitle.setPromptText("Contoh: Kuis: Abstract Class & Interface");
        VBox titleBox = controller.createInputField("Judul Kuis", txtTitle);

        // Deskripsi Input
        TextArea txtDesc = new TextArea(isEdit ? quizToEdit.getDesc() : "");
        txtDesc.setPrefHeight(60);
        txtDesc.setWrapText(true);
        txtDesc.setPromptText("Tuliskan deskripsi singkat kuis ini...");
        VBox descBox = controller.createInputField("Deskripsi Singkat", txtDesc);

        // Difficulty ComboBox
        ComboBox<String> cmbDiff = new ComboBox<>();
        cmbDiff.getItems().addAll("Mudah", "Sedang", "Sulit");
        cmbDiff.setValue(isEdit ? quizToEdit.getDifficulty() : "Mudah");
        cmbDiff.setMaxWidth(Double.MAX_VALUE);
        VBox diffBox = controller.createInputField("Tingkat Kesulitan", cmbDiff);

        // Question fields container
        VBox questionsBox = new VBox(12);
        
        HBox countSelectorRow = new HBox(12);
        countSelectorRow.setAlignment(Pos.CENTER_LEFT);
        
        Label lblSelectCount = new Label("Jumlah Soal:");
        lblSelectCount.getStyleClass().add("input-label");
        
        ComboBox<Integer> cmbQuestionCount = new ComboBox<>();
        cmbQuestionCount.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15);
        cmbQuestionCount.setValue(isEdit ? quizToEdit.getQuestions().size() : 3);
        cmbQuestionCount.getStyleClass().add("input-field");
        cmbQuestionCount.setPrefWidth(85);

        Button btnAddQ = new Button("+ Tambah Soal");
        btnAddQ.getStyleClass().addAll("btn-secondary", "btn-small");
        btnAddQ.setStyle("-fx-background-color: #059669; -fx-text-fill: white;");

        Button btnRemoveQ = new Button("- Hapus Soal");
        btnRemoveQ.getStyleClass().addAll("btn-ghost", "btn-small");
        btnRemoveQ.setStyle("-fx-text-fill: #DC2626; -fx-border-color: #DC2626; -fx-border-radius: 4px;");

        countSelectorRow.getChildren().addAll(lblSelectCount, cmbQuestionCount, btnAddQ, btnRemoveQ);

        VBox questionsContainer = new VBox(10);
        List<TextField> questionFields = new java.util.ArrayList<>();

        Runnable updateQuestionFields = () -> {
            int currentCount = questionFields.size();
            questionsContainer.getChildren().clear();
            for (int i = 0; i < currentCount; i++) {
                TextField tf = questionFields.get(i);
                tf.getStyleClass().add("input-field");
                tf.setPromptText("Tulis Soal " + (i + 1) + "...");
                
                Label lblNumber = new Label("Soal " + (i + 1));
                lblNumber.getStyleClass().add("input-label");
                lblNumber.setStyle("-fx-font-size: 12px; -fx-text-fill: #4B5563;");
                
                VBox singleQBox = new VBox(4, lblNumber, tf);
                questionsContainer.getChildren().add(singleQBox);
            }
        };

        // Populate with existing questions or initialize default
        if (isEdit) {
            for (String qText : quizToEdit.getQuestions()) {
                TextField tf = new TextField(qText);
                tf.getStyleClass().add("input-field");
                questionFields.add(tf);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                TextField tf = new TextField();
                tf.getStyleClass().add("input-field");
                questionFields.add(tf);
            }
        }
        updateQuestionFields.run();

        cmbQuestionCount.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            int targetCount = newVal;
            while (questionFields.size() < targetCount) {
                TextField tf = new TextField();
                tf.getStyleClass().add("input-field");
                questionFields.add(tf);
            }
            while (questionFields.size() > targetCount) {
                questionFields.remove(questionFields.size() - 1);
            }
            updateQuestionFields.run();
        });

        btnAddQ.setOnAction(e -> {
            int currentCount = questionFields.size();
            if (currentCount < 15) {
                cmbQuestionCount.setValue(currentCount + 1);
            }
        });

        btnRemoveQ.setOnAction(e -> {
            int currentCount = questionFields.size();
            if (currentCount > 1) {
                cmbQuestionCount.setValue(currentCount - 1);
            }
        });

        questionsBox.getChildren().addAll(countSelectorRow, questionsContainer);

        // Error Msg
        Label errorMsg = new Label();
        errorMsg.getStyleClass().add("error-label");
        errorMsg.setVisible(false);
        errorMsg.setManaged(false);

        // Action Buttons Row
        HBox buttonRow = new HBox(12);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancel = new Button("Batal");
        btnCancel.getStyleClass().addAll("btn-ghost", "btn-medium");
        btnCancel.setOnAction(e -> controller.showQuizContent());

        Button btnSave = new Button(isEdit ? "Simpan Perubahan" : "Simpan Kuis");
        btnSave.getStyleClass().addAll("btn-primary", "btn-medium");
        btnSave.setStyle("-fx-background-color: #059669;"); // colorful success green
        btnSave.setOnAction(e -> {
            String title = txtTitle.getText().trim();
            String desc = txtDesc.getText().trim();
            String difficulty = cmbDiff.getValue();

            if (title.isEmpty()) {
                showFormError(errorMsg, "Judul kuis tidak boleh kosong!");
                return;
            }

            List<String> questionTexts = new java.util.ArrayList<>();
            for (int i = 0; i < questionFields.size(); i++) {
                String qText = questionFields.get(i).getText().trim();
                if (qText.isEmpty()) {
                    showFormError(errorMsg, "Soal " + (i + 1) + " tidak boleh kosong!");
                    return;
                }
                questionTexts.add(qText);
            }

            if (desc.isEmpty()) {
                desc = questionFields.size() + " soal pilihan ganda tentang " + title + ".";
            }

            String color = "#059669";
            if ("Sedang".equals(difficulty)) color = "#D97706";
            else if ("Sulit".equals(difficulty)) color = "#DC2626";

            if (isEdit) {
                // Update in customQuizzes
                for (int i = 0; i < customQuizzes.size(); i++) {
                    if (customQuizzes.get(i).getId() == quizToEdit.getId()) {
                        customQuizzes.set(i, new QuizData(quizToEdit.getId(), title, desc, difficulty, color, questionTexts));
                        break;
                    }
                }
            } else {
                // Save the quiz to customQuizzes list
                customQuizzes.add(new QuizData(nextQuizId++, title, desc, difficulty, color, questionTexts));
            }

            controller.showCustomAlert(
                Alert.AlertType.INFORMATION,
                "Berhasil",
                isEdit ? "Kuis Berhasil Diperbarui!" : "Kuis Baru Berhasil Dibuat!",
                isEdit ? "Kuis '" + title + "' telah diperbarui." : "Kuis '" + title + "' kini sudah dapat diakses oleh seluruh siswa."
            );

            controller.showQuizContent();
        });

        buttonRow.getChildren().addAll(btnCancel, btnSave);
        formCard.getChildren().addAll(titleBox, descBox, diffBox, questionsBox, errorMsg, buttonRow);
        root.getChildren().addAll(headerBox, formCard);
        return root;
    }

    private VBox buildQuizCard(String title, String desc, String difficulty, String color, int questionCount) {
        VBox card = new VBox(12);
        card.getStyleClass().add("material-card");
        card.setPadding(new Insets(20));

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(5);
        dot.setFill(Color.web(color));
        Label diffLabel = new Label(difficulty);
        diffLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        top.getChildren().addAll(dot, diffLabel);

        Label titleL = new Label(title);
        titleL.getStyleClass().add("card-title");
        titleL.setStyle("-fx-font-size: 17px;");

        Label descL = new Label(desc);
        descL.getStyleClass().add("card-description");
        descL.setWrapText(true);

        javafx.scene.control.Separator div = new javafx.scene.control.Separator();

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button startBtn = new Button("Mulai Kuis");
        startBtn.getStyleClass().addAll("btn-primary", "btn-small");
        startBtn.setDisable(false);
        startBtn.setOnAction(e -> {
            startQuizGameplay(title, difficulty);
        });

        Label quizMeta = new Label(questionCount + " Soal  ·  " + ("Mudah".equals(difficulty) ? "5 mnt" : "8 mnt"));
        quizMeta.getStyleClass().add("progress-info");
        quizMeta.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF7A00;");

        Region spc = new Region();
        HBox.setHgrow(spc, Priority.ALWAYS);
        actions.getChildren().addAll(quizMeta, spc, startBtn);

        card.getChildren().addAll(top, titleL, descL, div, actions);
        return card;
    }

    // =====================================================================
    //  QUIZ GAMEPLAY ENGINE (INTERACTIVE SAMPLE)
    // =====================================================================

    private void startQuizGameplay(String title, String difficulty) {
        activeQuizTitle = title;
        currentQuestionIndex = 0;
        correctAnswersCount = 0;
        selectedOptionIndex = -1;

        if (quizTimer != null) {
            quizTimer.stop();
        }

        int minutes = title.contains("Dasar OOP") ? 5 : 8;
        secondsRemaining = minutes * 60;

        activeQuestions = new java.util.ArrayList<>();
        if (title.contains("Dasar OOP")) {
            activeQuestions.add(new QuizQuestion(
                "Manakah yang merupakan pilar utama dalam Object-Oriented Programming (OOP)?",
                new String[]{
                    "Inheritance, Polymorphism, Encapsulation, Abstraction",
                    "Compilation, Interpretation, Execution",
                    "Variables, Loops, Conditions",
                    "HTML, CSS, JavaScript"
                }, 0
            ));
            activeQuestions.add(new QuizQuestion(
                "Apakah fungsi utama dari Encapsulation (Pewadahan) dalam OOP?",
                new String[]{
                    "Membuat variabel global di seluruh program",
                    "Menyembunyikan detail implementasi kelas dan membatasi akses langsung ke data",
                    "Menghubungkan database eksternal dengan sistem lokal",
                    "Mempercepat waktu eksekusi program Java"
                }, 1
            ));
            activeQuestions.add(new QuizQuestion(
                "Pilar OOP yang memungkinkan objek baru mewarisi sifat dari objek induknya adalah...",
                new String[]{
                    "Polymorphism",
                    "Abstraction",
                    "Inheritance",
                    "Encapsulation"
                }, 2
            ));
        } else if (title.contains("Inheritance & Polymorphism")) {
            activeQuestions.add(new QuizQuestion(
                "Kata kunci (keyword) yang digunakan di Java untuk menerapkan pewarisan kelas (inheritance) adalah...",
                new String[]{
                    "implements",
                    "extends",
                    "inherits",
                    "super"
                }, 1
            ));
            activeQuestions.add(new QuizQuestion(
                "Apa yang dimaksud dengan Polymorphism (Polimorfisme) dalam OOP?",
                new String[]{
                    "Kemampuan suatu objek memiliki banyak bentuk/implementasi metode yang berbeda",
                    "Membagi kode menjadi beberapa modul terpisah",
                    "Membuat banyak class dalam satu file",
                    "Proses mengamankan program dari serangan hacker"
                }, 0
            ));
            activeQuestions.add(new QuizQuestion(
                "Keyword 'super' di Java digunakan untuk...",
                new String[]{
                    "Membuat objek baru dari kelas induk",
                    "Mengakses konstruktor, metode, atau variabel dari parent class",
                    "Mengakhiri eksekusi program secara paksa",
                    "Mendeklarasikan konstanta global"
                }, 1
            ));
        } else {
            QuizData foundQuiz = null;
            for (QuizData q : customQuizzes) {
                if (q.getTitle().equals(title)) {
                    foundQuiz = q;
                    break;
                }
            }
            if (foundQuiz != null) {
                for (String qText : foundQuiz.getQuestions()) {
                    activeQuestions.add(new QuizQuestion(
                        qText,
                        new String[]{
                            "Jawaban Benar (Pilihan A)",
                            "Jawaban Salah (Pilihan B)",
                            "Jawaban Salah (Pilihan C)",
                            "Jawaban Salah (Pilihan D)"
                        }, 0
                    ));
                }
            } else {
                activeQuestions.add(new QuizQuestion(
                    "Pertanyaan kuis default?",
                    new String[]{"Pilihan A", "Pilihan B", "Pilihan C", "Pilihan D"},
                    0
                ));
            }
        }

        userAnswers = new int[activeQuestions.size()];
        java.util.Arrays.fill(userAnswers, -1);

        quizTimer = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                secondsRemaining--;
                updateTimerLabelText();
                if (secondsRemaining <= 0) {
                    quizTimer.stop();
                    handleTimeOut();
                }
            })
        );
        quizTimer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        quizTimer.play();

        renderQuizQuestion();
    }

    private void renderQuizQuestion() {
        controller.getContentArea().getChildren().clear();

        QuizQuestion q = activeQuestions.get(currentQuestionIndex);
        selectedOptionIndex = userAnswers[currentQuestionIndex];

        VBox quizBox = new VBox(20);
        quizBox.getStyleClass().add("material-container");
        quizBox.setPadding(new Insets(28));
        quizBox.setMaxWidth(800);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Button prevBtn = new Button("Kembali");
        prevBtn.getStyleClass().addAll("btn-ghost", "btn-medium");
        prevBtn.setStyle(
            "-fx-text-fill: #FF7A00; " +
            "-fx-border-color: #FF7A00; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-width: 1.5px; " +
            "-fx-font-weight: bold;"
        );
        prevBtn.setOnAction(e -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                renderQuizQuestion();
            }
        });
        
        if (currentQuestionIndex == 0) {
            prevBtn.setVisible(false);
            prevBtn.setManaged(false);
        } else {
            prevBtn.setVisible(true);
            prevBtn.setManaged(true);
        }

        Label quizTitleLabel = new Label(activeQuizTitle);
        quizTitleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #6B7280;");

        Region spacerTop = new Region();
        HBox.setHgrow(spacerTop, Priority.ALWAYS);

        timerLabel = new Label();
        timerLabel.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EF4444; " +
            "-fx-background-color: #FEF2F2; -fx-background-radius: 6px; " +
            "-fx-padding: 4px 10px; -fx-border-color: #FCA5A5; " +
            "-fx-border-radius: 6px; -fx-border-width: 1px;"
        );
        updateTimerLabelText();

        Label progressLabel = new Label("Soal " + (currentQuestionIndex + 1) + " dari " + activeQuestions.size());
        progressLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FF7A00;");

        Button closeBtn = new Button();
        closeBtn.getStyleClass().add("btn-back-round");
        javafx.scene.shape.SVGPath closeIcon = new javafx.scene.shape.SVGPath();
        closeIcon.setContent("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z");
        closeIcon.setStyle("-fx-fill: #EF4444;");
        closeBtn.setGraphic(closeIcon);
        closeBtn.setTooltip(new Tooltip("Keluar dari Kuis"));
        closeBtn.setStyle(
            "-fx-background-color: #FFFFFF; -fx-background-radius: 20px; " +
            "-fx-border-color: #E5E0D8; -fx-border-radius: 20px; -fx-border-width: 1.5px; " +
            "-fx-padding: 8px; -fx-cursor: hand;"
        );
        closeBtn.setOnMouseEntered(ev -> {
            closeBtn.setStyle(
                "-fx-background-color: #FEF2F2; -fx-background-radius: 20px; " +
                "-fx-border-color: #EF4444; -fx-border-radius: 20px; -fx-border-width: 1.5px; " +
                "-fx-padding: 8px; -fx-cursor: hand;"
            );
            closeIcon.setStyle("-fx-fill: #DC2626;");
        });
        closeBtn.setOnMouseExited(ev -> {
            closeBtn.setStyle(
                "-fx-background-color: #FFFFFF; -fx-background-radius: 20px; " +
                "-fx-border-color: #E5E0D8; -fx-border-radius: 20px; -fx-border-width: 1.5px; " +
                "-fx-padding: 8px; -fx-cursor: hand;"
            );
            closeIcon.setStyle("-fx-fill: #EF4444;");
        });
        closeBtn.setOnAction(e -> confirmExitQuiz());

        topRow.getChildren().addAll(quizTitleLabel, spacerTop, timerLabel, progressLabel, closeBtn);

        ProgressBar quizProgressBar = new ProgressBar((double) (currentQuestionIndex + 1) / activeQuestions.size());
        quizProgressBar.setMaxWidth(Double.MAX_VALUE);
        quizProgressBar.setPrefHeight(8);
        quizProgressBar.getStyleClass().add("daily-progress");

        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();

        VBox questionBox = new VBox(12);
        Label qNumLabel = new Label("PERTANYAAN " + (currentQuestionIndex + 1));
        qNumLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF; -fx-letter-spacing: 1.5;");
        
        Label qTextLabel = new Label(q.question);
        qTextLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        qTextLabel.setWrapText(true);
        qTextLabel.setMaxWidth(700);

        questionBox.getChildren().addAll(qNumLabel, qTextLabel);

        VBox optionsContainer = new VBox(12);
        optionsContainer.setMaxWidth(700);

        VBox[] optionCards = new VBox[q.options.length];
        Button nextBtn = new Button(currentQuestionIndex == activeQuestions.size() - 1 ? "Kirim Jawaban" : "Selanjutnya");
        nextBtn.getStyleClass().addAll("btn-primary", "btn-medium");
        nextBtn.setDisable(selectedOptionIndex == -1);

        char optLetter = 'A';
        for (int i = 0; i < q.options.length; i++) {
            final int idx = i;
            String optionText = q.options[i];

            HBox cardContent = new HBox(14);
            cardContent.setAlignment(Pos.CENTER_LEFT);

            Label letterBadge = new Label(String.valueOf((char)(optLetter + i)));
            boolean isSelected = (selectedOptionIndex == idx);
            letterBadge.setStyle(isSelected ? 
                "-fx-background-color: #FF7A00; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13px; " +
                "-fx-min-width: 28px; -fx-min-height: 28px; " +
                "-fx-max-width: 28px; -fx-max-height: 28px; " +
                "-fx-alignment: center; " +
                "-fx-background-radius: 50%;"
                :
                "-fx-background-color: #FAF8F3; " +
                "-fx-text-fill: #6B7280; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13px; " +
                "-fx-min-width: 28px; -fx-min-height: 28px; " +
                "-fx-max-width: 28px; -fx-max-height: 28px; " +
                "-fx-alignment: center; " +
                "-fx-background-radius: 50%;" +
                "-fx-border-color: #E5E0D8; -fx-border-radius: 50%; -fx-border-width: 1px;"
            );

            Label optionLabel = new Label(optionText);
            optionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");
            optionLabel.setWrapText(true);
            HBox.setHgrow(optionLabel, Priority.ALWAYS);

            cardContent.getChildren().addAll(letterBadge, optionLabel);

            VBox card = new VBox(cardContent);
            card.setPadding(new Insets(14, 18, 14, 18));
            
            String baseStyle = 
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: #E5E0D8; " +
                "-fx-border-radius: 12px; " +
                "-fx-border-width: 1.5px; " +
                "-fx-cursor: hand;";
            String selectedStyle = 
                "-fx-background-color: #FFF0E0; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: #FF7A00; " +
                "-fx-border-radius: 12px; " +
                "-fx-border-width: 1.5px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 122, 0, 0.1), 6, 0, 0, 1);";
            card.setStyle(isSelected ? selectedStyle : baseStyle);

            card.setOnMouseEntered(ev -> {
                if (selectedOptionIndex != idx) {
                    card.setStyle(
                        "-fx-background-color: #FAF8F3; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-color: #D5CFC7; " +
                        "-fx-border-radius: 12px; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-cursor: hand;"
                    );
                }
            });
            card.setOnMouseExited(ev -> {
                if (selectedOptionIndex != idx) {
                    card.setStyle(baseStyle);
                }
            });

            card.setOnMouseClicked(ev -> {
                selectedOptionIndex = idx;
                userAnswers[currentQuestionIndex] = idx;
                nextBtn.setDisable(false);

                for (int j = 0; j < optionCards.length; j++) {
                    if (j == idx) {
                        optionCards[j].setStyle(selectedStyle);
                        letterBadge.setStyle(
                            "-fx-background-color: #FF7A00; " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 13px; " +
                            "-fx-min-width: 28px; -fx-min-height: 28px; " +
                            "-fx-max-width: 28px; -fx-max-height: 28px; " +
                            "-fx-alignment: center; " +
                            "-fx-background-radius: 50%;"
                        );
                    } else {
                        optionCards[j].setStyle(baseStyle);
                        Label otherLetterBadge = (Label)((HBox)optionCards[j].getChildren().get(0)).getChildren().get(0);
                        otherLetterBadge.setStyle(
                            "-fx-background-color: #FAF8F3; " +
                            "-fx-text-fill: #6B7280; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 13px; " +
                            "-fx-min-width: 28px; -fx-min-height: 28px; " +
                            "-fx-max-width: 28px; -fx-max-height: 28px; " +
                            "-fx-alignment: center; " +
                            "-fx-background-radius: 50%;" +
                            "-fx-border-color: #E5E0D8; -fx-border-radius: 50%; -fx-border-width: 1px;"
                        );
                    }
                }
            });

            optionCards[i] = card;
            optionsContainer.getChildren().add(card);
        }

        HBox actionRow = new HBox();
        actionRow.setAlignment(Pos.CENTER_LEFT);
        actionRow.setMaxWidth(Double.MAX_VALUE);

        prevBtn.setOnAction(e -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                renderQuizQuestion();
            }
        });
        
        Region spacerBottom = new Region();
        HBox.setHgrow(spacerBottom, Priority.ALWAYS);

        nextBtn.setOnAction(e -> handleNextQuestion());

        actionRow.getChildren().addAll(prevBtn, spacerBottom, nextBtn);

        quizBox.getChildren().addAll(topRow, quizProgressBar, separator, questionBox, optionsContainer, actionRow);
        controller.getContentArea().getChildren().add(quizBox);
        controller.getContentTitleLabel().setText("Pengerjaan Kuis");
    }

    private void handleNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < activeQuestions.size()) {
            renderQuizQuestion();
        } else {
            if (quizTimer != null) {
                quizTimer.stop();
            }
            renderQuizResult();
        }
    }

    private void updateTimerLabelText() {
        if (timerLabel == null) return;
        int mins = secondsRemaining / 60;
        int secs = secondsRemaining % 60;
        timerLabel.setText(String.format("⏱ %02d:%02d", mins, secs));
    }

    private void handleTimeOut() {
        javafx.application.Platform.runLater(() -> {
            controller.showCustomAlert(
                Alert.AlertType.WARNING,
                "Waktu Habis",
                "Waktu Pengerjaan Kuis Telah Habis!",
                "Kuis Anda akan otomatis dikirimkan berdasarkan jawaban yang sudah tersimpan."
            );
            renderQuizResult();
        });
    }

    private void confirmExitQuiz() {
        ButtonType response = controller.showCustomAlert(
            Alert.AlertType.CONFIRMATION,
            "Konfirmasi Keluar Kuis",
            "Keluar dari Pengerjaan Kuis?",
            "Semua jawaban Anda pada kuis ini akan hilang. Apakah Anda yakin?"
        );
        if (response == ButtonType.OK) {
            if (quizTimer != null) {
                quizTimer.stop();
            }
            controller.showQuizContent();
        }
    }

    private void renderQuizResult() {
        if (quizTimer != null) {
            quizTimer.stop();
        }

        controller.getContentArea().getChildren().clear();

        VBox resultBox = new VBox(24);
        resultBox.getStyleClass().add("material-container");
        resultBox.setPadding(new Insets(32));
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setMaxWidth(600);

        ImageView celebrationMascot = null;
        try {
            celebrationMascot = new ImageView(
                new Image(getClass().getResourceAsStream("/com/doamamah/edutrack/fe/images/mascot_face.png"))
            );
            celebrationMascot.setFitWidth(90);
            celebrationMascot.setPreserveRatio(true);
            celebrationMascot.setSmooth(true);
        } catch (Exception ex) {
            System.err.println("Gagal memuat gambar maskot perayaan: " + ex.getMessage());
        }

        correctAnswersCount = 0;
        for (int i = 0; i < activeQuestions.size(); i++) {
            if (userAnswers[i] == activeQuestions.get(i).correctIndex) {
                correctAnswersCount++;
            }
        }

        int score = (int) Math.round((double) correctAnswersCount * 100.0 / activeQuestions.size());

        Label congratLabel = new Label(score >= 60 ? "Selamat! Kuis Selesai!" : "Kuis Selesai!");
        congratLabel.getStyleClass().add("banner-title");
        congratLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #FF7A00;");

        VBox scoreBox = new VBox(4);
        scoreBox.setAlignment(Pos.CENTER);
        Label scoreTitle = new Label("SKOR AKHIR KAMU");
        scoreTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF; -fx-letter-spacing: 1.5;");

        Label scoreLabel = new Label(score + " / 100");
        scoreLabel.setStyle("-fx-font-size: 44px; -fx-font-weight: bold; -fx-text-fill: " + (score >= 60 ? "#059669" : "#D97706") + ";");
        scoreBox.getChildren().addAll(scoreTitle, scoreLabel);

        ProgressBar scoreBar = new ProgressBar((double) score / 100.0);
        scoreBar.setPrefWidth(300);
        scoreBar.setPrefHeight(10);
        scoreBar.getStyleClass().add("daily-progress");
        scoreBar.setStyle("-fx-accent: " + (score >= 60 ? "#059669" : "#D97706") + ";");

        Label commentLabel = new Label();
        commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-text-alignment: center;");
        commentLabel.setWrapText(true);
        commentLabel.setMaxWidth(400);

        if (score == 100) {
            commentLabel.setText("Luar biasa sempurna! Kamu telah menguasai seluruh konsep materi ini dengan sangat matang. Pertahankan prestasimu!");
        } else if (score >= 60) {
            commentLabel.setText("Kerja bagus! Pemahaman konsep Anda sudah cukup baik. Pelajari sedikit lagi untuk meraih nilai sempurna pada percobaan berikutnya!");
        } else {
            commentLabel.setText("Terus berusaha! Baca kembali materi e-learning dan coba kuis ini sekali lagi untuk memperkuat pemahaman Anda.");
        }

        Button backBtn = new Button("Kembali ke Halaman Kuis");
        backBtn.getStyleClass().addAll("btn-primary", "btn-large");
        backBtn.setOnAction(e -> controller.showQuizContent());

        if (celebrationMascot != null) {
            resultBox.getChildren().addAll(celebrationMascot, congratLabel, scoreBox, scoreBar, commentLabel, backBtn);
        } else {
            resultBox.getChildren().addAll(congratLabel, scoreBox, scoreBar, commentLabel, backBtn);
        }

        controller.getContentArea().getChildren().add(resultBox);
        controller.getContentTitleLabel().setText("Evaluasi Kuis");
    }

    private void showFormError(Label lbl, String msg) {
        lbl.setText("⚠ " + msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }
}
