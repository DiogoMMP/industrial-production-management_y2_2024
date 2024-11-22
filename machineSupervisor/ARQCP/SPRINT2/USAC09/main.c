#include <stdio.h>
#include "asm.h"

int main(void){
	
	int vec[] = {1000};
	int length = sizeof(vec) / sizeof(vec[0]);
	char order = 1;
			
	sort_array(vec,length,order);
	
	for (int i = 0; i < length; i++) {
        printf("%d ", vec[i]);
    }
    printf("\n");
		
	return 0;
}
