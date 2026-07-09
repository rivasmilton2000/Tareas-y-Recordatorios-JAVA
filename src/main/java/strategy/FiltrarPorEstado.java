package strategy;

import catalogo.Estado;
import java.util.List;
import modelo.Actividad;
import modelo.Tarea;

public class FiltrarPorEstado implements EstrategiaActividad {
    private final Estado estadoFiltro;

    public FiltrarPorEstado(Estado estadoFiltro) {
        this.estadoFiltro = estadoFiltro;
    }

    @Override
    public List<Actividad> aplicar(List<Actividad> actividades) {
        return actividades.stream()
                .filter(actividad -> actividad instanceof Tarea tarea && tarea.getEstado() == estadoFiltro)
                .toList();
    }
}
