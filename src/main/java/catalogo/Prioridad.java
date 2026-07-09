package catalogo;

public enum Prioridad {
    ALTA,
    MEDIA,
    BAJA;

    public String getPrioridad()
    {
        return switch (this)
        {
            case ALTA -> "Alta";
            case MEDIA -> "Media";
            case BAJA -> "Baja";
        };
    }
}
