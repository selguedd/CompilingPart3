import com.sun.javafx.css.Rule;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements a recursive descent LL(1) parser, that will parse through the File given in argument
 * and will detect the set of rules that were used.
 * So we built a method for each and every rule of the Grammar, and in every of these method
 * we implement the built of the parsetree with 
 * "ParseTree parseTree = new ParseTree(new Symbol(NotTerminal.Program), Arrays.asList ..."
 * This class also contains the methods that will print out the rules, as well as the more verbose specification
 * at the output of our program
 *
 * @author Salma El Gueddari, Naim Sassine
 */

public class Parser {
    private LexicalAnalyzer scanner;
    private Symbol current;
    private ArrayList<Integer> rules = new ArrayList<Integer>();

    public Parser(FileReader source)throws FileNotFoundException, IOException, SecurityException{
        this.scanner= new LexicalAnalyzer(source);
        this.current= scanner.nextToken();
    }

    private ParseTree match(LexicalUnit l) throws IOException, ParserException {
        if(current.getType().equals(LexicalUnit.END_OF_STREAM)){
            // System.out.println("Parser and execution  run successfully");
            current = scanner.nextToken();
            return null;
        }
        else if(!current.getType().equals(l)){

            throw new ParserException(getErrorMatch(current,l));

        }
        else {
            Symbol cur = current;
            current = scanner.nextToken();
            return new ParseTree(cur);
        }
    }

    public ParseTree start() throws IOException, ParserException {
        // Program is the initial symbol of the Imp grammar
        return this.program();

    }


    public ParseTree program() throws IOException, ParserException {
        // Program is the initial symbol of the grammar
        // <Program> -->  BEG <Code> END
        rules.add(1);
        ParseTree parseTree = new ParseTree(new Symbol(NotTerminal.Program), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.BEG),this.code(),this.match(LexicalUnit.END),this.match(LexicalUnit.END_OF_STREAM)}));
        return parseTree;
    }

    public ParseTree code() throws IOException, ParserException {
        //<Code> --> EPSILON
        //<Code> --> <InstList>
        switch(current.getType()){
            case IF:
            case VARNAME:
            case WHILE:
            case PRINT:
            case READ:
            case FOR:
                rules.add(3);
                ParseTree parseTree = new ParseTree(new Symbol(NotTerminal.Code), Arrays.asList(new ParseTree[]{this.instList()}));
                return parseTree;
            case END:
            case ENDWHILE:
            case ENDIF:
            case ELSE:
                rules.add(2);
                return new ParseTree(new Symbol(NotTerminal.Code), Arrays.asList(new ParseTree[]{new ParseTree(new Symbol(LexicalUnit.EPSILON))}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.IF,LexicalUnit.VARNAME,LexicalUnit.WHILE,LexicalUnit.PRINT,LexicalUnit.READ,LexicalUnit.FOR
                        ,LexicalUnit.END,LexicalUnit.ENDWHILE,LexicalUnit.ENDIF,LexicalUnit.ELSE)));
        }

    }



    public ParseTree instList() throws ParserException, IOException {
       // <InstList> --> <Instruction> <Ints>
        switch (current.getType()){
            case IF:
            case VARNAME:
            case WHILE:
            case PRINT:
            case READ:
            case FOR:
                rules.add(4);
                ParseTree parseTree = new ParseTree(new Symbol(NotTerminal.InstList), Arrays.asList(new ParseTree[]{this.instruction(),this.inst()}));
                return parseTree;
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.IF,LexicalUnit.VARNAME,LexicalUnit.WHILE,LexicalUnit.PRINT,LexicalUnit.READ,LexicalUnit.FOR)));
        }
    }

    private ParseTree inst() throws ParserException, IOException {
        //<Inst> --> SEMICOLON <InstList>
        //<Inst> --> EPSILON
        switch(current.getType()){
            case END:
            case ENDWHILE:
            case ENDIF:
            case ELSE:
                rules.add(6);
                return new ParseTree(new Symbol(NotTerminal.Inst), Arrays.asList(new ParseTree[]{new ParseTree(new Symbol(LexicalUnit.EPSILON))}));
            case SEMICOLON:
                rules.add(5);
                ParseTree parseTree = new ParseTree(new Symbol(NotTerminal.Inst), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.SEMICOLON),this.instList()}));
                return parseTree;
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.END,LexicalUnit.ENDWHILE,LexicalUnit.ENDIF,LexicalUnit.ELSE)));
        }
    }

    public ParseTree instruction() throws ParserException, IOException {
        //<Instruction> --> <Assign>
        //<Instruction> --> <If>
        //<Instruction> --> <While>
        //<Instruction> --> <For>
        //<Instruction> --> <Print>
        //<Instruction> --> <Read>
        switch (current.getType()){
            case IF:
                rules.add(8);
                return new ParseTree(new Symbol(NotTerminal.Instruction), Arrays.asList(new ParseTree[]{this.ifP()}));
            case VARNAME:
                rules.add(7);
                return new ParseTree(new Symbol(NotTerminal.Instruction), Arrays.asList(new ParseTree[]{this.assign()}));
            case WHILE:
                rules.add(9);
                return new ParseTree(new Symbol(NotTerminal.Instruction), Arrays.asList(new ParseTree[]{this.whileP()}));
            case PRINT:
                rules.add(11);
                return new ParseTree(new Symbol(NotTerminal.Instruction), Arrays.asList(new ParseTree[]{this.printP()}));
            case READ:
                rules.add(12);
                return new ParseTree(new Symbol(NotTerminal.Instruction), Arrays.asList(new ParseTree[]{this.read()}));
            case FOR:
                rules.add(10);
                return new ParseTree(new Symbol(NotTerminal.Instruction), Arrays.asList(new ParseTree[]{this.forP()}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.IF,LexicalUnit.VARNAME,LexicalUnit.WHILE,LexicalUnit.PRINT,LexicalUnit.READ,LexicalUnit.FOR)));
        }
    }

    private ParseTree forP() throws ParserException, IOException{
        //<For> --> FOR [VARNAME] FROM <ExprArith> BY <ExprArith> TO <ExprArith> DO <Code> ENDWHILE
        rules.add(46);
        return new ParseTree(new Symbol(NotTerminal.For), Arrays.asList(this.match(LexicalUnit.FOR),
                this.match(LexicalUnit.VARNAME),
                this.match(LexicalUnit.FROM),
                this.expArith(),
                this.match(LexicalUnit.BY),
                this.expArith(),
                this.match(LexicalUnit.TO),
                this.expArith(),
                this.match(LexicalUnit.DO),
                this.code(),
                this.match(LexicalUnit.ENDWHILE)));

    }

    private ParseTree read() throws ParserException, IOException {
        //<Read> --> READ LEFT_PARENTHESIS [VARNAME] RIGHT_PARENTHESIS
        rules.add(48);
        return new ParseTree(new Symbol(NotTerminal.Read), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.READ),
                this.match(LexicalUnit.LEFT_PARENTHESIS),
                this.match(LexicalUnit.VARNAME),
                this.match(LexicalUnit.RIGHT_PARENTHESIS)}));
    }

    private ParseTree printP()throws ParserException, IOException {
        //<Print> --> PRINT LEFT_PARENTHESIS [VARNAME] RIGHT_PARENTHESIS
        rules.add(47);
        return new ParseTree(new Symbol(NotTerminal.Print), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.PRINT),
                this.match(LexicalUnit.LEFT_PARENTHESIS),
                this.match(LexicalUnit.VARNAME),
                this.match(LexicalUnit.RIGHT_PARENTHESIS)}));
    }

    private ParseTree whileP() throws ParserException, IOException {
        //<While> --> WHILE <Cond> DO <Code> ENDWHILE
        rules.add(45);
        return new ParseTree(new Symbol(NotTerminal.While), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.WHILE),
                this.cond(),
                this.match(LexicalUnit.DO),
                this.code(),
                this.match(LexicalUnit.ENDWHILE)}));
    }

    private ParseTree assign() throws ParserException, IOException {
        //<Assign> --> [VARNAME] ASSIGN <ExprArith>
        rules.add(13);
        return new ParseTree(new Symbol(NotTerminal.Assign), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.VARNAME),
                this.match(LexicalUnit.ASSIGN),
                this.expArith()}));
    }

    public ParseTree ifP() throws ParserException, IOException {
        //<If> --> IF <Cond> THEN <Code> <IfTail>
        rules.add(28);
        return new ParseTree(new Symbol(NotTerminal.If), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.IF),
                this.cond(),
                this.match(LexicalUnit.THEN),
                this.code(),
                this.ifTail()}));
    }

    private ParseTree ifTail() throws ParserException, IOException {
        //<IfTail> --> ENDIF
        //<IfTail> --> ELSE <Code> ENDIF
        switch (current.getType()){
            case ENDIF:
                rules.add(29);
                return new ParseTree(new Symbol(NotTerminal.IfTail), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.ENDIF)}));
            case ELSE:
                rules.add(30);
                return new ParseTree(new Symbol(NotTerminal.IfTail), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.ELSE),
                        this.code(),
                        this.match(LexicalUnit.ENDIF)}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.ENDIF,LexicalUnit.ELSE)));
        }

    }

    private ParseTree cond()  throws ParserException, IOException {
        //<Cond> --> <AndExp> <OrExp>
        switch (current.getType()){
            case NOT:
            case NUMBER:
            case LEFT_PARENTHESIS:
            case MINUS:
            case VARNAME:
                rules.add(31);
                return new ParseTree(new Symbol(NotTerminal.Cond), Arrays.asList(new ParseTree[]{this.andExp(), this.orExp()}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.NOT,LexicalUnit.NUMBER,LexicalUnit.LEFT_PARENTHESIS,LexicalUnit.MINUS,LexicalUnit.VARNAME)));
        }
    }

    private ParseTree andExp() throws ParserException, IOException {
        //<AndExp> --> <Condis> <CondTail>
        switch (current.getType()){
            case NOT:
            case NUMBER:
            case LEFT_PARENTHESIS:
            case MINUS:
            case VARNAME:
                rules.add(34);
                return new ParseTree(new Symbol(NotTerminal.AndEpx), Arrays.asList(new ParseTree[]{this.condis(), this.condtail()}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.NOT,LexicalUnit.NUMBER,LexicalUnit.LEFT_PARENTHESIS,LexicalUnit.MINUS,LexicalUnit.VARNAME)));
        }
    }

    private ParseTree condtail()throws ParserException, IOException {
        //<CondTail>--> AND <Condis> <CondTail>
        //<CondTail>--> EPSILON
        switch (current.getType()){
            case OR:
            case DO:
            case THEN:
                rules.add(36);
                return new ParseTree(new Symbol(NotTerminal.CondTail), Arrays.asList(new ParseTree[]{new ParseTree(new Symbol(LexicalUnit.EPSILON))}));
            case AND:
                rules.add(35);
                return new ParseTree(new Symbol(NotTerminal.CondTail), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.AND),
                        this.condis(),
                        this.condtail()}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.OR,LexicalUnit.THEN,LexicalUnit.DO,LexicalUnit.AND)));
        }
    }
    private ParseTree condis()throws ParserException, IOException{
        //<Condis> --> NOT <Condis>
        //<Condis> --> <ExprArith> <Comp> <ExprArith>
        switch (current.getType()){
            case NOT:
                rules.add(37);
                return new ParseTree(new Symbol(NotTerminal.Condis), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.NOT),this.condis()}));
            case NUMBER:
            case LEFT_PARENTHESIS:
            case MINUS:
            case VARNAME:
                rules.add(38);
                return new ParseTree(new Symbol(NotTerminal.Condis), Arrays.asList(new ParseTree[]{this.expArith(),
                        this.comp(),
                        this.expArith()}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.NOT,LexicalUnit.NUMBER,LexicalUnit.LEFT_PARENTHESIS,LexicalUnit.MINUS,LexicalUnit.VARNAME)));
        }
    }

    private ParseTree expArith() throws ParserException, IOException{
        //<ExprArith> --> <ProdEx> <ExprTail>
        switch (current.getType()){
            case NUMBER:
            case LEFT_PARENTHESIS:
            case MINUS:
            case VARNAME:
                rules.add(14);
                return new ParseTree(new Symbol(NotTerminal.ExprArith), Arrays.asList(new ParseTree[]{this.prodEx(),
                        this.expTail()}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.NUMBER,LexicalUnit.LEFT_PARENTHESIS,LexicalUnit.MINUS,LexicalUnit.VARNAME)));
        }

    }

    private ParseTree expTail() throws ParserException, IOException {
        //<ExprTail>  --> <AddSous> <ProdEx> <ExprTail>
        //<ExprTail>  --> EPSILON
        switch (current.getType()){
            case MINUS:
            case PLUS:
                rules.add(15);
                return new ParseTree(new Symbol(NotTerminal.ExprTail), Arrays.asList(new ParseTree[]{this.addSous(),
                        this.prodEx(), this.expTail()}));
            case DO:
            case OR:
            case END:
            case IF:
            case BY:
            case TO:
            case THEN:
            case ENDIF:
            case ELSE:
            case RIGHT_PARENTHESIS:
            case AND:
            case ENDWHILE:
            case SEMICOLON:
            case GREATER:
            case GREATER_EQUAL:
            case SMALLER:
            case SMALLER_EQUAL:
            case DIFFERENT:
            case EQUAL:
                rules.add(16);
                return new ParseTree(new Symbol(NotTerminal.ExprTail), Arrays.asList(new ParseTree[]{new ParseTree(new Symbol(LexicalUnit.EPSILON))}));
            default:
                throw new ParserException(getErrorParse( Arrays.asList(LexicalUnit.OR,LexicalUnit.GREATER_EQUAL,LexicalUnit.GREATER,LexicalUnit.DO
                        ,LexicalUnit.END,LexicalUnit.IF,LexicalUnit.BY,LexicalUnit.TO,LexicalUnit.PLUS,LexicalUnit.TIMES,LexicalUnit.DIVIDE,LexicalUnit.MINUS
                        ,LexicalUnit.TIMES,LexicalUnit.ENDIF,LexicalUnit.ELSE,LexicalUnit.RIGHT_PARENTHESIS,LexicalUnit.AND,LexicalUnit.ENDWHILE,LexicalUnit.SEMICOLON
                        ,LexicalUnit.SMALLER,LexicalUnit.SMALLER_EQUAL,LexicalUnit.DIFFERENT,LexicalUnit.EQUAL)));
        }

    }

    private ParseTree addSous() throws ParserException, IOException {
        //<AddSous> --> PLUS
        //<AddSous> --> MINUS
        switch (current.getType()){
            case MINUS:
                rules.add(25);
                return new ParseTree(new Symbol(NotTerminal.AddSous), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.MINUS)}));
            case PLUS:
                rules.add(24);
                return new ParseTree(new Symbol(NotTerminal.AddSous), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.PLUS)}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.MINUS,LexicalUnit.PLUS)));
        }
    }

    private ParseTree prodEx() throws ParserException, IOException {
        //<ProdEx> --> <ProdAtom> <ProdTail>
        switch (current.getType()){
            case NUMBER:
            case LEFT_PARENTHESIS:
            case MINUS:
            case VARNAME:
                rules.add(17);
                return new ParseTree(new Symbol(NotTerminal.ProdEx), Arrays.asList(new ParseTree[]{this.prodAtom(), this.prodTail()}));
            default:
                throw new ParserException(getErrorParse( Arrays.asList(LexicalUnit.NUMBER,LexicalUnit.LEFT_PARENTHESIS,LexicalUnit.MINUS,LexicalUnit.VARNAME)));
        }
    }

    private ParseTree prodAtom() throws ParserException, IOException{
        //<ProdAtom> --> LEFT_PARENTHESIS <ExprArith> RIGHT_PARENTHESIS
        //<ProdAtom> --> MINUS <ProdAtom>
        //<ProdAtom> --> [VARNAME]
        //<ProdAtom> --> [NUMBER]
        switch (current.getType()){
            case NUMBER:
                rules.add(23);
                return new ParseTree(new Symbol(NotTerminal.ProdAtom), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.NUMBER)}));
            case LEFT_PARENTHESIS:
                rules.add(20);
                return new ParseTree(new Symbol(NotTerminal.ProdAtom), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.LEFT_PARENTHESIS),
                        this.expArith(),
                        this.match(LexicalUnit.RIGHT_PARENTHESIS)}));
            case MINUS:
                rules.add(21);
                return new ParseTree(new Symbol(NotTerminal.ProdAtom), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.MINUS),
                        this.prodAtom()}));
            case VARNAME:
                rules.add(22);
                return new ParseTree(new Symbol(NotTerminal.ProdAtom), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.VARNAME)}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.NUMBER,LexicalUnit.LEFT_PARENTHESIS,LexicalUnit.MINUS,LexicalUnit.VARNAME)));
        }
    }
    private ParseTree prodTail() throws ParserException, IOException{
        //<ProdTail> --> <ProdDiv> <ProdAtom> <ProdTail>
        //<ProdTail>  --> EPSILON
        switch (current.getType()){
            case TIMES:
            case DIVIDE:
                rules.add(18);
                return new ParseTree(new Symbol(NotTerminal.ProdTail), Arrays.asList(new ParseTree[]{this.prodDiv(),
                        this.prodAtom(),
                        this.prodTail()}));
            case DO:
            case OR:
            case END:
            case IF:
            case BY:
            case TO:
            case PLUS:
            case MINUS:
            case THEN:
            case ENDIF:
            case ELSE:
            case RIGHT_PARENTHESIS:
            case AND:
            case ENDWHILE:
            case SEMICOLON:
            case GREATER:
            case GREATER_EQUAL:
            case SMALLER:
            case SMALLER_EQUAL:
            case DIFFERENT:
            case EQUAL:
                rules.add(19);
                return new ParseTree(new Symbol(NotTerminal.ProdTail), Arrays.asList(new ParseTree[]{new ParseTree(new Symbol(LexicalUnit.EPSILON))}));
            default:
                throw new ParserException(getErrorParse( Arrays.asList(LexicalUnit.OR,LexicalUnit.GREATER_EQUAL,LexicalUnit.GREATER,LexicalUnit.DO
                ,LexicalUnit.END,LexicalUnit.IF,LexicalUnit.BY,LexicalUnit.TO,LexicalUnit.PLUS,LexicalUnit.TIMES,LexicalUnit.DIVIDE,LexicalUnit.MINUS
                ,LexicalUnit.TIMES,LexicalUnit.ENDIF,LexicalUnit.ELSE,LexicalUnit.RIGHT_PARENTHESIS,LexicalUnit.AND,LexicalUnit.ENDWHILE,LexicalUnit.SEMICOLON
                ,LexicalUnit.SMALLER,LexicalUnit.SMALLER_EQUAL,LexicalUnit.DIFFERENT,LexicalUnit.EQUAL)));
        }
    }

    private ParseTree prodDiv() throws ParserException, IOException {
        //<ProdDiv> --> TIMES
        //<ProdDiv> --> DIVIDE
        switch (current.getType()){
            case TIMES:
                rules.add(26);
                return new ParseTree(new Symbol(NotTerminal.ProdDiv), Arrays.asList(new ParseTree[]{match(LexicalUnit.TIMES)}));
            case DIVIDE:
                rules.add(27);
                return new ParseTree(new Symbol(NotTerminal.ProdDiv), Arrays.asList(new ParseTree[]{match(LexicalUnit.DIVIDE)}));
            default:
                throw new ParserException(getErrorParse( Arrays.asList(LexicalUnit.TIMES,LexicalUnit.DIVIDE)));
        }
    }

    private ParseTree comp()throws ParserException, IOException {
        //<Comp> --> EQUAL
        //<Comp> --> GREATER_EQUAL
        //<Comp> --> GREATER
        //<Comp> --> SMALLER_EQUAL
        //<Comp> --> SMALLER
        //<Comp> --> DIFFERENT
        switch (current.getType()){
            case GREATER:
                rules.add(41);
                return new ParseTree(new Symbol(NotTerminal.Comp), Arrays.asList(new ParseTree[]{match(LexicalUnit.GREATER)}));
            case GREATER_EQUAL:
                rules.add(40);
                return new ParseTree(new Symbol(NotTerminal.Comp), Arrays.asList(new ParseTree[]{match(LexicalUnit.GREATER_EQUAL)}));
            case EQUAL:
                rules.add(39);
                return new ParseTree(new Symbol(NotTerminal.Comp), Arrays.asList(new ParseTree[]{match(LexicalUnit.EQUAL)}));
            case SMALLER:
                rules.add(43);
                return new ParseTree(new Symbol(NotTerminal.Comp), Arrays.asList(new ParseTree[]{match(LexicalUnit.SMALLER)}));
            case SMALLER_EQUAL:
                rules.add(42);
                return new ParseTree(new Symbol(NotTerminal.Comp), Arrays.asList(new ParseTree[]{match(LexicalUnit.SMALLER_EQUAL)}));
            case DIFFERENT:
                rules.add(44);
                return new ParseTree(new Symbol(NotTerminal.Comp), Arrays.asList(new ParseTree[]{match(LexicalUnit.DIFFERENT)}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.GREATER,LexicalUnit.GREATER_EQUAL,LexicalUnit.EQUAL, LexicalUnit.SMALLER,LexicalUnit.SMALLER_EQUAL,LexicalUnit.DIFFERENT)));
        }
    }


    private ParseTree orExp()throws ParserException, IOException {
        //<OrExp>--> OR <AndExp> <OrExp>
        //<OrExp>--> EPSILON
        switch (current.getType()){
            case OR:
                rules.add(32);
                return new ParseTree(new Symbol(NotTerminal.OrExp), Arrays.asList(new ParseTree[]{this.match(LexicalUnit.OR),
                        this.andExp(),
                        this.orExp()}));
            case DO:
            case THEN:
                rules.add(33);
                return new ParseTree(new Symbol(NotTerminal.OrExp), Arrays.asList(new ParseTree[]{new ParseTree(new Symbol(LexicalUnit.EPSILON))}));
            default:
                throw new ParserException(getErrorParse(Arrays.asList(LexicalUnit.OR,LexicalUnit.THEN,LexicalUnit.DO)));
        }
    }

    public void printRules(){
        for(int i = 0; i < rules.size(); i++) {
            System.out.print(rules.get(i)+" ");
        }
    }
    public void printVerboseRules(){
        Rules verboseRules = new Rules();
        ArrayList<Integer> rulesDuplic = new ArrayList<Integer>();
        rulesDuplic = removeDuplicates(rules);
        System.out.print("\nHere is a more verbose specification of the rules that were used : \n");
        for(int i = 0; i < rulesDuplic.size(); i++) {
            System.out.print(rulesDuplic.get(i)+"- "+verboseRules.getRule(rulesDuplic.get(i)) + "\n");
        }
    }
    private String getErrorMatch(Symbol current,LexicalUnit expected){
        String message = "We expect a " + expected.toString() +" You give us "+ current.toString() +" at line "+ current.getLine();
        return message;
    }
    private String getErrorParse(List<LexicalUnit> listed){
        String message ="Parse exception at line "+ current.getLine()+ " : miss use of the rules !" + "the token expected are :";
        for (int i=0; i<listed.size(); i++){
            message = message +" "+listed.get(i).toString();
        }
        return message;
    }
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }
    }
