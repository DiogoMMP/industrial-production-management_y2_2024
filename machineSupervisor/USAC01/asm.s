.section .text
    .global extract_token
extract_token:
    # Initialize outputs to failure defaults
    movl $0, (%rcx)       # Set *value = 0
    movb $0, (%rdx)       # Set unit = empty string
    movl $0, %eax         # Return value = 0

    # Search for the token in the input string
search_token:
    movb (%rdi), %al      # Read next character of str
    cmpb $0, %al          # End of string?
    je not_found          # If yes, token not found

    # Compare token with the current position in the string
    push %rdi             # Save str pointer
    push %rsi             # Save token pointer
    call compare_token    # Compare token
    pop %rsi              # Restore token pointer
    pop %rdi              # Restore str pointer
    test %eax, %eax       # Was token found?
    jnz token_found       # If yes, jump to token_found

    # Move to the next character in the string
    inc %rdi
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
    push %rdi
    push %rsi

compare_loop:
    movb (%rdi), %al      # Read char from str
    movb (%rsi), %bl      # Read char from token
    cmpb $0, %bl          # End of token?
    je compare_end        # If yes, match found
    cmpb %al, %bl         # Match characters?
    jne compare_not_equal # If not, fail
    inc %rdi              # Move to next char
    inc %rsi
    jmp compare_loop

compare_end:
    movl $1, %eax         # Match found
    pop %rsi
    pop %rdi
    ret

compare_not_equal:
    movl $0, %eax         # Match not found
    pop %rsi
    pop %rdi
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
