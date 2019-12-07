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
            System.out.print(this.PTree.getChildren().get(i).getLabel().getVariable());
            if (this.PTree.getChildren().get(i).getLabel().getValue() != null
                    && this.PTree.getChildren().get(i).getLabel().getValue().equals("Code")) {
                    // Variables Tree
                    Code(this.PTree.getChildren().get(i));
                 }

        }

    }



    private void Code(ParseTree code) {
        // Instructions Tree
        /*Instructions(code.getChildren().get(0));
        if (code.getChildren().size() == 3) {
            Code(code.getChildren().get(2));
        }*/
        System.out.print("this is it ");

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