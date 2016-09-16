#include <stdio.h>
#include <stdlib.h>
void swap(int *a, int *b){
    int temp=*a;
    *a=*b;
    *b=temp;
}
int main()
{
    int i, j;
    int data[5];
    for(i=0; i<5; i++){
        scanf("%d", &data[i]);
    }
    for(i=0; i<5-1; i++){
        for(j=i+1; j<5; j++){
            if(data[i]<data[j]){
                swap(&data[i], &data[j]);
            }
        }
    }
    printf("%d‚n%d", (data[0]+data[1]+data[2]+data[3]+data[4])/5, data[2]);
    return 0;
}
