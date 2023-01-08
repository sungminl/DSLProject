# LatexConverter

## Language Features and Examples
Refer to the [User Manual](UserManual.pdf)

## Project Setup Using IntelliJ
1. Clone this repo to your local machine.
2. Use IntelliJ to open the ```LatexConverter``` folder.
3. Generate the recognizers for ```LatexConverterLexer.g4``` and ```LatexConverterParser.g4``` located in the ```src/parser``` folder using ANTLR.
4. Select ```Main``` as the main class in the ```src/ui``` folder.
5. Modify ```input.txt``` for your DSL input. Run ```Main``` to generate ```output.tex``` which is the output Latex file. If you would like to automatically generate a PDF version as well, proceed to the [Instructions for PDF Conversion](#instructions-for-pdf-conversion) section for setup instructions.

###### Setting up Libraries for JUnit
1. Go to ```File``` &#8594; ```Project Structure``` &#8594; ```Project Settings``` &#8594; ```Libraries``` and click on the + button to add a New Project Library.
2. Choose "Java".
3. Under ```LatexConverter/lib```, select all jar files **except for** ```antlr-4.10.1-complete.jar```.
4. Press OK.
5. Press Apply and OK and exit.

## Instructions for PDF Conversion
We have included a feature to automatically convert the Latex .tex files into a PDF. For this feature to work, you must install the Latex compiler [MiKTeX](https://miktex.org/download) on your local machine. Follow the tutorial for your desired OS, and make sure to use the Upgrade option after installation. Otherwise, you can copy and paste the .tex output into an online compiler such as Overleaf.
