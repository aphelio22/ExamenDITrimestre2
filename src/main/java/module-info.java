module com.example.examenditrimestre2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.swing;
    requires jasperreports;


    opens com.example.examenditrimestre2 to javafx.fxml;
    exports com.example.examenditrimestre2;
}