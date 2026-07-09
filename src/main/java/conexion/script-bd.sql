-- ===========================================================================
-- SCRIPT DE CREACION DE LA BASE DE DATOS: sistema_tareas
-- ===========================================================================

CREATE TABLE catalogo_prioridad (
                                    prioridad VARCHAR(10),
                                    PRIMARY KEY (prioridad)-- 'ALTA', 'MEDIA', 'BAJA'
);

CREATE TABLE catalogo_estado (
                                 estado VARCHAR(15),
                                 PRIMARY KEY (estado)   -- 'PENDIENTE', 'EN_PROGRESO', 'COMPLETADA', 'CANCELADA'
);

CREATE TABLE usuarios (
                          id INT generated always as identity,
                          nombre VARCHAR(100) NOT NULL,
                          correo VARCHAR(100) NOT NULL UNIQUE,
                          clave VARCHAR(255) NOT NULL,
                          tipo VARCHAR(15) NOT NULL, -- 'Clasico' o 'Premium'
                          PRIMARY key (id),
                          compartidos_recibidos INT DEFAULT 0,
                          CONSTRAINT chk_tipo_usuario CHECK (tipo IN ('Clasico', 'Premium'))
);

CREATE TABLE actividades (
                             id INT generated always as identity,
                             usuario_id INT NOT NULL, -- El propietario original de la actividad
                             tipo VARCHAR(15) NOT NULL, -- 'tarea' o 'recordatorio'
                             titulo VARCHAR(150) NOT NULL,
                             descripcion TEXT,
                             prioridad VARCHAR(10) NOT NULL,
                             PRIMARY key (id),
    -- Campos específicos para 'Tarea'
                             estado VARCHAR(15) NULL DEFAULT 'PENDIENTE',
    -- Campos específicos para 'Recordatorio' (Usado por MonitorRecordatorios)
                             fecha_hora TIMESTAMP NULL,
                             notificado BOOLEAN DEFAULT FALSE,

                             FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                             FOREIGN KEY (prioridad) REFERENCES catalogo_prioridad(prioridad),
                             FOREIGN KEY (estado) REFERENCES catalogo_estado(estado),
                             CONSTRAINT chk_tipo_actividad CHECK (tipo IN ('tarea', 'recordatorio'))
);

CREATE TABLE actividades_compartidas (
                                         actividad_id INT NOT NULL,
                                         usuario_destino_id INT NOT NULL,
                                         PRIMARY KEY (actividad_id, usuario_destino_id),
                                         FOREIGN KEY (actividad_id) REFERENCES actividades(id) ON DELETE CASCADE,
                                         FOREIGN KEY (usuario_destino_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
-- ===========================================================================
-- Inserción de valores permitidos por Enums
-- ===========================================================================
INSERT INTO catalogo_prioridad (prioridad) VALUES ('ALTA'), ('MEDIA'), ('BAJA');
INSERT INTO catalogo_estado (estado) VALUES ('PENDIENTE'), ('EN_PROGRESO'), ('COMPLETADA'), ('CANCELADA');
