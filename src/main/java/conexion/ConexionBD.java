package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/sistema_tareas";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "1581";
    private static final String LEGACY_PASSWORD = "cocade3litros";

    private static Connection conexion = null;
    private static boolean esquemaVerificado = false;
    private ConexionBD() {}

    public static synchronized Connection getInstancia() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexion = abrirConexion();
                asegurarEsquema(conexion);
                System.out.println("Conexion establecida exitosamente con PostgreSQL (sistema_tareas).");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontro el Driver de PostgreSQL. Verifica tu build.gradle. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error en base de datos al conectar a PostgreSQL: " + e.getMessage());
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    esquemaVerificado = false;
                    System.out.println("Conexion a PostgreSQL cerrada.");
                }
            } catch (SQLException e) {
                System.err.println("Error en base de datos al cerrar la conexion: " + e.getMessage());
            }
        }
    }

    private static Connection abrirConexion() throws SQLException {
        String url = leerConfiguracion("TASKMIND_DB_URL", DEFAULT_URL);
        String user = leerConfiguracion("TASKMIND_DB_USER", DEFAULT_USER);
        String passwordEnv = leerConfiguracion("TASKMIND_DB_PASSWORD", System.getenv("PGPASSWORD"));

        if (passwordEnv != null && !passwordEnv.isBlank()) {
            return DriverManager.getConnection(url, user, passwordEnv);
        }

        SQLException ultimaExcepcion = null;
        for (String password : new String[]{DEFAULT_PASSWORD, LEGACY_PASSWORD}) {
            try {
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                ultimaExcepcion = e;
            }
        }

        throw ultimaExcepcion;
    }

    private static String leerConfiguracion(String variable, String valorPorDefecto) {
        String valor = System.getenv(variable);
        if (valor == null || valor.isBlank()) {
            return valorPorDefecto;
        }
        return valor;
    }

    private static void asegurarEsquema(Connection connection) {
        if (esquemaVerificado) {
            return;
        }

        String consultarTipo = """
                select data_type
                from information_schema.columns
                where table_name = 'actividades'
                  and column_name = 'fecha_hora'
                """;

        try (var ps = connection.prepareStatement(consultarTipo);
             var rs = ps.executeQuery()) {
            if (rs.next()) {
                String tipoActual = rs.getString("data_type");
                if ("date".equalsIgnoreCase(tipoActual)) {
                    try (var alter = connection.prepareStatement(
                            "alter table actividades alter column fecha_hora type timestamp using fecha_hora::timestamp")) {
                        alter.executeUpdate();
                        System.out.println("Esquema actualizado: actividades.fecha_hora ahora es TIMESTAMP.");
                    }
                }
            }
            esquemaVerificado = true;
        } catch (SQLException e) {
            System.err.println("Error al verificar/actualizar el esquema de actividades: " + e.getMessage());
        }
    }
}
