' script writes windows user startup directory path 
' to a text file in user temporary directory,
' temporary file is named ndsrStartUpPath.txt 

dim objShell, objFSO, strStartupPath, objTextFile, temporaryDirectory, openMode

' Create the File System Object
Set objFSO = CreateObject("Scripting.FileSystemObject")
set objShell = CreateObject("WScript.Shell")
' GetSpecialFolder flags: 0 - Windows dir, 1 - System32 dir, 2 - Temporary dir
temporaryDirectory = objFSO.GetSpecialFolder(2)
' modes: Appending = 8 Reading = 1, Writing = 2
Set objTextFile = objFSO.OpenTextFile(temporaryDirectory & "/" & "ndsrStartUpPath.txt", 2, True)
objTextFile.WriteLine(objShell.SpecialFolders("Startup"))
objTextFile.Close