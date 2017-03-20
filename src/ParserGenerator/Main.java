package ParserGenerator;

import Automata.Automata;
import Automata.GrammarUtilies;
import Automata.State;
import Automata.GrammarDetail;
import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import com.google.common.collect.RowSortedTable;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Main {

    public static void main(String argv[]) throws Exception {
//        String cupFileContent = "";
//        try {
//            File file = new File("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\src\\RunProject\\ycalc.cup");
//            //File file = new File( "/Users/viramirez/Documents/Compiler/ParserGenerator/src/ParserGenerator/file.cup");
//            FileInputStream fis = new FileInputStream(file);
//            byte[] data = new byte[(int) file.length()];
//            fis.read(data);
//            fis.close();
//
//            cupFileContent = new String(data, "UTF-8");
//        } catch (Exception e) {
//            System.out.print("error:" + e.getMessage());
//        }
//        /*          LEXER            */
//        Lexer lexer = new Lexer(cupFileContent);
//
//        /*          PARSER            */
//        Parser parser = new Parser(lexer);
//        ArrayList<StatementNode> statements = parser.Parse();
//
//        /*          SEMANTIC            */
//        for (StatementNode statement : statements) {
//            statement.ValidateSemantic();
//        }
//
//        ArrayList<ProductionStatementNode> productions = new ArrayList<>();
//        for (StatementNode statementNode : statements){
//            if (statementNode instanceof ProductionStatementNode){
//                productions.add((ProductionStatementNode)statementNode);
//            }
//        }
//
//        /*          GENERATE AUTOMATA            */
//        Automata automata = new Automata();
//        ArrayList<State> thisStates = automata.generateAllStates(productions);
//        System.out.println("");
//
//        /*          GENERATE TABLE            */
//        ArrayList<GrammarDetail> grammarDetails = GrammarUtilies.getGrammarDetails(productions);
//        RowSortedTable<String, String, String> table = ParserTable.getTable(thisStates, grammarDetails);
//
//        /*          GENERATE FILES             */

    }
}
