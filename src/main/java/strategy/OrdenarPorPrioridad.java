package strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import modelo.Actividad;

public class OrdenarPorPrioridad implements EstrategiaActividad {
    @Override
    public List<Actividad> aplicar(List<Actividad> actividades) {
        List<Actividad> copia = new ArrayList<>(actividades);
        copia.sort(Comparator.comparingInt(actividad -> actividad.getPrioridad().ordinal()));
        return copia;
    }
}
