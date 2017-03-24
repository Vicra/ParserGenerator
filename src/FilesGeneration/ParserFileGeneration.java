package FilesGeneration;

import Automata.GrammarDetail;
import Automata.Label;
import ParserGenerator.TreeComponents.Statements.ImportStatementNode;
import com.google.common.collect.RowSortedTable;

import java.io.*;
import java.util.ArrayList;

public class ParserFileGeneration {
    public static void generate(RowSortedTable<String, String, String> table, ArrayList<GrammarDetail> productionDetails, ArrayList<ImportStatementNode> importList) throws FileNotFoundException, UnsupportedEncodingException {

        String top = "import Tree.*;\n";
        String bottom = "";
        String fileContent = "";
        for (ImportStatementNode importStatementNode : importList) {
            fileContent += "import ";
            for (Integer i = 0; i < importStatementNode.ImportIdentifiers.size(); i++) {
                if (i == importStatementNode.ImportIdentifiers.size() - 1) {
                    fileContent += importStatementNode.ImportIdentifiers.get(i);
                } else {
                    fileContent += importStatementNode.ImportIdentifiers.get(i) + ".";
                }
            }
            fileContent += ";\n";
        }
        try {
            File file = new File("src//FilesGeneration//header.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            top += new String(data, "UTF-8");

            file = new File("src//FilesGeneration//footer.txt");
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            bottom = new String(data, "UTF-8");
        } catch (Exception e) {
            System.out.print("error:" + e.getMessage());
        }
        fileContent += top;
        for (String row : table.rowKeySet()) {
            for (String column : table.columnKeySet()) {
                if (table.contains(row, column)) {
                    String tablePut = "\t\ttable.put(\"" + row + "\", \"" + column + "\", \"" + table.get(row, column) + "\");\n";
                    fileContent += tablePut;
                }
            }
        }

        for (GrammarDetail productionLine : productionDetails) {
            String addProduction = "\t\tproductionDetails.add(new Production(\"" + productionLine.LeftHandSideKey + "\",new ArrayList<>(Arrays.asList(";
            String suffix = "\"";
            for (String rightHandSideToken : productionLine.RightHandSide) {
                addProduction += suffix;
                suffix = "\", \"";
                addProduction += rightHandSideToken;
            }
            addProduction += "\"))));\n";
            fileContent += addProduction;
        }
        fileContent += bottom;

        for (Integer i = 0; i < productionDetails.size(); i++) {
            String casePopPush = "\n\t\t\tcase " + (i + 1) + ":\n\t\t\t{";
            for (Label lable : productionDetails.get(i).LabelList)
            {
                casePopPush = casePopPush + "\n\t\t\t\t" + lable.Type + " " + lable.Name + ";" ;
            }
            for (Label lable : productionDetails.get(i).LabelList)
            {
                casePopPush = casePopPush + "\n\t\t\t\t" + lable.Name + " = ";
                if(!lable.Type.equals("Object"))
                {
                    casePopPush = casePopPush + "(" + lable.Type + ") ";
                }
                casePopPush = casePopPush + "stack.elementAt(stack.size() - " + (2 * (productionDetails.get(i).RightHandSide.size() - lable.position)) + ");";
            }
            casePopPush = casePopPush + "\n\t\t\t\tpop(magnitude);";
            for(String javaCode : productionDetails.get(i).JavaCodeList)
            {
                String java = javaCode.replace('{',' ');
                java = java.replace('}',' ');
                java = java.replace(':' , ' ');
                casePopPush = casePopPush + "\n\t\t\t\t" + java;
            }
            casePopPush = casePopPush + "\n\t\t\t\tstack.push(RESULT);\n\t\t\t\treturn;\n\t\t\t}";
            fileContent += casePopPush;
        }
        fileContent += "\n\t\t\tdefault:\n\t\t\t\treturn;\n\t\t}\n\t}\n}";

        PrintWriter writer = new PrintWriter("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\RunProject1\\src\\parser.java", "UTF-8");
        writer.println(fileContent);
        writer.close();
    }
}
