#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


void call_func ( void (*f)(int* ptr, int num),int* ptr, int num);  

void setUp(void) {
    // set stuff up here
}

void tearDown(void) {
    // clean stuff up here
}



void run_test(int * vec, int in_num, int * exp_vec)
{
    int vec1[100];


    // setup 
        memset(vec1, 0x55, sizeof vec1);
     
	memcpy(vec1+1,vec,in_num*sizeof(int));  //   
	call_func(sort_array,vec1+1,in_num);
    
    TEST_ASSERT_EQUAL_INT(0x55555555, vec1[in_num+1]);    // check sentinel 
    TEST_ASSERT_EQUAL_INT(0x55555555, vec1[0]);    // check sentinel  
    if ( in_num != 0 ) 
    TEST_ASSERT_EQUAL_INT_ARRAY(exp_vec, vec1+1, in_num);    // check vec 
    
}


void test_NullVector()
{ 
    run_test((int[]){0},0,(int[]){0}); 
}
void test_One()
{ 
    run_test((int[]){1000},1,(int[]){1000}); 
}
void test_Zero()
{ 
    run_test((int[]){10,0,1},3,(int[]){0,1,10}); 
}
void test_Three()
{ 
    run_test((int[]){-1,-3,-2},3,(int[]){-3,-2,-1}); 
}
void test_Five()
{ 
    run_test((int[]){1,1,1,1,2},5,(int[]){1,1,1,1,2}); 
}

int main()
  { 

    UNITY_BEGIN();
    RUN_TEST(test_NullVector);
    RUN_TEST(test_One);
    RUN_TEST(test_Zero);
    RUN_TEST(test_Three);
    RUN_TEST(test_Five);
    return UNITY_END();  

  } 






