#include <stdio.h>
void main()
{
 printf("type 5 numbers.");//���ڡ������������ÿ�
 int i,j,data[5];//������ۣ��ݣ����ڡ�����
 scanf("%d",&data[5]);
 for(i=1;i<=4;i++)
  scanf("%d",&data[i]);
 for(i=1;i<4;i++)
  for(j=i+1;j<=4;j++)
   swap(&data[i],&data[j]);//���ڸ������á��������Ρ�����
   
 printf("%d�n%d",(data[1]+data[2]+data[3]+data[4]+data[5])/5,data[3]);
}
void swap(int *z,int *y)
{
 int t=*z;
 *z=*y;
 *y=t;
}
