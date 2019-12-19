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

        } }



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
                    Assign(inst.getChildren().get(0));
                    break;
                case If:
                    If(inst.getChildren().get(0));
                    break;
                case While:
                    While(inst.getChildren().get(0));
                    break;
                case For:
                    //inst = FOR Node
                    For(inst.getChildren().get(0));
                    break;
                case Print:
                    //inst.getChildren().get(2) = Explist Node
                    //ExpList(inst.getChildren().get(2));
                    Print(inst.getChildren().get(0));
                    break;
                case Read:
                    Read(inst.getChildren().get(0));
                    break;

    }}


    private void Print(ParseTree printTree) {


        String var = printTree.getChildren().get(2).getLabel().getValue();
        if (!this.llvmCode.Isvar(var)) {
            LlvmGeneratecodeError(var + " not declared !");
        }
        this.llvmCode.PrintVar(var);

    }

    private void Read(ParseTree varlist) {

        String var = varlist.getChildren().get(2).getLabel().getValue().toString();
        if (!this.llvmCode.Isvar(var)) {
            this.llvmCode.DeclareVars(var);
        }
        this.llvmCode.ReadInt(var);

    }


    private void Assign(ParseTree assignTree){

        String var = assignTree.getChildren().get(0).getLabel().getValue().toString();
        // if this var is not alredy declared ,we declare it
        this.llvmCode.DeclareVars(var);
        String exp = ExpArth(assignTree.getChildren().get(2));
        this.llvmCode.StoreValue("%" + var, exp);
    }



    private void Inst(ParseTree inst) {
        if (!inst.getChildren().get(0).getLabel().getType().toString().equals("EPSILON")){
            InstList(inst.getChildren().get(1));
        }
    }









 private String ExpArth(ParseTree exprarith) {

       return ExprTail(exprarith.getChildren().get(1), ProdTail(exprarith.getChildren().get(0).getChildren().get(1), ProdAtom(exprarith.getChildren().get(0).getChildren().get(0))));

    }

    private String ProdAtom(ParseTree beta) {
        String var = "";
        switch (beta.getChildren().get(0).getLabel().getType()) {
        case VARNAME:
            String vartoload = beta.getChildren().get(0).getLabel().getValue().toString();
            var = this.llvmCode.LoadVar(vartoload);
            if (var.equals("Not Found")) {
                LlvmGeneratecodeError("Varriable " + var + " Not initialised !");
            }
            return var;
        case NUMBER:
            var = beta.getChildren().get(0).getLabel().getValue().toString();

            return this.llvmCode.Uvars(var);

        case MINUS:
            var = ProdAtom(beta.getChildren().get(1));

            return this.llvmCode.Experssion("0", "sub nsw", var);
        case LEFT_PARENTHESIS:
            var = ExpArth(beta.getChildren().get(1));
            return var;
        }
        return var;
    }

    private String ProdTail(ParseTree tmp, String valbet) {
        String val = "";
        if (tmp.getChildren().size() > 1) {
            switch (tmp.getChildren().get(0).getChildren().get(0).getLabel().getType()) {
            case TIMES:
                val = this.llvmCode.Experssion(valbet, "mul", ProdAtom(tmp.getChildren().get(1)));

                return ProdTail(tmp.getChildren().get(2), val);

            case DIVIDE:
                val = this.llvmCode.Experssion(valbet, "sdiv", ProdAtom(tmp.getChildren().get(1)));

                return ProdTail(tmp.getChildren().get(2), val);
            }

        }
        return valbet;
    }

    private String ExprTail(ParseTree exp, String tmp) {

        if (exp.getChildren().size() > 1) {
            String val = "";
            switch (exp.getChildren().get(0).getChildren().get(0).getLabel().getType()) {
            case PLUS:
                //val = ExpArth(exp.getChildren().get(1));
                val = ExprTail(exp.getChildren().get(2), ProdTail(exp.getChildren().get(1).getChildren().get(1), ProdAtom(exp.getChildren().get(1).getChildren().get(0))));
                return this.llvmCode.Experssion(tmp, "add", val);
            case MINUS:
                //val = ExpArth(exp.getChildren().get(1));
                val = ExprTail(exp.getChildren().get(2), ProdTail(exp.getChildren().get(1).getChildren().get(1), ProdAtom(exp.getChildren().get(1).getChildren().get(0))));
                return this.llvmCode.Experssion(tmp, "sub", val);
            }
        }
        return tmp;
    }







 private String CondPrime(ParseTree condp) {
        // getLabel().getVariable().toString().equals("InstList")
        if (condp.getChildren().get(0).getChildren().get(0).getLabel().getVariable().toString().equals("NOT")) {

            String cnd = CondPrimeBeta(condp.getChildren().get(1), SimpleCond(condp.getChildren().get(0).getChildren().get(1)));
            return this.llvmCode.Bitw("xor", cnd, "true");
        }

        return CondPrimeBeta(condp.getChildren().get(1), SimpleCond(condp.getChildren().get(0)));

    }

    private String CondBeta(ParseTree condB, String condP) {

        if (!condB.getChildren().get(0).getLabel().getType().toString().equals("EPSILON")) {
            String orvar = CondBeta(condB.getChildren().get(2), CondPrime(condB.getChildren().get(1)));
            return this.llvmCode.Bitw("or", orvar, condP);

        }
        return condP;
    }

    private String CondPrimeBeta(ParseTree condpb, String condprimeVar) {
        //getLabel().getType().toString().equals("EPSILON")
        if (!condpb.getChildren().get(0).getLabel().getType().toString().equals("EPSILON")) {

            String andvar = CondPrimeBeta(condpb.getChildren().get(2), CondPrime(condpb.getChildren().get(1)));
            return this.llvmCode.Bitw("and", andvar, condprimeVar);

        }
        return condprimeVar;
    }


    private String SimpleCond(ParseTree SimpleCond) {
        String expA = ExpArth(SimpleCond.getChildren().get(0));
        String expB = ExpArth(SimpleCond.getChildren().get(2));
        String comp = Comp(SimpleCond.getChildren().get(1));
        return this.llvmCode.BooleanOp(comp, expA, expB);
    }

    private String Comp(ParseTree comp) {
        switch (comp.getChildren().get(0).getLabel().getValue().toString()) {
        case "EQUAL":
            return "eq";
        case "GREATER_EQUAL":
            return "sge";
        case "GREATER":
            return "sgt";

        case "SMALLER_EQUAL":
            return "sle";

        case "SMALLER":
            return "slt";

        case "DIFFERENT":
            return "ne";
        }
        return null;
    }



    private void For(ParseTree forTree) {

        String var = forTree.getChildren().get(1).getLabel().getValue().toString();
        if (!this.llvmCode.Isvar(var)) {
            this.llvmCode.DeclareVars(var);
        }

        String p = ExpArth(forTree.getChildren().get(5));
        String i = ExpArth(forTree.getChildren().get(3));
        String n = ExpArth(forTree.getChildren().get(7));



        this.llvmCode.StoreValue("%" + var, i);
        int forid = this.llvmCode.Forid();
         // faudera modifier paske c est une autre for loop dans notre lannguage
        this.llvmCode.ForLop(var, n, forid);
        Code(forTree.getChildren().get(9));
        this.llvmCode.EndFor(var, forid);
        this.llvmCode.AfterFor(forid);
    }








    private void If(ParseTree iftree) {
        // <If> --> IF <Cond> THEN <Code> <IfTail>
        int ifid = this.llvmCode.Iflabel();
        String ifcnd = CondBeta(iftree.getChildren().get(1).getChildren().get(1), CondPrime(iftree.getChildren().get(1).getChildren().get(0)));
        String labelcond = Ifst(iftree.getChildren().get(4));

        this.llvmCode.If(ifcnd, labelcond, ifid);
        Code(iftree.getChildren().get(3));
        ElseCode(iftree.getChildren().get(4), ifid);

    }


    private String Ifst(ParseTree IfstTree) {
        if (IfstTree.getChildren().get(0).getLabel().getValue().equals("ELSE")) {
            return "ELSE";
        }
        return "ENDIF";

    }

    private void ElseCode(ParseTree ElseCode, int ifid) {
        if (ElseCode.getChildren().get(0).getLabel().getValue().equals("ELSE")) {
            this.llvmCode.Ifstlabel("ELSE", ifid);
            Code(ElseCode.getChildren().get(2));
            this.llvmCode.Ifstlabel("ENDIF", ifid);
        } else
            this.llvmCode.Ifstlabel("ENDIF", ifid);

    }


    private void While(ParseTree whiletree) {

        int whileid = this.llvmCode.whilelabel();
        String whilecnd = CondBeta(whiletree.getChildren().get(1).getChildren().get(1), CondPrime(whiletree.getChildren().get(1).getChildren().get(0)));

        this.llvmCode.While(whilecnd, whileid);
        Code(whiletree.getChildren().get(3));
        this.llvmCode.Endwhile(whilecnd, whileid);
        this.llvmCode.Afterwhile(whileid);

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
