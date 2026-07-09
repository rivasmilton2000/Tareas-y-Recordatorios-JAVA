package catalogo;

public enum Estado {
    PENDIENTE,
    EN_PROGRESO,
    COMPLETADA,
    CANCELADA;

    public String getEtiqueta() {
        return switch (this) {
            case PENDIENTE -> "Pendiente";
            case EN_PROGRESO -> "En progreso";
            case COMPLETADA -> "Completada";
            case CANCELADA -> "Cancelada";
        };
    }
}
