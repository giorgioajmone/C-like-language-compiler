# BAIL-IN #
This language allows for:
* Multiple declarations
* Multiple assignment
* Statically-typed multi-return functions
The project provides a compiler for the language, and it is built on top of the TAM machine specification. Therefore, when a source file is read, an output file is generated, and it contains a sequence of assembly instructions to be run by the TAM machine.
AST, grammar and a glossary explaining the language specification are provided for convenience.
## Note ##
The code has been written considering an internal Windows folder as reference for compiler execution. Hence, in order to make the compiler build the TAM instructions for an arbitrary file (say test.txt, which is provided as an example of code syntax), EXAMPLES_DIR string inside compiler/Compiler.java must be modified to point to the correct path.
## Build ##
The Java main file is compiler/Compiler.java.
Use either the javac CLI compiler or an IDE to run it.
## Credits ##
* Giorgio Ajmone
* Marco De Luca
* Stefano Gregis
* Mattia Ottoborgo

