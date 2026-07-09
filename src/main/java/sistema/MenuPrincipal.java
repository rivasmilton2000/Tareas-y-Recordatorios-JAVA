package sistema;

import Hilos.CargadorActividades;
import Hilos.MonitorRecordatorios;
import catalogo.Prioridad;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import modelo.Actividad;
import modelo.Usuario;
import strategy.FiltrarPorEstado;
import strategy.OrdenarPorFecha;
import strategy.OrdenarPorPrioridad;

public class MenuPrincipal {
    private final Scanner scanner = new Scanner(System.in);
    private final GestorUsuarios gestorUsuarios = new GestorUsuarios();
    private final GestorActividades gestorActividades = new GestorActividades(gestorUsuarios);
    private int siguienteIdActividad = 1;

    public void iniciar() {
        while (true) {
            System.out.println("\n=== Sistema de Gestion de Tareas ===");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Iniciar sesion");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opcion: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> registrarUsuario();
                case "2" -> iniciarSesion();
                case "3" -> {
                    System.out.println("Saliendo del sistema.");
                    return;
                }
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private void registrarUsuario() {
        System.out.print("Tipo de usuario (clasico/premium): ");
        String tipo = scanner.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Correo: ");
        String correo = scanner.nextLine().trim();
        System.out.print("Clave: ");
        String clave = scanner.nextLine().trim();

        Usuario usuario = gestorUsuarios.registrarUsuario(tipo, nombre, correo, clave);
        if (usuario == null) {
            System.out.println("No se pudo registrar el usuario.");
            return;
        }

        System.out.println("Usuario registrado: " + usuario.getNombre() + " (" + usuario.getTipo() + ")");
    }

    private void iniciarSesion() {
        if (!gestorUsuarios.hayUsuariosRegistrados()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.print("Correo: ");
        String correo = scanner.nextLine().trim();
        System.out.print("Clave: ");
        String clave = scanner.nextLine().trim();

        Usuario usuario = gestorUsuarios.iniciarSesion(correo, clave);
        if (usuario == null) {
            System.out.println("Credenciales incorrectas.");
            return;
        }

        String tipoUsuario = gestorUsuarios.obtenerTipo(usuario);
        if ("Clasico".equalsIgnoreCase(tipoUsuario)) {
            cargarMenuClasico(usuario);
        } else {
            cargarMenuPremium(usuario);
        }
    }

    private void cargarMenuClasico(Usuario usuario) {
        mostrarMenu(usuario, "clasico");
    }

    private void cargarMenuPremium(Usuario usuario) {
        mostrarMenu(usuario, "premium");
    }

    private void mostrarMenu(Usuario usuario, String tipoMenu) {
        MonitorRecordatorios monitorRecordatorios = new MonitorRecordatorios(usuario, 500);
        Thread hiloMonitor = new Thread(monitorRecordatorios, "Hilo-Monitor-" + usuario.getId());
        hiloMonitor.start();

        try {
            boolean activo = true;
            while (activo) {
                System.out.println("\n=== Menu " + tipoMenu + " ===");
                System.out.println("1. Crear actividad");
                System.out.println("2. Mostrar actividades");
                System.out.println("3. Editar actividad");
                System.out.println("4. Eliminar actividad");
                System.out.println("5. Compartir actividad");
                System.out.println("6. Aplicar estrategia por prioridad");
                System.out.println("7. Aplicar estrategia por fecha");
                System.out.println("8. Filtrar pendientes");
                System.out.println("9. Cerrar sesion");
                System.out.print("Seleccione una opcion: ");
                String opcion = scanner.nextLine().trim();

                switch (opcion) {
                    case "1" -> crearActividad(usuario);
                    case "2" -> mostrarActividades(usuario);
                    case "3" -> editarActividad(usuario);
                    case "4" -> eliminarActividad(usuario);
                    case "5" -> compartirActividad(usuario);
                    case "6" -> aplicarEstrategiaPrioridad(usuario);
                    case "7" -> aplicarEstrategiaFecha(usuario);
                    case "8" -> aplicarEstrategiaPendientes(usuario);
                    case "9" -> activo = false;
                    default -> System.out.println("Opcion invalida.");
                }
            }
        } finally {
            monitorRecordatorios.detener();
            hiloMonitor.interrupt();
        }
    }

    private void crearActividad(Usuario usuario) {
        DatosActividad datos = solicitarDatos();
        if (datos == null) {
            System.out.println("Datos invalidos.");
            return;
        }

        Actividad actividadCreada = gestorActividades.crearActividad(usuario, datos);
        if (actividadCreada == null) {
            if (!usuario.puedeCrearActividad()) {
                System.out.println("Limite de actividades alcanzadas.");
            } else {
                System.out.println("No se pudo crear la actividad.");
            }
            return;
        }

        usuario.eliminarElemento(actividadCreada.getId());

        Thread hiloCarga = new Thread(
                new CargadorActividades(usuario, List.of(actividadCreada), 100, "Registro individual"),
                "Hilo-Carga-" + actividadCreada.getId()
        );

        hiloCarga.start();
        esperarHilo(hiloCarga);
        System.out.println("Actividad creada correctamente.");
    }

    private DatosActividad solicitarDatos() {
        try {
            System.out.print("Tipo (tarea/recordatorio): ");
            String tipo = scanner.nextLine().trim();

            System.out.print("Titulo: ");
            String titulo = scanner.nextLine().trim();

            System.out.print("Descripcion: ");
            String descripcion = scanner.nextLine().trim();

            System.out.print("Prioridad (ALTA/MEDIA/BAJA): ");
            Prioridad prioridad = Prioridad.valueOf(scanner.nextLine().trim().toUpperCase());

            LocalDateTime fechaHora = null;

            if ("recordatorio".equalsIgnoreCase(tipo)) {
                System.out.print("Fecha del recordatorio (YYYY-MM-DD): ");
                LocalDate fecha = LocalDate.parse(scanner.nextLine().trim());

                System.out.print("Hora del recordatorio (HH:mm): ");
                LocalTime hora = LocalTime.parse(scanner.nextLine().trim());

                fechaHora = LocalDateTime.of(fecha, hora);
            }

            return new DatosActividad(
                    siguienteIdActividad++,
                    tipo,
                    titulo,
                    descripcion,
                    prioridad,
                    fechaHora
            );

        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
    }

    private void mostrarActividades(Usuario usuario) {
        List<Actividad> actividades = gestorActividades.obtenerActividades(usuario);
        if (actividades == null || actividades.isEmpty()) {
            System.out.println("No hay actividades.");
            return;
        }

        imprimirActividades(actividades);
    }

    private void editarActividad(Usuario usuario) {
        System.out.print("ID de la actividad a editar: ");
        int idActividad = leerEntero();

        Actividad actividad = gestorActividades.buscarActividad(usuario, idActividad);
        if (actividad == null) {
            System.out.println("Actividad no encontrada.");
            return;
        }

        DatosActividad datos = solicitarDatosEdicion(actividad);
        if (datos == null) {
            System.out.println("Datos invalidos.");
            return;
        }

        Actividad actividadEditada = gestorActividades.actualizarActividad(usuario, actividad, datos);
        if (actividadEditada == null) {
            System.out.println("No se pudo actualizar la actividad.");
            return;
        }

        System.out.println("Actividad actualizada correctamente.");
    }

    private DatosActividad solicitarDatosEdicion(Actividad actividad) {
        try {
            System.out.print("Nuevo titulo: ");
            String titulo = scanner.nextLine().trim();

            System.out.print("Nueva descripcion: ");
            String descripcion = scanner.nextLine().trim();

            System.out.print("Nueva prioridad (ALTA/MEDIA/BAJA): ");
            Prioridad prioridad = Prioridad.valueOf(scanner.nextLine().trim().toUpperCase());
            LocalDateTime fechaHora = null;
            String tipo = "tarea";

            if (actividad instanceof modelo.Recordatorio) {
                tipo = "recordatorio";

                System.out.print("Nueva fecha (YYYY-MM-DD): ");
                LocalDate fecha = LocalDate.parse(scanner.nextLine().trim());

                System.out.print("Nueva hora (HH:mm): ");
                LocalTime hora = LocalTime.parse(scanner.nextLine().trim());

                fechaHora = LocalDateTime.of(fecha, hora);
            }

            return new DatosActividad(
                    actividad.getId(),
                    tipo,
                    titulo,
                    descripcion,
                    prioridad,
                    fechaHora
            );

        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
    }

    private void eliminarActividad(Usuario usuario) {
        System.out.print("ID de la actividad a eliminar: ");
        int idActividad = leerEntero();

        if (gestorActividades.eliminarActividad(usuario, idActividad)) {
            System.out.println("Actividad eliminada.");
        } else {
            System.out.println("No se encontro la actividad.");
        }
    }

    private void compartirActividad(Usuario usuario) {
        System.out.print("ID de la actividad a compartir: ");
        int idActividad = leerEntero();

        System.out.print("Correo del usuario destino: ");
        String correoDestino = scanner.nextLine().trim();

        boolean compartida = gestorActividades.compartirActividad(usuario, idActividad, correoDestino);
        if (compartida) {
            System.out.println("Actividad compartida correctamente.");
        } else {
            System.out.println("Actividad o usuario no encontrado, o limite de compartidos alcanzado.");
        }
    }

    private void aplicarEstrategiaPrioridad(Usuario usuario) {
        usuario.setEstrategia(new OrdenarPorPrioridad());
        imprimirActividades(usuario.aplicarEstrategia());
    }

    private void aplicarEstrategiaFecha(Usuario usuario) {
        usuario.setEstrategia(new OrdenarPorFecha());
        imprimirActividades(usuario.aplicarEstrategia());
    }

    private void aplicarEstrategiaPendientes(Usuario usuario) {
        usuario.setEstrategia(new FiltrarPorEstado(catalogo.Estado.PENDIENTE));
        imprimirActividades(usuario.aplicarEstrategia());
    }

    private void imprimirActividades(List<Actividad> actividades) {
        for (Actividad actividad : actividades) {
            actividad.mostrarInfo();
            System.out.println("-------------------");
        }
    }

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void esperarHilo(Thread hilo) {
        try {
            hilo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
