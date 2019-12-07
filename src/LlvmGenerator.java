import java.util.*;
import java.io.*;
/**
 *
 * parcour the ParseTree and call LlvmCoda.java to write the code
 *
 * @see LlvmCode
 *
 */
public class LlvmGenerator {
    /**
     * ParseTree
     */
    ParseTree PTree;
    /** LLVM code generator */
    LlvmCode llvmCode;

    /**
     *
     * @param PTree ParseTree
     */
    public LlvmGenerator(ParseTree PTree) {
        this.PTree = PTree;
        this.llvmCode = new LlvmCode();
    }

    /**
     * Generate the llvm code
     */
    public void Generate() {

        // Childrens of the ParseTree
        for (int i = 0; i < this.PTree.getChildren().size() - 1; i++) {
            if (this.PTree.getChildren().get(i).getLabel().getVariable() != null) {
                if (this.PTree.getChildren().get(i).getLabel().getVariable().toString().equals("Code")) {
                    // Variables Tree
                    Code(this.PTree.getChildren().get(i));
                }
            }

        }

    }



    private void Code(ParseTree code) {
        System.out.println("n"+code.getLabel()+"n");
        if (code.getChildren().get(0).getLabel().getVariable().toString().equals("InstList")) {
            // code appel Instructions, et Inst, Inst peu soit ne rien appeler, soit appeler Instruction et se re appeler
            Instructions(code.getChildren().get(0));
            Inst(code.getChildren().get(1));
        }
    }


    private void Instructions(ParseTree instructions) {
        System.out.println("Je suis la");
    }

    private void Inst(ParseTree inst) {
        System.out.println("Je suis ici");
    }



    private void LlvmGeneratecodeError(String var) {
        System.out.println(var);
        System.exit(-1);
    }

    /**
     * llvm code to string
     */
    public String toString() {
        return this.llvmCode.toString();
    }
}