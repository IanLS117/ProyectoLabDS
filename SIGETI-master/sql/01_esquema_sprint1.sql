CREATE DATABASE IF NOT EXISTS sigeti_sprint1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sigeti_sprint1;

CREATE TABLE rol (
    id_rol INT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE permiso (
    id_permiso INT PRIMARY KEY,
    nombre_permiso VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE rol_permiso (
    id_rol INT NOT NULL,
    id_permiso INT NOT NULL,
    PRIMARY KEY (id_rol, id_permiso),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol),
    FOREIGN KEY (id_permiso) REFERENCES permiso(id_permiso)
) ENGINE=InnoDB;

CREATE TABLE usuario (
    rfc VARCHAR(13) PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE=InnoDB;

CREATE TABLE usuario_final (
    rfc_usuario VARCHAR(13) PRIMARY KEY,
    FOREIGN KEY (rfc_usuario) REFERENCES usuario(rfc)
) ENGINE=InnoDB;

CREATE TABLE equipo (
    numero_inventario VARCHAR(30) PRIMARY KEY,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    area_asignada VARCHAR(80) NOT NULL,
    estado_hardware VARCHAR(30) NOT NULL DEFAULT 'Operativo',
    rfc_usuario_asignado VARCHAR(13) NULL,
    FOREIGN KEY (rfc_usuario_asignado) REFERENCES usuario_final(rfc_usuario)
) ENGINE=InnoDB;

CREATE TABLE tipo_incidencia (
    id_tipo_incidencia INT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE ticket (
    id_ticket INT AUTO_INCREMENT PRIMARY KEY,
    folio VARCHAR(40) NOT NULL UNIQUE,
    rfc_usuario_final VARCHAR(13) NOT NULL,
    numero_inventario VARCHAR(30) NOT NULL,
    id_tipo_incidencia INT NOT NULL,
    descripcion_problema VARCHAR(4000) NOT NULL,
    estatus VARCHAR(20) NOT NULL DEFAULT 'Enviado',
    prioridad VARCHAR(10) NOT NULL DEFAULT 'Media',
    fecha_hora_creacion DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (rfc_usuario_final) REFERENCES usuario_final(rfc_usuario),
    FOREIGN KEY (numero_inventario) REFERENCES equipo(numero_inventario),
    FOREIGN KEY (id_tipo_incidencia) REFERENCES tipo_incidencia(id_tipo_incidencia),
    CHECK (estatus IN ('Enviado', 'En atención', 'Terminado'))
) ENGINE=InnoDB;
