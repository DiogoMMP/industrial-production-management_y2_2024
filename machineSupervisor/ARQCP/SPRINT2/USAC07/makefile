prog: main.o asm.o
	gcc -z noexecstack  main.o asm.o -o prog

main.o: main.c asm.h
	gcc -g -Wall -Wextra -fanalyzer -c main.c

asm.o: asm.s asm.h
	gcc -g -Wall -Wextra -fanalyzer -c asm.s

debug: prog
	gdb -tui prog

vg: prog
	valgrind ./prog

run: prog
	./prog

clean:
	rm -vf *.o prog