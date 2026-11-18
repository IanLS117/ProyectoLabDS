# SIGETI · Trabajo del equipo

Esta rama incluye la base compartida del equipo y los avances PBI-TIC-US-02 y PBI-TIC-US-03: consulta de tickets propios, atención y cierre con solución. Conserva el seguimiento, la asignación automática y la consulta del administrador ya incorporados. Para configurar el botón ▶, sigue [ARRANQUE_INTELLIJ.md](ARRANQUE_INTELLIJ.md).

## Contenido

Cuatro módulos Maven: entidad, persistencia, negocio y vista. Incluye sesión y permisos, equipos propios, registro de tickets, asignación automática, Mis tickets, observaciones, cierre con solución y consulta administrativa. Cada integrante genera su WAR con Maven; las carpetas target no se versionan.

## Probar los avances 02 y 03

`PBI-TIC-US-03` parte del commit guardado de `PBI-TIC-US-02` e incluye ambas funciones. Para probar el avance completo, descarga la rama 03 y abre `SIGETI-master/pom.xml` como proyecto Maven. No hace falta combinar manualmente las dos ramas ni copiar archivos entre carpetas.

- [Mis tickets: comportamiento y pruebas](PRUEBA_MIS_TICKETS.md).
- [Atención y cierre: comportamiento y pruebas](PRUEBA_ATENCION_TICKETS.md).

Usa Java 21 y las rutas, puerto y credenciales de tu computadora. Al actualizar una base existente, comprueba únicamente las estructuras pendientes indicadas abajo. Los PBI 02 y 03 no requieren otro script SQL aparte de la base compartida. Los tickets antiguos sin técnico asignado no se reasignan automáticamente; la asignación de los nuevos permite probar la atención.

## 1. Tomcat y Java

Descarga **64-bit Windows zip**, en **Binary Distributions → Core**, desde la [página oficial de Tomcat 10](https://tomcat.apache.org/download-10.cgi). Usa la serie **10.1**, no 10.0. Descomprime el ZIP; no hace falta instalarlo como servicio. La carpeta correcta contiene `bin`, `conf` y `lib`.

Compila y ejecuta con **Java 21**. Configura ese JDK tanto en el proyecto como en Maven y Tomcat. Cada integrante usa la ruta de su instalación; no copies una ruta personal de otra computadora. El script intenta detectar el Java de IntelliJ si JAVA_HOME no está configurado.

## 2. Base de demostración

Con MySQL Server utiliza Workbench; con MariaDB de XAMPP puedes utilizar phpMyAdmin. Para una base de demostración nueva y vacía ejecuta estos archivos en orden:

1. `sql/01_esquema_sprint1.sql`
2. `sql/02_datos_demo.sql`
3. `sql/03_asignacion_automatica.sql`
4. `sql/04_tabla_seguimiento.sql`

Si ya existe la base, **no repitas 01 ni 02**. Revisa si faltan la columna `ticket.rfc_tecnico` o la tabla `SEGUIMIENTO` y aplica respectivamente 03 o 04 una sola vez. La [guía de IntelliJ](ARRANQUE_INTELLIJ.md) incluye las consultas de comprobación. Hibernate valida el esquema al arrancar; no crea estas estructuras automáticamente.

Crean **sigeti_sprint1**. Son scripts para una base nueva y vacía; no son una migración de datos existentes ni deben repetirse sobre tablas ya creadas. No utilices el instalador antiguo del PHP.

Si estás migrando una base existente, importa su respaldo en el servidor nuevo en lugar de repetir los datos de demostración. En la computadora del usuario ya se verificó la restauración del ticket anterior y un alta nueva con MySQL 9.4.0; esa base no necesita reinstalarse.

El servicio que XAMPP denomina MySQL utiliza MariaDB. El arranque predeterminado conserva esa configuración. Para el MySQL Server del equipo usa `-Motor MySQL`; el script selecciona `org.hibernate.dialect.MySQLDialect` sin editar archivos compartidos. Consulta `PRUEBA_MYSQL_EQUIPO.md` para validar las versiones informadas por los compañeros.

Alternativa: abre una terminal en esta carpeta y ejecuta:

```powershell
& C:\xampp\mysql\bin\mysql.exe -u root -p
```

Dentro del cliente MySQL:

```sql
SOURCE sql/01_esquema_sprint1.sql;
SOURCE sql/02_datos_demo.sql;
SOURCE sql/03_asignacion_automatica.sql;
SOURCE sql/04_tabla_seguimiento.sql;
EXIT;
```

La contraseña solicitada aquí es la de MySQL; es diferente de la de las cuentas de demostración.

## 3. Arrancar

Abre PowerShell en esta carpeta y sustituye la ruta del ejemplo por la carpeta real donde descomprimiste Tomcat:

```powershell
.\iniciar-SIGETI.ps1 -TomcatHome "RUTA_DE_TOMCAT" -JavaHome "RUTA_DE_JAVA_21" -Motor MySQL
```

El script pide la contraseña de MySQL y arranca Tomcat en primer plano. Su usuario predeterminado es `root`; se cambia con `-DbUser`. Para otro puerto de MySQL indica `-DbUrl`, por ejemplo `jdbc:mysql://127.0.0.1:3307/sigeti_sprint1?serverTimezone=UTC`.

El ejemplo anterior usa MySQL Server. Si utilizas MariaDB de XAMPP, cambia el argumento por `-Motor MariaDB`.

Para conectar al servidor MySQL de un compañero, ese compañero puede ejecutar el mismo script en su propia computadora añadiendo `-Motor MySQL` y usando sus rutas y credenciales. La contraseña se introduce en su terminal, no se comparte ni se guarda en el código. No apuntes `-Motor MySQL` al MariaDB de XAMPP.

Al ejecutar desde IntelliJ o configurar Tomcat sin el script, las variables son `SIGETI_DB_USER`, `SIGETI_DB_PASSWORD`, `SIGETI_DB_URL` y `SIGETI_DB_ENGINE`. El selector admite `MySQL` o `MariaDB`. Si se omite, se conserva el valor MariaDB del descriptor para mantener la demostración local existente.

Abre **http://localhost:8087/sigeti-sprint1/login.xhtml**. Mantén la terminal abierta mientras pruebas; para detener el servidor presiona **Ctrl+C**.

Se genera una configuración propia en `.runtime` dentro de esta copia; no se alteran las aplicaciones de tu instalación de Tomcat. Sus mensajes y logs aparecen en la terminal y en `.runtime/logs`. Usa una instalación de Tomcat detenida.

## 4. Probar

Todas las cuentas son ficticias. Contraseña común: **Sprint1Demo!**, almacenada como hash PBKDF2.

| RFC | Prueba | Equipos |
|---|---|---|
| DEMO000001AAA | Usuario final A | INV-DEMO-001 |
| DEMO000002BBB | Usuario final B | INV-DEMO-002 |
| DEMO000003CCC | Usuario sin equipos | Ninguno |
| DEMO000004SSS | Soporte | Sin permiso para registrar tickets de usuario final |
| DEMO000005DDD | Administrador | Sin permiso para registrar tickets de usuario final |

Entra como A, consulta Mis equipos y registra un ticket. Debes recibir un folio con estado Enviado. En Mis tickets deben aparecer únicamente sus incidencias. Cierra sesión y entra como B para comprobar la separación de equipos y tickets. Con la cuenta sin equipos debe mostrarse el aviso y quedar bloqueado el registro. Consulta [PRUEBA_ASIGNACION.md](PRUEBA_ASIGNACION.md) para verificar el técnico asignado.

Como soporte, abre Seguimientos, selecciona un ticket asignado e inicia la atención. Puedes guardar observaciones mientras esté En atención. Para terminarlo debes escribir la solución final; si falta, se muestra un aviso flotante y no se cierra. El historial conserva la observación y distingue la solución final. El usuario ve Terminado en Mis tickets y el administrador lo cuenta en Finalizados. Las guías de cada PBI detallan las comprobaciones.

Comprueba el guardado en phpMyAdmin:

```sql
SELECT folio, rfc_usuario_final, numero_inventario, estatus, fecha_hora_creacion
FROM sigeti_sprint1.ticket
ORDER BY id_ticket DESC;
```

## Compilar cambios

En IntelliJ abre el **pom.xml de esta carpeta**, configura Java 21 para Maven y ejecuta `package` desde el proyecto raíz. Desde una terminal con Java 21 y Maven:

```text
mvn package
```

Produce `vista/target/sigeti.war`. Maven compila los cuatro módulos y ejecuta las pruebas automáticas sin necesitar tu MySQL. La primera compilación requiere Internet para descargar dependencias. Ejecuta desde el POM padre, no únicamente desde vista. Pasaron 60 pruebas de asignación, consulta y atención, incluidas comprobaciones con una base temporal en memoria. En el entorno local también se probó el flujo de registro, atención, observación, rechazo de cierre vacío, solución final y consulta del estado. Cada integrante debe comprobar su conexión y arranque con su configuración.

## Procedencia y revisión

La autenticación procede de AUT `9295554`. El modelo de equipo utiliza la clave textual de inventario del SQL PHP y añade la relación de resguardo ausente. La consulta sigue el filtrado por RFC de INV `298f7b2`. El alta conserva la intención de TIC `d0ac71e` corrigiendo sus incompatibilidades, sin copiar su login ni sus POM.

La base del Sprint 2 conserva los avances revisados de ASI-US-01, TIC-US-04 y TIC-US-05. Sus contenidos se incorporaron al commit de base compartida; ese paso no sustituyó el historial original de las ramas de los compañeros. Los PBI 02 y 03 agregan los cambios descritos arriba y sus guías de prueba. `VALIDACION.md` conserva la evidencia histórica del Sprint 1.
