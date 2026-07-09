package factory;

import catalogo.Prioridad;
import java.time.LocalDateTime;
import modelo.Recordatorio;

public class RecordatorioProgramadoFactory extends RecordatorioFactory {
    @Override
    public Recordatorio crearRecordatorio(
            int id,
            String titulo,
            String descripcion,
            Prioridad prioridad,
            LocalDateTime fechaHora
    ) {
        return new Recordatorio(id, titulo, descripcion, prioridad, fechaHora);
    }
}
