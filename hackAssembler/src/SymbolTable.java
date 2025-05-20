import java.util.*;

public class SymbolTable {
    private HashMap<String, Integer> table;

    public SymbolTable() {
        table = new HashMap<>();

        // Predefined symbols
        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }

        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
    }

    public HashMap<String, Integer> getTable() {
        return this.table;
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }
}
