package FilesGeneration;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.TerminalDeclarationNode;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SymFileGeneration {
    public static void generate(ArrayList<TerminalDeclarationNode> terminalDeclarationNodes) throws FileNotFoundException, UnsupportedEncodingException {
        String fileContent = "";
        fileContent += "public class sym {\n";
        Integer contador = 2;

        fileContent += "\tpublic static final int EOF = 0;\n";
        fileContent += "\tpublic static final int error  = 1;\n";
        for (TerminalDeclarationNode terminalDeclarationNode : terminalDeclarationNodes){
            for (Token token : terminalDeclarationNode.DeclaresTerm){
                fileContent += "\tpublic static final int " + token.Lexeme + " = " + contador + ";\n";
                contador++;
            }
        }
        fileContent += "\n\tpublic static final String[] terminalNames = new String[]{\n";
        fileContent += "\t\"$\",\n";
        fileContent += "\t\"error\",\n";
        for (TerminalDeclarationNode terminalDeclarationNode : terminalDeclarationNodes){
            for (Integer i = 0; i< terminalDeclarationNode.DeclaresTerm.size(); i++){
                fileContent += "\t\"" + terminalDeclarationNode.DeclaresTerm.get(i).Lexeme + "\",\n";
            }
        }
        fileContent += "\t};\n";
        fileContent += "}";
        PrintWriter writer = new PrintWriter("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\RunProject1\\src\\sym.java", "UTF-8");
        writer.println(fileContent);
        writer.close();
    }
}
