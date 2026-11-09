# SIGETI · Copia candidata para el Sprint 1

Implementación separada para revisar AUT-US-01, INV-US-02 y TIC-US-03 juntos. Conserva el acceso por RFC de AUT, adapta el modelo de equipos y completa el alta de tickets con el diseño visual del PHP. No es un merge aplicado a las ramas del equipo.

## Contenido

Cuatro módulos Maven: entidad, persistencia, negocio y vista. Incluye sesión y permisos, consulta de equipos propios, formulario JSF/PrimeFaces, validación de resguardo dentro de la transacción, folio UUID único y estado Enviado. También contiene una base de demostración y un WAR precompilado.

## 1. Tomcat y Java

Descarga **64-bit Windows zip**, en **Binary Distributions → Core**, desde la [página oficial de Tomcat 10](https://tomcat.apache.org/download-10.cgi). Usa la serie **10.1**, no 10.0. Descomprime el ZIP; no hace falta instalarlo como servicio. La carpeta correcta contiene `bin`, `conf` y `lib`.

La aplicación requiere **Java 21**. En esta computadora se verificó Java 21 en la carpeta `jbr` de IntelliJ IDEA 2025.3.2. El script intenta detectarla cuando JAVA_HOME no está configurado. Puedes indicarla mediante `-JavaHome` sin cambiar el Java global de Windows.

## 2. Base de demostración

Con MySQL Server utiliza Workbench; con MariaDB de XAMPP puedes utilizar phpMyAdmin. Para una base de demostración nueva y vacía ejecuta estos archivos en orden:

1. `sql/01_esquema_sprint1.sql`
2. `sql/02_datos_demo.sql`

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
EXIT;
```

La contraseña solicitada aquí es la de MySQL; es diferente de la de las cuentas de demostración.

## 3. Arrancar

Abre PowerShell en esta carpeta y sustituye la ruta del ejemplo por la carpeta real donde descomprimiste Tomcat:

```powershell
.\iniciar-SIGETI.ps1 -TomcatHome "C:\Users\jkari\Downloads\apache-tomcat-10.1.61" -JavaHome "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr" -Motor MySQL
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

Entra como A, consulta Mis equipos y registra un ticket. Debes recibir un folio con estado Enviado. Cierra sesión y entra como B para comprobar que sólo aparece su equipo. Con la cuenta sin equipos debe mostrarse el aviso y quedar bloqueado el registro. Las herramientas de atención y administración de sprints posteriores no están implementadas.

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

Produce `vista/target/sigeti.war`. El paquete ya incluye un WAR construido con javac y las dependencias locales; no procede de una ejecución Maven exitosa. La descarga de dependencias de los complementos de Maven quedó bloqueada por los permisos de esta sesión. Antes de llevarlo al repositorio compartido valida también la construcción Maven normal.

## Procedencia y revisión

La autenticación procede de AUT `9295554`. El modelo de equipo utiliza la clave textual de inventario del SQL PHP y añade la relación de resguardo ausente. La consulta sigue el filtrado por RFC de INV `298f7b2`. El alta conserva la intención de TIC `d0ac71e` corrigiendo sus incompatibilidades, sin copiar su login ni sus POM.

Esta base es un modelo mínimo del Sprint 1, no una migración automática del proyecto anterior. Lee `VALIDACION.md` para distinguir lo probado del flujo de navegador pendiente. Después de probarlo y revisarlo con el equipo se puede trasladar la propuesta a una rama de integración autorizada. No se han creado commits, merges ni pushes.
