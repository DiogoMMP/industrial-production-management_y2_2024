#include <string.h>  
#include "../../unity.h"
#include "asm.h" 


void call_func ( void (*f)(char* str,char* tok, int* out),char* str,char *tok, int* out);  

void setUp(void) {
    // set stuff up here
}

void tearDown(void) {
    // clean stuff up here
}



void run_test(char * str, char* tok,  int  exp_res)
{
	int vec[3]={0x55555555,0x55555555,0x55555555}; 


    // setup 
	call_func(extract_token,str,tok,&vec[1]);
    
    TEST_ASSERT_EQUAL_INT(0x55555555, vec[2]);    // check sentinel 
    TEST_ASSERT_EQUAL_INT(0x55555555, vec[0]);    // check sentinel  
    TEST_ASSERT_EQUAL_INT(exp_res, vec[1]);    // check res  
    
}


void test_Null()
{ 
    run_test("","",0x55555555); 
}
void test_One()
{ 
    run_test("sensor_id:8#type:atmospheric_temperature#value:21.60#unit:celsius#time:2470030","sensor_id:",8); 
}
void test_Zero()
{ 
    run_test("sensor_id:8#type:atmospheric_temperature#value:21.60#unit:celsius#time:2470030","zenzor_id:",0x55555555); 
}
void test_Three()
{ 
    run_test("sensor_id:8#type:atmospheric_temperature#value:21.60#unit:celsius#time:2470030","value:",2160); 
}
void test_Four()
{ 
    run_test("sensor_id:8#type:atmospheric_temperature#value:21.60#unit:celsius#time:2470030","time:",2470030); 
}
void test_Five()
{ 
    run_test("sensor_id:8#type:atmospheric_temperature#value:21.60#unit:celsius#time:2470030","atmos:",0x55555555); 
}

int main()
  { 

    UNITY_BEGIN();
    RUN_TEST(test_Null);
    RUN_TEST(test_One);
    RUN_TEST(test_Zero);
    RUN_TEST(test_Three);
    RUN_TEST(test_Four);
    RUN_TEST(test_Five);
    return UNITY_END();  

  } 






