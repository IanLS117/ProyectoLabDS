# Prueba del Sprint 1 en MySQL Server

## Versiones informadas

| Evidencia compartida | Servidor | Estado |
|---|---|---|
| Primera captura | MySQL Community Server 9.4.0 | Confirmado en la captura |
| Segunda captura | MySQL Community Server 8.0.17 | Confirmado en la captura |
| Tercera captura | MySQL Community Server 9.4.0 | Confirmado en la captura |
| Cuarto compañero | Posiblemente 9.4.0 | Suposición del usuario, aún sin confirmar |

Workbench es el cliente. Estas capturas sí identifican el motor y la versión del servidor al que se conectaron.

## Configuración preparada

El mismo código permite elegir:

- `-Motor MariaDB`: conserva la prueba local con XAMPP; es el valor predeterminado.
- `-Motor MySQL`: selecciona MySQLDialect para el servidor MySQL.

La elección se transmite mediante `SIGETI_DB_ENGINE`. El host, puerto y base se configuran por `-DbUrl`; el usuario mediante `-DbUser` y la contraseña se solicita al arrancar. No se agregan credenciales al repositorio.

El conector incluido es MySQL Connector/J 9.2.0. Sus [notas oficiales](https://docs.oracle.com/cd/E17952_01/connector-j-relnotes-en/news-9-2-0.html) declaran uso con MySQL Server 8.0 y posteriores. La [documentación de Hibernate 6.6](https://docs.hibernate.org/orm/6.6/dialect/) enumera MySQL 8.0 como versión mínima de MySQLDialect. Esto da una base documental de compatibilidad, pero no reemplaza ejecutar las pruebas en 8.0.17 y 9.4.0.

## Pasos en una computadora del equipo

1. Descomprimir `SIGETI-Sprint1.zip` en una carpeta propia y disponer de Java 21 y Tomcat 10.1.
2. Conectar Workbench a su MySQL Server y comprobar host y puerto de esa conexión. El servidor MySQL debe estar iniciado.
3. Crear la base de demostración ejecutando `sql/01_esquema_sprint1.sql` y luego `sql/02_datos_demo.sql`. Sólo hacerlo si `sigeti_sprint1` no existe en ese servidor. Si ya existe, no repetir la instalación; revisar primero qué contiene. Son scripts de demostración, no una migración de la base anterior.
4. Abrir PowerShell en la carpeta del proyecto y arrancar con sus rutas reales:

```powershell
.\iniciar-SIGETI.ps1 -TomcatHome "RUTA_DE_TOMCAT" -JavaHome "RUTA_DE_JAVA_21" -Motor MySQL -DbUser "USUARIO_MYSQL" -DbUrl "jdbc:mysql://127.0.0.1:3306/sigeti_sprint1?serverTimezone=UTC"
```

Si MySQL usa otro puerto, sustituir 3306. La ruta y credencial de cada compañero son locales a su computadora. No es necesario habilitar acceso remoto a sus bases.

Si usa el puerto 3306 y la base sigeti_sprint1, se puede omitir `-DbUrl`: el script ya contiene la dirección correcta. Si se especifica, el prefijo debe ser exactamente `jdbc:mysql://`, con los dos puntos y las dos barras. Una dirección como `jdbc:mysql/127.0.0.1:3306/sigeti_sprint1` es inválida y no permite abrir la conexión.

Si PowerShell indica que la ejecución de scripts está deshabilitada, puede iniciar únicamente este arranque en un proceso con política temporal:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\iniciar-SIGETI.ps1" -TomcatHome "RUTA_DE_TOMCAT" -JavaHome "RUTA_DE_JAVA_21" -Motor MySQL -DbUser "USUARIO_MYSQL" -DbUrl "jdbc:mysql://127.0.0.1:3306/sigeti_sprint1?serverTimezone=UTC"
```

Esto no modifica permanentemente las políticas del equipo ni del usuario. Las políticas de una organización, si existen, tienen precedencia; si continúa bloqueado, revisar `Get-ExecutionPolicy -List`. [Referencia de Microsoft](https://learn.microsoft.com/en-us/powershell/module/microsoft.powershell.core/about/about_execution_policies?view=powershell-5.1).

5. Introducir la contraseña de su cuenta MySQL en la terminal y abrir `http://localhost:8087/sigeti-sprint1/login.xhtml`.
6. Repetir las pruebas siguientes y consultar los datos desde Workbench.

## Recorrido para validar

Todas las cuentas ficticias usan `Sprint1Demo!` como contraseña.

| Caso | Esperado |
|---|---|
| DEMO000001AAA | Login correcto; sólo INV-DEMO-001; puede registrar un ticket. |
| DEMO000002BBB | Sólo INV-DEMO-002; no debe ver el equipo de A. |
| DEMO000003CCC | Aviso sin equipos y registro deshabilitado. |
| Contraseña incorrecta | Acceso rechazado. |
| Formulario vacío o descripción con espacios | Rechazo sin nuevo ticket. |
| Alta válida | Folio, estado Enviado y fila guardada en ese MySQL Server. |
| DEMO000004SSS | Sin opciones de usuario final; acceso directo a nuevo-ticket.xhtml rechazado con 403. |

```sql
SELECT VERSION() AS version_servidor, @@version_comment AS motor;
SELECT folio, rfc_usuario_final, numero_inventario,
       id_tipo_incidencia, estatus, fecha_hora_creacion
FROM sigeti_sprint1.ticket
ORDER BY id_ticket DESC;
```

Conservar el resultado de ambas consultas y las capturas del flujo. Si hay un fallo, revisar la consola de Tomcat y `.runtime/logs` antes de cambiar SQL o dependencias.

## Qué se verificó y qué falta

Se compiló el código y se verificaron catorce casos adicionales de configuración: selección de motor, URL/puerto, rechazo de opciones y direcciones inválidas, inicialización de las siete entidades con MySQLDialect sin conexión y generación del bloqueo de lectura para las versiones informadas. También se verificó que el script rechaza una URL mal formada antes de preparar Tomcat. Las diecinueve comprobaciones anteriores también siguen pasando con la configuración MariaDB.

El usuario informó que un compañero ejecutó la copia y completó las validaciones guiadas con MySQL Community Server 9.4.0. Ese resultado se registra como prueba realizada por el compañero y reportada por el usuario; el agente no se conectó a su servidor. Las primeras capturas observadas del recorrido completo y la fila persistida corresponden al MariaDB local del usuario. MySQL 8.0.17 continúa pendiente de prueba funcional.

Después, el usuario migró también su entorno local a MySQL Community Server 9.4.0 en el puerto 3306. Sus capturas confirman la importación del respaldo, el login, un alta nueva y la consulta independiente de Workbench con el ticket restaurado y el recién registrado. Esta evidencia local de MySQL se suma al reporte del compañero; el agente no ejecutó directamente las solicitudes en esos servidores.

Para la presentación, el equipo debe elegir un servidor concreto entre los que ya tiene y ensayar ahí el avance completo. No es necesario reinstalar todos los equipos únicamente para interpretar estas capturas. Registrar también el resultado en la otra versión si ambos entornos seguirán usándose.
