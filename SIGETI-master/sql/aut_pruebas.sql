-- SIGETI - PBI-AUT-US-01 - Base para pruebas
-- Cuentas de prueba RFC - contraseña
-- USUARIO01 / u
-- SOPORTE01 / s
-- ADMIN01   / a

CREATE DATABASE IF NOT EXISTS sigeti_aut_pruebas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sigeti_aut_pruebas;

-- 1 Crear las tablas
CREATE TABLE rol (
    id_rol INT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

CREATE TABLE permiso (
    id_permiso INT PRIMARY KEY,
    nombre_permiso VARCHAR(80) NOT NULL
);

CREATE TABLE rol_permiso (
    id_rol INT NOT NULL,
    id_permiso INT NOT NULL,
    PRIMARY KEY (id_rol, id_permiso),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol),
    FOREIGN KEY (id_permiso) REFERENCES permiso(id_permiso)
);

CREATE TABLE usuario (
    rfc CHAR(13) PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- 2 Cargar los roles y permisos de prueba
INSERT INTO rol (id_rol, nombre_rol) VALUES
    (1, 'Usuario final'),
    (2, 'Soporte tecnico'),
    (3, 'Administrador');

INSERT INTO permiso (id_permiso, nombre_permiso) VALUES
    (1, 'consultar_equipos'),
    (2, 'crear_ticket'),
    (3, 'atender_tickets'),
    (4, 'administrar_tickets');

INSERT INTO rol_permiso (id_rol, id_permiso) VALUES
    (1, 1),
    (1, 2),
    (2, 3),
    (3, 4);

-- 3 Cargar los de prueba con sus hashes
INSERT INTO usuario
    (rfc, nombre, apellidos, correo, contrasena, id_rol)
VALUES
    (
        'USUARIO01', 'Usuario', 'Prueba', 'usuario@s',
        'pbkdf2$600000$71krUfd/dPL7gwtawMEiLQ==$PcFNieia5u1ADP2kMjiJTKrKlrN3oOgYpbZUBdY3fOs=',
        1
    ),
    (
        'SOPORTE01', 'Soporte', 'Prueba', 'soporte@s',
        'pbkdf2$600000$+xGOObK7BN0jlulDxOivsw==$sTnwmBjZSfou6+S5uGrHSp/s4eYW7/2Jymp5YAfOxTA=',
        2
    ),
    (
        'ADMIN01', 'Administrador', 'Prueba', 'admin@a',
        'pbkdf2$600000$xmKc3weD0r0I8dAu4g4stQ==$N/uSUDb3cvkQ3U1hZnIWFu55V3CJsHsVPDaU8JJCl20=',
        3
    );

SELECT u.rfc, u.correo, r.nombre_rol
FROM usuario u
JOIN rol r ON u.id_rol = r.id_rol
ORDER BY r.id_rol;