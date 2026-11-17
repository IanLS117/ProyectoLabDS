# PBI-ASI-US-01: asignación automática

## Reglas implementadas

- Los candidatos son los usuarios con rol 2 (soporte).
- La carga activa cuenta tickets `Enviado` y `En atención`, excluye `Terminado`.
- Se incluyen técnicos con cero tickets. Gana la menor carga; el RFC ascendente desempata.
- Sin técnicos, el registro muestra un aviso y revierte la transacción.
- El estado inicial sigue siendo `Enviado`.
- La selección pertenece a negocio (`AsignadorTecnico`); las consultas pertenecen a `TicketDAO`.
- El DAO bloquea la fila del rol 2 antes de leer y mantiene el bloqueo hasta confirmar el alta. Esto serializa las asignaciones de este flujo incluso con varias instancias de la aplicación. Cualquier futuro flujo de reasignación deberá respetar el mismo bloqueo.

## Paso 1: actualizar la base existente

Detén la aplicación. En MySQL Workbench abre y ejecuta **una sola vez**
`sql/03_asignacion_automatica.sql` sobre `sigeti_sprint1`.
No vuelvas a ejecutar `01_esquema_sprint1.sql` ni `02_datos_demo.sql`.
La migración agrega una columna, un índice y una clave foránea; conserva los datos.
Los tickets anteriores quedan sin técnico y no se reparten retroactivamente.
El nuevo WAR necesita esta columna porque Hibernate valida el esquema al arrancar.

Esta rama también necesita la tabla `SEGUIMIENTO` del PBI anterior. Si no existe,
ejecuta `sql/04_tabla_seguimiento.sql` una sola vez. Para arrancar con el botón ▶,
sigue [ARRANQUE_INTELLIJ.md](ARRANQUE_INTELLIJ.md).

## Paso 2: iniciar y registrar

Compila con Java 21 y `mvn package`, despliega `vista/target/sigeti.war`
y utiliza la configuración MySQL existente. Inicia sesión como usuario final,
selecciona su equipo y registra un ticket desde el formulario habitual.

## Paso 3: comprobar en Workbench

```sql
SELECT t.folio, t.estatus, t.rfc_tecnico, u.nombre, u.id_rol
FROM sigeti_sprint1.ticket t
LEFT JOIN sigeti_sprint1.usuario u ON u.rfc = t.rfc_tecnico
ORDER BY t.id_ticket DESC;

SELECT u.rfc, u.nombre, COUNT(t.id_ticket) AS carga_activa
FROM sigeti_sprint1.usuario u
LEFT JOIN sigeti_sprint1.ticket t
  ON t.rfc_tecnico = u.rfc AND t.estatus IN ('Enviado', 'En atención')
WHERE u.id_rol = 2
GROUP BY u.rfc, u.nombre
ORDER BY carga_activa, u.rfc;
```

Con los cinco usuarios actuales, cada ticket nuevo debe mostrar
`DEMO000004SSS` como técnico, con rol 2. Los usuarios finales y el administrador
no participan en la selección.

## Validación y alcance

Las cinco pruebas de negocio comprueban menor carga, carga cero, desempate,
único técnico y ausencia de técnicos; se ejecutan con `mvn package`.
No necesitan una conexión a la base de datos.

Para validar el reparto entre varios técnicos se requiere una base de pruebas
separada con al menos dos usuarios de rol 2. Allí se debe comprobar también que
los tickets terminados no aumentan la carga y que dos altas simultáneas no
seleccionan usando un conteo anterior al alta precedente.
Estas pruebas de integración MySQL y la prueba del formulario no están
cubiertas por las pruebas unitarias. No se han ejecutado scripts contra la
base local por el agente durante la implementación. Posteriormente, las capturas
del usuario confirman la ejecución correcta de los scripts 03 y 04 en Workbench;
el registro de Tomcat confirma el despliegue y el usuario confirmó el login.
El usuario indicó haber realizado la prueba guiada de registro; no se adjuntó
el resultado SQL para comprobar de forma independiente la asignación.
