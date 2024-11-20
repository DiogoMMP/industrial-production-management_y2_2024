prog: main.o asm.o sort_array.o
	gcc -z noexecstack  main.o asm.o sort_array.o -o prog

main.o: main.c asm.h
	gcc -g -Wall -Wextra -fanalyzer -c main.c

sort_array.o: sort_array.h sort_array.s
	gcc -g -Wall -Wextra -fanalyzer -c sort_array.s

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