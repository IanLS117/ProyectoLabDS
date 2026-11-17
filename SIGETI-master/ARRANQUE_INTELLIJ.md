# Arrancar SIGETI con el botón ▶ de IntelliJ

## 1. Preparación por computadora

- Java 21, MySQL iniciado y Apache Tomcat descomprimido.
- El proyecto recomienda Tomcat 10.1. También se confirmó el despliegue con Tomcat 11.0.8 y Java 21 desde IntelliJ, y el usuario confirmó el inicio de sesión. No confundir Tomcat con TomEE.
- IntelliJ con integración Tomcat disponible y el complemento Tomcat and TomEE habilitado. Si la edición/licencia no permite esta integración, usar el script del README.
- Descargar la rama y abrir `SIGETI-master/pom.xml` como proyecto Maven. Deben aparecer entidad, persistencia, negocio y vista.

En File → Project Structure selecciona Java 21 como Project SDK. En Settings → Build, Execution, Deployment → Build Tools → Maven, usa Maven Bundled y configura Java 21 para Runner (JRE) e Importing (JDK for importer). Recarga los proyectos Maven si cambiaste estas opciones.

## 2. Base de datos

Cada integrante utiliza sus propias credenciales. Si `sigeti_sprint1` no existe, ejecuta en Workbench los archivos SQL 01, 02, 03 y 04 en orden, una sola vez.

Si la base ya existe, no vuelvas a instalarla ni a cargar datos de demostración. Ejecuta primero estas consultas de lectura:

```sql
USE sigeti_sprint1;
SHOW TABLES;
SHOW COLUMNS FROM ticket LIKE 'rfc_tecnico';
```

- Si falta `rfc_tecnico`, ejecuta `sql/03_asignacion_automatica.sql`.
- Si falta `seguimiento` o `SEGUIMIENTO`, ejecuta `sql/04_tabla_seguimiento.sql`.
- Si ya existen, no repitas esos scripts. Si la estructura procede de otra versión, compárala con los scripts antes de cambiarla.

Estos cambios conservan los datos existentes. Los tickets anteriores quedan sin técnico; la asignación se aplica a los nuevos. En las capturas del usuario, 03 y 04 ya se ejecutaron correctamente en su MySQL local.

## 3. Primera compilación

En el panel Maven ejecuta desarrollo → Lifecycle → package. Espera BUILD SUCCESS. Esto genera `vista/target/sigeti.war` y ejecuta las pruebas. No hace falta arrancar MySQL para compilar.

Si Maven no aparece, agrega `SIGETI-master/pom.xml` desde el panel Maven. Si falla la descarga de dependencias, revisa la conexión a Internet y la configuración de Maven.

## 4. Configurar Tomcat una vez

En Run → Edit Configurations → + → Tomcat Server → Local:

| Campo | Valor |
|---|---|
| Name | SIGETI |
| Application server → Configure | Carpeta local de Apache Tomcat que contiene bin, conf y lib |
| JRE | Java 21 |
| HTTP port | 8087 |
| URL | http://localhost:8087/sigeti-sprint1/login.xhtml |

En Deployment, pulsa + → External Source y elige `vista/target/sigeti.war` dentro de tu copia. En Application context escribe `/sigeti-sprint1`. No es necesario crear un artifact manual.

En Before launch agrega Run Maven Goal: selecciona el POM raíz `SIGETI-master/pom.xml` (desarrollo) y el comando `package`. Esta tarea debe terminar correctamente antes de arrancar Tomcat. La tarea Build de IntelliJ por sí sola no regenera el WAR de Maven.

En Startup/Connection → Run → Environment variables agrega:

| Nombre | Valor local |
|---|---|
| SIGETI_DB_USER | Tu usuario MySQL, por ejemplo root |
| SIGETI_DB_PASSWORD | La contraseña de ese usuario MySQL |
| SIGETI_DB_ENGINE | MySQL |
| SIGETI_DB_URL | jdbc:mysql://127.0.0.1:3306/sigeti_sprint1?serverTimezone=UTC |

Adapta host y puerto si tu conexión es diferente. Si utilizas Debug, configura también sus variables. No publiques contraseñas ni actives Store as project file para esta configuración con credenciales. La carpeta .idea y los archivos .iml se excluyen de Git.

Guarda con Apply y OK. Esta configuración es local: cada compañero señala sus propias rutas y credenciales una vez.

## 5. Después de cambiar código o actualizar la rama

1. Detén SIGETI con el botón Stop. No dejes otra instancia arrancada mediante el script.
2. Si cambió un pom.xml, recarga Maven. Si la rama agrega scripts de actualización, lee sus instrucciones y aplica solo los pendientes.
3. Selecciona SIGETI y pulsa ▶. Maven compila y prueba antes de que Tomcat despliegue el WAR actualizado.
4. Espera el despliegue correcto y abre el login. Si falla, revisa el primer error de Maven o Tomcat; no continúes usando un WAR anterior.

Para una comprobación desde cero, con Tomcat detenido ejecuta `clean package` desde el POM raíz. No se necesitan copias manuales de JAR ni WAR de otro compañero.

## 6. Prueba mínima antes de entregar un PBI

Entra con DEMO000001AAA y la contraseña de demostración Sprint1Demo!, registra un ticket de su equipo y comprueba en Workbench el folio y el técnico. Las consultas están en PRUEBA_ASIGNACION.md. Con los datos originales, el técnico esperado es DEMO000004SSS.

Compilar correctamente no verifica las credenciales, el esquema MySQL ni el formulario. El despliegue está respaldado por el registro de Tomcat y el usuario confirmó el login. El usuario indicó haber realizado la prueba guiada del ticket; no se adjuntó el resultado SQL para verificarlo independientemente. El reparto con varios técnicos y las altas simultáneas siguen pendientes de prueba en MySQL. Los cinco tests de negocio no consultan MySQL.

## Problemas frecuentes

| Mensaje o síntoma | Qué revisar |
|---|---|
| release version 21 not supported | Maven debe ejecutar con JDK 21, no basta cambiar el SDK del proyecto. |
| No encuentra entidad o persistencia al compilar vista | Ejecuta package desde desarrollo, el POM raíz de los cuatro módulos. |
| Falta rfc_tecnico o SEGUIMIENTO | Revisa las migraciones 03 y 04 de la base a la que realmente se conecta la aplicación. |
| Configura SIGETI_DB_USER | Faltan las variables de entorno en la configuración de Run. |
| Access denied o error de conexión | Revisa servicio MySQL, usuario, contraseña, host y puerto. |
| Puerto 8087 ocupado | Detén la otra instancia de Tomcat o el script antes de usar ▶. |
| Error 404 | Revisa el despliegue, el contexto /sigeti-sprint1 y los errores de arranque de Tomcat. |
| No aparecen los cambios | Comprueba que Before launch ejecuta package y Deployment apunta al WAR de esta copia. |

Referencias: [Tomcat en IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-tomcat-server.html) y [panel Maven](https://www.jetbrains.com/help/idea/maven-projects-tool-window.html).
