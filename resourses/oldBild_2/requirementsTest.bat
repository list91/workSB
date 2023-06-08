set arch=32
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" set arch=64
if "%PROCESSOR_ARCHITEW6432%"=="AMD64" set arch=64
if %arch%==64 (
	powershell -Command "(New-Object Net.WebClient).DownloadFile('https://drive.google.com/uc?id=1R2SGcY88gVJki8xSsJiCejMGhqKf_HWr&export=download&confirm=t&uuid=479fe41d-af2e-40f5-b365-1329b547b655&at=AKKF8vx_P6mQakTLwXUezamAlTIs:1684313646037', 'jdk-20_windows-x64_bin.exe')"
	setx /m JAVA_HOME "C:\Program Files\Java\jdk-20\bin"
	.\jdk-20_windows-x64_bin.exe /s
) else (
	powershell -Command "(New-Object Net.WebClient).DownloadFile('https://drive.google.com/uc?id=1GOiIqNWf51mpx0Xuv_kZF6dkweK8EVS_&export=download&confirm=t&uuid=f6ca3d07-1e39-4d1b-876b-26bdc8b254fa&at=AKKF8vxph4bV4dCujEwEPywip-rY:1684312212177', 'jdk-8u371-windows-i586.exe')"
	setx /m JAVA_HOME "C:\Program Files (x86)\Java\jdk-1.8\bin"
	.\jdk-8u371-windows-i586.exe /s
)
