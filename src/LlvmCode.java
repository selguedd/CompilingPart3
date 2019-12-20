import java.util.*;

/**
 * This class is responsible for LLVM code writing.
 * It contains multiple and different methods to write
 * LLVM code that is going to be generated at the output of our program.
 *
 */

public class LlvmCode {
    // store llvmcode
    private StringBuffer llvmcode;
    // llvm variables counter
    private int uvars;
    // list of variables
    private Set varriables;
    // FOR counter
    private int forid;
    // IF counter
    private int ifid;
    // WHILE counter
    private int whileid;
    // if read used in code
    private boolean print;
    // if print used in code
    private boolean read;
    // counter
    private int count;

    public LlvmCode() {
        this.llvmcode = new StringBuffer();
        this.uvars = 0;
        this.varriables = new HashSet();
        this.forid = 1;
        this.ifid = 1;
        this.whileid = 1;
        this.print = false;
        this.read = false;
        this.count=0;
        this.CreatMain();
    }
    public void assign(String variable, String value){
        if(varriables.contains(variable)){
            this.llvmcode.append("\tstore i32 " + value + ",i32* %" + variable + "\n");
        }
        else{
            this.llvmcode.append("\t%" + variable + " = alloca i32\n");
            this.llvmcode.append("\tstore i32 " + value + ",i32* %" + variable + "\n");
            varriables.add(variable);
        }

    }

    public void CreatMain() {

        // Main
        this.llvmcode.append("define i32 @main() {\n");
        this.llvmcode.append("entry:\n");

    }

    // declare variables
    public boolean DeclareVars(String var) {

        if (varriables.add(var)) {
            this.llvmcode.append("\t%" + var + " = alloca i32\n");
            return true;
        }
        return false;

    }

    // counter for llvm code variables
    public String DeclareUvars() {
        int id = this.uvars;
        this.uvars++;
        return "%" + id;
    }

    public String Uvars(String val) {
        String returnvar, uvar;
        uvar = DeclareUvars();
        this.llvmcode.append("\t" + uvar + " = alloca i32\n");
        StoreValue(uvar, val);
        returnvar = DeclareUvars();
        this.llvmcode.append("\t" + returnvar + " = load i32 , i32* " + uvar + "\n");
        return returnvar;
    }

    // store value in variable
    public void StoreValue(String uvar, String val) {
        this.llvmcode.append("\tstore i32 " + val + ",i32* " + uvar + "\n");
    }

    // load variable
    public String LoadVar(String var) {

        String uvar = DeclareUvars();
        this.llvmcode.append("\t" + uvar + " = load i32 , i32* %" + var + "\n");
        return uvar;

    }

    // check if the variable is alredy exist
    public boolean Isvar(String var) {
        if (this.varriables.contains(var)) {
            return true;
        }
        return false;

    }

    // Expression code
    public String Experssion(String a, String op, String b) {
        String var = DeclareUvars();
        this.llvmcode.append("\t" + var + " = " + op + " i32 " + a + "," + b + "\n");
        return var;
    }

    // Print code
    public void PrintVar(String print) {
        if(varriables.contains(print)){
            this.llvmcode.append("\t %" +this.uvars + " = load i32 , i32* %" + print + "\n");
            this.llvmcode.append("\tcall void @println(i32 %" + this.uvars + ")\n");
            this.uvars++;
        }
        this.print = true;
    }

    // Read code
    public void ReadVar(String read) {
        this.read = true;
        this.llvmcode.append("\t%" + this.uvars + " =call i32 @readInt()\n");
        if(varriables.contains(read)){
            this.llvmcode.append("\tstore i32 %" + this.uvars  + ",i32* %" + read + "\n");
        }
        else{
            this.llvmcode.append("\t%" + read + " = alloca i32\n");
            this.llvmcode.append("\tstore i32 %" + this.uvars + ",i32* %" + read + "\n");
            varriables.add(read);


        }
        this.uvars++;
    }

    // create id for 'for loop'
    public int Forid() {
        this.llvmcode.append("\tbr label %forloop" + forid + "\n");
        this.llvmcode.append("forloop" + forid + ":\n");
        return this.forid++;
    }

    // for code
    public void ForLop(String var, String n, int fid) {
        String uvar1 = this.LoadVar(var);
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + "=icmp sle i32 " + uvar1 + "," + n + "\n");
        this.llvmcode.append("\tbr i1 " + uvar + ", label %innerFor" + fid + ", label %afterFor" + fid + "\n");
        this.llvmcode.append("\n innerFor" + fid + ": \n");

    }

    // end For
    public void EndFor(String i, int fid) {
        this.llvmcode.append("\tbr label %endfor" + fid + "\n");
        this.llvmcode.append("endfor" + fid + ":\n");
        String it = this.LoadVar(i);
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + " =add i32 " + it + " ,1\n");
        this.StoreValue("%" + i, uvar);
        this.llvmcode.append("\tbr label %forloop" + fid + "\n");
    }

    // exit for
    public void AfterFor(int fid) {
        this.llvmcode.append("afterFor" + fid + ":\n");
    }

    // boolean expression
    public String BooleanOp(String op, String a, String b) {
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + " = icmp " + op + " i32 " + a + "," + b + "\n");
        return uvar;
    }

    // Bitwise operations
    public String Bitw(String bit, String a, String b) {
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + " =  " + bit + " i1 " + a + "," + b + "\n");
        return uvar;
    }

    // startr IF
    public int Iflabel() {

        this.llvmcode.append("\tbr label %if" + this.ifid + "\n");
        this.llvmcode.append("if" + this.ifid + ":\n");
        return this.ifid++;

    }

    // Condition IF
    public void If(String ifcnd, String condlbl, int ifid) {

        this.llvmcode.append("\tbr i1 " + ifcnd + ", label %innerif" + ifid + " ,label %" + condlbl + ifid + " \n");
        this.llvmcode.append("innerif" + ifid + ": \n");

    }

    // Label to start ELSE
    public void Ifstlabel(String lbl, int ifid) {
        this.llvmcode.append("\tbr label %endif" + ifid + "\n");
        this.llvmcode.append(lbl + ifid + ":\n");

    }

    // label to start while
    public int whilelabel() {

        this.llvmcode.append("\t br label %while" + this.whileid + "\n");
        this.llvmcode.append("while" + this.whileid + ":\n");
        return this.whileid++;

    }

    // while code
    public void While(String whilecnd, int whileid) {

        this.llvmcode.append(
                "\t br i1 " + whilecnd + ", label %innerwhile" + whileid + " ,label %afterwhile" + whileid + " \n");
        this.llvmcode.append("innerwhile" + whileid + ": \n");

    }

    // end while
    public void Endwhile(String whilecnd, int whileid) {
        this.llvmcode
                .append("\t br i1 " + whilecnd + ", label %while" + whileid + " ,label %afterwhile" + whileid + " \n");

    }

    // label after wile
    public void Afterwhile(int whileid) {

        this.llvmcode.append("afterwhile" + whileid + ": \n");

    }

    // Function Read
    private String declareRead() {
        String read="@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n" +
                "\n" +
                "; Function Attrs: nounwind uwtable\n" +
                "define i32 @readInt() #0 {\n" +
                "  %x = alloca i32, align 4\n" +
                "  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)\n" +
                "  %2 = load i32, i32* %x, align 4\n" +
                "  ret i32 %2\n" +
                "}\n" +
                "\n" +
                "declare i32 @__isoc99_scanf(i8*, ...) #1";
        return read;
    }

    // Function Read
    public String declarePrint() {
        String str="; Defining a function wich print integer\n" +
                "\n" +
                "define void @println(i32 %x) #0 {\n" +
                "  %1 = alloca i32, align 4\n" +
                "  store i32 %x, i32* %1, align 4\n" +
                "  %2 = load i32, i32* %1, align 4\n" +
                "  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)\n" +
                "  ret void\n" +
                "}\n" +
                "\n" +
                "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\n" +
                "\n" +
                "declare i32 @printf(i8*, ...) #1\n";
        return str;
    }

    // llvm code tostring
    public String toString() {
        String code = "";
        this.llvmcode.append("ret i32 0 \n }");

        if (this.read) {
            code += declareRead();
        }
        if (this.print) {
            code += declarePrint();

        }
        return code + this.llvmcode.toString();
    }
}