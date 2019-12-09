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
        if (code.getChildren().get(0).getLabel().getVariable().toString().equals("InstList")) {
            // code appel Instructions, et Inst, Inst peu soit ne rien appeler, soit appeler Instruction et se re appeler
            InstList(code.getChildren().get(0));
        }
    }


    private void InstList(ParseTree instlist) {
        if (instlist.getChildren().get(0).getLabel().getVariable().toString().equals("Instruction")){

            Instructions(instlist.getChildren().get(0));
            Inst(instlist.getChildren().get(1));

        }
    }

    private void Instructions(ParseTree instructions) {

            if (!inst.getLabel().getValue().equals("EPSILON")) {
                switch (inst.getChildren().get(0).getLabel().getType()) {
                // Assign
                case VARNAME:
                    String var = inst.getChildren().get(0).getLabel().getValue().toString();
                    if (!this.llvmCode.Isvar(var)) {
                        LlvmGeneratecodeError("Varriable " + var + " Not declared !");
                    }
                    String exp = ExpArth(inst.getChildren().get(2), inst.getChildren().get(3), inst.getChildren().get(4));
                    this.llvmCode.StoreValue("%" + var, exp);
                    break;

                case IF:
                    If(inst);
                    break;
                case WHILE:
                    While(inst);
                    break;
                case FOR:
                    // inst = FOR Node
                    For(inst);
                    break;
                case PRINT:
                    // inst.getChildren().get(2) = Explist Node
                    ExpList(inst.getChildren().get(2));
                    break;
                case READ:
                    // inst.getChildren().get(2) = VarList Node
                    ReadVarList(inst.getChildren().get(2));
                    break;
                }

            }

    }

    private void Inst(ParseTree inst) {
        if (!inst.getChildren().get(0).getLabel().getType().toString().equals("EPSILON")){
            InstList(inst.getChildren().get(1));
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