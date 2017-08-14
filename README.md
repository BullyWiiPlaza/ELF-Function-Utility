## What does this do?
This project allows you to parse `ELF` executables and list all defined functions (excluding the entry point function because it is not of interest). Furthermore you can copy the machine code of each function.

## What is this good for?
This allows you to compile any `C/C++` code and easily retrieve the machine code so that it can be used in a cheat code easily by [`JGecko U`](https://github.com/BullyWiiPlaza/JGeckoU)'s execute assembly code wizard for example.

## Credits  
`Fredrik Fornwall` for the [ELF Parser library](https://github.com/fornwall/jelf)
`BullyWiiPlaza` for everything else