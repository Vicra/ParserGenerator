package Automaton;

import java.util.ArrayList;

public class DetalleProduccion {
    public String LeftSideKey;
    public ArrayList<String> rhs = new ArrayList<>();
    public int puntero = 0;
    public ArrayList<String> conjunto = new ArrayList<>();

    @Override
    public boolean equals(Object e){
        if (e == null)
            return false;
        if (!(e instanceof  DetalleProduccion)){
            return false;
        }
        DetalleProduccion produccion =((DetalleProduccion)e);
        return produccion.puntero == this.puntero && produccion.conjunto.equals(conjunto) && produccion.rhs.equals(rhs) && produccion.LeftSideKey.equals(LeftSideKey);
    }
}
