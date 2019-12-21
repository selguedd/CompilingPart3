import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * Our Main class that contains the main function will be responsible for the execution of the program.
 * In this class, we also deal with the options that the user can enter, in fact :
 * -v will give him more details on the rules that were used
 * -wt filename.tex will write down (in latex) the parsetree in the .tex file given
 *
 * @author Salma El Gueddari, Naim Sassine
 */

public class Main{
    public static void main(String[] args)throws IOException, SecurityException, ParserException {

        if (args.length == 0) {
            wrongArguments();
        }
        // String tex = ".tex";
        String ll = ".ll";
        String outputfile = "";
        int argNumber = args.length-1;
        FileReader Source = null;
        Parser parse = null;
        ParseTree parseTree=null;


        try {
             Source = new FileReader(args[0]);
        } catch (IOException ioe) {
             System.out.println("File not found: check your path");
                        }
        parse = new Parser(Source);
        parseTree = parse.start();
        LlvmGenerator llvm = new LlvmGenerator(parseTree);
        llvm.Generate();
        System.out.println(llvm.toString());
        // parse.printRules();  // Code for printing rules
		 for(int i = 1; i < args.length; ++i){

            if (args[i].equals("-o") && args[i+1].toLowerCase().contains(ll)){
                FileWriter fwTree = null;
                BufferedWriter bwTree = null;
                outputfile = args[i+1];
                try {
                    fwTree = new FileWriter(outputfile);
                    bwTree = new BufferedWriter(fwTree);
                    bwTree.write(llvm.toString());
                    bwTree.close();
                    } catch (IOException e) {
                      e.printStackTrace();
                                        }


            }


        }


    }





	public static void wrongArguments(){
		System.out.println("Usage:  -jar part3.jar inputFile.alg -o outputFile.ll \n");
		System.exit(0);
	}




}

