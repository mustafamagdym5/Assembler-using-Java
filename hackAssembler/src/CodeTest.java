import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CodeTest {
    @Test
    void testInstructionType() {
        var c = new Code(new ArrayList<>());

        assertEquals("A", c.instructionType("@3"));
        assertEquals("A", c.instructionType("@sum"));
        assertEquals("C", c.instructionType("M+1"));
        assertEquals("C", c.instructionType("D"));
        assertEquals("L", c.instructionType("(LOOP)"));
        assertEquals("L", c.instructionType("(END)"));
    }

    @Test
    void testExtractFieldsFromCInstruction() {
        var c = new Code(new ArrayList<>());

        ArrayList<String> expected1 = new ArrayList<>(Arrays.asList(
                null,
                "M",
                null
        ));

        ArrayList<String> expected2 = new ArrayList<>(Arrays.asList(
                null,
                "D+1",
                "JGE"
        ));

        ArrayList<String> expected3 = new ArrayList<>(Arrays.asList(
                "M",
                "D+1",
                "JMP"
        ));


        assertEquals(expected1, c.extractFieldsFromCInstruction("M"));
        assertEquals(expected2, c.extractFieldsFromCInstruction("D+1;JGE"));
        assertEquals(expected3, c.extractFieldsFromCInstruction("M=D+1;JMP"));
    }

    @Test
    void testAssembleInstruction() {
        var c = new Code(new ArrayList<>());
        var t = new SymbolTable().getTable();

        assertEquals("1110110000010000", c.assembleInstruction("D=A", t));
        assertEquals("1110000010010000", c.assembleInstruction("D=D+A", t));
        assertEquals("1110001100000011", c.assembleInstruction("D;JGE", t));
        assertEquals("0000000000000000", c.assembleInstruction("@R0", t));
        assertEquals("0000000000000001", c.assembleInstruction("@R1", t));
        assertEquals("0000000000000011", c.assembleInstruction("@3", t));
        assertEquals("0100000000000000", c.assembleInstruction("@SCREEN", t));
        assertEquals("0110000000000000", c.assembleInstruction("@KBD", t));
    }

    @Test
    void testUpdateSymbolTable() {
        ArrayList<String> assembly = new ArrayList<>();
        assembly.add("@R0");
        assembly.add("D=M");
        assembly.add("@sum");
        assembly.add("M=D");
        assembly.add("(LOOP)");
        assembly.add("@R1");
        assembly.add("D=M");
        assembly.add("@R2");
        assembly.add("D=D+M");
        assembly.add("@sum");
        assembly.add("M=D");
        assembly.add("(STOP)");
        assembly.add("@STOP");
        assembly.add("0;JMP");

        Code c = new Code(assembly);

        Map<String, Integer> symbolTable = new HashMap<>(Map.ofEntries(
                Map.entry("R0", 0),
                Map.entry("R1", 1),
                Map.entry("R2", 2),
                Map.entry("R3", 3),
                Map.entry("R4", 4),
                Map.entry("R5", 5),
                Map.entry("R6", 6),
                Map.entry("R7", 7),
                Map.entry("R8", 8),
                Map.entry("R9", 9),
                Map.entry("R10", 10),
                Map.entry("R11", 11),
                Map.entry("R12", 12),
                Map.entry("R13", 13),
                Map.entry("R14", 14),
                Map.entry("R15", 15),
                Map.entry("SCREEN", 16384),
                Map.entry("KBD", 24576),
                Map.entry("SP", 0),
                Map.entry("LCL", 1),
                Map.entry("ARG", 2),
                Map.entry("THIS", 3),
                Map.entry("THAT", 4)
        ));
        symbolTable.put("LOOP", 4);
        symbolTable.put("STOP", 10);
        symbolTable.put("sum", 16);  // first variable

        assertEquals(symbolTable, c.updateSymbolTable());

    }

    @Test
    void testTranslateCode() {
        ArrayList<String> expectedMachineCode = new ArrayList<>(List.of(
                "0000000000000000", // @R0
                "1111110000010000", // D=M
                "0000000000000001", // @R1
                "1111010011010000", // D=D-M
                "0000000000001010", // @ITSR0
                "1110001100000001", // D;JGT
                "0000000000000001", // @R1
                "1111110000010000", // D=M
                "0000000000001101", // @OUTPUT_D
                "1110101010000111", // 0;JMP
                "0000000000000000", // @R0  (ITSR0 label)
                "1111110000010000", // D=M
                "0000000000000010", // @R2  (OUTPUT_D label)
                "1110001100001000", // M=D
                "0000000000001111", // @END
                "1110101010000111"  // 0;JMP
        ));

        var p = new Parser("max/Max.asm");
        var c = new Code(p.parse());
        assertEquals(expectedMachineCode, c.translateCode());
    }
}