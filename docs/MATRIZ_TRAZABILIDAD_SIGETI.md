# Matriz de Trazabilidad - SIGETI

**Sistema de Gestión de Tickets e Inventario**  
*Roles estandarizados: `Usuario`, `Usuario final`, `Soporte técnico`, `Administrador`*

## 1\. Historias de Usuario Funcionales

|US ID|Historia de Usuario|Prioridad|Sprint|Estado|Asignado a|Caso de Prueba|Complejidad|Estabilidad|Code `{commit}`|
|:-:|-|:-:|:-:|:-:|:-:|-|:-:|:-:|:-:|
|**AUT-US-01**|Como **usuario**, quiero iniciar sesión con mi RFC y contraseña para acceder a las funciones correspondientes a mi rol.|Alta|Sprint 1|Pendiente|-|Ingresar un RFC y contraseña válidos → presionar “Iniciar sesión” → verificar que el sistema permita el acceso y muestre únicamente las opciones correspondientes al rol del usuario. Después probar credenciales incorrectas y verificar que el acceso sea rechazado.|Media|Alta|-|
|**INV-US-02**|Como **usuario final**, quiero consultar los equipos del inventario que tengo asignados para identificar y seleccionar el equipo afectado al reportar una incidencia.|Alta|Sprint 1|Pendiente|-|Abrir el formulario de nuevo ticket → desplegar la lista de equipos → verificar que se listen únicamente los equipos en resguardo asignados al usuario final autenticado. Probar con un usuario sin equipos y verificar que muestre el aviso correspondiente.|Baja|Alta|-|
|**TIC-US-03**|Como **usuario final**, quiero registrar un ticket seleccionando el equipo afectado y el tipo de incidencia para solicitar soporte técnico.|Alta|Sprint 1|Pendiente|-|Seleccionar un equipo asignado y el tipo de incidencia en los menús desplegables → agregar la descripción y presionar “Registrar ticket” → verificar que se cree con estado «Enviado» y folio único. Después intentar enviar el formulario vacío y verificar que solicite los campos obligatorios.|Media|Alta|-|
|**ASI-US-04**|Como **administrador**, quiero que los tickets nuevos se asignen automáticamente a un soporte técnico para distribuir equitativamente la atención de incidencias.|Alta|Sprint 2|Pendiente|-|Registrar un ticket nuevo como usuario final → consultar el detalle del ticket como administrador → verificar que el sistema lo haya asignado automáticamente al soporte técnico con menor carga activa.|Alta|Media|-|
|**TIC-US-05**|Como **usuario final**, quiero consultar mis tickets registrados y su estado actual para dar seguimiento a la atención de mis incidencias.|Media|Sprint 2|Pendiente|-|Iniciar sesión como usuario final → ingresar a la sección “Mis tickets” → verificar que se muestren únicamente los tickets propios con su estado actual («Enviado», «En atención» o «Terminado») y que no sean visibles los tickets de otros usuarios.|Baja|Alta|-|
|**ASI-US-06**|Como **soporte técnico**, quiero consultar los tickets que tengo asignados para organizar y atender mi carga de trabajo.|Alta|Sprint 2|Pendiente|-|Iniciar sesión como soporte técnico → abrir la bandeja de tickets asignados → verificar que se listen todos los tickets bajo su responsabilidad ordenados por prioridad y fecha, excluyendo los asignados a otro soporte técnico.|Baja|Alta|-|
|**TIC-US-07**|Como **soporte técnico**, quiero cambiar el estado de un ticket entre «Enviado», «En atención» y «Terminado» documentando la solución para reflejar el progreso y cierre de la incidencia.|Alta|Sprint 2|Pendiente|-|Seleccionar un ticket asignado → cambiar el estado a «En atención» y luego a «Terminado» capturando la solución → verificar que el cambio se guarde correctamente. Después intentar cambiar a «Terminado» dejando vacía la solución y verificar que el sistema impida el cierre.|Media|Alta|-|
|**TIC-US-08**|Como **soporte técnico**, quiero registrar observaciones de seguimiento durante la atención de un ticket para documentar el trabajo realizado.|Media|Sprint 3|Pendiente|-|Abrir un ticket en estado «En atención» → escribir una nota de avance y presionar “Guardar observación” → verificar que el comentario quede registrado en el historial del ticket con fecha, hora y nombre del soporte técnico.|Media|Alta|-|
|**BIT-US-09**|Como **administrador**, quiero consultar la bitácora de cambios de estado, asignación y prioridad de cada ticket para auditar la trazabilidad de las incidencias.|Media|Sprint 3|Pendiente|-|Realizar cambios de estado, asignación y prioridad en un ticket → ingresar como administrador a la bitácora del ticket → verificar que cada movimiento aparezca registrado cronológicamente indicando valor anterior, valor nuevo, fecha y autor.|Alta|Media|-|
|**TIC-US-10**|Como **administrador**, quiero consultar todos los tickets registrados en el sistema para supervisar la atención de incidencias.|Media|Sprint 3|Pendiente|-|Iniciar sesión como administrador → acceder al listado general de tickets → verificar que se visualicen todos los tickets del sistema y probar los filtros por estado, prioridad y soporte técnico asignado.|Baja|Alta|-|
|**ASI-US-11**|Como **administrador**, quiero reasignar un ticket a otro soporte técnico para ajustar la distribución de la carga de trabajo.|Media|Sprint 3|Pendiente|-|Seleccionar un ticket activo como administrador → elegir a otro soporte técnico en la opción “Reasignar” y confirmar → verificar que el ticket desaparezca de la bandeja del soporte técnico anterior y aparezca en la del nuevo.|Media|Alta|-|
|**TIC-US-12**|Como **administrador**, quiero modificar la prioridad de un ticket para atender primero las incidencias más críticas.|Media|Sprint 3|Pendiente|-|Abrir un ticket como administrador → cambiar el nivel de prioridad (Baja, Media o Alta) y guardar → verificar que la nueva prioridad se actualice en la vista general y en la bandeja del soporte técnico asignado.|Baja|Alta|-|
|**REP-US-13**|Como **administrador**, quiero generar reportes estadísticos y consultar métricas de desempeño de los tickets para evaluar la atención de incidencias del área de soporte técnico.|Baja|Sprint 4|Pendiente|-|Ingresar al módulo de reportes como administrador → seleccionar un rango de fechas y presionar “Generar reporte” → verificar que el dashboard muestre las métricas correctas de tickets por estado y desempeño del soporte técnico, y que permita exportar el reporte.|Alta|Media|-|

\---

## 2\. Glosario de Prefijos (`US ID`)

|Prefijo|Módulo / Dominio|Ejemplo|Alcance Funcional|
|:-:|-|:-:|-|
|**`AUT`**|Autenticación y Acceso|`AUT-US-01`|Inicio de sesión con RFC y carga de permisos según rol.|
|**`INV`**|Inventario de Equipos|`INV-US-02`|Consulta de equipos en resguardo asignados al usuario final.|
|**`TIC`**|Gestión de Tickets|`TIC-US-03`|Registro, consulta, seguimiento, cambio de estado y prioridad.|
|**`ASI`**|Asignación de Soporte|`ASI-US-04`|Asignación automática, consulta de carga y reasignación.|
|**`BIT`**|Bitácora y Auditoría|`BIT-US-09`|Consulta del historial de cambios de estado, asignación y prioridad.|
|**`REP`**|Reportes y Estadísticas|`REP-US-13`|Generación de reportes estadísticos y visualización de métricas.|



