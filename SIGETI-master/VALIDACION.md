# Comprobaciones de esta copia

## Estado del avance

Las capturas compartidas muestran el recorrido válido de los tres PBI en la copia de demostración con MariaDB de XAMPP: acceso por RFC, consulta de equipos propios y alta de ticket persistido con folio y estado Enviado. También muestran rechazo de credenciales erróneas, validaciones de campos, cuenta sin equipos y acceso directo de Soporte denegado con HTTP 403.

Además, el usuario informó que un compañero completó las validaciones guiadas con MySQL Community Server 9.4.0. Se registra como prueba realizada por el compañero y reportada por el usuario, no como ejecución directa del agente. MySQL 8.0.17 todavía no se ha probado funcionalmente.

Posteriormente, las capturas del propio usuario confirman MySQL Community Server 9.4.0 en el puerto 3306, la restauración del ticket anterior y el registro de un ticket nuevo desde SIGETI. La consulta de Workbench muestra ambos folios con el usuario, equipo y estado esperados.

Estos resultados respaldan la copia candidata del avance, pero no constituyen aprobación completa del sprint. El usuario autorizó trasladar los archivos a integracion/sprint-1; la escritura en el repositorio sigue pendiente porque esta sesión no recibió ese permiso. Los merges, commits y pushes no están autorizados.

## Ejecutado

- Compilación de los cuatro módulos con javac para Java 21: correcta.
- Construcción completa con Maven y Java 21 el 8 de noviembre de 2026: BUILD SUCCESS para entidad, persistencia, negocio y vista sobre la copia con el parche aplicado en work/integracion-base/SIGETI-master. Se generó vista/target/sigeti.war. Maven no encontró pruebas automatizadas en los módulos. El primer intento en la carpeta de demostración compiló el código, pero no pudo reemplazar el WAR existente; la comprobación completa se hizo en la copia para conservar la demostración en uso.
- Lectura de XML y XHTML como documentos XML: correcta.
- Inicialización de las siete entidades JPA con Hibernate 6.6.11 sin conexión: correcta. No valida las consultas contra MySQL.
- Diecinueve comprobaciones locales: contraseña de demostración; rechazo de contraseña incorrecta y texto sin hash; sesión, equipo, tipo y descripción obligatorios; límite de descripción; permisos de las rutas de equipos y tickets; redirección sin sesión, inicialización de JPA, dialecto MariaDB configurado y cláusula de bloqueo compatible.
- Creación del esquema y sus datos en una instancia aislada de MariaDB 10.4 de XAMPP, sin conexiones de red: correcta.
- Siete aserciones SQL sobre cuentas, asignaciones, usuario sin equipos, catálogo, alta con valores predeterminados y reversión: correctas.
- WAR construido con las clases Java 21, recursos y dependencias locales. La API completa de Jakarta EE no se incluye dentro del WAR.
- Tomcat 10.1.61 y Java 21 reconocidos en la computadora. Se preparó una instancia propia y se cargaron Weld, Mojarra 4.0.1 y PrimeFaces 14 sin errores de inicialización de la aplicación. El conector HTTP no pudo abrir su conexión local por la restricción de permisos de esta sesión.

## Corrección durante la prueba de interfaz

Las capturas del usuario confirman la importación del esquema con sus datos y el inicio de sesión de DEMO000001AAA en la aplicación. La pantalla de equipos se mostraba sin tabla ni aviso: la plantilla contenía f:metadata, que debe declararse en la página que utiliza la plantilla. Eso impedía ejecutar la carga inicial mediante f:viewAction. Se corrigió tanto Mis equipos como Registrar ticket, se actualizó el WAR y se comprobó la estructura de metadatos de las dos páginas. Después de reiniciar, la captura del usuario confirma la carga de INV-DEMO-001 en Mis equipos.

La distribución de metadatos sigue el patrón de plantillas de la [documentación de Jakarta Faces](https://jakarta.ee/specifications/faces/2.3/vdldoc/f/metadata.html).

## Corrección de compatibilidad con MariaDB

Después del reinicio, la captura del usuario confirma que Mis equipos muestra INV-DEMO-001 para DEMO000001AAA. El formulario de registro carga el equipo y el catálogo. El intento de alta falló antes del INSERT: el registro de Tomcat muestra que Hibernate detectó MySQL y generó `FOR SHARE`, que la instancia de MariaDB no acepta.

Se configuró explícitamente `org.hibernate.dialect.MariaDBDialect` en la unidad de persistencia, conservando la transacción y los bloqueos de validación del resguardo y del catálogo. La comprobación de JPA confirma que ese dialecto genera `LOCK IN SHARE MODE`. La consulta antigua reproduce el error 1064 en la base aislada. La consulta corregida, el bloqueo de equipo y un INSERT seguido de ROLLBACK se comprueban en esa misma base. El SQL de bloqueo compartido está documentado en [MariaDB](https://mariadb.com/docs/server/reference/sql-statements/data-manipulation/selecting-data/lock-in-share-mode).

El código fuente, el JAR de persistencia dentro del WAR y el ZIP de entrega contienen la configuración corregida. Después de reiniciar Tomcat y repetir el alta, la captura del usuario muestra la confirmación de registro con folio y estado Enviado.

## Recorrido observado en el navegador del usuario

- Login correcto con DEMO000001AAA y menú de usuario final.
- Mis equipos muestra INV-DEMO-001, con sus datos de inventario.
- El formulario permite seleccionar ese equipo, Hardware y una descripción.
- Tras enviar, se muestra «El ticket se registró correctamente», con folio y estado Enviado.
- La captura de phpMyAdmin confirma mediante una consulta independiente que el ticket está guardado con el mismo folio, DEMO000001AAA, INV-DEMO-001, tipo 1, estado Enviado y fecha de creación.
- La captura de DEMO000002BBB muestra únicamente INV-DEMO-002 en Mis equipos, sin el equipo del usuario A.
- La cuenta sin equipos muestra una tabla vacía con el aviso de que no tiene equipos asignados.
- Al abrir Registrar ticket con esa cuenta, aparecen el aviso de resguardo requerido, los campos deshabilitados y el botón Registrar ticket deshabilitado. Esto confirma la restricción de interfaz; el rechazo de una petición manipulada aún debe comprobarse por separado.
- Al enviar el formulario vacío desde la cuenta A, la captura muestra los tres mensajes de campos obligatorios: equipo, tipo y descripción.
- Al seleccionar equipo y tipo e introducir únicamente espacios en la descripción, la captura muestra «La descripción del problema es obligatoria» y no aparece una confirmación ni un nuevo folio. La comprobación independiente de que estas pruebas no añadieron filas sigue pendiente.
- La captura del login muestra «RFC o contraseña incorrectos» para el intento con credenciales erróneas.
- La captura de DEMO000004SSS muestra el rol Soporte técnico, con Inicio y Cerrar sesión, sin Mis equipos ni Registrar ticket.
- Al abrir directamente /nuevo-ticket.xhtml manteniendo esa sesión de Soporte, la captura muestra HTTP 403 Forbidden y no el formulario. Esto confirma protección de esa ruta en el servidor, además de la restricción del menú.

Estas observaciones se basan en las capturas compartidas por el usuario, no en solicitudes de navegador ejecutadas por el agente. Corresponden a la copia local con MariaDB de XAMPP. La validación posterior del compañero en MySQL se registra por separado a continuación.

## Preparación para el MySQL Server del equipo

Las capturas recibidas confirman dos servidores MySQL Community Server 9.4.0 y uno 8.0.17. El cuarto servidor se supone 9.4.0, pero todavía no está confirmado.

Se añadió `-Motor MySQL` al arranque y la variable `SIGETI_DB_ENGINE` a la configuración de persistencia. El valor predeterminado sigue siendo MariaDB para conservar el entorno local ya probado. La selección modifica el dialecto al arrancar, sin editar el código por compañero. Se recompiló el código y pasaron catorce comprobaciones adicionales de configuración, además de las diecinueve anteriores. Esas pruebas inicializan JPA sin conexiones; no validan la ejecución contra MySQL.

La guía `PRUEBA_MYSQL_EQUIPO.md` contiene los pasos para repetir el flujo en los servidores del equipo. El WAR y el ZIP incluyen el selector actualizado; no se modificó el servidor local que está ejecutándose.

El primer intento de un compañero mostró «No fue posible acceder al sistema». La captura de su registro identifica una dirección JDBC incompleta, sin el prefijo `jdbc:mysql://` válido, y `Unable to make JDBC Connection`. Esto no confirma un rechazo de la contraseña de SIGETI ni una incompatibilidad del esquema. Se añadió validación del prefijo tanto al script como a la configuración Java, con tres casos de regresión para direcciones incompletas. El script también rechaza ese caso antes de acceder a archivos de Tomcat. Tras corregir el comando, el usuario informó que el compañero pudo continuar.

## Validación reportada en MySQL 9.4.0

El usuario confirmó que a su compañero le funcionó la aplicación y que realizó las validaciones con su servidor MySQL Community Server 9.4.0. El recorrido solicitado en esa prueba incluía login, equipos propios, alta y consulta del ticket guardado; la guía también incluía credenciales incorrectas, campos obligatorios, cuenta sin equipos y rechazo HTTP 403 de Soporte.

Se consigna el resultado global favorable comunicado por el usuario, sin extenderlo a pruebas adicionales no reportadas, como concurrencia, peticiones manipuladas, fallos forzados de persistencia o ejecución Maven. El agente no accedió a la computadora ni a la base del compañero. Las evidencias de la demostración deben conservarse para la revisión del equipo.

## Migración local comprobada a MySQL 9.4.0

El usuario instaló MySQL Server y Workbench. La consulta independiente de versión confirmó 9.4.0, MySQL Community Server - GPL y puerto 3306. El encabezado de Workbench mostraba otra versión; para esta comprobación se utiliza el resultado de la consulta SQL.

El respaldo exportado desde phpMyAdmin se importó en el servidor nuevo. El primer intento falló con 1046 por no seleccionar una base. Tras añadir CREATE DATABASE IF NOT EXISTS y USE sigeti_sprint1, la captura indicó 43 sentencias ejecutadas sin errores.

La prueba posterior muestra login y confirmación de alta desde la aplicación. Workbench confirma los dos registros siguientes, ambos con DEMO000001AAA, INV-DEMO-001 y estado Enviado:

- Ticket restaurado: TCK-EECB0F4E-D700-4F1E-9393-E2A651868D90.
- Ticket nuevo: TCK-9C8F7E56-5A41-4532-9066-BDF8424F8791.

Esto acredita la conservación del registro de referencia y la creación de otro en el MySQL local, según las capturas del usuario. No acredita el respaldo de otras bases ni de proyectos PHP ajenos a SIGETI. La desinstalación de XAMPP todavía no fue ejecutada por el agente y debe conservarse el respaldo antes de realizarla.

## Comprobaciones pendientes

- Repetir `mvn package` en C:/SIGETI/SIGETI-master después de aplicar el parche y recargar Maven en IntelliJ; la construcción completa de la copia de revisión ya pasó.
- Contrastar en BD que los rechazos de formulario vacío y descripción con espacios no añadieron filas; los mensajes de rechazo ya se observaron en las capturas.
- Comprobar rechazo de un equipo ajeno manipulado en la petición, incluso con los controles de interfaz eludidos.
- Completar las pruebas de administrador y de acceso después de cerrar sesión. Para Soporte ya se observó el rechazo HTTP 403 al abrir directamente la ruta de registro de tickets.
- Registrar dos tickets concurrentes y confirmar folios distintos.
- Comprobar que un fallo de persistencia no deje un alta parcial ni muestre confirmación de éxito.
- Validar MySQL 8.0.17 si ese entorno seguirá utilizándose y confirmar la versión del cuarto compañero. La validación guiada de MySQL 9.4.0 ya fue reportada como exitosa.

Estas comprobaciones no equivalen todavía a una aprobación funcional completa. El código y WAR son una propuesta ejecutable para terminar las pruebas en un servidor compatible. No se modificó la base ni el repositorio del equipo.
