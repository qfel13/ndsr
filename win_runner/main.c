#include <windows.h>

int main(int argc, char** argv) {
    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
//    system("java -cp . -jar Ndsr.jar");
//    WinExec("java -cp . -jar Ndsr.jar", SW_HIDE);
    if (!CreateProcess( NULL,   // No module name (use command line)
        "java -cp . -jar Ndsr.jar",        // Command line
        NULL,           // Process handle not inheritable
        NULL,           // Thread handle not inheritable
        FALSE,          // Set handle inheritance to FALSE
        CREATE_NO_WINDOW, // creation flags
        NULL,           // Use parent's environment block
        NULL,           // Use parent's starting directory 
        &si,            // Pointer to STARTUPINFO structure
        &pi)           // Pointer to PROCESS_INFORMATION structure
    ) {
        printf( "CreateProcess failed (%d).\n", GetLastError() );
        return;
    }
    return 0;
}

