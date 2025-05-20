import java.util.*;

// convert the list of asm instructions to  a list of binary instructions
public class Code {
    private ArrayList<String> instructionsList;

    public Code (ArrayList<String> instructionsList) {
        this.instructionsList = instructionsList;
    }

    public ArrayList<String> getInstructionsList() {
        return instructionsList;
    }

    public void setInstructionsList(ArrayList<String> instructionsList) {
        this.instructionsList = instructionsList;
    }

    public String instructionType(String instruction) {
        if (instruction.startsWith("@")) {
            return "A";
        } else if (instruction.startsWith("(")) {
            return "L";
        } else {
            return "C";
        }
    }

    public ArrayList<String> extractFieldsFromCInstruction(String CInstruction) {
        String dest, comp, jump;
        dest = null;
        comp = "0";
        jump = null;

        String[] jumpSplit = CInstruction.split(";");
        String destComp = jumpSplit[0];
        jump = (jumpSplit.length == 2) ? jumpSplit[1] : null;
        String[] destCompArr = destComp.split("=");
        if (destCompArr.length == 2) {
            dest = destCompArr[0];
            comp = destCompArr[1];
        } else {
            comp = destComp;
        }

        ArrayList<String> fields = new ArrayList<>(Arrays.asList(
                dest,
                comp,
                jump
        ));
        return fields;
    }

    public String assembleInstruction(String instruction, HashMap<String, Integer> table) {

        Map<String, String> compMap = new HashMap<>();
        compMap.put("0",   "0101010");
        compMap.put("1",   "0111111");
        compMap.put("-1",  "0111010");
        compMap.put("D",   "0001100");
        compMap.put("A",   "0110000");
        compMap.put("M",   "1110000");
        compMap.put("!D",  "0001101");
        compMap.put("!A",  "0110001");
        compMap.put("!M",  "1110001");
        compMap.put("-D",  "0001111");
        compMap.put("-A",  "0110011");
        compMap.put("-M",  "1110011");
        compMap.put("D+1", "0011111");
        compMap.put("A+1", "0110111");
        compMap.put("M+1", "1110111");
        compMap.put("D-1", "0001110");
        compMap.put("A-1", "0110010");
        compMap.put("M-1", "1110010");
        compMap.put("D+A", "0000010");
        compMap.put("D+M", "1000010");
        compMap.put("D-A", "0010011");
        compMap.put("D-M", "1010011");
        compMap.put("A-D", "0000111");
        compMap.put("M-D", "1000111");
        compMap.put("D&A", "0000000");
        compMap.put("D&M", "1000000");
        compMap.put("D|A", "0010101");
        compMap.put("D|M", "1010101");

        Map<String, String> destMap = new HashMap<>();
        destMap.put(null,  "000");
        destMap.put("M",   "001");
        destMap.put("D",   "010");
        destMap.put("MD",  "011");
        destMap.put("A",   "100");
        destMap.put("AM",  "101");
        destMap.put("AD",  "110");
        destMap.put("AMD", "111");

        Map<String, String> jumpMap = new HashMap<>();
        jumpMap.put(null,  "000");
        jumpMap.put("JGT", "001");
        jumpMap.put("JEQ", "010");
        jumpMap.put("JGE", "011");
        jumpMap.put("JLT", "100");
        jumpMap.put("JNE", "101");
        jumpMap.put("JLE", "110");
        jumpMap.put("JMP", "111");

        if (instructionType(instruction).equals("A")) {
            if (instruction.substring(1).matches("\\d+")) {
                int address = Integer.parseInt(instruction.substring(1));
                String binary = Integer.toBinaryString(address);

                // For negative numbers, take the last 16 bits of two's complement
                if (address < 0) {
                    binary = binary.substring(binary.length() - 16);
                } else {
                    // Pad with leading zeros to make 16 bits
                    binary = String.format("%16s", binary).replace(' ', '0');
                }

                return binary;
            } else {
                String variableName = instruction.substring(1);
                int address = table.get(variableName);

                String binary = Integer.toBinaryString(address);

                // For negative numbers, take the last 16 bits of two's complement
                if (address < 0) {
                    binary = binary.substring(binary.length() - 16);
                } else {
                    // Pad with leading zeros to make 16 bits
                    binary = String.format("%16s", binary).replace(' ', '0');
                }

                return binary;
            }
        } else if (instructionType(instruction).equals("L")) {
            return null;
        } else {
            String dest, comp, jump;
            dest = extractFieldsFromCInstruction(instruction).get(0);
            comp = extractFieldsFromCInstruction(instruction).get(1);
            jump = extractFieldsFromCInstruction(instruction).get(2);

            return "111" + compMap.get(comp) + destMap.get(dest) + jumpMap.get(jump);
        }
    }

    public HashMap<String, Integer> updateSymbolTable() {
        SymbolTable table = new SymbolTable();
        int count = 0;
        int availableAddress = 16;

        // first pass (for labels)
        for (String cmd: instructionsList) {
            if (instructionType(cmd).equals("L")) {
                table.addEntry(cmd.replace("(", "").replace(")", ""), count);
            } else {
                count++;
            }
        }

        // second pass (for variables)
        for (String cmd: instructionsList) {
            String actualCmd = cmd.substring(1);
            if (instructionType(cmd).equals("A") && !(actualCmd.matches("\\d+"))) {
                if (!(table.getTable().containsKey(actualCmd))){
                    table.addEntry(actualCmd, availableAddress);
                    availableAddress++;
                }
            }
        }

        return table.getTable();
    }

    public ArrayList<String> translateCode() {
        ArrayList<String> assembledCode = new ArrayList<String>();
        HashMap<String, Integer> table = updateSymbolTable();
        for (String instruction: instructionsList) {
            if (assembleInstruction(instruction, table) != null) {
                assembledCode.add(assembleInstruction(instruction, table));
            }
        }
        return assembledCode;
    }
}
