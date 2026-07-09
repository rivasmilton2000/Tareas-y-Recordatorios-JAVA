package strategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import modelo.Actividad;
import modelo.Recordatorio;

public class OrdenarPorFecha implements EstrategiaActividad {
    @Override
    public List<Actividad> aplicar(List<Actividad> actividades) {
        List<Actividad> copia = new ArrayList<>(actividades);
        copia.sort(Comparator.comparing(this::obtenerFechaOrden));
        return copia;
    }

    private LocalDateTime obtenerFechaOrden(Actividad actividad) {
        if (actividad instanceof Recordatorio recordatorio && recordatorio.getFechaHora() != null) {
            return recordatorio.getFechaHora();
        }
        return LocalDateTime.MAX;
    }
}
