public class sym {
	public static final int EOF = 0;
	public static final int error  = 1;
	public static final int PLUS = 2;
	public static final int LPAREN = 3;
	public static final int RPAREN = 4;
	public static final int PRINT = 5;
	public static final int SEMI = 6;
	public static final int EQUALS = 7;
	public static final int MINUS = 8;
	public static final int TIMES = 9;
	public static final int DIVIDE = 10;
	public static final int ID = 11;
	public static final int NUMBER = 12;

	public static final String[] terminalNames = new String[]{
	"$",
	"error",
	"PLUS",
	"LPAREN",
	"RPAREN",
	"PRINT",
	"SEMI",
	"EQUALS",
	"MINUS",
	"TIMES",
	"DIVIDE",
	"ID",
	"NUMBER",
	};
}
