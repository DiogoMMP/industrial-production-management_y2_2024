# Compiler
CC = gcc

# Compiler flags
CFLAGS = -g -Wall -Wextra -fanalyzer

# Source files
C_SRCS = main.c usac01.c usac02.c usac03.c usac04.c usac05.c usac06.c usac07.c usac08.c usac09.c usac10.c
ASM_SRCS = usac01.s usac02.s usac03.s usac04.s usac05.s usac06.s usac07.s usac08.s usac09.s usac10.s

# Header files
HEADERS = usac.h usac01.h usac02.h usac03.h usac04.h usac05.h usac06.h usac07.h usac08.h usac09.h usac10.h

# Object files
C_OBJS = $(C_SRCS:.c=.o)
ASM_OBJS = $(ASM_SRCS:.s=.o)

# Executable name
EXEC = prog

# Default target
all: $(EXEC)

# Link the executable
$(EXEC): $(C_OBJS) $(ASM_OBJS)
	$(CC) -z noexecstack $(C_OBJS) $(ASM_OBJS) -o $@

# Compile C source files to object files
%.o: %.c $(HEADERS)
	$(CC) $(CFLAGS) -c $< -o $@

# Compile assembly source files to object files
%.o: %.s $(HEADERS)
	$(CC) $(CFLAGS) -c $< -o $@

# Clean up build files
clean:
	rm -vf *.o $(EXEC)

# Run the program
run: $(EXEC)
	./$(EXEC)

# Debug the program
debug: $(EXEC)
	gdb -tui ./$(EXEC)

# Run with Valgrind
vg: $(EXEC)
	valgrind ./$(EXEC)

# Run with DDD
ddd: $(EXEC)
	ddd ./$(EXEC)

# Phony targets
.PHONY: all clean run debug vg ddd
