package com.example.examenditrimestre2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TextField tfEdad;
    @FXML
    private TextField tfEstatura;
    @FXML
    private TextField tfPeso;
    @FXML
    private RadioButton rbMujer;
    @FXML
    private RadioButton rbHombre;
    @FXML
    private ChoiceBox chbActividad;

    private ToggleGroup grupoGenero = new ToggleGroup();
    private ToggleGroup grupoPeso = new ToggleGroup();
    @FXML
    private Label lbCalculo;
    @FXML
    private TextField tfNombre;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rbHombre.setToggleGroup(grupoGenero);
        rbMujer.setToggleGroup(grupoGenero);

        ObservableList<String> actividad = FXCollections.observableArrayList();
        actividad.addAll("Sedentario", "Moderado", "Activo", "Muy Activo");

        chbActividad.setItems(actividad);
        chbActividad.getSelectionModel().selectFirst();
    }

    @FXML
    public void calcular(ActionEvent actionEvent) {
        if (tfNombre.getText().isEmpty() || tfEdad.getText().isEmpty() || tfEstatura.getText().isEmpty() || tfPeso.getText().isEmpty()) {
            lbCalculo.setText("Debes rellenar todos los campos.");
            lbCalculo.setStyle("-fx-background-color: RED;");
        } else {
        String nombreCliente = tfNombre.getText();

        Integer edad = Integer.parseInt(tfEdad.getText());
        Double estatura = Double.parseDouble(tfEstatura.getText());
        Double peso = Double.parseDouble(tfPeso.getText());

        if ((edad > 0 && edad <= 100) && (estatura > 0.0 && estatura < 250.0) && (peso > 0.0 && peso < 250.0)) {

            Double ger;
            Double get = 0.0;
            if (rbHombre.isSelected()) {
                ger = (66.473 + 13.751 * peso) + (5.0033 * estatura) - (6.755 * edad);
            } else {
                ger = (655.0955 + 9.463 * peso) + (1.8496 * estatura) - (4.6756 * edad);
            }

            if (rbHombre.isSelected() && chbActividad.getValue().equals("Sedentario")) {
                get = ger * 1.3;
            } else if (rbHombre.isSelected() && chbActividad.getValue().equals("Moderado")) {
                get = ger * 1.6;
            } else if (rbHombre.isSelected() && chbActividad.getValue().equals("Activo")) {
                get = ger * 1.7;
            } else if (rbHombre.isSelected() && chbActividad.getValue().equals("Muy Activo")) {
                get = ger * 2.1;
            } else if (rbMujer.isSelected() && chbActividad.getValue().equals("Sedentario")) {
                get = ger * 1.3;
            } else if (rbMujer.isSelected() && chbActividad.getValue().equals("Moderado")) {
                get = ger * 1.5;
            } else if (rbMujer.isSelected() && chbActividad.getValue().equals("Activo")) {
                get = ger * 1.6;
            } else if (rbMujer.isSelected() && chbActividad.getValue().equals("Muy Activo")) {
                get = ger * 1.9;
            }

            Long gerRedondeado = Math.round(ger);
            Long getRedondeado = Math.round(get);

            lbCalculo.setText("El cliente " + nombreCliente + " tiene un GER de " + gerRedondeado + " y un GET de " + getRedondeado);
            lbCalculo.setStyle("-fx-background-color: #00FF00;");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("La edad, la estatura y el peso deben ser positivas.");
            alert.showAndWait();
        }
        }
    }




    @FXML
    public void descargar(ActionEvent actionEvent) {
        Stage primaryStage = new Stage();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/informacioncliente", "root", "Enterprise1701Voyager74656");
            JasperPrint jasperPrint = JasperFillManager.fillReport("InformacionCliente.jasper",null, connection);
            SwingNode swingNode = new SwingNode();
            contenidoEnSwing(swingNode, jasperPrint);

            StackPane root = new StackPane();
            root.getChildren().add(swingNode);
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Detalles Clientes");
            primaryStage.setScene(scene);
            primaryStage.show();

            JRPdfExporter exp = new JRPdfExporter();
            exp.setExporterInput(new SimpleExporterInput(jasperPrint));
            exp.setExporterOutput(new SimpleOutputStreamExporterOutput("informeCLiente.pdf"));
            exp.setConfiguration(new SimplePdfExporterConfiguration());
            exp.exportReport();
        } catch (SQLException e) { //Manejo de excepciones.
            throw new RuntimeException(e);
        } catch (JRException e) { //Manejo de excepciones.
            throw new RuntimeException(e);
        }
    }
    private void contenidoEnSwing(final SwingNode swingNode, JasperPrint jasperPrint) {
        SwingUtilities.invokeLater(() -> {
            JRViewer viewer = new JRViewer(jasperPrint);
            swingNode.setContent(viewer);
        });
    }
}