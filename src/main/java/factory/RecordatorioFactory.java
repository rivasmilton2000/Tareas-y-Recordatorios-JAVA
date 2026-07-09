package factory;

import catalogo.Prioridad;
import java.time.LocalDateTime;
import modelo.Recordatorio;

public abstract class RecordatorioFactory {
    public abstract Recordatorio crearRecordatorio(
            int id,
            String titulo,
            String descripcion,
            Prioridad prioridad,
            LocalDateTime fechaHora
    );
}
