package com.doamamah.edutrack.fe.controller;

import com.doamamah.edutrack.fe.model.Student;
import com.doamamah.edutrack.fe.model.Teacher;
import com.doamamah.edutrack.fe.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardHomeView {

    private final DashboardController controller;

    public DashboardHomeView(DashboardController controller) {
        this.controller = controller;
    }

    public Node buildContent() {
        User currentUser = controller.getCurrentUser();
        HBox mainLayout = new HBox(20);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        // --- KOLOM KIRI (Utama) ---
        VBox leftColumn = new VBox(20);
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // Welcome Banner (HBox)
        HBox banner = new HBox(20);
        banner.getStyleClass().add("dashboard-banner");
        banner.setPadding(new Insets(12, 28, 12, 28));
        banner.setAlignment(Pos.CENTER_LEFT);

        VBox bannerText = new VBox(6);
        String greetTime = getGreetingByTime();
        String roleName = (currentUser instanceof Teacher) ? "Pengajar" : "Siswa";
        String firstName = currentUser.getFullName() != null
                ? currentUser.getFullName().split(" ")[0] : "Pengguna";

        Label welcomeLabel = new Label(greetTime + ", " + firstName + "!");
        welcomeLabel.getStyleClass().add("banner-title");

        String todayStr = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.of("id", "ID")));
        Label dateLabel = new Label(todayStr + "  ·  " + roleName);
        dateLabel.getStyleClass().add("banner-subtitle");

        bannerText.getChildren().addAll(welcomeLabel, dateLabel);
        HBox.setHgrow(bannerText, Priority.ALWAYS);

        try {
            ImageView mascotView = new ImageView(
                new Image(getClass().getResourceAsStream("/com/doamamah/edutrack/fe/images/dashboard_mascot.png"))
            );
            mascotView.setFitHeight(115);
            mascotView.setPreserveRatio(true);
            mascotView.setSmooth(true);
            banner.getChildren().addAll(bannerText, mascotView);
        } catch (Exception e) {
            System.err.println("Gagal memuat maskot dashboard: " + e.getMessage());
            banner.getChildren().add(bannerText);
        }

        // Statistik Grid 3 kolom
        HBox statsRow = new HBox(14);
        statsRow.setMaxWidth(Double.MAX_VALUE);
        int totalMaterials = controller.getMaterialService().getAllMaterials().size();

        if (currentUser instanceof Student) {
            statsRow.getChildren().addAll(
                buildRichStatCard("Materi Tersedia", String.valueOf(totalMaterials), "materi", "#FF7A00", "📚", 1.0),
                buildRichStatCard("Kuis Aktif",      "2", "kuis",   "#059669", "📝", 0.5),
                buildRichStatCard("Progress Belajar","65", "%",      "#D97706", "🎯", 0.65)
            );
        } else {
            statsRow.getChildren().addAll(
                buildRichStatCard("Total Materi",  String.valueOf(totalMaterials),  "materi", "#FF7A00", "📚", 1.0),
                buildRichStatCard("Total Siswa",   "24", "siswa",  "#059669", "👥", 0.7),
                buildRichStatCard("Kuis Dibuat",   "3",  "kuis",   "#D97706", "📝", 0.6)
            );
        }

        // Progress Belajar Hari Ini
        VBox progressSection = new VBox(10);
        progressSection.getStyleClass().add("section-box");
        progressSection.setPadding(new Insets(20));

        Label progressTitle = new Label("Progress Hari Ini");
        progressTitle.getStyleClass().add("section-title");

        ProgressBar dailyBar = new ProgressBar(0.65);
        dailyBar.setMaxWidth(Double.MAX_VALUE);
        dailyBar.setPrefHeight(10);
        dailyBar.getStyleClass().add("daily-progress");

        HBox progressInfo = new HBox();
        progressInfo.setAlignment(Pos.CENTER_LEFT);
        Label pLeft = new Label("3 dari 5 materi selesai");
        pLeft.getStyleClass().add("progress-info");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pRight = new Label("65%");
        pRight.getStyleClass().add("progress-percent");
        progressInfo.getChildren().addAll(pLeft, spacer, pRight);

        progressSection.getChildren().addAll(progressTitle, dailyBar, progressInfo);
        leftColumn.getChildren().addAll(banner, statsRow, progressSection);

        // --- KOLOM KANAN (Aksi Cepat & Tips) ---
        VBox rightColumn = new VBox(20);
        rightColumn.setPrefWidth(320);
        rightColumn.setMaxWidth(320);

        // Quick Actions Card
        VBox actionsCard = new VBox(14);
        actionsCard.getStyleClass().add("section-box");
        actionsCard.setPadding(new Insets(20));

        Label actionTitle = new Label("Aksi Cepat");
        actionTitle.getStyleClass().add("section-title");

        Button goMaterials = new Button("Lihat Materi");
        goMaterials.getStyleClass().addAll("btn-primary", "btn-medium");
        goMaterials.setMaxWidth(Double.MAX_VALUE);
        goMaterials.setOnAction(e -> controller.showMaterialsContent());

        Button goQuiz = new Button("Ikuti Kuis");
        goQuiz.getStyleClass().addAll("btn-secondary", "btn-medium");
        goQuiz.setMaxWidth(Double.MAX_VALUE);
        goQuiz.setOnAction(e -> controller.showQuizContent());

        actionsCard.getChildren().addAll(actionTitle, goMaterials, goQuiz);

        // Tips Box
        HBox tipsBox = new HBox(12);
        tipsBox.getStyleClass().add("tips-box");
        tipsBox.setPadding(new Insets(16));
        tipsBox.setAlignment(Pos.CENTER_LEFT);

        Label bulb = new Label("💡");
        bulb.setStyle("-fx-font-size: 28px;");

        VBox tipContent = new VBox(4);
        Label tipTitle = new Label("Tips Belajar Efektif");
        tipTitle.getStyleClass().add("tip-title");
        Label tipText = new Label("Buat catatan singkat setelah selesai menonton video. Tulis 3 hal baru!");
        tipText.getStyleClass().add("tip-text");
        tipText.setWrapText(true);
        tipContent.getChildren().addAll(tipTitle, tipText);
        HBox.setHgrow(tipContent, Priority.ALWAYS);

        tipsBox.getChildren().addAll(bulb, tipContent);
        rightColumn.getChildren().addAll(actionsCard, tipsBox);

        mainLayout.getChildren().addAll(leftColumn, rightColumn);
        return mainLayout;
    }

    private VBox buildRichStatCard(String label, String value, String unit,
                                    String accentColor, String icon, double progress) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Row atas: icon + label
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");
        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("stat-label");
        topRow.getChildren().addAll(iconLabel, nameLabel);

        // Angka besar
        HBox valRow = new HBox(4);
        valRow.setAlignment(Pos.BASELINE_LEFT);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        Label unitLabel = new Label(unit);
        unitLabel.getStyleClass().add("stat-unit");
        valRow.getChildren().addAll(valueLabel, unitLabel);

        // Mini progress bar
        ProgressBar miniBar = new ProgressBar(progress);
        miniBar.setMaxWidth(Double.MAX_VALUE);
        miniBar.setPrefHeight(6);
        miniBar.getStyleClass().add("mini-progress");
        miniBar.setStyle("-fx-accent: " + accentColor + ";");

        card.getChildren().addAll(topRow, valRow, miniBar);
        return card;
    }

    private String getGreetingByTime() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 11)  return "Selamat Pagi";
        if (hour < 15)  return "Selamat Siang";
        if (hour < 18)  return "Selamat Sore";
        return "Selamat Malam";
    }
}
