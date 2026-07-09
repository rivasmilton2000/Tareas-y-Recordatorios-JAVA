package factory;

import catalogo.Estado;
import catalogo.Prioridad;
import modelo.Tarea;

public class TareaPendienteFactory extends TareaFactory {
    @Override
    public Tarea crearTarea(int id, String titulo, String descripcion, Prioridad prioridad) {
        return new Tarea(id, titulo, descripcion, prioridad, Estado.PENDIENTE);
    }
}
