package factory;

import catalogo.Prioridad;
import modelo.Tarea;

public abstract class TareaFactory {
    public abstract Tarea crearTarea(int id, String titulo, String descripcion, Prioridad prioridad);
}
