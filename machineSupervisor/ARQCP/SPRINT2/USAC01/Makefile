prog: main.o asm.o
	gcc -z noexecstack main.o asm.o -o prog

main.o: main.c asm.h
	gcc -g -Wall -Wextra -fanalyzer -c main.c

asm.o: asm.s asm.h
	gcc -g -Wall -Wextra -fanalyzer -c asm.s

clean:
	rm -vf *.o prog

run: prog
	./prog

debug: prog
	gdb -tui ./prog

vg: prog
	valgrind ./prog
	
ddd: prog
	ddd ./prog