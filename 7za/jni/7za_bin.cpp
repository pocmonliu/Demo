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

int get_7z_progress(char s[]) {
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

int c7z(char * const cmd[]){
    fprintf(stdout, "++++++cmd=%s, %s, %s, %s, %s, %s, %s, %s.\n", cmd[0], cmd[1], cmd[2], cmd[3], cmd[4], cmd[5], cmd[6], cmd[7]);
    int progress = 0;

    int pipefd[2];
    if(pipe(pipefd) < 0){
//        fprintf(stderr, "pipe failed. (%s)\n", strerror(errno));
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

        int p = get_7z_progress(buffer);
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

int c7z_compression(char* src, char* dst){
    char * const cmd[] = {"/data/user/7za"
            , "a"
            , dst
            , src
            , "-bsp2"
            , "-bse1"
            , (char*)0};

    return c7z(cmd);
}

int c7z_compression(char* src, char* dst, char* pwd){
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

    return c7z(cmd);
}

int c7z_decompression(char* src, char* dst){
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

   return c7z(cmd);
}

int c7z_decompression(char* src, char* dst, char* pwd){
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

    return c7z(cmd);
}

/**
 * @param path
 *
 *   Signature   The signature of the local file header. This is always '\x50\x4b\x03\x04'.
 *   Version PKZip version needed to extract
 *   Flags   General purpose bit flag:
 *   Bit 00: encrypted file
 *   Bit 01: compression option
 *   Bit 02: compression option
 *   Bit 03: data descriptor
 *   Bit 04: enhanced deflation
 *   Bit 05: compressed patched data
 *   Bit 06: strong encryption
 *   Bit 07-10: unused
 *   Bit 11: language encoding
 *   Bit 12: reserved
 *   Bit 13: mask header values
 *   Bit 14-15: reserved
 */
bool is_encrypted_zip(const char* path){
    if (access(path, 0) == -1) {
        fprintf(stdout, "%s file does not exist.\n", path);
        return false;
    }

    int len = strlen(path);
    fprintf(stdout, "zip extension = %s\n", path + len -4);
    if(strcmp(path + len -4, ".zip") != 0){
        fprintf(stdout, "%s is not zip file.\n", path);
    }

    FILE * file;
    file = fopen(path, "r");
    if (file == NULL) {
        fprintf(stdout, "open zip file failed.(%s)\n", strerror(errno));
        return false;
    }
    fprintf(stdout, "open zip file succeed.\n");

    char buf[8] = {0};
    fread(buf, sizeof(buf), 1, file);
    fprintf(stdout, "zip file head 8 byte:%02x %02x %02x %02x %02x %02x %02x %02x.\n"
            , buf[0], buf[1], buf[2], buf[3], buf[4], buf[5], buf[6], buf[7]);

    if((buf[6] & 0x1) == 1){
        fclose(file);
        return true;
    }

    fclose(file);
    return false;
}

int main(int argc, char** argv) {
//    int rtn = c7z_compression("/sdcard/recovery.zip", "/sdcard/recovery_no_pwd.zip");
//    int rtn = c7z_compression("/sdcard/recovery.zip", "/sdcard/recovery_pwd.zip", "123456");
//    fprintf(stdout, "Execute C7zCompression, Rtn = %d.\n", rtn);

//    int rtn = c7z_decompression("/sdcard/recovery.zip", "/sdcard/");
//    int rtn = c7z_decompression("/sdcard/update_pwd.zip", "/sdcard/", "123456");
//    fprintf(stdout, "Execute C7zDecompression, Rtn = %d.\n", rtn);

    char* path1 = "/sdcard/recovery.zip";
    if(is_encrypted_zip(path1)){
        fprintf(stdout, "%s is encrypted zip file.\n", path1);
    }else{
        fprintf(stdout, "%s is not encrypted zip file.\n", path1);
    }

    char* path2 = "/sdcard/update_pwd.zip";
    if(is_encrypted_zip(path2)){
        fprintf(stdout, "%s is encrypted zip file.\n", path2);
    }else{
        fprintf(stdout, "%s is not encrypted zip file.\n", path2);
    }

    return 0;
}
