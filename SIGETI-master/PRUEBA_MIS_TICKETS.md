# PBI-TIC-US-02: consulta de tickets propios

Historia: como usuario final, consultar los tickets registrados y su estado actual.
Issue: https://github.com/IanLS117/ProyectoLabDS/issues/12

Base de trabajo: integración compartida del Sprint 2, guardada localmente en el commit 2d3fc9c.
Esta funcionalidad no requiere una nueva migración SQL ni cargar otra vez los datos de prueba.

## Comportamiento implementado

- Acceso por Mis tickets en el menú y en el inicio del usuario final.
- La identidad se obtiene de la sesión, sin aceptar un RFC de los parámetros de la página.
- La consulta filtra en la base por el propietario del ticket. Se comprueban permiso crear_ticket y perfil de usuario final en la base.
- Folio, equipo, tipo de incidencia, descripción, fecha y estado guardado. Orden de fecha descendente y, en caso de empate, identificador descendente.
- Botón Actualizar para volver a consultar y paginación de diez registros.
- Mensaje de lista vacía y mensaje separado de fallo de consulta.
- El listado incluye los tickets anteriores que todavía no tienen técnico asignado.
- La ruta se protege por sesión y permiso. Soporte y administrador conservan sus pantallas existentes.

Las condiciones de prueba siguientes se derivan de la historia; los criterios de aceptación de la issue todavía están como plantilla y requieren revisión del equipo.

## Pruebas automatizadas

Desde el directorio que contiene el pom.xml padre, ejecutar Maven package con Java 21.
MisTicketsTest prueba consultas reales con Hibernate contra H2 en memoria: separación A/B, los tres estados, orden, lista vacía, técnicos y administradores rechazados, usuario sin perfil, identidades vacías o inválidas, permiso revocado, lectura de relaciones después del cierre de la conexión y cambio de estado visible en una nueva consulta.

La base de estas pruebas es temporal, no usa las variables de MySQL y no modifica sigeti_sprint1. H2 y JUnit son dependencias de pruebas y no forman parte del WAR.

## Verificación manual pendiente en MySQL y navegador

Usar las cuentas de demostración existentes, si no fueron modificadas. Contraseña común: Sprint1Demo!

1. Acceder como DEMO000001AAA y abrir Mis tickets. Deben verse sus tickets y el folio del último ticket registrado. Comparar los resultados con SELECT folio, estatus FROM sigeti_sprint1.ticket WHERE rfc_usuario_final = 'DEMO000001AAA' ORDER BY fecha_hora_creacion DESC, id_ticket DESC;
2. Cerrar sesión, acceder como DEMO000002BBB y comprobar que no aparece ningún folio de A. Si B no tiene tickets, debe aparecer el mensaje de lista vacía. No hace falta crear un ticket de prueba para comprobar ese resultado vacío.
3. Acceder como DEMO000003CCC y comprobar la lista vacía, si esa cuenta sigue sin tickets.
4. Comprobar que folio, equipo, tipo, fecha y estado se muestran sin errores al cargar la página y al usar Actualizar. En caso de más de diez registros, comprobar la paginación.
5. Con soporte DEMO000004SSS y administrador DEMO000005DDD, comprobar que no aparece Mis tickets. El acceso directo a /sigeti-sprint1/mis-tickets.xhtml debe responder 403.
6. Sin sesión, abrir esa ruta y comprobar la redirección al login.
7. Una vez implementado TIC-US-03, cambiar el estado de un ticket mediante la aplicación y comprobar que Actualizar refleja el nuevo estado para su propietario. No es necesario modificar el estado directamente por SQL.

Con el contexto y puerto usados en la prueba local, la dirección es http://localhost:8080/sigeti-sprint1/mis-tickets.xhtml. Ajustar el puerto/contexto si la configuración del entorno es diferente.
