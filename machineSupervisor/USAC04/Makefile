prog: main.o asm.o get_number_binary.o
	gcc -z noexecstack main.o asm.o get_number_binary.o -o prog

asm.o: asm.h asm.s
	gcc -g -Wall -Wextra -fanalyzer -c asm.s

get_number_binary.o: get_number_binary.h get_number_binary.s
	gcc -g -Wall -Wextra -fanalyzer -c get_number_binary.s

main.o: main.c asm.h get_number_binary.h
	gcc -g -Wall -Wextra -fanalyzer -c main.c

clean:
	rm -f *.o prog

run: prog
	./prog

debug: prog
	gdb -tui prog

vg: prog
	valgrind ./prog