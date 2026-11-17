USE sigeti_sprint1;

INSERT INTO rol VALUES (1, 'Usuario final'), (2, 'Soporte técnico'), (3, 'Administrador');
INSERT INTO permiso VALUES (1, 'consultar_equipos'), (2, 'crear_ticket'), (3, 'atender_tickets'), (4, 'administrar_tickets');
INSERT INTO rol_permiso VALUES (1, 1), (1, 2), (2, 3), (3, 4);

INSERT INTO usuario (rfc, nombre, apellidos, correo, contrasena, id_rol) VALUES
('DEMO000001AAA', 'Usuario', 'Prueba A', 'usuario-a@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 1),
('DEMO000002BBB', 'Usuario', 'Prueba B', 'usuario-b@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 1),
('DEMO000003CCC', 'Usuario', 'Sin equipos', 'sin-equipos@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 1),
('DEMO000004SSS', 'Soporte', 'Prueba A', 'soporte@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 2),
('DEMO000006SSS', 'Soporte', 'Prueba B', 'soporte-b@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 2),
('DEMO000005DDD', 'Administrador', 'Prueba', 'administrador@example.invalid', 'pbkdf2$210000$rlNuSraSG/cN3nWtzgfVFA==$KWo/6xWXWU1KuEQ0+1BDCjj7w/YdfO1pkRcrpwrlS68=', 3);

INSERT INTO usuario_final VALUES ('DEMO000001AAA'), ('DEMO000002BBB'), ('DEMO000003CCC');

INSERT INTO equipo VALUES
('INV-DEMO-001', 'Dell', 'OptiPlex', 'Oficina A', 'Operativo', 'DEMO000001AAA'),
('INV-DEMO-002', 'Lenovo', 'ThinkCentre', 'Oficina B', 'Operativo', 'DEMO000002BBB'),
('INV-DEMO-003', 'HP', 'ProDesk', 'Almacén', 'Operativo', NULL);

INSERT INTO tipo_incidencia VALUES
(1, 'Hardware', TRUE), (2, 'Software', TRUE), (3, 'Red', TRUE),
(4, 'Impresión', TRUE), (5, 'Acceso', TRUE), (6, 'Otro', TRUE);

INSERT INTO ticket (folio, rfc_usuario_final, numero_inventario, id_tipo_incidencia, descripcion_problema, estatus, prioridad, rfc_soporte) VALUES
('TCK-TEST-001', 'DEMO000001AAA', 'INV-DEMO-001', 3, 'Sin acceso a red corporativa en estación de trabajo.', 'En atención', 'Alta', 'DEMO000004SSS'),
('TCK-TEST-002', 'DEMO000002BBB', 'INV-DEMO-002', 4, 'Error en cola de impresión de facturas.', 'En atención', 'Media', 'DEMO000004SSS'),
('TCK-TEST-003', 'DEMO000001AAA', 'INV-DEMO-001', 2, 'Actualización pendiente de agente antivirus.', 'Enviado', 'Baja', 'DEMO000004SSS'),
('TCK-TEST-004', 'DEMO000002BBB', 'INV-DEMO-002', 5, 'Solicitud de acceso a carpeta compartida de Finanzas.', 'Enviado', 'Media', 'DEMO000006SSS'),
('TCK-TEST-005', 'DEMO000001AAA', 'INV-DEMO-001', 1, 'Revisión preventiva de teclado y monitor.', 'Terminado', 'Baja', 'DEMO000004SSS');
