package modelo;

import catalogo.Prioridad;
import java.util.ArrayList;
import java.util.List;

public abstract class Actividad {
    protected int id;
    protected int usuarioId;
    protected String titulo;
    protected String descripcion;
    protected Prioridad prioridad;
    private String compartidaPor;
    private final List<String> usuariosCompartidos = new ArrayList<>();

    public Actividad(int id, String titulo, String descripcion, Prioridad prioridad) {
        this(id, 0, titulo, descripcion, prioridad);
    }

    public Actividad(int id, int usuarioId, String titulo, String descripcion, Prioridad prioridad) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public List<String> getUsuariosCompartidos() {
        return new ArrayList<>(usuariosCompartidos);
    }

    public String getCompartidaPor() {
        return compartidaPor;
    }

    public void setCompartidaPor(String compartidaPor) {
        this.compartidaPor = compartidaPor;
    }

    public void editarDatos(String titulo, String descripcion, Prioridad prioridad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
    }

    public void agregarUsuarioCompartido(Usuario usuarioDestino) {
        if (!usuariosCompartidos.contains(usuarioDestino.getCorreo())) {
            usuariosCompartidos.add(usuarioDestino.getCorreo());
        }
    }

    public abstract Actividad clonarParaCompartir();

    public void mostrarInfo() {
        System.out.println("ID: " + id);
        System.out.println("Titulo: " + titulo);
        System.out.println("Descripcion: " + descripcion);
        System.out.println("Prioridad: " + prioridad.getPrioridad());
        if (!usuariosCompartidos.isEmpty()) {
            System.out.println("Compartido con: " + usuariosCompartidos);
        }
    }

    @Override
    public String toString() {
        return "Actividad{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", prioridad=" + prioridad +
                '}';
    }
}
