USE sigeti_sprint1;

INSERT INTO rol VALUES (1, 'Usuario final'), (2, 'Soporte técnico'), (3, 'Administrador');
INSERT INTO permiso VALUES (1, 'consultar_equipos'), (2, 'crear_ticket'), (3, 'atender_tickets'), (4, 'administrar_tickets');
INSERT INTO rol_permiso VALUES (1, 1), (1, 2), (2, 3), (3, 4);

INSERT INTO usuario (rfc, nombre, apellidos, correo, contrasena, id_rol) VALUES
('DEMO000001AAA', 'Usuario', 'Prueba A', 'usuario-a@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 1),
('DEMO000002BBB', 'Usuario', 'Prueba B', 'usuario-b@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 1),
('DEMO000003CCC', 'Usuario', 'Sin equipos', 'sin-equipos@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 1),
('DEMO000004SSS', 'Soporte', 'Prueba', 'soporte@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 2),
('DEMO000005DDD', 'Administrador', 'Prueba', 'administrador@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 3);

INSERT INTO usuario_final VALUES ('DEMO000001AAA'), ('DEMO000002BBB'), ('DEMO000003CCC');
INSERT INTO equipo VALUES
('INV-DEMO-001', 'Dell', 'OptiPlex', 'Oficina A', 'Operativo', 'DEMO000001AAA'),
('INV-DEMO-002', 'Lenovo', 'ThinkCentre', 'Oficina B', 'Operativo', 'DEMO000002BBB'),
('INV-DEMO-003', 'HP', 'ProDesk', 'Almacén', 'Operativo', NULL);
INSERT INTO tipo_incidencia VALUES
(1, 'Hardware', TRUE), (2, 'Software', TRUE), (3, 'Red', TRUE),
(4, 'Impresión', TRUE), (5, 'Acceso', TRUE), (6, 'Otro', TRUE);
