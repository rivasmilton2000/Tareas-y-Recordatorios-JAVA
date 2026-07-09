package sistema;

import catalogo.Estado;
import catalogo.Prioridad;
import conexion.ConexionBD;
import dao.TareaDAO;
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
import modelo.Usuario;

public class ListaActividades {
    private final TareaDAO tareaDAO = new TareaDAO();

    public boolean guardarActividad(Usuario usuario, Actividad actividadCreada) {
        if (usuario == null || actividadCreada == null) {
            return false;
        }

        if (actividadCreada instanceof Tarea tarea) {
            tarea.setUsuarioId(usuario.getId());
            return tareaDAO.guardarTarea(tarea);
        }

        String sql = "insert into actividades (usuario_id, tipo, titulo, descripcion, prioridad, estado, fecha_hora, notificado) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?) returning id";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());

            if (actividadCreada instanceof Recordatorio rec) {
                ps.setString(2, "recordatorio");
                ps.setString(3, rec.getTitulo());
                ps.setString(4, rec.getDescripcion());
                ps.setString(5, rec.getPrioridad().name());
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setTimestamp(7, Timestamp.valueOf(rec.getFechaHora()));
                ps.setBoolean(8, false);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    actividadCreada.setId(rs.getInt("id"));
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al guardar actividad: " + e.getMessage());
            return false;
        }
    }

    public List<Actividad> buscarActividades(Usuario usuario) {
        List<Actividad> lista = new ArrayList<>();
        if (usuario == null) {
            return lista;
        }

        String sql = "select * from actividades where usuario_id = ?";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return lista;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(crearActividadDesdeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al buscar actividades: " + e.getMessage());
        }
        return lista;
    }

    public Actividad obtenerActividad(Usuario usuario, int idActividad) {
        if (usuario == null) {
            return null;
        }
        String sql = "select * from actividades where usuario_id = ? and id = ?";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return null;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ps.setInt(2, idActividad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearActividadDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al obtener actividad: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarActividad(Usuario usuario, Actividad actividadEditada) {
        if (usuario == null || actividadEditada == null) {
            return false;
        }

        if (actividadEditada instanceof Tarea tarea) {
            tarea.setUsuarioId(usuario.getId());
            return tareaDAO.actualizarTarea(tarea);
        }

        String sql = "update actividades set titulo = ?, descripcion = ?, prioridad = ?, estado = ?, fecha_hora = ?, "
                + "notificado = ? where id = ? and usuario_id = ?";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, actividadEditada.getTitulo());
            ps.setString(2, actividadEditada.getDescripcion());
            ps.setString(3, actividadEditada.getPrioridad().name());
            if (actividadEditada instanceof Recordatorio rec) {
                ps.setNull(4, java.sql.Types.VARCHAR);
                ps.setTimestamp(5, Timestamp.valueOf(rec.getFechaHora()));
                ps.setBoolean(6, rec.isNotificado());
            }
            ps.setInt(7, actividadEditada.getId());
            ps.setInt(8, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al actualizar actividad: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarActividad(Usuario usuario, Actividad actividad) {
        if (usuario == null || actividad == null) {
            return false;
        }

        if (actividad instanceof Tarea) {
            return tareaDAO.eliminarTarea(actividad.getId());
        }

        String sql = "delete from actividades where id = ? and usuario_id = ?";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, actividad.getId());
            ps.setInt(2, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al eliminar actividad: " + e.getMessage());
            return false;
        }
    }

    public List<Recordatorio> buscarNotificacionesPendientes(Usuario usuario) {
        List<Recordatorio> notificaciones = new ArrayList<>();
        if (usuario == null) {
            return notificaciones;
        }

        String sql = "select id, usuario_id, titulo, descripcion, prioridad, fecha_hora, notificado "
                + "from actividades "
                + "where usuario_id = ? and tipo = 'recordatorio' and fecha_hora <= current_timestamp and notificado = false "
                + "order by fecha_hora, id";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return notificaciones;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recordatorio recordatorio = new Recordatorio(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("descripcion"),
                            Prioridad.valueOf(rs.getString("prioridad")),
                            toLocalDateTime(rs.getTimestamp("fecha_hora"))
                    );
                    recordatorio.setUsuarioId(rs.getInt("usuario_id"));
                    recordatorio.setNotificado(rs.getBoolean("notificado"));
                    notificaciones.add(recordatorio);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al buscar notificaciones pendientes: " + e.getMessage());
        }
        return notificaciones;
    }

    public boolean marcarRecordatorioComoNotificado(Usuario usuario, int idRecordatorio) {
        if (usuario == null || idRecordatorio <= 0) {
            return false;
        }

        String sql = "update actividades set notificado = true where id = ? and usuario_id = ? and tipo = 'recordatorio'";
        Connection con = ConexionBD.getInstancia();
        if (con == null) {
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRecordatorio);
            ps.setInt(2, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al marcar recordatorio como notificado: " + e.getMessage());
            return false;
        }
    }

    private Actividad crearActividadDesdeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String titulo = rs.getString("titulo");
        String descripcion = rs.getString("descripcion");
        Prioridad prioridad = Prioridad.valueOf(rs.getString("prioridad"));
        int usuarioId = rs.getInt("usuario_id");

        if ("tarea".equalsIgnoreCase(tipo)) {
            Estado estado = Estado.valueOf(rs.getString("estado"));
            return new Tarea(id, usuarioId, titulo, descripcion, prioridad, estado);
        }

        LocalDateTime fecha = toLocalDateTime(rs.getTimestamp("fecha_hora"));
        Recordatorio recordatorio = new Recordatorio(id, titulo, descripcion, prioridad, fecha);
        recordatorio.setUsuarioId(usuarioId);
        recordatorio.setNotificado(rs.getBoolean("notificado"));
        return recordatorio;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
