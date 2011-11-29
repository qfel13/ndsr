' Make sure variables are declared.

option explicit

' Routine to create a link in the StartUp directory.
' Arguments:
' 1 - target directory
' 2 - target executable filename
' 3 - shortcut name

sub CreateShortCut()
  dim objShell, strStartUpPath, objLink, targetDirectory, targetExecutable, linkName
  set objShell = CreateObject("WScript.Shell")
  If WScript.Arguments.Count < 3 Then
	WScript.Quit -1
  Else 
	If WScript.Arguments.Count < 1 Then
		Wscript.Quit -2 
	Else
		targetDirectory = WScript.Arguments(0)
	End If
	If WScript.Arguments.Count < 2 Then
		Wscript.Quit -3
	Else
		targetExecutable = WScript.Arguments(1)
	End If
	linkName = WScript.Arguments(2)
  End If
  strStartUpPath = objShell.SpecialFolders("Startup")
  set objLink = objShell.CreateShortcut(strStartUpPath & "/" & linkName)
  objLink.Description = "Shortcut to " & linkName
  objLink.TargetPath = targetDirectory & "/" & targetExecutable
  objLink.WindowStyle = 1
  objLink.WorkingDirectory = targetDirectory
  objLink.Save
end sub

' Program starts running here.

call CreateShortCut()