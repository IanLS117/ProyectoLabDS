-- Ejecutar una vez chavalones sobre la base existente, antes del nuevo WAR.

USE sigeti_sprint1;

ALTER TABLE ticket
    ADD COLUMN rfc_tecnico VARCHAR(13) NULL,
    ADD INDEX idx_ticket_tecnico_estatus (rfc_tecnico, estatus),
    ADD CONSTRAINT fk_ticket_tecnico
        FOREIGN KEY (rfc_tecnico) REFERENCES usuario(rfc);
