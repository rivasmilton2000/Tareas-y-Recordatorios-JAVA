package modelo;

import interfaces.Autenticable;
import java.util.ArrayList;
import java.util.List;
import strategy.EstrategiaActividad;

public class Usuario implements Autenticable {
    protected int id;
    protected String nombre;
    protected String correo;
    protected String clave;
    protected ArrayList<Actividad> elementos = new ArrayList<>();
    private EstrategiaActividad estrategia;

    public Usuario(int id, String nombre, String correo, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTipo() {
        return "Clasico";
    }

    public synchronized void crearElemento(Actividad actividad) {
        elementos.add(actividad);
    }

    public synchronized boolean eliminarElemento(int idActividad) {
        return elementos.removeIf(actividad -> actividad.getId() == idActividad);
    }

    public synchronized List<Actividad> obtenerElementos() {
        return new ArrayList<>(elementos);
    }

    public synchronized int obtenerCantidadElementos() {
        return elementos.size();
    }

    public synchronized Actividad buscarActividadPorId(int idActividad) {
        for (Actividad actividad : elementos) {
            if (actividad.getId() == idActividad) {
                return actividad;
            }
        }
        return null;
    }

    public boolean puedeCrearActividad() {
        return true;
    }

    public boolean limiteDisponible() {
        return true;
    }

    public int limiteCompartidos() {
        return Integer.MAX_VALUE;
    }

    public void recibirActividadCompartida(Actividad actividad) {
        crearElemento(actividad);
    }

    public void setEstrategia(EstrategiaActividad estrategia) {
        this.estrategia = estrategia;
    }

    public List<Actividad> aplicarEstrategia() {
        if (estrategia == null) {
            return obtenerElementos();
        }
        return estrategia.aplicar(obtenerElementos());
    }

    public void compartirElementos() {
        System.out.println("Elemento compartido");
    }

    public void mostrarElementos() {
        for (Actividad actividad : obtenerElementos()) {
            actividad.mostrarInfo();
            System.out.println("-------------------");
        }
    }

    @Override
    public boolean iniciarSesion(String correo, String clave) {
        return this.correo.equals(correo) && this.clave.equals(clave);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", tipo='" + getTipo() + '\'' +
                '}';
    }
}
