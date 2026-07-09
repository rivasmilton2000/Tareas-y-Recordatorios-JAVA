package strategy;

import java.util.List;
import modelo.Actividad;

public interface EstrategiaActividad {
    List<Actividad> aplicar(List<Actividad> actividades);
}
