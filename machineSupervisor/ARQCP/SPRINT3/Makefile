# Compilador e flags
CC = gcc
ASM = nasm
CFLAGS = -Wall -Iinclude
LDFLAGS = -lm

# Diretórios e fontes
SRC_DIR = src
ASM_DIR = src/assembly
OBJ_DIR = obj
BIN_DIR = bin
INCLUDE_DIR = include

SRC_C = $(SRC_DIR)/main.c \
        $(SRC_DIR)/machine.c \
        $(SRC_DIR)/machmanager.c \
        $(SRC_DIR)/operations.c \
		$(SRC_DIR)/utils.c

SRC_ASM = $(ASM_DIR)/usac01.s \
          $(ASM_DIR)/usac02.s \
		  $(ASM_DIR)/usac03.s \
		  $(ASM_DIR)/usac04.s \
		  $(ASM_DIR)/usac05.s \
		  $(ASM_DIR)/usac06.s \
		  $(ASM_DIR)/usac07.s \
		  $(ASM_DIR)/usac08.s \
		  $(ASM_DIR)/usac09.s \
		  $(ASM_DIR)/usac10.s

OBJ_C = $(patsubst $(SRC_DIR)/%.c, $(OBJ_DIR)/%.o, $(SRC_C))
OBJ_ASM = $(patsubst $(ASM_DIR)/%.asm, $(OBJ_DIR)/%.o, $(SRC_ASM))

# Nome do executável
EXEC = $(BIN_DIR)/system_manager

# Regra principal
all: setup $(EXEC)

# Compilar o executável
$(EXEC): $(OBJ_C) $(OBJ_ASM)
	$(CC) $(OBJ_C) $(OBJ_ASM) -o $@ $(LDFLAGS)

# Compilar arquivos C
$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

# Compilar arquivos ASM
$(OBJ_DIR)/%.o: $(ASM_DIR)/%.asm
	$(ASM) -f elf64 $< -o $@

# Configuração de diretórios
setup:
	@mkdir -p $(OBJ_DIR)
	@mkdir -p $(BIN_DIR)

# Executar o programa
run: all
	./$(EXEC)

# Analisar com Valgrind
vg: all
	valgrind --leak-check=full --track-origins=yes ./$(EXEC)

# Limpeza dos arquivos compilados
clean:
	rm -rf $(OBJ_DIR) $(BIN_DIR)
