#include <Stdio.h>
void Swap(int *a, int *b); // 인자값 두개를 교환해주는 함수 선언
void align(int num[]);  // 중앙값 구해주는 함수 선언
int main()
{
 int num[5]; // 입력받을 값 5개를 저장할 배열
 int i,sum=0; // for문에 조건 제시용으로 쓰일 i와 평균값을 구할때 쓸 입력값의 합을 담을 변수 sum선언.
 for(i=0; i<=4; i++) // 배열의 5칸에 사용자가 값을 입력받는다.
 {
  scanf("%d",&num[i]);
  sum=num[i] + sum; // 하나 입력 받을 때마다 sum에 더해나가는 식으로 입력받은 값의 총합을 구한다
 }
 align(num); // align 함수로 num 배열 정렬
 printf("굈%d굈%d굈",(sum/5),num[2]);  // 총합/5로 구한 평균값과 정렬해둔 배열의 가운데값인 중앙값을 출력.
}
void align(int num[])
{
 int i,j; // for문에 조건을 걸때 쓸 i, j 선언
 for(i=0; i<=4; i++)
 {
  for (j=i+1; j<=4; j++)
  {
   if (num[i] > num[j]) // num[i]의 값이 더 크면 num[j]와 교체
   {
    Swap(&num[i], &num[j]);
   }
  }
 }
}
void Swap(int *a, int *b) // 값 두개를 넘겨 받았을 때 두 변수의 값을 뒤바꿔주는 함수
{
 int temp = *a; // 빈 공간으로 쓸 temp 변수 선언과 동시에 a값으로 초기화.
 *a = *b; // temp에 값을 백업해둔 a 변수에 교체할 b값을 담는다.
 *b = temp; // a 변수에 값을 넣은 b 변수에 temp에 백업해둔 a 값을 담는다.
}
