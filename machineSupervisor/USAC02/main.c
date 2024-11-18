#include <stdio.h>
#include "asm.h"


int main()
{
	int value = 26; 
	char bits [5];
	int res = get_number_binary ( value , bits );
	printf ( "%d: %d , %d , %d , %d , %d\n" ,res , bits [4] , bits [3] , bits [2] , bits [1] , bits[0]) ;
}
