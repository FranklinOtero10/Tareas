package com.example.tareas.controlador;

import com.example.tareas.modelo.Tarea;
import com.example.tareas.servicio.TareaServicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.*;

import javax.swing.text.TabableView;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class IndexControlador implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    @Autowired
    private TareaServicio tareaServicio;

    @FXML
    private TableView<Tarea> tareaTabla;

    @FXML
    private TableColumn<Tarea, Integer> idTareaColumna;

    @FXML
    private TableColumn<Tarea, String> tareaColumna;

    @FXML
    private TableColumn<Tarea, String> responsableColumna;

    @FXML
    private TableColumn<Tarea, String> estatusColumna;

    @FXML
    private TextField textoTarea;

    @FXML
    private TextField textoResponsable;

    @FXML
    private TextField textoEstatus;

    private Integer idTareaInterno;

    private final ObservableList<Tarea> tareaList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        configurarColumnas();
        listarTareas();
    }

    private void configurarColumnas() {
        idTareaColumna.setCellValueFactory( new PropertyValueFactory<>("idTarea"));
        tareaColumna.setCellValueFactory( new PropertyValueFactory<>("nombreTarea"));
        responsableColumna.setCellValueFactory( new PropertyValueFactory<>("responsable"));
        estatusColumna.setCellValueFactory( new PropertyValueFactory<>("estatus"));
    }

    private void listarTareas() {
        logger.info("Ejecutando listado de tareas");
        tareaList.clear();
        tareaList.addAll(tareaServicio.listarTareas());
        tareaTabla.setItems(tareaList);

    }

    public void agregarTarea(){
        if (textoTarea.getText().isEmpty()){
            mostrarMensaje("Error!", "Debe escribir una tarea");
            textoTarea.requestFocus();
            return;
        }
        else {
            var tarea = new Tarea();
            getDataForm(tarea);
            tarea.setIdTarea(null);
            tareaServicio.guardarTarea(tarea);
            mostrarMensaje("Info...", "Tarea guardada!");
            clearForm();
            listarTareas();
        }

    }

    public void cargarTareaForm(){
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if (tarea != null) {
            idTareaInterno = tarea.getIdTarea();
            textoTarea.setText(tarea.getNombreTarea());
            textoResponsable.setText(tarea.getResponsable());
            textoEstatus.setText(tarea.getEstatus());
        }
    }

    public void modificarTarea(){
        if (idTareaInterno == null) {
            mostrarMensaje("Informacion...", "Selecciona una tarea");
            return;
        }

        if (textoTarea.getText().isEmpty()) {
            mostrarMensaje("Informacion...", "Escriba una tarea");
            return;
        }

        var tarea = new Tarea();
        getDataForm(tarea);
        tareaServicio.guardarTarea(tarea);
        mostrarMensaje("Info...", "Tarea modificada");
        clearForm();
        listarTareas();
    }

    public void eliminarTarea(){
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if (tarea != null) {
            logger.info("Registro a eliminar" + tarea.toString());
            tareaServicio.eliminarTarea(tarea);
            mostrarMensaje("Info...", "Tarea eliminada: " + tarea.getNombreTarea().toString());
            clearForm();
            listarTareas();
        }
        else {
            mostrarMensaje("Info...", "Seleccione la tarea a elminar");
        }
    }

    private void getDataForm(Tarea tarea) {
        if (idTareaInterno != null) {
            tarea.setIdTarea(idTareaInterno);
            tarea.setNombreTarea(textoTarea.getText());
            tarea.setResponsable(textoResponsable.getText());
            tarea.setEstatus(textoEstatus.getText());
        }
    }

    public void clearForm() {
        idTareaInterno = null;
        textoTarea.clear();
        textoResponsable.clear();
        textoEstatus.clear();
    }

    private void mostrarMensaje(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
