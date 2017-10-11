#include<sys/types.h>
#include<sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

//int atoi (char s[])
//{
//    int i,n,sign;
//    for(i=0;isspace(s[i]);i++)//跳过空白符;
//    sign=(s[i]=='-')?-1:1;
//    if(s[i]=='+'||s[i]==' -')//跳过符号
//      i++;
//    for(n=0;isdigit(s[i]);i++)
//           n=10*n+(s[i]-'0');//将数字字符转换成整形数字
//    return sign *n;
//}

int get7zaProgress(char s[])
{
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

int And7zDecompression(){
    fprintf(stdout, "And7zDecompression() start.\n");

    int progress = 0;

    int pipefd[2];
    if(pipe(pipefd) < 0){
        fprintf(stderr, "pipe failed. %s\n", strerror(errno));
        return -1;
    }

    fprintf(stdout, "pipe succeed...\n");

    pid_t pid = fork();
    if (pid == 0) {
        //child
        fprintf(stdout, "this is child pid.\n");

        close(pipefd[0]);
        if (pipefd[1] != STDERR_FILENO)
        {
            dup2(pipefd[1], STDERR_FILENO);
            close(pipefd[1]);
        }

        char *argv[] = {"7za", "x", "/sdcard/update_pwd.zip", "-o/sdcard/", "-p123456", "-aoa", "-bsp2", "-bse1", (char *)0};
        execv("/data/user/7za", argv);

        _exit(-1);
    }

    //parent
    fprintf(stdout, "this is parent pid.\n");
    close(pipefd[1]);

    char buffer[20];
    FILE* from_child = fdopen(pipefd[0], "r");
    while (fgets(buffer, sizeof(buffer), from_child) != NULL) {
        fprintf(stdout, "===%s\n", buffer);

        int p = get7zaProgress(buffer);
        if(p > progress){
            progress = p;
            fprintf(stdout, "===%d\n", progress);
        }
    }

    fclose(from_child);
    close(pipefd[0]);
    waitpid(pid, NULL, 0);

    fprintf(stdout, "And7zDecompression() end.\n");

    return 0;
}

int main(int argc, char** argv) {
    fprintf(stdout, "Execute And7zDecompression().\n");

    And7zDecompression();

    return 0;
}
