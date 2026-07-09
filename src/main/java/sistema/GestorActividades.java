package sistema;

import dao.ActividadCompartidaDAO;
import factory.RecordatorioFactory;
import factory.RecordatorioProgramadoFactory;
import factory.TareaFactory;
import factory.TareaPendienteFactory;
import java.util.List;
import modelo.Actividad;
import modelo.Recordatorio;
import modelo.Tarea;
import modelo.Usuario;

public class GestorActividades {
    private final ListaActividades listaActividades = new ListaActividades();
    private final ActividadCompartidaDAO actividadCompartidaDAO = new ActividadCompartidaDAO();
    private final GestorUsuarios gestorUsuarios;
    private final TareaFactory tareaFactory = new TareaPendienteFactory();
    private final RecordatorioFactory recordatorioFactory = new RecordatorioProgramadoFactory();

    public GestorActividades(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
    }

    public Actividad crearActividad(Usuario usuario, DatosActividad datos) {
        if (esTarea(datos)) {
            return crearTarea(usuario, datos);
        }

        if (esRecordatorio(datos)) {
            return crearRecordatorio(usuario, datos);
        }
        return null;
    }

    public List<Actividad> obtenerActividades(Usuario usuario) {
        return listaActividades.buscarActividades(usuario);
    }

    public List<Recordatorio> obtenerNotificacionesPendientes(Usuario usuario) {
        return listaActividades.buscarNotificacionesPendientes(usuario);
    }

    public boolean marcarNotificacionComoAtendida(Usuario usuario, int idRecordatorio) {
        return listaActividades.marcarRecordatorioComoNotificado(usuario, idRecordatorio);
    }

    public List<Actividad> obtenerActividadesCompartidas(Usuario usuario) {
        if (usuario == null) {
            return List.of();
        }
        return actividadCompartidaDAO.listarCompartidasPorUsuario(usuario.getId());
    }

    public Actividad buscarActividad(Usuario usuario, int idActividad) {
        return listaActividades.obtenerActividad(usuario, idActividad);
    }

    public Actividad actualizarActividad(Usuario usuario, Actividad actividad, DatosActividad datos) {
        if (actividad == null || datos == null) {
            return null;
        }
        actividad.editarDatos(datos.getTitulo(), datos.getDescripcion(), datos.getPrioridad());
        if (actividad instanceof Recordatorio recordatorio && datos.getFechaRecordatorio() != null) {
            recordatorio.editarFecha(datos.getFechaRecordatorio());
        }
        if (actividad instanceof Tarea tarea && datos.getTipo().equalsIgnoreCase("tarea")) {
            tarea.cambiarEstado(tarea.getEstado());
        }
        if (listaActividades.actualizarActividad(usuario, actividad)) {
            return actividad;
        }
        return null;
    }

    public boolean eliminarActividad(Usuario usuario, int idActividad) {
        Actividad actividad = buscarActividad(usuario, idActividad);
        if (actividad == null) {
            return false;
        }
        return listaActividades.eliminarActividad(usuario, actividad);
    }

    public boolean compartirActividad(Usuario usuario, int idActividad, String correoDestino) {
        Actividad actividadEncontrada = buscarActividad(usuario, idActividad);
        Usuario usuarioDestino = gestorUsuarios.buscarUsuario(correoDestino);

        if (actividadEncontrada == null || usuarioDestino == null) {
            return false;
        }

        if (usuario.getId() == usuarioDestino.getId()) {
            return false;
        }

        boolean destinoClasico = "Clasico".equalsIgnoreCase(gestorUsuarios.obtenerTipo(usuarioDestino));
        return actividadCompartidaDAO.compartirActividad(
                actividadEncontrada.getId(),
                usuarioDestino.getId(),
                destinoClasico,
                usuarioDestino.limiteCompartidos()
        );
    }

    private Actividad crearTarea(Usuario usuario, DatosActividad datos) {
        if (!validarDatosTarea(datos) || !puedeCrear(usuario)) {
            return null;
        }

        Tarea tarea = tareaFactory.crearTarea(
                datos.getId(),
                datos.getTitulo(),
                datos.getDescripcion(),
                datos.getPrioridad()
        );

        if (listaActividades.guardarActividad(usuario, tarea)) {
            return tarea;
        }
        return null;
    }

    private Actividad crearRecordatorio(Usuario usuario, DatosActividad datos) {
        if (!validarDatosRecordatorio(datos) || !puedeCrear(usuario)) {
            return null;
        }

        Recordatorio recordatorio = recordatorioFactory.crearRecordatorio(
                datos.getId(),
                datos.getTitulo(),
                datos.getDescripcion(),
                datos.getPrioridad(),
                datos.getFechaRecordatorio()
        );

        if (listaActividades.guardarActividad(usuario, recordatorio)) {
            return recordatorio;
        }
        return null;
    }

    private boolean puedeCrear(Usuario usuario) {
        return usuario != null && usuario.puedeCrearActividad();
    }

    private boolean validarDatosTarea(DatosActividad datos) {
        return datos != null
                && datos.getTitulo() != null
                && !datos.getTitulo().isBlank()
                && datos.getDescripcion() != null
                && !datos.getDescripcion().isBlank()
                && datos.getPrioridad() != null;
    }

    private boolean validarDatosRecordatorio(DatosActividad datos) {
        return datos != null
                && datos.getTitulo() != null
                && !datos.getTitulo().isBlank()
                && datos.getDescripcion() != null
                && !datos.getDescripcion().isBlank()
                && datos.getPrioridad() != null
                && datos.getFechaRecordatorio() != null;
    }

    private boolean esTarea(DatosActividad datos) {
        return datos != null && "tarea".equalsIgnoreCase(datos.getTipo());
    }

    private boolean esRecordatorio(DatosActividad datos) {
        return datos != null && "recordatorio".equalsIgnoreCase(datos.getTipo());
    }
}
