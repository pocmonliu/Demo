#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

char get_resolution_type(){
    const char* node = "/sys/cvbs_pr2000/cvbs_status";

    if (access(node, 0) == -1) {
        fprintf(stdout, "pr2000 node does not exist.\n", node);
        return (char)0xFF;
    }

    FILE * fp;
    fp = fopen(node, "r");
    if (fp == NULL) {
        fprintf(stdout, "fopen pr2000 node failed, (%s).\n", strerror(errno));
        return (char)0xFF;
    }

    char type;
    if((type = fgetc(fp)) != EOF){
        fprintf(stdout, "pr2000 node value = %X.\n", type);
        return type;
    }

    return (char)0xFF;
}

int main(int argc, char** argv) {

    int type = (int)get_resolution_type();
    switch((int)type){
     case 0:
     case 1:
         fprintf(stdout, "%d pr2000 resolution cvbs.\n", type);
         break;
     case 2:
         fprintf(stdout, "%d pr2000 resolution 720P.\n", type);
         break;
     case 3:
         fprintf(stdout, "%d pr2000 resolution 1080P.\n", type);
         break;
     default:
         fprintf(stdout, "%d pr2000 no camera detected.\n", type);
         break;
    }

    return 0;
}
