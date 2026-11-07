$ErrorActionPreference = 'Stop'

$tomcat = 'C:\Users\w2000\OneDrive\Escritorio\Trabajos UABC 2024\apache-tomcat-11.0.8'
$env:CATALINA_HOME = $tomcat
$env:CATALINA_BASE = $tomcat
$env:JAVA_HOME = 'C:\Users\w2000\.jdks\ms-21.0.12.1'

# se usa el usuario y contraseña para conectarse ala BS
$credencial = Get-Credential -Message 'Ingresa tu usuario y contraseña de MySQL'
$env:SIGETI_DB_USER = $credencial.UserName
$env:SIGETI_DB_PASSWORD = $credencial.GetNetworkCredential().Password

# aqui es la carpeta donde se guardo el scrip
$war = Join-Path $PSScriptRoot 'vista\target\web.war'
Copy-Item $war "$tomcat\webapps\web.war" -Force

& "$tomcat\bin\catalina.bat" run