
$ErrorActionPreference = "Stop"

# Resolve project directories
$backendDir = Split-Path -Parent $PSScriptRoot
$projectRoot = Split-Path -Parent $backendDir
$envFile = Join-Path $projectRoot "config.env"

Write-Host "Starting JKServices development environment..." -ForegroundColor Cyan

# Validate environment file
if (-not (Test-Path $envFile)) {
    throw "Environment file not found: $envFile"
}

# Load environment variables into current process
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()

    # Ignore blank lines and comments
    if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
        return
    }

    if ($line -notmatch '^([A-Za-z_][A-Za-z0-9_]*)=(.*)$') {
        throw "Invalid environment variable declaration."
    }

    $name = $Matches[1]
    $value = $Matches[2].Trim()

    # This loader intentionally supports unquoted KEY=VALUE entries.
    if ($value.StartsWith('"') -or $value.StartsWith("'")) {
        throw "Quoted values are not supported by this development loader."
    }

    [Environment]::SetEnvironmentVariable(
        $name,
        $value,
        [EnvironmentVariableTarget]::Process
    )
}

# Validate required Spring Boot variables
$requiredVariables = @(
    "DB_URL",
    "DB_USERNAME",
    "DB_PASSWORD"
)

foreach ($name in $requiredVariables) {
    $value = [Environment]::GetEnvironmentVariable(
        $name,
        [EnvironmentVariableTarget]::Process
    )

    if ([string]::IsNullOrWhiteSpace($value)) {
        throw "Required environment variable is missing: $name"
    }
}

# Explicitly activate development profile
$env:SPRING_PROFILES_ACTIVE = "dev"

Write-Host "Environment configuration loaded." -ForegroundColor Green
Write-Host "Active Spring profile: dev" -ForegroundColor Green
Write-Host "Starting Spring Boot using Maven..." -ForegroundColor Cyan

# Start backend from its own directory
Push-Location $backendDir

try {
    mvn spring-boot:run

    if ($LASTEXITCODE -ne 0) {
        throw "Spring Boot startup failed. Maven exit code: $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}
