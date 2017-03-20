package FilesGeneration;

import Automata.GrammarDetail;
import ParserGenerator.TreeComponents.Statements.ImportStatementNode;
import com.google.common.collect.RowSortedTable;

import java.io.*;
import java.util.ArrayList;

public class ParserFileGeneration {
    public static void generate(RowSortedTable<String, String, String> table, ArrayList<GrammarDetail> productionDetails, ArrayList<ImportStatementNode> importList) throws FileNotFoundException, UnsupportedEncodingException {

        String top = "";
        String bottom = "";
        String fileContent = "";
        for (ImportStatementNode importIdNode : importList)
        {
            fileContent += "import ";
            for(Integer i =0; i< importIdNode.ImportIdentifiers.size();i++){
                if (i == importIdNode.ImportIdentifiers.size()-1){
                    fileContent += importIdNode.ImportIdentifiers.get(i);
                }
                else{
                    fileContent += importIdNode.ImportIdentifiers.get(i) + ".";
                }
            }
            fileContent += ";\n";
        }
        try
        {
            File file = new File("src//FilesGeneration//header.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            top = new String(data, "UTF-8");

            file = new File("src//FilesGeneration//footer.txt");
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            bottom = new String(data, "UTF-8");
        }
        catch(Exception e){
            System.out.print("error:" + e.getMessage());
        }
        fileContent += top;
        for (String row : table.rowKeySet())
        {
            for(String column : table.columnKeySet())
            {
                if(table.contains(row,column))
                {
                    String s = "\t\ttable.put(\"" + row + "\", \"" + column + "\", \"" + table.get(row,column) + "\");\n";
                    fileContent += s;
                }
            }
        }

        for(GrammarDetail productionLine : productionDetails)
        {
            String s = "\t\tproductionDetails.add(new GrammarDetail(\"" + productionLine.LeftHandSideKey + "\",new ArrayList<>(Arrays.asList(";
            String suffix = "\"";
            for(String p : productionLine.RightHandSide)
            {
                s += suffix;
                suffix = "\", \"";
                s += p;
            }
            s += "\"))));\n";
            fileContent += s;
        }
        fileContent += bottom;

        for(Integer i = 0; i < productionDetails.size(); i++)
        {
            String s = "\n\t\t\tcase " + (i + 1) + ":\n\t\t\t{";
            s = s + "\n\t\t\t\tpop(magnitude);";
            s = s + "\n\t\t\t\tstack.push(RESULT);\n\t\t\t\treturn;\n\t\t\t}";
            fileContent += s;
        }
        fileContent += "\n\t\t\tdefault:\n\t\t\t\treturn;\n\t\t}\n\t}\n}";

        PrintWriter writer = new PrintWriter("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\RunProject1\\src\\parser.java", "UTF-8");
        writer.println(fileContent);
        writer.close();
    }
}
