package Swing;

import Automata.Automata;
import Automata.State;
import Automata.GrammarDetail;
import Automata.GrammarUtilies;
import FilesGeneration.ParserFileGeneration;
import FilesGeneration.SymFileGeneration;
import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.ParserTable;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ImportStatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatementNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.NonTerminalDeclarationNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.TerminalDeclarationNode;
import com.google.common.collect.RowSortedTable;
import javafx.util.Pair;

import javax.swing.*;
import javax.xml.bind.SchemaOutputResolver;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.List;

public class MainWindow extends JPanel {
    private boolean DEBUG = false;

    public MainWindow(RowSortedTable<String, String, String> ptable) {
        super(new GridLayout(1, 0));

        String[] columnNames = new String[ptable.columnKeySet().size()];
        for (int i = 0; i < ptable.columnKeySet().size(); i++) {
            columnNames[i] = (String) ptable.columnKeySet().toArray()[i];
        }
        Integer rows = ptable.rowKeySet().size();
        Integer columns = ptable.columnKeySet().size();
        Object[][] data = new Object[rows][columns];

        String[] columnNames2 = new String[ptable.columnKeySet().size() + 1];
        columnNames2[0] = "States";
        for (int i = 0; i < columnNames.length; i++) {
            columnNames2[i + 1] = columnNames[i];
        }
        Object[][] data2 = new Object[rows][columns + 1];


        //ordenar
        ArrayList<Pair<String, Map<String, String>>> sortedArray = new ArrayList<>();
        for (Integer c = 0; c < ptable.rowMap().size(); c++) {
            Map<String, String> currentMap = getMapByKey(ptable.rowMap(), c.toString());
            Pair<String, Map<String, String>> pair = new Pair<>(c.toString(), currentMap);
            sortedArray.add(pair);
        }
        System.out.println("");
        //ordenar

        //columna State
        for (int i = 0; i < sortedArray.size(); i++) {
            Pair<String, Map<String, String>> pair = sortedArray.get(i);

            for (int columnIndex = 0; columnIndex < ptable.columnKeySet().size(); columnIndex++) {
                String keyColumn = (String) ptable.columnKeySet().toArray()[columnIndex];

                String mapKeyValue = "";
                if (pair.getValue().get(keyColumn) != null) {
                    mapKeyValue = pair.getValue().get(keyColumn);
                }
                if (!mapKeyValue.isEmpty()) {
                    data[i][columnIndex] = mapKeyValue;
                } else {
                    data[i][columnIndex] = "";
                }
            }
        }

        for (Integer i = 0; i < rows; i++) {
            for (Integer j = 0; j < columns; j++) {
                data2[i][j + 1] = data[i][j];
            }
        }

        for (Integer i =0 ;i<rows;i++){
            data2[i][0] = i;
        }

        final JTable table = new JTable(data2, columnNames2);
        table.setPreferredScrollableViewportSize(new Dimension(1200, 450));
        table.setFillsViewportHeight(true);

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    private Map<String, String> getMapByKey(SortedMap<String, Map<String, String>> sortedMap, String key) {
        for (Integer c = 0; c < sortedMap.size(); c++) {
            String currentKey = (String) sortedMap.keySet().toArray()[c];

            if (currentKey.equals(key)) {
                return sortedMap.get(currentKey);
            }
        }
        return null;
    }

    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(RowSortedTable<String, String, String> table) {
        //Create and set up the window.
        JFrame frame = new JFrame("MainWindow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        MainWindow newContentPane = new MainWindow(table);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.setSize(1000, 1000);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        String cupFileContent = "";
        try {
            File file = new File("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\RunProject1\\src\\ycalc.cup");
            //File file = new File( "/Users/viramirez/Documents/Compiler/ParserGenerator/src/ParserGenerator/file.cup");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            cupFileContent = new String(data, "UTF-8");
        } catch (Exception e) {
            System.out.print("error:" + e.getMessage());
        }
        /*          LEXER            */
        Lexer lexer = new Lexer(cupFileContent);

        /*          PARSER            */
        Parser parser = new Parser(lexer);
        ArrayList<StatementNode> statements = parser.Parse();

        /*          SEMANTIC            */
        for (StatementNode statement : statements) {
            statement.ValidateSemantic();
        }

        ArrayList<ProductionStatementNode> productions = new ArrayList<>();
        ArrayList<SymbolListStatementNode> symbolList = new ArrayList<>();
        ArrayList<ImportStatementNode> importList = new ArrayList<>();
        for (StatementNode statementNode : statements) {
            if (statementNode instanceof ProductionStatementNode) {
                productions.add((ProductionStatementNode) statementNode);
            }
            else if(statementNode instanceof TerminalDeclarationNode){
                symbolList.add((TerminalDeclarationNode)statementNode);
            }
            else if(statementNode instanceof NonTerminalDeclarationNode){
                symbolList.add((NonTerminalDeclarationNode)statementNode);
            }
            else if(statementNode instanceof ImportStatementNode){
                importList.add((ImportStatementNode)statementNode);
            }
        }

        /*          GENERATE AUTOMATA            */
        Automata automata = new Automata();
        ArrayList<State> thisStates = automata.generateAllStates(productions);


        Map<String,String> typeTable= GrammarUtilies.getTypeMap(symbolList);

        /*          GENERATE TABLE            */
        ArrayList<GrammarDetail> grammarDetails = GrammarUtilies.getGrammarDetails(productions, typeTable);
        RowSortedTable<String, String, String> table = ParserTable.getTable(thisStates, grammarDetails);

        /*          SYM FILE GENERATION        */
        ArrayList<TerminalDeclarationNode> terminalDeclarationNodes = new ArrayList<>();
        ArrayList<NonTerminalDeclarationNode> nonTerminalDeclarationNodes= new ArrayList<>();
        for (StatementNode statementNode : statements){
            if (statementNode instanceof TerminalDeclarationNode){
                terminalDeclarationNodes.add((TerminalDeclarationNode)statementNode);
            }
            if (statementNode instanceof NonTerminalDeclarationNode){
                nonTerminalDeclarationNodes.add((NonTerminalDeclarationNode)statementNode);
            }
        }
        SymFileGeneration.generate(terminalDeclarationNodes);

        /*          PARSER FILE GENERATION        */
        ParserFileGeneration.generate(table, grammarDetails, importList);

        /*          SHOW TABLE                 */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(table);
            }
        });

        /*          PRINT DETAILS               */
        for (TerminalDeclarationNode term : terminalDeclarationNodes){
            System.out.println(term.DeclaresTerm.size() + " Terminals Declared.");
        }
        for (NonTerminalDeclarationNode nonterminal : nonTerminalDeclarationNodes){
            System.out.println(nonterminal.DeclaresTerm.size() + " Non Terminals Declared.");
        }
        System.out.println( "Code written to \"parser.java\", and \"sym.java\".");
    }
}
