#include <stdio.h>
void swap(int *a,int *b)
{
 int t= *a;
 *a= *b;
 *b= t;
}
int main()
{
 int i,j,aver;
 int data[6];
 for(i=1;i<=5;i++)
  scanf("%d",&data[i]);
 aver= (data[1]+data[2]+data[3]+data[4]+data[5])/5;
 for(i=1;i<5;i++)
  for(j=i+1;j<=5;j++)
   if(data[i]>data[j])
    swap(&data[i],&data[j]);
    printf("%d‚n%d",aver,data[3]);    
return 0;      
}
