public class Symbol {
    private  Object value;
    private int type;
    private int yyline;
    private int yycolumn;

    public Symbol(int type, int yyline, int yycolumn) {
        this.type = type;
        this.yyline = yyline;
        this.yycolumn = yycolumn;
    }

    public Symbol(int type, int yyline, int yycolumn, Object value) {
        this.type = type;
        this.yyline = yyline;
        this.yycolumn = yycolumn;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getYyline() {
        return yyline;
    }

    public int getYycolumn() {
        return yycolumn;
    }
}
