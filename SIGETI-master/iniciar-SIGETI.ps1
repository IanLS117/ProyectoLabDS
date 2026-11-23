[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome,
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$DbUser = 'root',
    [ValidatePattern('^jdbc:mysql://')]
    [string]$DbUrl = 'jdbc:mysql://127.0.0.1:3306/sigeti_sprint1?serverTimezone=UTC',
    [ValidateSet('MariaDB', 'MySQL')]
    [string]$Motor = 'MariaDB',
    [ValidateRange(1024, 65535)]
    [int]$Puerto = 8087,
    [switch]$SoloPreparar
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$tomcatDirectory = (Resolve-Path -LiteralPath $TomcatHome).Path
$servletApi = Join-Path $tomcatDirectory 'lib/servlet-api.jar'
$catalina = Join-Path $tomcatDirectory 'bin/catalina.bat'
if (!(Test-Path $servletApi) -or !(Test-Path $catalina)) {
    throw 'Selecciona la carpeta descomprimida de Tomcat, con sus subcarpetas bin, conf y lib.'
}
$archive = [IO.Compression.ZipFile]::OpenRead($servletApi)
try {
    if ($null -eq $archive.GetEntry('jakarta/servlet/Servlet.class')) {
        throw 'Esta aplicación requiere Tomcat 10.1 o 11. El Tomcat anterior de XAMPP no es compatible.'
    }
} finally {
    $archive.Dispose()
}
if ([string]::IsNullOrWhiteSpace($JavaHome)) {
    $candidates = Get-ChildItem -Path "$env:ProgramFiles/JetBrains/IntelliJ IDEA*/jbr" -Directory -ErrorAction SilentlyContinue
    $JavaHome = ($candidates | Sort-Object FullName -Descending | Select-Object -First 1).FullName
}
if ([string]::IsNullOrWhiteSpace($JavaHome) -or !(Test-Path "$JavaHome/bin/java.exe")) {
    throw 'Indica Java 21 o posterior mediante -JavaHome.'
}
$javaOutput = @(& "$JavaHome/bin/java.exe" --version 2>&1)
$javaExit = $LASTEXITCODE
$javaVersion = $javaOutput[0].ToString()
$major = [regex]::Match($javaVersion, '(\d+)')
if ($javaExit -ne 0 -or !$major.Success -or [int]$major.Groups[1].Value -lt 21) {
    throw 'El proyecto requiere Java 21 o posterior. Puedes usar la carpeta jbr de IntelliJ.'
}
$war = Join-Path $PSScriptRoot 'vista/target/sigeti.war'
if (!(Test-Path $war)) {
    throw 'Falta vista/target/sigeti.war. Compila el POM raíz con Maven: mvn package.'
}
$runtime = Join-Path $PSScriptRoot '.runtime'
$configurationDirectory = Join-Path $runtime 'conf'
if (!(Test-Path $configurationDirectory)) {
    New-Item -ItemType Directory -Path $runtime -Force | Out-Null
    Copy-Item -LiteralPath "$tomcatDirectory/conf" -Destination $configurationDirectory -Recurse
}
foreach ($directory in @('logs', 'temp', 'work', 'webapps')) {
    New-Item -ItemType Directory -Path (Join-Path $runtime $directory) -Force | Out-Null
}
$serverFile = Join-Path $configurationDirectory 'server.xml'
[xml]$server = Get-Content -LiteralPath $serverFile -Raw
$server.Server.SetAttribute('port', '-1')
$connectors = @($server.SelectNodes('//Service/Connector'))
$http = $connectors | Where-Object {
    ($_.GetAttribute('protocol') -like 'HTTP*' -or $_.GetAttribute('protocol') -like '*Http11*') -and
    $_.GetAttribute('SSLEnabled') -ne 'true'
} | Select-Object -First 1
if ($null -eq $http) { throw 'No se encontró un conector HTTP en Tomcat.' }
$http.SetAttribute('port', $Puerto.ToString())
$http.SetAttribute('address', '127.0.0.1')
foreach ($connector in $connectors) {
    if ($connector -ne $http) { $null = $connector.ParentNode.RemoveChild($connector) }
}
$server.Save($serverFile)
Copy-Item -LiteralPath $war -Destination "$runtime/webapps/sigeti-sprint1.war" -Force
Write-Host "Aplicación preparada: http://localhost:$Puerto/sigeti-sprint1/login.xhtml"
Write-Host "Motor seleccionado: $Motor"
if ($SoloPreparar) { return }
$password = Read-Host "Contraseña de MySQL para $DbUser (Enter si está vacía)" -AsSecureString
$previousPassword = $env:SIGETI_DB_PASSWORD
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($password)
try {
    $env:SIGETI_DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
}
$env:SIGETI_DB_USER = $DbUser
$env:SIGETI_DB_URL = $DbUrl
$env:SIGETI_DB_ENGINE = $Motor
$env:JAVA_HOME = (Resolve-Path -LiteralPath $JavaHome).Path
$env:CATALINA_HOME = $tomcatDirectory
$env:CATALINA_BASE = $runtime
$env:JRE_HOME = $env:JAVA_HOME
Write-Host 'Mantén esta ventana abierta. Para detener el servidor, presiona Ctrl+C.'
try {
    & $catalina run
    if ($LASTEXITCODE -ne 0) { throw 'Tomcat no pudo arrancar. Revisa el mensaje anterior.' }
} finally {
    $env:SIGETI_DB_PASSWORD = $previousPassword
}
