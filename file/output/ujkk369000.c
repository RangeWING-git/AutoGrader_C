#include <stdio.h>
void main()
{
 printf("type 5 numbers.");//숫자　５개를　쓰시오
 int i,j,data[5];//ｄａｔａ［５］＝숫자　５개
 scanf("%d",&data[5]);
 for(i=1;i<=4;i++)
  scanf("%d",&data[i]);
 for(i=1;i<4;i++)
  for(j=i+1;j<=4;j++)
   swap(&data[i],&data[j]);//숫자를　선택　정렬으로　나열
   
 printf("%d굈%d",(data[1]+data[2]+data[3]+data[4]+data[5])/5,data[3]);
}
void swap(int *z,int *y)
{
 int t=*z;
 *z=*y;
 *y=t;
}
