import java.util.*;
import java.io.*;
/**
 * The goal of this class is to go through the Parse Tree given in argument to the constructor,
 * identify each and every node to generate the corresponding LLVM code by calling methods from the LLVMCode class.
 *
 * @see LlvmCode
 * @author Salma El Gueddari, Naim Sassine
 */
public class LlvmGenerator {
    ParseTree PTree;
    LlvmCode llvmCode;


    public LlvmGenerator(ParseTree PTree) {
        this.PTree = PTree;
        this.llvmCode = new LlvmCode();
    }

/**
 * Method that is called to launch the reading through the parse tree
 *
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

/**
 * Method that is called when the symbol "Code" is matched
 * in the parsetree
 */

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
                    For(inst.getChildren().get(0));
                    break;
                case Print:
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

        String var = varlist.getChildren().get(2).getLabel().getValue();
        this.llvmCode.ReadVar(var);

    }


    private void Assign(ParseTree assignTree){

        String var = assignTree.getChildren().get(0).getLabel().getValue().toString();
        // if this var is not alredy declared ,we declare it

        String exp = ExpArth(assignTree.getChildren().get(2));
        this.llvmCode.assign(var,exp);

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
            String vartoload = beta.getChildren().get(0).getLabel().getValue();
            var = this.llvmCode.LoadVar(vartoload);
            if (var.equals("Not Found")) {
                LlvmGeneratecodeError("Varriable " + var + " Not initialised !");
            }
            return var;
        case NUMBER:
            var = beta.getChildren().get(0).getLabel().getValue();

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
                val = ExprTail(exp.getChildren().get(2), ProdTail(exp.getChildren().get(1).getChildren().get(1), ProdAtom(exp.getChildren().get(1).getChildren().get(0))));
                return this.llvmCode.Experssion(tmp, "add", val);
            case MINUS:
                val = ExprTail(exp.getChildren().get(2), ProdTail(exp.getChildren().get(1).getChildren().get(1), ProdAtom(exp.getChildren().get(1).getChildren().get(0))));
                return this.llvmCode.Experssion(tmp, "sub", val);
            }
        }
        return tmp;
    }







    private String andExp(ParseTree condp) {

        if (condp.getLabel().toString().equals("CondTail")) {
            if(condp.getChildren().get(1).getChildren().get(0).getLabel().toString().equals("NOT")){
                String cnd = CondTail(condp.getChildren().get(1), SimpleCond(condp.getChildren().get(0).getChildren().get(1)));
                return this.llvmCode.Bitw("xor", cnd, "true");
            }
            return CondTail(condp.getChildren().get(2), SimpleCond(condp.getChildren().get(1)));
        }
        else if(condp.getChildren().get(0).getChildren().get(0).getLabel().toString().equals("NOT")){
            String cnd = CondTail(condp.getChildren().get(1), SimpleCond(condp.getChildren().get(0).getChildren().get(1)));
            return this.llvmCode.Bitw("xor", cnd, "true");

        }
        return CondTail(condp.getChildren().get(1), SimpleCond(condp.getChildren().get(0)));

    }

    private String orExp(ParseTree condB, String condP) {

        if (!condB.getChildren().get(0).getLabel().getType().toString().equals("EPSILON")) {
            String orvar = orExp(condB.getChildren().get(2), andExp(condB.getChildren().get(1)));
            return this.llvmCode.Bitw("or", orvar, condP);

        }
        return condP;
    }

    private String CondTail(ParseTree condpb, String condprimeVar) {
        if (condpb.getChildren().get(0).getLabel().toString().equals("AND")) {
            String andvar = CondTail(condpb.getChildren().get(2), andExp(condpb));
            return this.llvmCode.Bitw("and", andvar, condprimeVar);

        }
        return condprimeVar;
    }


    private String SimpleCond(ParseTree SimpleCond) {
        if(SimpleCond!=null){
        String expA = ExpArth(SimpleCond.getChildren().get(0));
        String expB = ExpArth(SimpleCond.getChildren().get(2));
        String comp = Comp(SimpleCond.getChildren().get(1));
        return this.llvmCode.BooleanOp(comp, expA, expB);
        }
        return null;
    }

    private String Comp(ParseTree comp) {
        switch (comp.getChildren().get(0).getLabel().getValue().toString()) {
        case "=":
            return "eq";
        case ">=":
            return "sge";
        case ">":
            return "sgt";

        case "<=":
            return "sle";

        case "<":
            return "slt";

        case "/=":
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
        this.llvmCode.ForLop(var, n, forid);
        Code(forTree.getChildren().get(9));
        this.llvmCode.EndFor(var, forid);
        this.llvmCode.AfterFor(forid);
    }








    private void If(ParseTree iftree) {
        int ifid = this.llvmCode.Iflabel();
        String ifcnd = orExp(iftree.getChildren().get(1).getChildren().get(1), andExp(iftree.getChildren().get(1).getChildren().get(0)));
        String labelcond = Ifst(iftree.getChildren().get(4));

        this.llvmCode.If(ifcnd, labelcond, ifid);
        Code(iftree.getChildren().get(3));
        IfTail(iftree.getChildren().get(4), ifid);

    }


    private String Ifst(ParseTree IfstTree) {
        if (IfstTree.getChildren().get(0).getLabel().getValue().equals("else")) {
            return "else";
        }
        return "endif";

    }

    private void IfTail(ParseTree ElseCode, int ifid) {
        if (ElseCode.getChildren().get(0).getLabel().getValue().equals("else")) {
            this.llvmCode.Ifstlabel("else", ifid);
            Code(ElseCode.getChildren().get(1));
            this.llvmCode.Ifstlabel("endif", ifid);
        } else
            this.llvmCode.Ifstlabel("endif", ifid);

    }


    private void While(ParseTree whiletree) {

        int whileid = this.llvmCode.whilelabel();
        String whilecnd = orExp(whiletree.getChildren().get(1).getChildren().get(1), andExp(whiletree.getChildren().get(1).getChildren().get(0)));

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
