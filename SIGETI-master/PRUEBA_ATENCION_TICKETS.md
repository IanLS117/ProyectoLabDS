# PBI-TIC-US-03: estado y solución del ticket

Historia: https://github.com/IanLS117/ProyectoLabDS/issues/14

Base: PBI-TIC-US-02, commit local 011aefb. La nueva rama PBI-TIC-US-03 parte de esa rama para poder comprobar el reflejo del estado en Mis tickets. Los cambios propios de la 03 quedan en un commit posterior y en un parche incremental separado.

## Comportamiento preparado

- Desde Seguimientos, soporte selecciona un ticket asignado a su cuenta.
- Enviado: Iniciar atención lo cambia a En atención.
- En atención: se conservan las observaciones y se habilita Solución final / Terminar ticket.
- Terminar exige una solución con contenido, de hasta 4000 caracteres. Se eliminan espacios exteriores.
- Terminado: el historial conserva las observaciones y distingue el registro Solución final. Ya no se habilitan cambios ni nuevas observaciones.
- La solución se guarda en SEGUIMIENTO, usando Es_Solucion_Final y Fecha, junto con estatus=Terminado en una sola transacción. No se duplican campos ni se requiere una nueva migración SQL.
- Identidad de sesión, permiso atender_tickets y asignación se comprueban en el servidor para consultas y escrituras. Estado actual y asignación se vuelven a comprobar al guardar.
- Las escrituras del mismo ticket se serializan bloqueando su registro. La numeración del historial se calcula dentro de la misma transacción.
- El listado, el detalle y el historial se actualizan tras guardar. El contador del administrador reconoce Terminado como finalizado, conservando compatibilidad con los nombres anteriores Resuelto/Cerrado.

Se adoptó el flujo hacia delante que describe el caso de prueba de la matriz. No se implementaron reaperturas, retrocesos ni saltos directos Enviado a Terminado. La issue sigue teniendo criterios de plantilla: el equipo debe revisar y aceptar estas reglas.

## Compatibilidad con los avances del equipo

Se conserva la pantalla de seguimiento y el historial previo. Las llamadas de observaciones ahora reciben RFC de sesión e identificador de ticket; ya no confían en el estado de un objeto Ticket enviado desde la vista. Así, una pantalla antigua no permite agregar notas después del cierre.

El selector muestra solamente tickets con rfc_tecnico igual al soporte autenticado. Los tickets anteriores a la asignación automática pueden tener rfc_tecnico NULL y no aparecen en esta pantalla. No se reasignan registros antiguos automáticamente. Esta consulta es la selección necesaria para atender el ticket; no acredita por sí sola la bandeja completa con prioridad de ASI-US-02.

El historial distingue tipo de registro y fecha. El nombre mostrado sobre el detalle es el soporte asignado actualmente. La tabla existente no guarda el autor histórico de cada nota: no se presenta el usuario que consulta como si fuera autor. La atribución histórica y la bitácora completa deben coordinarse con TIC-US-04 / BIT-US-01; no se declaran implementadas por este PBI.

## Pruebas automatizadas

Maven package con Java 21: 60 pruebas correctas (41 AtencionTicketTest, 14 MisTicketsTest y cinco AsignadorTecnicoTest).

Las nuevas pruebas ejecutan Hibernate contra H2 en memoria y cubren:

- Transiciones permitidas y rechazo de saltos, retrocesos, reaperturas y estados desconocidos.
- Solución nula, vacía, solo espacios, exceso de longitud y límite exacto.
- Persistencia de solución final, conservación del historial y lectura del estado desde Mis tickets.
- Permisos en base, identidades inválidas, tickets ajenos, inexistentes, sin asignación y reasignados después de seleccionar.
- Observaciones solamente durante En atención, numeración consecutiva y cierre único.
- Dos cierres simultáneos, dos observaciones simultáneas y cierre concurrente con una observación.
- Falla simulada después de enviar cambios a la base temporal y antes de confirmar: se revierten estado y solución.

No se modifica sigeti_sprint1 durante estas pruebas. Los resultados de H2 no sustituyen la prueba manual del despliegue contra MySQL.

## Prueba manual en el entorno local

1. Compilar desde el Maven padre desarrollo, ejecutar package y comprobar BUILD SUCCESS. Arrancar SIGETI desde IntelliJ.
2. Acceder como DEMO000004SSS, contraseña de demostración Sprint1Demo!, y abrir Seguimientos. El ticket 5 registrado previamente tiene rfc_tecnico=DEMO000004SSS; los cuatro tickets antiguos sin técnico pueden desaparecer del listado.
3. Seleccionar ese ticket y pulsar Iniciar atención. Verificar En atención en el detalle y el listado.
4. Guardar una observación de prueba y comprobar su aparición en el historial. Recargar y verificar persistencia.
5. Pulsar Terminar ticket con la solución vacía y después con solo espacios. Debe mostrar validación, conservar En atención y no crear solución final.
6. Escribir una solución de prueba y pulsar Terminar ticket. Verificar Terminado y un único registro Solución final, además de las observaciones anteriores.
7. Recargar y verificar que permanece Terminado, con la solución visible y sin controles para nuevas notas o cambios.
8. Cerrar sesión y entrar como DEMO000001AAA. En Mis tickets, pulsar Actualizar y comprobar el estado Terminado del mismo folio. Repetir la consulta después de reiniciar la aplicación si se desea comprobar persistencia entre arranques.
9. Como administrador DEMO000005DDD, abrir Administrar tickets, actualizar y comprobar el estado del folio y su inclusión en Finalizados.
10. Con usuario final o administrador, abrir directamente /sigeti-sprint1/seguimientos.xhtml: debe responder 403. Sin sesión, debe redirigir al login.

No hace falta crear otro soporte en la base real para probar el aislamiento: las pruebas automatizadas usan dos soportes independientes. No se deben reasignar masivamente los tickets antiguos para esta demostración.

Consultas de comprobación, ajustando el identificador si se usa otro ticket:

```sql
SELECT id_ticket, folio, estatus, rfc_tecnico
FROM sigeti_sprint1.ticket
WHERE id_ticket = 5;

SELECT ID_Ticket, Numero_Seguimiento, Fecha, Descripcion,
       CAST(Es_Solucion_Final AS UNSIGNED) AS es_solucion_final
FROM sigeti_sprint1.SEGUIMIENTO
WHERE ID_Ticket = 5
ORDER BY Numero_Seguimiento;
```
