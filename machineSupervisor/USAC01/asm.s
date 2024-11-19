.section .text
    .global extract_data
extract_data:
    # Initialize outputs to failure defaults
    movl $0, (%rcx)       # Set *value = 0
    mov %rdi, %r9        # Set unit = str
    movb $0, (%rdx)       # Set unit = empty string
    movl $0, %eax         # Return value = 0

    # Search for the token in the input string
search_token:
    movb (%rdi), %al      # Read next character of str
    cmpb $0, %al          # End of string?
    je not_found          # If yes, token not found

    # Compare token with the current position in the string
    call compare_token    # Compare token
    test %eax, %eax       # Was token found?
    jnz token_found       # If yes, jump to token_found

get_next_token:
    movb (%rdi), %al
    cmpb $0, %al
    je not_found
    cmpb $'#', %al        # Check if next char is '#'
    je next_char         # If yes, move to next char and set the token to the next char
    # Move to the next character in the string
    inc %rdi
    jmp get_next_token

next_char:
    inc %rdi
    mov %rdi, %r9        # Set unit = str
    jmp search_token

token_found:
    # Skip to &unit:
    mov %rdi, %r8
skip_to_unit:
    movb (%r8), %al
    cmpb $0, %al          # End of string?
    je not_found          # If yes, token not found
    cmpb $'&', %al        # Look for '&'
    jne next_char_unit
    cmpb $'u', 1(%r8)     # Is "&unit"?
    jne next_char_unit
    add $6, %r8           # Move past "&unit:"
    jmp found_unit

next_char_unit:
    inc %r8
    jmp skip_to_unit

found_unit:
    # Copy unit string
    mov %r8, %rdi         # Source = start of unit
    mov %rdx, %rsi        # Destination = unit
    call copy_string      # Copy until '&'

    # Skip to &value:
    mov %r8, %rdi         # Start looking for &value
skip_to_value:
    movb (%r8), %al
    cmpb $0, %al          # End of string?
    je not_found          # If yes, token not found
    cmpb $'&', %al        # Look for '&'
    jne next_char_value
    cmpb $'v', 1(%r8)     # Is "&value"?
    jne next_char_value
    add $7, %r8           # Move past "&value:"
    jmp found_value

next_char_value:
    inc %r8
    jmp skip_to_value

found_value:
    # Extract integer value
    mov %r8, %rdi         # Start of value
    mov %rcx, %rsi        # Destination = value
    call extract_value    # Extract integer value

    # Success
    movl $1, %eax         # Set return value = 1
    ret

not_found:
    # Defaults already set: *value = 0, unit = ""
    ret

# Helper: Compare token with substring
compare_token:
    push %r9
    push %rsi

compare_loop:
    # Compare token with substring
    movb (%r9), %al
    movb (%rsi), %r10b
    # Check if token is at the end
    cmpb $0, %r10b
    je compare_end
    # Compare characters
    cmpb %al, %r10b
    jne compare_not_equal
    inc %r9
    inc %rsi
    jmp compare_loop

compare_end:
    # Check if token is at the end
    movb (%rsi), %r10b
    cmpb $0, %r10b
    jne compare_not_equal
    movl $1, %eax
    pop %rsi
    pop %r9
    ret

compare_not_equal:
    # Token not found
    movl $0, %eax
    pop %rsi
    pop %r9
    ret

# Helper: Copy string until '&'
copy_string:
    push %rdi
    push %rsi

copy_loop:
    movb (%rdi), %al      # Read char from source
    cmpb $'&', %al        # Stop at '&'
    je copy_done
    movb %al, (%rsi)      # Write to destination
    inc %rdi              # Move to next char
    inc %rsi
    jmp copy_loop

copy_done:
    movb $0, (%rsi)       # Null-terminate
    pop %rsi
    pop %rdi
    ret

# Helper: Extract integer value
extract_value:
    push %rdi
    push %rsi

    movl $0, (%rsi)       # Initialize *value = 0

extract_loop:
    movb (%rdi), %al      # Read char from str
    cmpb $0, %al          # End of string?
    je extract_done
    cmpb $'0', %al        # Is it a digit?
    jb extract_done
    cmpb $'9', %al
    ja extract_done

    subb $'0', %al        # Convert char to digit
    imull $10, (%rsi), %edx # Multiply existing value by 10
    addl %eax, %edx       # Add new digit
    movl %edx, (%rsi)     # Store new value
    inc %rdi              # Move to next char
    jmp extract_loop

extract_done:
    movl $1, %eax         # Success
    pop %rsi
    pop %rdi
    ret
    