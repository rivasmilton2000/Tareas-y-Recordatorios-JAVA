# TaskMind

Aplicacion de escritorio desarrollada en Java para gestionar tareas y recordatorios con interfaz grafica en JavaFX y persistencia en PostgreSQL.

El proyecto permite registrar usuarios, iniciar sesion, crear actividades, editarlas, eliminarlas, compartirlas con otros usuarios y recibir alertas automaticas cuando un recordatorio llega a su fecha y hora programada.

## Autor

Proyecto desarrollado por el autor de este repositorio: `rivasmilton2000`.

## Que hace el proyecto

TaskMind implementa un sistema de organizacion personal con dos tipos de actividades:

- `Tarea`: incluye titulo, descripcion, prioridad y estado.
- `Recordatorio`: incluye titulo, descripcion, prioridad, fecha y hora programada.

Desde la aplicacion se puede:

- Registrar usuarios.
- Iniciar sesion con correo y contrasena.
- Crear tareas.
- Crear recordatorios.
- Editar tareas y recordatorios existentes.
- Eliminar actividades por ID.
- Compartir tareas o recordatorios con otro usuario por correo.
- Visualizar actividades propias y actividades compartidas.
- Filtrar tareas por estado.
- Ordenar actividades por prioridad.
- Ordenar recordatorios por fecha y hora.
- Ver un resumen con totales en el menu principal.
- Mostrar alertas automaticas para recordatorios vencidos o activados.
- Marcar notificaciones como atendidas desde la vista de notificaciones.

## Funcionalidades implementadas

### Gestion de usuarios

- Registro de usuarios desde interfaz grafica.
- Inicio de sesion con validacion contra base de datos.
- Soporte para dos tipos de usuario:
  - `Clasico`
  - `Premium`

### Gestion de actividades

- Creacion de tareas con prioridad `ALTA`, `MEDIA` o `BAJA`.
- Creacion de tareas con estado inicial `PENDIENTE`.
- Edicion de tareas, incluyendo cambio de estado.
- Creacion de recordatorios con fecha y hora.
- Edicion de recordatorios, incluyendo reprogramacion.
- Eliminacion de tareas y recordatorios.

### Compartir actividades

- Permite compartir una actividad con otro usuario usando su correo.
- Las actividades compartidas se registran en una tabla dedicada de base de datos.
- La aplicacion tambien muestra quien compartio una actividad al usuario receptor.
- El modelo contempla restricciones para usuario clasico, especialmente en actividades compartidas recibidas.

### Consultas y visualizacion

- Vista separada para tareas.
- Vista separada para recordatorios.
- Vista de actividades compartidas.
- Vista de tareas pendientes.
- Vista de tareas en progreso.
- Vista de tareas completadas.
- Vista de tareas canceladas.
- Vista de recordatorios ordenados por fecha.
- Vista de actividades ordenadas por prioridad.

### Notificaciones automaticas

- El proyecto ejecuta un programador en segundo plano al iniciar sesion.
- Cada 5 segundos revisa recordatorios pendientes del usuario activo.
- Cuando encuentra recordatorios vencidos o programados para el momento actual, muestra una alerta visual.
- Las notificaciones pueden marcarse como atendidas para que no vuelvan a mostrarse.

## Tecnologias utilizadas

- Java 17
- JavaFX 21.0.4
- Gradle
- PostgreSQL
- JDBC
- FXML
- CSS

## Arquitectura del proyecto

El proyecto esta organizado en paquetes con responsabilidades claras:

- `app`: arranque de la aplicacion JavaFX, escena responsiva y scheduler de recordatorios.
- `controlador`: controladores JavaFX y navegacion entre pantallas.
- `modelo`: entidades principales del dominio.
- `dao`: acceso a datos y operaciones SQL.
- `conexion`: conexion a PostgreSQL y script de creacion de base de datos.
- `sistema`: logica de negocio y coordinacion entre capas.
- `factory`: creacion de usuarios, tareas y recordatorios.
- `strategy`: ordenamiento y filtrado de actividades.
- `catalogo`: enums para prioridad y estado.
- `Hilos`: clases auxiliares para manejo concurrente en la logica del proyecto.
- `resources/FXML`: vistas de la interfaz.
- `diagramasUML` y `docs`: documentacion visual y diagramas.

## Patrones y decisiones de diseno presentes

El codigo refleja varias decisiones orientadas a organizacion y reutilizacion:

- `DAO`: separacion del acceso a base de datos.
- `Factory Method`: creacion de usuarios, tareas y recordatorios.
- `Strategy`: filtrado y ordenamiento de actividades.
- `Singleton`: manejo de estado global con `AppState` y conexion compartida con `ConexionBD`.
- `Scheduler / concurrencia`: revision automatica de recordatorios en segundo plano.

## Requisitos para ejecutar el proyecto

Necesitas lo siguiente:

- JDK 17 instalado.
- PostgreSQL activo localmente o accesible por red.
- Base de datos creada.
- Puerto de PostgreSQL disponible, normalmente `5432`.
- Gradle Wrapper incluido en el proyecto.

## Configuracion de base de datos

La aplicacion intenta conectarse por defecto a:

```text
jdbc:postgresql://localhost:5432/sistema_tareas
```

### 1. Crear la base de datos

```sql
CREATE DATABASE sistema_tareas;
```

### 2. Ejecutar el script SQL del proyecto

Archivo:

```text
src/main/java/conexion/script-bd.sql
```

Ejemplo con `psql`:

```bash
psql -U postgres -d sistema_tareas -f src/main/java/conexion/script-bd.sql
```

### 3. Configurar credenciales

La forma recomendada es usar variables de entorno:

```text
TASKMIND_DB_URL
TASKMIND_DB_USER
TASKMIND_DB_PASSWORD
```

Tambien puede tomar el password desde:

```text
PGPASSWORD
```

Si no defines estas variables, la clase `ConexionBD` usa valores de desarrollo embebidos en el codigo. Para un entorno real o para subirlo a GitHub con una configuracion limpia, conviene reemplazar esos valores o usar variables de entorno.

### Nota sobre el esquema

Al iniciar, `ConexionBD` verifica el tipo de la columna `fecha_hora` en la tabla `actividades`. Si encuentra un esquema antiguo con tipo `DATE`, intenta migrarlo automaticamente a `TIMESTAMP`.

## Estructura de base de datos

El script crea estas tablas:

- `catalogo_prioridad`
- `catalogo_estado`
- `usuarios`
- `actividades`
- `actividades_compartidas`

Relaciones principales:

- Un usuario puede tener muchas actividades.
- Una actividad puede compartirse con varios usuarios destino.
- Las tareas usan estado.
- Los recordatorios usan fecha, hora y bandera de notificacion.

## Como ejecutar el proyecto

### En Windows

```powershell
.\gradlew.bat run
```

### En Linux o macOS

```bash
./gradlew run
```

## Como compilar

```powershell
.\gradlew.bat build
```

El artefacto generado queda en:

```text
build/libs/
```

Para desarrollo local, la forma mas estable de ejecucion es `gradlew run`.

## Punto de entrada

El punto de entrada actual de la aplicacion es:

```text
src/main/java/Main.java
```

Este delega el arranque a:

```text
app.TaskMindApp
```

La interfaz principal se construye con JavaFX y carga sus vistas desde FXML.

## Estructura del proyecto

```text
src/main/java/
- Main.java
- app/
- catalogo/
- conexion/
- controlador/
- dao/
- factory/
- Hilos/
- interfaces/
- modelo/
- resources/FXML/
- sistema/
- strategy/
```

## Pantallas principales

La interfaz incluye, entre otras, las siguientes pantallas:

- Pantalla de bienvenida.
- Registro de usuario.
- Inicio de sesion.
- Menu principal.
- Crear tarea.
- Crear recordatorio.
- Editar tarea.
- Editar recordatorio.
- Eliminar tarea.
- Eliminar recordatorio.
- Compartir tarea.
- Compartir recordatorio.
- Mostrar actividades.
- Mostrar actividades compartidas.
- Filtrar tareas por estado.
- Mostrar notificaciones.

## Documentacion incluida en el repositorio

El proyecto ya incluye documentacion visual util para presentaciones, revision tecnica o defensa academica:

- Diagramas UML de actividad.
- Diagramas UML de secuencia.
- Diagramas de clases.
- Archivo PlantUML en `docs/diagrama-clases.puml`.

## Estado actual del proyecto

Actualmente el proyecto:

- Compila correctamente con Gradle.
- Usa JavaFX como interfaz principal.
- Usa PostgreSQL como persistencia.
- No incluye pruebas automatizadas en `src/test`.

## Observaciones tecnicas

- Las contrasenas se almacenan tal como se reciben; no hay hash ni cifrado de credenciales.
- La navegacion entre vistas se hace cargando FXML y reconstruyendo la escena.
- El sistema usa IDs de actividad para editar, eliminar y compartir.
- Existen clases de apoyo en `sistema` y `Hilos` que muestran parte de la evolucion del proyecto y de su logica concurrente.

## Resumen

TaskMind es un gestor de tareas y recordatorios hecho en Java con una base tecnica clara: interfaz JavaFX, persistencia en PostgreSQL, separacion por capas, uso de patrones clasicos de diseno y un modulo de notificaciones automaticas. El repositorio tambien incluye documentacion UML y una estructura adecuada para seguir creciendo.
