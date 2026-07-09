package dao;

import catalogo.Estado;
import catalogo.Prioridad;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Tarea;

public class TareaDAO {
    public boolean guardarTarea(Tarea tarea) {
        if (tarea == null || tarea.getUsuarioId() <= 0) {
            return false;
        }

        String sql = "insert into actividades (usuario_id, tipo, titulo, descripcion, prioridad, estado, fecha_hora, notificado) " +
                "values (?, 'tarea', ?, ?, ?, ?, ?, ?) returning id";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, tarea.getUsuarioId());
            ps.setString(2, tarea.getTitulo());
            ps.setString(3, tarea.getDescripcion());
            ps.setString(4, tarea.getPrioridad().name());
            ps.setString(5, tarea.getEstado().name());
            ps.setNull(6, java.sql.Types.TIMESTAMP);
            ps.setBoolean(7, false);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tarea.setId(rs.getInt("id"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al guardar tarea: " + e.getMessage());
        }
        return false;
    }

    public List<Tarea> listarTareas() {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "select id, usuario_id, titulo, descripcion, prioridad, estado from actividades where tipo = 'tarea' order by id";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return tareas;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tareas.add(crearTarea(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al listar tareas: " + e.getMessage());
        }
        return tareas;
    }

    public List<Tarea> listarTareasPorUsuario(int idUsuario) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "select id, usuario_id, titulo, descripcion, prioridad, estado from actividades where tipo = 'tarea' and usuario_id = ? order by id";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return tareas;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tareas.add(crearTarea(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al listar tareas por usuario: " + e.getMessage());
        }
        return tareas;
    }

    public Tarea buscarTareaPorId(int id) {
        String sql = "select id, usuario_id, titulo, descripcion, prioridad, estado from actividades where tipo = 'tarea' and id = ?";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return null;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearTarea(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al buscar tarea por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarTarea(Tarea tarea) {
        if (tarea == null || tarea.getUsuarioId() <= 0) {
            return false;
        }

        String sql = "update actividades set usuario_id = ?, titulo = ?, descripcion = ?, prioridad = ?, estado = ?, fecha_hora = ?, notificado = ? " +
                "where id = ? and tipo = 'tarea'";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, tarea.getUsuarioId());
            ps.setString(2, tarea.getTitulo());
            ps.setString(3, tarea.getDescripcion());
            ps.setString(4, tarea.getPrioridad().name());
            ps.setString(5, tarea.getEstado().name());
            ps.setNull(6, java.sql.Types.TIMESTAMP);
            ps.setBoolean(7, false);
            ps.setInt(8, tarea.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al actualizar tarea: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarTarea(int id) {
        String sql = "delete from actividades where tipo = 'tarea' and id = ?";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al eliminar tarea: " + e.getMessage());
        }
        return false;
    }



    

    private Tarea crearTarea(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int usuarioId = rs.getInt("usuario_id");
        String titulo = rs.getString("titulo");
        String descripcion = rs.getString("descripcion");
        Prioridad prioridad = Prioridad.valueOf(rs.getString("prioridad"));
        Estado estado = Estado.valueOf(rs.getString("estado"));
        return new Tarea(id, usuarioId, titulo, descripcion, prioridad, estado);
    }
}
