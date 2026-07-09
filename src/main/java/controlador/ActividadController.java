package controlador;

import catalogo.Estado;
import catalogo.Prioridad;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import modelo.Actividad;
import modelo.Recordatorio;
import modelo.Tarea;
import modelo.Usuario;
import sistema.DatosActividad;
import sistema.GestorActividades;

public class ActividadController {
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Parent root;

    @FXML private Label lblTotalActividades;
    @FXML private Label lblTotalTareas;
    @FXML private Label lblTotalRecordatorios;
    @FXML private Label lblTotalCompartidas;

    @FXML private TextField txtTituloTarea;
    @FXML private TextField txtDescripcionTarea;
    @FXML private TextField txtEstadoTarea;
    @FXML private TextField txtIdTarea;
    @FXML private TextField txtIdEliminarTarea;
    @FXML private TextField txtIdCompartirTarea;
    @FXML private TextField txtCorreoDestinoTarea;
    @FXML private ComboBox<Prioridad> cmbPrioridadTarea;
    @FXML private ComboBox<Estado> cmbEstadoTarea;

    @FXML private TextField txtTituloRecordatorio;
    @FXML private TextField txtDescripcionRecordatorio;
    @FXML private TextField txtIdRecordatorio;
    @FXML private TextField txtIdEliminarRecordatorio;
    @FXML private TextField txtIdCompartirRecordatorio;
    @FXML private TextField txtCorreoDestinoRecordatorio;
    @FXML private TextField txtHoraRecordatorio;
    @FXML private ComboBox<Prioridad> cmbPrioridadRecordatorio;
    @FXML private DatePicker dpFechaRecordatorio;

    @FXML private ComboBox<Estado> cmbEstadoFiltro;

    @FXML private ListView<String> listTareas;
    @FXML private ListView<String> listRecordatorios;
    @FXML private ListView<String> listTareasCompartidas;
    @FXML private ListView<String> listRecordatoriosCompartidos;
    @FXML private ListView<String> listTareasOrdenadasPrioridad;
    @FXML private ListView<String> listRecordatoriosOrdenadosPrioridad;
    @FXML private ListView<String> listTareasCanceladas;
    @FXML private ListView<String> listTareasCompletadas;
    @FXML private ListView<String> listTareasEnProgreso;
    @FXML private ListView<String> listTareasPendientes;
    @FXML private ListView<String> listRecordatoriosOrdenadosFecha;
    @FXML private ListView<String> listNotificaciones;
    @FXML private Label lblTotalActividadesResumen;
    @FXML private Label lblTotalTareasResumen;
    @FXML private Label lblTotalRecordatoriosResumen;
    @FXML private Label lblTotalCompartidasResumen;

    private final AppState appState = AppState.getInstance();
    private final List<Recordatorio> notificacionesActuales = new ArrayList<>();

    @FXML
    private void accionDesdeBoton(ActionEvent event) {
        if (event.getSource() instanceof Node node && event.getSource() instanceof Labeled labeled) {
            ejecutarAccion(node, labeled.getText());
        }
    }

    @FXML
    private void initialize() {
        cargarCombos();
        configurarCampoHora(txtHoraRecordatorio);
        enlazarAcciones(root);
        configurarColoresPrioridad();
        Platform.runLater(this::cargarDatosDePantalla);
    }

    @FXML
    private void CrearActividad(MouseEvent event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/CrearActividad.fxml");
    }

    @FXML private void abrirCrearTarea(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/CrearTarea.fxml");
    }

    @FXML private void abrirCrearRecordatorio(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/CrearRecordatorio.fxml");
    }

    @FXML private void abrirEditarTarea(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/EditarTarea.fxml");
    }

    @FXML private void abrirEditarRecordatorio(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/EditarRecordatorio.fxml");
    }

    @FXML private void abrirEliminarTarea(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/EliminarTarea.fxml");
    }

    @FXML private void abrirEliminarRecordatorio(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/EliminarRecordatorio.fxml");
    }

    @FXML private void abrirCompartirTarea(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/CompartirTarea.fxml");
    }

    @FXML private void abrirCompartirRecordatorio(Event event) {
        abrir((Node) event.getSource(), "/FXML/Actividad/CompartirRecordatorio.fxml");
    }

    @FXML private void crearTarea(Event event) {
        crearTarea();
    }

    @FXML private void crearRecordatorio(Event event) {
        crearRecordatorio();
    }

    @FXML private void editarTarea(Event event) {
        editarTarea();
    }

    @FXML private void editarRecordatorio(Event event) {
        editarRecordatorio();
    }

    @FXML private void eliminarTarea(Event event) {
        eliminarActividad(txtIdEliminarTarea);
    }

    @FXML private void eliminarRecordatorio(Event event) {
        eliminarActividad(txtIdEliminarRecordatorio);
    }

    @FXML private void compartirTarea(Event event) {
        compartirActividad(txtIdCompartirTarea, txtCorreoDestinoTarea);
    }

    @FXML private void compartirRecordatorio(Event event) {
        compartirActividad(txtIdCompartirRecordatorio, txtCorreoDestinoRecordatorio);
    }

    @FXML private void filtrarTareaPorEstado(Event event) {
        filtrarPorEstado();
    }

    private void cargarCombos() {
        cargarComboPrioridad(cmbPrioridadTarea);
        cargarComboPrioridad(cmbPrioridadRecordatorio);
        cargarComboEstado(cmbEstadoTarea);
        cargarComboEstado(cmbEstadoFiltro);
        if (txtEstadoTarea != null) {
            txtEstadoTarea.setText(Estado.PENDIENTE.name());
        }
    }

    private void cargarComboPrioridad(ComboBox<Prioridad> combo) {
        if (combo != null) {
            combo.setItems(FXCollections.observableArrayList(Prioridad.values()));
        }
    }

    private void cargarComboEstado(ComboBox<Estado> combo) {
        if (combo != null) {
            combo.setItems(FXCollections.observableArrayList(Estado.values()));
        }
    }

    private void configurarCampoHora(TextField campoHora) {
        if (campoHora == null) {
            return;
        }

        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.isEmpty()) {
                return change;
            }
            if (!nuevoTexto.matches("\\d{0,2}(:\\d{0,2})?")) {
                return null;
            }
            return nuevoTexto.length() <= 5 ? change : null;
        };

        campoHora.setPromptText("08:30");
        campoHora.setTextFormatter(new TextFormatter<>(filtro));
        if (campoHora.getText() == null || campoHora.getText().isBlank()) {
            campoHora.setText(horaSugerida());
        }
    }

    private String horaSugerida() {
        LocalTime sugerida = LocalTime.now().plusMinutes(5L).withSecond(0).withNano(0);
        return sugerida.format(HOUR_FORMATTER);
    }

    private LocalDateTime leerFechaHoraRecordatorio() {
        if (dpFechaRecordatorio == null || dpFechaRecordatorio.getValue() == null || textoVacio(txtHoraRecordatorio)) {
            return null;
        }
        try {
            LocalTime hora = LocalTime.parse(txtHoraRecordatorio.getText().trim(), HOUR_FORMATTER);
            txtHoraRecordatorio.setText(hora.format(HOUR_FORMATTER));
            return LocalDateTime.of(dpFechaRecordatorio.getValue(), hora);
        } catch (DateTimeParseException e) {
            Alertas.error("Hora invalida", "Ingrese la hora con formato HH:mm, por ejemplo 08:30 o 17:45.");
            return null;
        }
    }

    private String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(DATE_TIME_FORMATTER);
    }

    private void enlazarAcciones(Node node) {
        if (node instanceof Button button) {
            button.setOnAction(event -> ejecutarAccion(button, button.getText()));
        }
        if (node instanceof Label label) {
            label.setOnMouseClicked(event -> ejecutarAccion(label, label.getText()));
        }
        if (node instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            enlazarAcciones(scrollPane.getContent());
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                enlazarAcciones(child);
            }
        }
    }

    private void ejecutarAccion(Node source, String texto) {
        String accion = normalizarAccion(texto);
        switch (accion) {
            case "inicio" -> abrir(source, "/FXML/Actividad/Menu.fxml");
            case "crear actividad" -> abrir(source, "/FXML/Actividad/CrearActividad.fxml");
            case "crear tarea" -> {
                if (txtTituloTarea == null) {
                    abrir(source, "/FXML/Actividad/CrearTarea.fxml");
                } else {
                    crearTarea();
                }
            }
            case "crear recordatorio" -> {
                if (txtTituloRecordatorio == null) {
                    abrir(source, "/FXML/Actividad/CrearRecordatorio.fxml");
                } else {
                    crearRecordatorio();
                }
            }
            case "editar actividad" -> abrir(source, "/FXML/Actividad/EditarActividad.fxml");
            case "editar tarea" -> {
                if (txtIdTarea == null) {
                    abrir(source, "/FXML/Actividad/EditarTarea.fxml");
                } else {
                    editarTarea();
                }
            }
            case "editar recordatorio" -> {
                if (txtIdRecordatorio == null) {
                    abrir(source, "/FXML/Actividad/EditarRecordatorio.fxml");
                } else {
                    editarRecordatorio();
                }
            }
            case "compartir actividad" -> abrir(source, "/FXML/Actividad/CompartirActividad.fxml");
            case "compartir tarea" -> {
                if (txtIdCompartirTarea == null) {
                    abrir(source, "/FXML/Actividad/CompartirTarea.fxml");
                } else {
                    compartirActividad(txtIdCompartirTarea, txtCorreoDestinoTarea);
                }
            }
            case "compartir recordatorio" -> {
                if (txtIdCompartirRecordatorio == null) {
                    abrir(source, "/FXML/Actividad/CompartirRecordatorio.fxml");
                } else {
                    compartirActividad(txtIdCompartirRecordatorio, txtCorreoDestinoRecordatorio);
                }
            }
            case "eliminar actividad" -> abrir(source, "/FXML/Actividad/EliminarActividad.fxml");
            case "eliminar tarea" -> {
                if (txtIdEliminarTarea == null) {
                    abrir(source, "/FXML/Actividad/EliminarTarea.fxml");
                } else {
                    eliminarActividad(txtIdEliminarTarea);
                }
            }
            case "eliminar recordatorio" -> {
                if (txtIdEliminarRecordatorio == null) {
                    abrir(source, "/FXML/Actividad/EliminarRecordatorio.fxml");
                } else {
                    eliminarActividad(txtIdEliminarRecordatorio);
                }
            }
            case "mostrar actividad" -> abrir(source, "/FXML/Actividad/MostrarActividad(ListView).fxml");
            case "filtrar tarea por estado", "aceptar" -> {
                if (cmbEstadoFiltro == null) {
                    abrir(source, "/FXML/Actividad/FiltrarTareaPorEstado.fxml");
                } else {
                    filtrarPorEstado();
                }
            }
            case "ordenar recordatorio por fecha", "recordatorios ordenados por fecha" ->
                    abrir(source, "/FXML/Actividad/OrdenarRecordatoriosFecha(ListView).fxml");
            case "ordenar actividades por prioridad" ->
                    abrir(source, "/FXML/Actividad/MostrarActividadesOrdenadasPrioridad(ListView).fxml");
            case "actividades compartidas" ->
                    abrir(source, "/FXML/Actividad/MostrarActividadesCompartidas(ListView).fxml");
            case "notificaciones" -> abrir(source, "/FXML/Actividad/Notificaciones(ListView).fxml");
            case "cerrar sesion" -> cerrarSesion(source);
            case "usuario" -> mostrarUsuario();
            case "eliminar notificacion" -> eliminarNotificacion();
            default -> {
            }
        }
    }

    private void configurarColoresPrioridad() {
        configurarListaConPrioridad(listTareas);
        configurarListaConPrioridad(listRecordatorios);
        configurarListaConPrioridad(listTareasCompartidas);
        configurarListaConPrioridad(listRecordatoriosCompartidos);
        configurarListaConPrioridad(listTareasOrdenadasPrioridad);
        configurarListaConPrioridad(listRecordatoriosOrdenadosPrioridad);
        configurarListaConPrioridad(listTareasCanceladas);
        configurarListaConPrioridad(listTareasCompletadas);
        configurarListaConPrioridad(listTareasEnProgreso);
        configurarListaConPrioridad(listTareasPendientes);
        configurarListaConPrioridad(listRecordatoriosOrdenadosFecha);
        configurarListaConPrioridad(listNotificaciones);
    }

    private void configurarListaConPrioridad(ListView<String> lista) {
        if (lista == null) {
            return;
        }

        lista.setFixedCellSize(-1);

        lista.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.isBlank()) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                TextFlow contenido = crearTextoActividad(item);
                contenido.setLineSpacing(3);
                contenido.setMaxWidth(listView.getWidth() - 50);
                contenido.prefWidthProperty().bind(listView.widthProperty().subtract(50));

                setText(null);
                setGraphic(contenido);
            }
        });
    }

    private TextFlow crearTextoActividad(String textoActividad) {
        TextFlow textFlow = new TextFlow();
        String[] lineas = textoActividad.split("\n", -1);

        for (int i = 0; i < lineas.length; i++) {
            agregarLineaFormateada(textFlow, lineas[i]);

            if (i < lineas.length - 1) {
                textFlow.getChildren().add(crearTextoNormal("\n"));
            }
        }

        return textFlow;
    }

    private void agregarLineaFormateada(TextFlow textFlow, String linea) {
        int posicionDosPuntos = linea.indexOf(":");

        if (posicionDosPuntos == -1) {
            textFlow.getChildren().add(crearTextoNormal(linea));
            return;
        }

        String etiqueta = linea.substring(0, posicionDosPuntos + 1);
        String valor = linea.substring(posicionDosPuntos + 1).trim();

        textFlow.getChildren().add(crearTextoEtiqueta(etiqueta + " "));

        if ("Prioridad:".equalsIgnoreCase(etiqueta)) {
            textFlow.getChildren().add(crearTextoPrioridad(valor));
        } else {
            textFlow.getChildren().add(crearTextoNormal(valor));
        }
    }

    private Text crearTextoEtiqueta(String contenido) {
        Text texto = new Text(contenido);
        texto.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-fill: #222831; -fx-font-weight: bold;");
        return texto;
    }

    private Text crearTextoNormal(String contenido) {
        Text texto = new Text(contenido);
        texto.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-fill: #222831;");
        return texto;
    }

    private Text crearTextoPrioridad(String prioridad) {
        Text texto = new Text(prioridad);
        texto.setStyle(estiloPrioridad(prioridad));
        return texto;
    }

    private String estiloPrioridad(String prioridad) {
        return switch (prioridad.toUpperCase()) {
            case "ALTA" -> "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-fill: #C62828; -fx-font-weight: bold;";
            case "MEDIA" -> "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-fill: #EF6C00; -fx-font-weight: bold;";
            case "BAJA" -> "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-fill: #2E7D32; -fx-font-weight: bold;";
            default -> "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-fill: #222831;";
        };
    }

    private void cargarDatosDePantalla() {
        if (usuarioActual() == null) {
            if (root != null && root.getScene() != null) {
                abrir(root, "/FXML/Inicio/IniciarSesion.fxml");
            }
            return;
        }

        actualizarTopbarUsuario(root);
        List<Actividad> actividades = actividades();
        List<Actividad> actividadesCompartidas = gestor().obtenerActividadesCompartidas(usuarioActual());

        actualizarResumenMenu(actividades, actividadesCompartidas);

        cargarLista(listTareas, actividades.stream().filter(Tarea.class::isInstance).toList());
        cargarLista(listRecordatorios, actividades.stream().filter(Recordatorio.class::isInstance).toList());
        cargarLista(listTareasCompartidas, actividadesCompartidas.stream()
                .filter(Tarea.class::isInstance)
                .toList());
        cargarLista(listRecordatoriosCompartidos, actividadesCompartidas.stream()
                .filter(Recordatorio.class::isInstance)
                .toList());
        cargarLista(listTareasOrdenadasPrioridad, actividades.stream()
                .filter(Tarea.class::isInstance)
                .sorted(Comparator.comparing(Actividad::getPrioridad))
                .toList());
        cargarLista(listRecordatoriosOrdenadosPrioridad, actividades.stream()
                .filter(Recordatorio.class::isInstance)
                .sorted(Comparator.comparing(Actividad::getPrioridad))
                .toList());
        cargarLista(listRecordatoriosOrdenadosFecha, actividades.stream()
                .filter(Recordatorio.class::isInstance)
                .map(Recordatorio.class::cast)
                .sorted(Comparator.comparing(Recordatorio::getFechaHora, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(a -> (Actividad) a)
                .toList());
        cargarLista(listTareasPendientes, tareasPorEstado(Estado.PENDIENTE));
        cargarLista(listTareasEnProgreso, tareasPorEstado(Estado.EN_PROGRESO));
        cargarLista(listTareasCompletadas, tareasPorEstado(Estado.COMPLETADA));
        cargarLista(listTareasCanceladas, tareasPorEstado(Estado.CANCELADA));
        actualizarResumenMenu(actividades, actividadesCompartidas);
        cargarNotificaciones();
    }
    private void actualizarResumenMenu(List<Actividad> actividades, List<Actividad> actividadesCompartidas) {
        long totalTareas = actividades.stream()
                .filter(Tarea.class::isInstance)
                .count();

        long totalRecordatorios = actividades.stream()
                .filter(Recordatorio.class::isInstance)
                .count();

        if (lblTotalActividades != null) {
            lblTotalActividades.setText(formatearRegistros(actividades.size()));
            lblTotalTareas.setText(formatearRegistros(totalTareas));
            lblTotalRecordatorios.setText(formatearRegistros(totalRecordatorios));
            lblTotalCompartidas.setText(formatearRegistros(actividadesCompartidas.size()));
        }

        if (lblTotalActividadesResumen != null) {
            lblTotalActividadesResumen.setText(formatearRegistros(actividades.size()));
            lblTotalTareasResumen.setText(formatearRegistros(totalTareas));
            lblTotalRecordatoriosResumen.setText(formatearRegistros(totalRecordatorios));
            lblTotalCompartidasResumen.setText(formatearRegistros(actividadesCompartidas.size()));
        }
    }

    private String formatearRegistros(long cantidad) {
        return cantidad + " registro(s)";
    }

    private void actualizarTopbarUsuario(Node node) {
        Usuario usuario = usuarioActual();
        if (usuario == null || node == null) {
            return;
        }

        if (node instanceof Labeled labeled && "Usuario".equals(labeled.getText())) {
            labeled.setText(usuario.getNombre());
            if (labeled instanceof Button button) {
                button.setOnAction(event -> mostrarUsuario());
            } else if (labeled instanceof Label label) {
                label.setOnMouseClicked(event -> mostrarUsuario());
            }
            return;
        }

        if (node instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            actualizarTopbarUsuario(scrollPane.getContent());
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                actualizarTopbarUsuario(child);
            }
        }
    }

    private void crearTarea() {
        if (textoVacio(txtTituloTarea) || textoVacio(txtDescripcionTarea) || cmbPrioridadTarea.getValue() == null) {
            Alertas.error("Datos incompletos", "Complete titulo, descripcion y prioridad.");
            return;
        }

        DatosActividad datos = new DatosActividad(
                appState.siguienteIdTemporal(),
                "tarea",
                txtTituloTarea.getText().trim(),
                txtDescripcionTarea.getText().trim(),
                cmbPrioridadTarea.getValue(),
                null
        );
        guardarActividad(datos);
    }

    private void crearRecordatorio() {
        if (textoVacio(txtTituloRecordatorio)
                || textoVacio(txtDescripcionRecordatorio)
                || cmbPrioridadRecordatorio.getValue() == null
                || dpFechaRecordatorio.getValue() == null
                || textoVacio(txtHoraRecordatorio)) {
            Alertas.error("Datos incompletos", "Complete titulo, descripcion, prioridad, fecha y hora.");
            return;
        }

        LocalDateTime fechaHoraRecordatorio = leerFechaHoraRecordatorio();
        if (fechaHoraRecordatorio == null) {
            return;
        }

        DatosActividad datos = new DatosActividad(
                appState.siguienteIdTemporal(),
                "recordatorio",
                txtTituloRecordatorio.getText().trim(),
                txtDescripcionRecordatorio.getText().trim(),
                cmbPrioridadRecordatorio.getValue(),
                fechaHoraRecordatorio
        );

        guardarActividad(datos);
    }

    private void guardarActividad(DatosActividad datos) {
        Actividad actividad = gestor().crearActividad(usuarioActual(), datos);
        if (actividad == null) {
            Alertas.error("Actividad", "No se pudo guardar la actividad.");
            return;
        }
        Alertas.info("Actividad", "Actividad guardada correctamente.");
        limpiarFormulario();
    }

    private void editarTarea() {
        int id = leerId(txtIdTarea);
        if (id <= 0 || textoVacio(txtTituloTarea) || textoVacio(txtDescripcionTarea)
                || cmbPrioridadTarea.getValue() == null || cmbEstadoTarea.getValue() == null) {
            Alertas.error("Datos incompletos", "Complete ID, titulo, descripcion, prioridad y estado.");
            return;
        }

        Actividad actividad = gestor().buscarActividad(usuarioActual(), id);
        if (!(actividad instanceof Tarea tarea)) {
            Alertas.error("Editar tarea", "No se encontro una tarea con ese ID.");
            return;
        }

        tarea.setEstado(cmbEstadoTarea.getValue());
        DatosActividad datos = new DatosActividad(id, "tarea", txtTituloTarea.getText().trim(),
                txtDescripcionTarea.getText().trim(), cmbPrioridadTarea.getValue(), null);
        guardarEdicion(actividad, datos);
    }

    private void editarRecordatorio() {
        int id = leerId(txtIdRecordatorio);
        if (id <= 0 || textoVacio(txtTituloRecordatorio) || textoVacio(txtDescripcionRecordatorio)
                || cmbPrioridadRecordatorio.getValue() == null || dpFechaRecordatorio.getValue() == null
                || cmbPrioridadRecordatorio.getValue() == null
                || dpFechaRecordatorio.getValue() == null
                || textoVacio(txtHoraRecordatorio)) {
            Alertas.error("Datos incompletos", "Complete ID, titulo, descripcion, prioridad, fecha y hora.");
            return;
        }

        Actividad actividad = gestor().buscarActividad(usuarioActual(), id);
        if (!(actividad instanceof Recordatorio)) {
            Alertas.error("Editar recordatorio", "No se encontro un recordatorio con ese ID.");
            return;
        }

        LocalDateTime fechaHoraRecordatorio = leerFechaHoraRecordatorio();
        if (fechaHoraRecordatorio == null) {
            return;
        }

        DatosActividad datos = new DatosActividad(id, "recordatorio", txtTituloRecordatorio.getText().trim(),
                txtDescripcionRecordatorio.getText().trim(), cmbPrioridadRecordatorio.getValue(),
                fechaHoraRecordatorio);
        guardarEdicion(actividad, datos);
    }

    private void guardarEdicion(Actividad actividad, DatosActividad datos) {
        Actividad actualizada = gestor().actualizarActividad(usuarioActual(), actividad, datos);
        if (actualizada == null) {
            Alertas.error("Editar actividad", "No se pudo actualizar la actividad.");
            return;
        }
        Alertas.info("Editar actividad", "Actividad actualizada correctamente.");
        limpiarFormulario();
    }

    private void eliminarActividad(TextField campoId) {
        int id = leerId(campoId);
        if (id <= 0) {
            Alertas.error("Eliminar actividad", "Ingrese un ID valido.");
            return;
        }
        if (!gestor().eliminarActividad(usuarioActual(), id)) {
            Alertas.error("Eliminar actividad", "No se encontro la actividad.");
            return;
        }
        Alertas.info("Eliminar actividad", "Actividad eliminada correctamente.");
        campoId.clear();
    }

    private void compartirActividad(TextField campoId, TextField campoCorreo) {
        int id = leerId(campoId);
        String correo = campoCorreo.getText().trim();
        if (id <= 0 || correo.isEmpty()) {
            Alertas.error("Compartir actividad", "Ingrese ID y correo destino.");
            return;
        }
        if (!gestor().compartirActividad(usuarioActual(), id, correo)) {
            Alertas.error("Compartir actividad", "No se pudo compartir la actividad.");
            return;
        }
        Alertas.info("Compartir actividad", "Actividad compartida correctamente.");
        campoId.clear();
        campoCorreo.clear();
    }

    private void filtrarPorEstado() {
        Estado estado = cmbEstadoFiltro.getValue();
        if (estado == null) {
            Alertas.error("Filtro", "Seleccione un estado.");
            return;
        }
        List<Actividad> filtradas = tareasPorEstado(estado);
        if (estado == Estado.PENDIENTE) {
            abrir(root, "/FXML/Actividad/MostrarTareasPendientes(ListView).fxml");
        } else if (estado == Estado.EN_PROGRESO) {
            abrir(root, "/FXML/Actividad/MostrarTareasEnProgreso(ListView).fxml");
        } else if (estado == Estado.COMPLETADA) {
            abrir(root, "/FXML/Actividad/MostrarTareasCompletadas(ListView).fxml");
        } else if (estado == Estado.CANCELADA) {
            abrir(root, "/FXML/Actividad/MostrarTareasCanceladas(ListView).fxml");
        }
        if (filtradas.isEmpty()) {
            Alertas.info("Filtro", "No hay tareas con ese estado.");
        }
    }

    private List<Actividad> tareasPorEstado(Estado estado) {
        return actividades().stream()
                .filter(Tarea.class::isInstance)
                .map(Tarea.class::cast)
                .filter(tarea -> tarea.getEstado() == estado)
                .map(tarea -> (Actividad) tarea)
                .toList();
    }

    private void cargarNotificaciones() {
        if (listNotificaciones == null) {
            return;
        }
        notificacionesActuales.clear();
        notificacionesActuales.addAll(gestor().obtenerNotificacionesPendientes(usuarioActual()));
        listNotificaciones.setItems(FXCollections.observableArrayList(
                notificacionesActuales.stream().map(this::formatearNotificacion).toList()
        ));
    }

    private void eliminarNotificacion() {
        if (listNotificaciones == null) {
            return;
        }

        int index = listNotificaciones.getSelectionModel().getSelectedIndex();
        if (index < 0 || index >= notificacionesActuales.size()) {
            Alertas.error("Notificaciones", "Seleccione una notificacion para eliminar.");
            return;
        }

        Recordatorio recordatorio = notificacionesActuales.get(index);
        if (!gestor().marcarNotificacionComoAtendida(usuarioActual(), recordatorio.getId())) {
            Alertas.error("Notificaciones", "No se pudo actualizar la notificacion.");
            return;
        }

        notificacionesActuales.remove(index);
        listNotificaciones.getItems().remove(index);
        Alertas.info("Notificaciones", "Notificacion eliminada correctamente.");
    }

    private void cargarLista(ListView<String> lista, List<Actividad> actividades) {
        if (lista == null) {
            return;
        }
        lista.setItems(FXCollections.observableArrayList(
                actividades.stream().map(this::formatearActividad).toList()
        ));
    }

    private String formatearActividad(Actividad actividad) {
        String compartida = actividad.getUsuariosCompartidos().isEmpty()
                ? ""
                : "\nCompartida con: " + String.join(", ", actividad.getUsuariosCompartidos());
        String compartidaPor = actividad.getCompartidaPor() == null || actividad.getCompartidaPor().isBlank()
                ? ""
                : "\nCompartida por: " + actividad.getCompartidaPor();
        if (actividad instanceof Tarea tarea) {
            return "ID: " + tarea.getId()
                    + "\nTitulo: " + tarea.getTitulo()
                    + "\nDescripcion: " + tarea.getDescripcion()
                    + "\nPrioridad: " + tarea.getPrioridad().name()
                    + "\nEstado: " + tarea.getEstado().getEtiqueta()
                    + compartida
                    + compartidaPor;
        }
        if (actividad instanceof Recordatorio recordatorio) {
            return "ID: " + recordatorio.getId()
                    + "\nTitulo: " + recordatorio.getTitulo()
                    + "\nDescripcion: " + recordatorio.getDescripcion()
                    + "\nPrioridad: " + recordatorio.getPrioridad().getPrioridad()
                    + "\nFecha y hora: " + formatearFechaHora(recordatorio.getFechaHora())
                    + compartida
                    + compartidaPor;
        }
        return Objects.toString(actividad);
    }

    private String formatearNotificacion(Recordatorio recordatorio) {
        return "Recordatorio activado"
                + "\nID: " + recordatorio.getId()
                + "\nTitulo: " + recordatorio.getTitulo()
                + "\nDescripcion: " + recordatorio.getDescripcion()
                + "\nFecha y hora: " + formatearFechaHora(recordatorio.getFechaHora())
                + "\nPrioridad: " + recordatorio.getPrioridad().getPrioridad();
    }

    private List<Actividad> actividades() {
        Usuario usuario = usuarioActual();
        if (usuario == null) {
            return List.of();
        }
        return gestor().obtenerActividades(usuario);
    }

    private Usuario usuarioActual() {
        return appState.getUsuarioActual();
    }

    private GestorActividades gestor() {
        return appState.getGestorActividades();
    }

    private void mostrarUsuario() {
        Usuario usuario = usuarioActual();
        if (usuario == null) {
            Alertas.error("Usuario", "No hay usuario activo.");
            return;
        }
        Alertas.info("Usuario", usuario.getNombre() + "\n" + usuario.getCorreo() + "\n" + usuario.getTipo());
    }

    private void cerrarSesion(Node source) {
        appState.cerrarSesion();
        abrir(source, "/FXML/Inicio/Main.fxml");
    }

    private void abrir(Node source, String ruta) {
        Navegacion.cambiar(source, ruta);
    }

    private boolean textoVacio(TextField campo) {
        return campo == null || campo.getText() == null || campo.getText().trim().isEmpty();
    }

    private int leerId(TextField campo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    private void limpiarFormulario() {
        TextField[] campos = {txtTituloTarea, txtDescripcionTarea, txtIdTarea, txtIdEliminarTarea, txtIdCompartirTarea,
                txtCorreoDestinoTarea, txtTituloRecordatorio, txtDescripcionRecordatorio, txtIdRecordatorio,
                txtIdEliminarRecordatorio, txtIdCompartirRecordatorio, txtCorreoDestinoRecordatorio,
                txtHoraRecordatorio};
        java.util.Arrays.stream(campos)
                .filter(Objects::nonNull)
                .forEach(TextField::clear);
        if (cmbPrioridadTarea != null) cmbPrioridadTarea.setValue(null);
        if (cmbEstadoTarea != null) cmbEstadoTarea.setValue(null);
        if (cmbPrioridadRecordatorio != null) cmbPrioridadRecordatorio.setValue(null);
        if (dpFechaRecordatorio != null) dpFechaRecordatorio.setValue(null);
        if (txtHoraRecordatorio != null) txtHoraRecordatorio.setText(horaSugerida());
        if (txtEstadoTarea != null) txtEstadoTarea.setText(Estado.PENDIENTE.name());
    }

    private String normalizarAccion(String texto) {
        if (texto == null) {
            return "";
        }
        String textoCorregido = texto
                .replace("Ã¡", "á")
                .replace("Ã©", "é")
                .replace("Ã­", "í")
                .replace("Ã³", "ó")
                .replace("Ãº", "ú")
                .replace("Ã±", "ñ")
                .replace("Â", "");
        return Normalizer.normalize(textoCorregido.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .trim();
    }
}
