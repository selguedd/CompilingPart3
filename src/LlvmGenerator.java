import java.util.*;

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
            // chek if there is a Varriables Node
            if (this.PTree.getChildren().get(i).getLabel().getValue() != null
                    && this.PTree.getChildren().get(i).getLabel().getValue().equals("Varriables")) {
                // Variables Tree
                Varriables(this.PTree.getChildren().get(i));

            } else if (this.PTree.getChildren().get(i).getLabel().getValue() != null
                    && this.PTree.getChildren().get(i).getLabel().getValue().equals("Code")) {
                // Code Tree
                Code(this.PTree.getChildren().get(i));

            }

        }

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