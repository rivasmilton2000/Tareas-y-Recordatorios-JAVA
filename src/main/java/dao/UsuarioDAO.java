package dao;

import conexion.ConexionBD;
import factory.UsuarioClasicoFactory;
import factory.UsuarioFactory;
import factory.UsuarioPremiumFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

public class UsuarioDAO {
    private final UsuarioFactory usuarioClasicoFactory = new UsuarioClasicoFactory();
    private final UsuarioFactory usuarioPremiumFactory = new UsuarioPremiumFactory();

    public boolean guardarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        String sql = "insert into usuarios (nombre, correo, clave, tipo) values (?, ?, ?, ?) returning id";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getClave());
            ps.setString(4, normalizarTipo(usuario.getTipo()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt("id"));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al guardar usuario: " + e.getMessage());
        }
        return false;
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "select id, nombre, correo, clave, tipo from usuarios order by id";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return usuarios;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(crearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public Usuario buscarUsuarioPorId(int id) {
        String sql = "select id, nombre, correo, clave, tipo from usuarios where id = ?";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return null;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        String sql = "select id, nombre, correo, clave, tipo from usuarios where correo = ?";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return null;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al buscar usuario por correo: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        String sql = "update usuarios set nombre = ?, correo = ?, clave = ?, tipo = ? where id = ?";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getClave());
            ps.setString(4, normalizarTipo(usuario.getTipo()));
            ps.setInt(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarUsuario(int id) {
        String sql = "delete from usuarios where id = ?";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en base de datos al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    public boolean existenUsuarios() {
        String sql = "select exists (select 1 from usuarios)";
        Connection conexion = ConexionBD.getInstancia();

        if (conexion == null) {
            return false;
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en base de datos al verificar usuarios existentes: " + e.getMessage());
        }
        return false;
    }

    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String correo = rs.getString("correo");
        String clave = rs.getString("clave");
        String tipo = rs.getString("tipo");

        if ("Premium".equalsIgnoreCase(tipo)) {
            return usuarioPremiumFactory.crearUsuario(id, nombre, correo, clave);
        }
        return usuarioClasicoFactory.crearUsuario(id, nombre, correo, clave);
    }

    private String normalizarTipo(String tipo) {
        return "Premium".equalsIgnoreCase(tipo) ? "Premium" : "Clasico";
    }
}
