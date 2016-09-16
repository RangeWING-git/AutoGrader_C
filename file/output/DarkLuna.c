#include <stdio.h>
void change(int *n,int *m)
{
   int l=*n;
   *n=*m;
   *m=l;
}
int main()
{
    int a[5];
    int c=0,j,i;
    for(i=0;i<5;i++)
   {
      scanf("%d",&a[i]);
      c+=a[i];
   }
   c=c/5;
   printf("%d‚n",c);
   for(i=0;i<5;i++)
   {
      for(j=0;j<5;j++)
      if(a[i]>a[j])
      {
           change(&a[i],&a[j]);
      }
   }
   printf("%d",a[2]);
   return 0;
}
