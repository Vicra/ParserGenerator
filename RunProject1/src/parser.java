import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class parser {
    private RowSortedTable<String, String, String> table;
    private ArrayList<Production> productionDetails;
    private Stack stack;
    private MyLexer lexer;
    private boolean next = false;

    private class Production {
        public String LeftHandSide;
        public ArrayList<String> RightHandSide;

        public Production(String producer, ArrayList<String> productions) {
            LeftHandSide = producer;
            RightHandSide = productions;
        }
    }

    public class SyntaxException extends Exception
    {
        public SyntaxException(String message) {
            super(message);
        }
    }

    public parser(MyLexer lexer)
    {
        table = TreeBasedTable.create();
        productionDetails = new ArrayList<>();
        stack = new Stack();
        this.lexer = lexer;
		table.put("0", "ID", "s4");
		table.put("0", "PRINT", "s3");
		table.put("0", "expr_list", "1");
		table.put("0", "expr_part", "2");
		table.put("1", "$", "acc");
		table.put("1", "ID", "s4");
		table.put("1", "PRINT", "s3");
		table.put("1", "expr_part", "5");
		table.put("10", "DIVIDE", "r12");
		table.put("10", "MINUS", "r12");
		table.put("10", "PLUS", "r12");
		table.put("10", "RPAREN", "r12");
		table.put("10", "SEMI", "r12");
		table.put("10", "TIMES", "r12");
		table.put("11", "DIVIDE", "r13");
		table.put("11", "MINUS", "r13");
		table.put("11", "PLUS", "r13");
		table.put("11", "RPAREN", "r13");
		table.put("11", "SEMI", "r13");
		table.put("11", "TIMES", "r13");
		table.put("12", "ID", "s11");
		table.put("12", "LPAREN", "s9");
		table.put("12", "NUMBER", "s10");
		table.put("12", "expr", "19");
		table.put("12", "factor", "7");
		table.put("12", "term", "8");
		table.put("13", "$", "r3");
		table.put("13", "ID", "r3");
		table.put("13", "PRINT", "r3");
		table.put("14", "ID", "s11");
		table.put("14", "LPAREN", "s9");
		table.put("14", "NUMBER", "s10");
		table.put("14", "factor", "20");
		table.put("14", "term", "8");
		table.put("15", "ID", "s11");
		table.put("15", "LPAREN", "s9");
		table.put("15", "NUMBER", "s10");
		table.put("15", "factor", "21");
		table.put("15", "term", "8");
		table.put("16", "ID", "s11");
		table.put("16", "LPAREN", "s9");
		table.put("16", "NUMBER", "s10");
		table.put("16", "term", "22");
		table.put("17", "ID", "s11");
		table.put("17", "LPAREN", "s9");
		table.put("17", "NUMBER", "s10");
		table.put("17", "term", "23");
		table.put("18", "MINUS", "s15");
		table.put("18", "PLUS", "s14");
		table.put("18", "RPAREN", "s24");
		table.put("19", "MINUS", "s15");
		table.put("19", "PLUS", "s14");
		table.put("19", "SEMI", "s25");
		table.put("2", "$", "r2");
		table.put("2", "ID", "r2");
		table.put("2", "PRINT", "r2");
		table.put("20", "DIVIDE", "s17");
		table.put("20", "MINUS", "r5");
		table.put("20", "PLUS", "r5");
		table.put("20", "RPAREN", "r5");
		table.put("20", "SEMI", "r5");
		table.put("20", "TIMES", "s16");
		table.put("21", "DIVIDE", "s17");
		table.put("21", "MINUS", "r6");
		table.put("21", "PLUS", "r6");
		table.put("21", "RPAREN", "r6");
		table.put("21", "SEMI", "r6");
		table.put("21", "TIMES", "s16");
		table.put("22", "DIVIDE", "r8");
		table.put("22", "MINUS", "r8");
		table.put("22", "PLUS", "r8");
		table.put("22", "RPAREN", "r8");
		table.put("22", "SEMI", "r8");
		table.put("22", "TIMES", "r8");
		table.put("23", "DIVIDE", "r9");
		table.put("23", "MINUS", "r9");
		table.put("23", "PLUS", "r9");
		table.put("23", "RPAREN", "r9");
		table.put("23", "SEMI", "r9");
		table.put("23", "TIMES", "r9");
		table.put("24", "DIVIDE", "r11");
		table.put("24", "MINUS", "r11");
		table.put("24", "PLUS", "r11");
		table.put("24", "RPAREN", "r11");
		table.put("24", "SEMI", "r11");
		table.put("24", "TIMES", "r11");
		table.put("25", "$", "r4");
		table.put("25", "ID", "r4");
		table.put("25", "PRINT", "r4");
		table.put("3", "ID", "s11");
		table.put("3", "LPAREN", "s9");
		table.put("3", "NUMBER", "s10");
		table.put("3", "expr", "6");
		table.put("3", "factor", "7");
		table.put("3", "term", "8");
		table.put("4", "EQUALS", "s12");
		table.put("5", "$", "r1");
		table.put("5", "ID", "r1");
		table.put("5", "PRINT", "r1");
		table.put("6", "MINUS", "s15");
		table.put("6", "PLUS", "s14");
		table.put("6", "SEMI", "s13");
		table.put("7", "DIVIDE", "s17");
		table.put("7", "MINUS", "r7");
		table.put("7", "PLUS", "r7");
		table.put("7", "RPAREN", "r7");
		table.put("7", "SEMI", "r7");
		table.put("7", "TIMES", "s16");
		table.put("8", "DIVIDE", "r10");
		table.put("8", "MINUS", "r10");
		table.put("8", "PLUS", "r10");
		table.put("8", "RPAREN", "r10");
		table.put("8", "SEMI", "r10");
		table.put("8", "TIMES", "r10");
		table.put("9", "ID", "s11");
		table.put("9", "LPAREN", "s9");
		table.put("9", "NUMBER", "s10");
		table.put("9", "expr", "18");
		table.put("9", "factor", "7");
		table.put("9", "term", "8");
		productionDetails.add(new Production("expr_list",new ArrayList<>(Arrays.asList("expr_list", "expr_part"))));
		productionDetails.add(new Production("expr_list",new ArrayList<>(Arrays.asList("expr_part"))));
		productionDetails.add(new Production("expr_part",new ArrayList<>(Arrays.asList("PRINT", "expr", "SEMI"))));
		productionDetails.add(new Production("expr_part",new ArrayList<>(Arrays.asList("ID", "EQUALS", "expr", "SEMI"))));
		productionDetails.add(new Production("expr",new ArrayList<>(Arrays.asList("expr", "PLUS", "factor"))));
		productionDetails.add(new Production("expr",new ArrayList<>(Arrays.asList("expr", "MINUS", "factor"))));
		productionDetails.add(new Production("expr",new ArrayList<>(Arrays.asList("factor"))));
		productionDetails.add(new Production("factor",new ArrayList<>(Arrays.asList("factor", "TIMES", "term"))));
		productionDetails.add(new Production("factor",new ArrayList<>(Arrays.asList("factor", "DIVIDE", "term"))));
		productionDetails.add(new Production("factor",new ArrayList<>(Arrays.asList("term"))));
		productionDetails.add(new Production("term",new ArrayList<>(Arrays.asList("LPAREN", "expr", "RPAREN"))));
		productionDetails.add(new Production("term",new ArrayList<>(Arrays.asList("NUMBER"))));
		productionDetails.add(new Production("term",new ArrayList<>(Arrays.asList("ID"))));
 }

    public void parse() throws SyntaxException {
        stack.push("0");
        try
        {
            Symbol currentSymbol = lexer.yylex();
            while(currentSymbol != null)
            {
                Object top = stack.peek();
                String type = sym.terminalNames[currentSymbol.getType()];
                if(!table.contains(top,type)){
                    throw new SyntaxException("Syntax error line: " + currentSymbol.getYyline() + " column: " + currentSymbol.getYycolumn());
                }
                String action = table.get(top,type);
                doAction(action,currentSymbol.getValue());
                if(stack.peek().equals("acc")){
                    break;
                }
                if(next){
                    currentSymbol = lexer.yylex();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void pop(Integer magnitude)
    {
        for(Integer i = 0; i < magnitude * 2; i++)
        {
            stack.pop();
        }
    }

    private void doAction(String action, Object value) throws SyntaxException {
        char firstCharacter = action.charAt(0);
        if(action.equals("acc"))
        {
            stack.push(action);
            next = true;
            return;
        }
        if(firstCharacter == 's'){
            stack.push(value);
            String indexGramatica = action.replace("s","");
            stack.push(indexGramatica);
            next = true;
        }
        else if(firstCharacter == 'r'){
            String indexGramatica2 = action.replace("r","");
            Integer grammarIndex = Integer.parseInt(indexGramatica2);
            Production production = productionDetails.get(grammarIndex - 1);
            Integer magnitude = 0;
            if(production.RightHandSide.size() > 1){
                magnitude = production.RightHandSide.size();
            }
            else if(!production.RightHandSide.get(0).equals("ɛ")){
                magnitude = 1;
            }
            reductionCases(grammarIndex, magnitude);
            if(!table.contains(stack.elementAt(stack.size()-2),production.LeftHandSide)){
                throw new SyntaxException("Syntax error");
            }
            stack.push(table.get(stack.elementAt(stack.size()-2),production.LeftHandSide));
            next = false;
        }
    }

    private void reductionCases(Integer grammarIndex, Integer magnitude)
    {
        Object RESULT = null;
        switch (grammarIndex)
        {
			case 1:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 2:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 3:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 4:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 5:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 6:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 7:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 8:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 9:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 10:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 11:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 12:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			case 13:
			{
				pop(magnitude);
				stack.push(RESULT);
				return;
			}
			default:
				return;
		}
	}
}
