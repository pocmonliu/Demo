#include<sys/types.h>
#include<sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

//int atoi (char s[]) {
//    int i,n,sign;
//    for(i=0;isspace(s[i]);i++)//跳过空白符;
//    sign=(s[i]=='-')?-1:1;
//    if(s[i]=='+'||s[i]==' -')//跳过符号
//      i++;
//    for(n=0;isdigit(s[i]);i++)
//           n=10*n+(s[i]-'0');//将数字字符转换成整形数字
//    return sign *n;
//}

int get7zaProgress(char s[]) {
    if(strstr(s, "%") == NULL){
        return -1;
    }

    if(s[0] == '%'){
        return -1;
    }

    int i, n;
    i = 0;
    while(!isdigit(s[i])){   //跳过非数字;
        i++;
    }

    for(n = 0; isdigit(s[i]); i++){
        n = 10 * n + (s[i] - '0');  //将数字字符转换成整形数字
    }

    return n;
}

int C7z(char * const cmd[]){
    fprintf(stdout, "++++++cmd=%s, %s, %s, %s, %s, %s, %s, %s.\n", cmd[0], cmd[1], cmd[2], cmd[3], cmd[4], cmd[5], cmd[6], cmd[7]);
    int progress = 0;

    int pipefd[2];
    if(pipe(pipefd) < 0){
        fprintf(stderr, "pipe failed. (%s)\n", strerror(errno));
        return -1;
    }

    pid_t pid = fork();
    if (pid == 0) {
        //child
        close(pipefd[0]);
        if (pipefd[1] != STDERR_FILENO) {
            dup2(pipefd[1], STDERR_FILENO);
            close(pipefd[1]);
        }

        execv(cmd[0], cmd);

        _exit(-1);
    }

    //parent
    close(pipefd[1]);
    char buffer[20];
    FILE* from_child = fdopen(pipefd[0], "r");
    while (fgets(buffer, sizeof(buffer), from_child) != NULL) {
        fprintf(stdout, "===%s\n", buffer);

        int p = get7zaProgress(buffer);
        if(p > progress){
            progress = p;
            fprintf(stdout, "***%d\n", progress);
        }
    }

    fclose(from_child);
    close(pipefd[0]);
    waitpid(pid, NULL, 0);

    return 0;
}

int C7zCompression(char* src, char* dst){
    char * const cmd[] = {"/data/user/7za"
            , "a"
            , dst
            , src
            , "-bsp2"
            , "-bse1"
            , (char*)0};

    return C7z(cmd);
}

int C7zCompression(char* src, char* dst, char* pwd){
    char ppwd[128] = "-p";
    strcat(ppwd, pwd);
//    fprintf(stdout, "+++ pwd = %s, ppwd = %s, len = %d\n", pwd, ppwd, strlen(pwd));

    char * const cmd[] = {"/data/user/7za"
            , "a"
            , dst
            , src
            , ppwd
            , "-bsp2"
            , "-bse1"
            , (char*)0};

    return C7z(cmd);
}

int C7zDecompression(char* src, char* dst){
   char odst[128] = "-o";

   strcat(odst, dst);
//   fprintf(stdout, "+++ dst = %s, odst = %s, len = %d\n", dst, odst, strlen(dst));

   char * const cmd[] = {"/data/user/7za"
           , "x"
           , src
           , odst
           , "-aoa"
           , "-bsp2"
           , "-bse1"
           , (char*)0};

   return C7z(cmd);
}

int C7zDecompression(char* src, char* dst, char* pwd){
    char odst[128] = "-o";
    char ppwd[128] = "-p";

    strcat(odst, dst);
//    fprintf(stdout, "+++ dst = %s, odst = %s, len = %d\n", dst, odst, strlen(dst));

    strcat(ppwd, pwd);
//    fprintf(stdout, "+++ pwd = %s, ppwd = %s, len = %d\n", pwd, ppwd, strlen(pwd));

    char * const cmd[] = {"/data/user/7za"
            , "x"
            , src
            , odst
            , ppwd
            , "-aoa"
            , "-bsp2"
            , "-bse1"
            , (char*)0};

    return C7z(cmd);
}

int main(int argc, char** argv) {
    int rtn = C7zCompression("/sdcard/recovery.zip", "/sdcard/recovery_no_pwd.zip");
//    int rtn = C7zCompression("/sdcard/recovery.zip", "/sdcard/recovery_pwd.zip", "123456");
    fprintf(stdout, "Execute C7zCompression, Rtn = %d.\n", rtn);

//    int rtn = C7zDecompression("/sdcard/recovery.zip", "/sdcard/");
//    int rtn = C7zDecompression("/sdcard/update_pwd.zip", "/sdcard/", "123456");
//    fprintf(stdout, "Execute C7zDecompression, Rtn = %d.\n", rtn);

    return 0;
}
