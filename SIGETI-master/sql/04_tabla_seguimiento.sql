-- Ejecutar una sola vez si la tabla SEGUIMIENTO no existe.
-- Completa la tabla que necesita el código de PBI-TIC-US-04.
-- No elimina datos ni vuelve a cargar usuarios de prueba.
USE sigeti_sprint1;

CREATE TABLE SEGUIMIENTO (
    ID_Ticket INT NOT NULL,
    Numero_Seguimiento INT NOT NULL,
    Fecha DATETIME(6) NOT NULL,
    Descripcion TEXT NOT NULL,
    Es_Solucion_Final BIT NOT NULL DEFAULT b'0',
    PRIMARY KEY (ID_Ticket, Numero_Seguimiento),
    CONSTRAINT fk_seguimiento_ticket
        FOREIGN KEY (ID_Ticket) REFERENCES ticket(id_ticket)
) ENGINE=InnoDB;
