package dao;

import catalogo.Estado;
import catalogo.Prioridad;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelo.Actividad;
import modelo.Recordatorio;
import modelo.Tarea;

public class ActividadCompartidaDAO {

    public boolean compartirActividad(int actividadId, int usuarioDestinoId, boolean destinoClasico, int limiteCompartidos) {
        Connection conexion = ConexionBD.getInstancia();
        if (conexion == null) {
            return false;
        }

        try {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            if (!insertarCompartida(conexion, actividadId, usuarioDestinoId)) {
                conexion.rollback();
                conexion.setAutoCommit(autoCommitOriginal);
                return false;
            }

            if (destinoClasico && !incrementarCompartidosRecibidos(conexion, usuarioDestinoId, limiteCompartidos)) {
                conexion.rollback();
                conexion.setAutoCommit(autoCommitOriginal);
                return false;
            }

            conexion.commit();
            conexion.setAutoCommit(autoCommitOriginal);
            return true;

        } catch (SQLException e) {
            revertirTransaccion(conexion);
            System.err.println("Error en base de datos al compartir actividad: " + e.getMessage());
            return false;
        }
    }

    public List<Actividad> listarCompartidasPorUsuario(int usuarioDestinoId) {
        List<Actividad> actividades = new ArrayList<>();

        String sql = "select a.id, a.usuario_id, a.tipo, a.titulo, a.descripcion, a.prioridad, a.estado, a.fecha_hora, " +
                "u.correo as correo_propietario " +
                "from actividades a " +
                "inner join actividades_compartidas ac on ac.actividad_id = a.id " +
                "inner join usuarios u on u.id = a.usuario_id " +
                "where ac.usuario_destino_id = ? " +
                "order by a.id";

        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return actividades;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuarioDestinoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    actividades.add(crearActividad(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en base de datos al listar actividades compartidas: " + e.getMessage());
        }

        return actividades;
    }

    private boolean insertarCompartida(Connection conexion, int actividadId, int usuarioDestinoId) throws SQLException {
        String sql = "insert into actividades_compartidas (actividad_id, usuario_destino_id) " +
                "values (?, ?) on conflict do nothing";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, actividadId);
            ps.setInt(2, usuarioDestinoId);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean incrementarCompartidosRecibidos(Connection conexion, int usuarioDestinoId, int limiteCompartidos)
            throws SQLException {

        String sql = "update usuarios set compartidos_recibidos = compartidos_recibidos + 1 " +
                "where id = ? and compartidos_recibidos < ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuarioDestinoId);
            ps.setInt(2, limiteCompartidos);
            return ps.executeUpdate() > 0;
        }
    }

    private Actividad crearActividad(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int usuarioId = rs.getInt("usuario_id");
        String tipo = rs.getString("tipo");
        String titulo = rs.getString("titulo");
        String descripcion = rs.getString("descripcion");
        Prioridad prioridad = Prioridad.valueOf(rs.getString("prioridad"));
        String correoPropietario = rs.getString("correo_propietario");

        Actividad actividad;

        if ("tarea".equalsIgnoreCase(tipo)) {
            Estado estado = Estado.valueOf(rs.getString("estado"));
            actividad = new Tarea(id, usuarioId, titulo, descripcion, prioridad, estado);
        } else {
            Timestamp fechaHoraBD = rs.getTimestamp("fecha_hora");
            LocalDateTime fechaHora = fechaHoraBD != null ? fechaHoraBD.toLocalDateTime() : null;

            Recordatorio recordatorio = new Recordatorio(id, titulo, descripcion, prioridad, fechaHora);
            recordatorio.setUsuarioId(usuarioId);

            actividad = recordatorio;
        }

        actividad.setCompartidaPor(correoPropietario);
        return actividad;
    }

    private void revertirTransaccion(Connection conexion) {
        try {
            if (conexion != null) {
                conexion.rollback();
                conexion.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error al revertir compartir actividad: " + e.getMessage());
        }
    }
}
