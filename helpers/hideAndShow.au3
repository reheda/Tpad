AutoItSetOption("WinTitleMatchMode", 1)

if $CmdLine[0] > 0 Then
    if $CmdLine[1] == 0 Then
        WinSetState($CmdLine[2], "", @SW_HIDE)
    ElseIf $CmdLine[1] == 1 Then
        WinSetState($CmdLine[2], "", @SW_SHOW)
    Else
    EndIf
EndIf