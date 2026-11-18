USE sigeti_sprint1;

CREATE TABLE IF NOT EXISTS bitacora_ticket (
    id_bitacora INT AUTO_INCREMENT PRIMARY KEY,
    id_ticket INT NOT NULL,
    rfc_usuario_actor VARCHAR(13) NULL,
    tipo_evento VARCHAR(30) NOT NULL,
    valor_anterior VARCHAR(150) NULL,
    valor_nuevo VARCHAR(150) NOT NULL,
    observacion VARCHAR(500) NULL,
    fecha_hora DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_bitacora_ticket (id_ticket, fecha_hora),
    CONSTRAINT fk_bitacora_ticket FOREIGN KEY (id_ticket) REFERENCES ticket(id_ticket) ON DELETE CASCADE,
    CONSTRAINT fk_bitacora_actor FOREIGN KEY (rfc_usuario_actor) REFERENCES usuario(rfc)
    ) ENGINE=InnoDB;

INSERT INTO bitacora_ticket (id_ticket, rfc_usuario_actor, tipo_evento, valor_anterior, valor_nuevo, observacion, fecha_hora)
SELECT t.id_ticket,
       t.rfc_usuario_final,
       'CREACION',
       NULL,
       CONCAT('Estado: ', t.estatus, ' | Prioridad: ', t.prioridad, ' | Técnico: ', COALESCE(t.rfc_tecnico, 'Sin asignar')),
       'Registro inicial histórico',
       t.fecha_hora_creacion
FROM ticket t
WHERE NOT EXISTS (
    SELECT 1 FROM bitacora_ticket b WHERE b.id_ticket = t.id_ticket
);