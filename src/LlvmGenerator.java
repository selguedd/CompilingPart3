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

    private void Instructions(ParseTree inst) {

                switch (inst.getChildren().get(0).getLabel().getVariable()) {
                // Assign
                case Assign:
                    //Assign(inst);
                    System.out.println("Assign");
                    break;
                case If:
                    //If(inst);
                    System.out.println("If");
                    break;
                case While:
                    //While(inst);
                    System.out.println("While");
                    break;
                case For:
                    // inst = FOR Node
                    For(inst.getChildren().get(0));
                    break;
                case Print:
                    // inst.getChildren().get(2) = Explist Node
                    //ExpList(inst.getChildren().get(2));
                    System.out.println("Print");
                    break;
                case Read:
                    // inst.getChildren().get(2) = VarList Node
                    //ReadVarList(inst.getChildren().get(2));
                    System.out.println("Read");
                    break;
                }



    }

    private void Inst(ParseTree inst) {
        if (!inst.getChildren().get(0).getLabel().getType().toString().equals("EPSILON")){
            InstList(inst.getChildren().get(1));
        }
    }




    private void For(ParseTree forTree) {

        String var = forTree.getChildren().get(1).getLabel().getValue().toString();
        if (!this.llvmCode.Isvar(var)) {
            this.llvmCode.DeclareVars(var);
        } // arrivé ici lundi soir
        /*String n = ExpArth(forTree.getChildren().get(7), forTree.getChildren().get(8), forTree.getChildren().get(9));
        String i = ExpArth(forTree.getChildren().get(3), forTree.getChildren().get(4), forTree.getChildren().get(5));
        this.llvmCode.StoreValue("%" + var, i);
        int forid = this.llvmCode.Forid();

        this.llvmCode.ForLop(var, n, forid);
        Code(forTree.getChildren().get(12));
        this.llvmCode.EndFor(var, forid);
        this.llvmCode.AfterFor(forid);*/
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




/* String var = inst.getChildren().get(0).getLabel().getValue().toString();
                       if (!this.llvmCode.Isvar(var)) {
                           LlvmGeneratecodeError("Varriable " + var + " Not declared !");
                       }
                       String exp = ExpArth(inst.getChildren().get(2), inst.getChildren().get(3), inst.getChildren().get(4));
                       this.llvmCode.StoreValue("%" + var, exp);
                       break;*/