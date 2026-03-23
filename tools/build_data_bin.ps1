param(
    [Parameter(Mandatory = $true)]
    [string]$InputApk,
    [Parameter(Mandatory = $true)]
    [string]$OutputBin,
    [string]$Passphrase = "X3n0Key@2026"
)

$plain = [System.IO.File]::ReadAllBytes($InputApk)
$pass = [System.Text.Encoding]::UTF8.GetBytes($Passphrase)

$salt = New-Object byte[] 16
$iv = New-Object byte[] 16
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($salt)
$rng.GetBytes($iv)

$kdf = New-Object System.Security.Cryptography.Rfc2898DeriveBytes($pass, $salt, 120000, [System.Security.Cryptography.HashAlgorithmName]::SHA256)
$derived = $kdf.GetBytes(64)
$encKey = $derived[0..31]
$macKey = $derived[32..63]

$aes = [System.Security.Cryptography.Aes]::Create()
$aes.Mode = [System.Security.Cryptography.CipherMode]::CBC
$aes.Padding = [System.Security.Cryptography.PaddingMode]::PKCS7
$aes.KeySize = 256
$aes.Key = $encKey
$aes.IV = $iv
$encryptor = $aes.CreateEncryptor()
$cipher = $encryptor.TransformFinalBlock($plain, 0, $plain.Length)

$magic = [System.Text.Encoding]::ASCII.GetBytes("EXNOBIN1")
$lenBytes = [BitConverter]::GetBytes([int]$cipher.Length)
if ([BitConverter]::IsLittleEndian) { [Array]::Reverse($lenBytes) }

$signed = New-Object byte[] ($magic.Length + $salt.Length + $iv.Length + $lenBytes.Length + $cipher.Length)
$offset = 0
[Array]::Copy($magic, 0, $signed, $offset, $magic.Length)
$offset += $magic.Length
[Array]::Copy($salt, 0, $signed, $offset, $salt.Length)
$offset += $salt.Length
[Array]::Copy($iv, 0, $signed, $offset, $iv.Length)
$offset += $iv.Length
[Array]::Copy($lenBytes, 0, $signed, $offset, $lenBytes.Length)
$offset += $lenBytes.Length
[Array]::Copy($cipher, 0, $signed, $offset, $cipher.Length)

$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = $macKey
$tag = $hmac.ComputeHash($signed)

$out = New-Object byte[] ($signed.Length + $tag.Length)
[Array]::Copy($signed, 0, $out, 0, $signed.Length)
[Array]::Copy($tag, 0, $out, $signed.Length, $tag.Length)

$dir = Split-Path -Path $OutputBin -Parent
if ($dir -and -not (Test-Path $dir)) {
    New-Item -ItemType Directory -Path $dir | Out-Null
}

[System.IO.File]::WriteAllBytes($OutputBin, $out)
Get-Item $OutputBin | Select-Object FullName, Length, LastWriteTime
