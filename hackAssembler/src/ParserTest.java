import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void testInitialize() {
        var p = new Parser("add/Add.asm");

        String expected = "\n" +
                "\n" +
                "@2\n" +
                "D=A\n" +
                "@3\n" +
                "D=D+A\n" +
                "@0\n" +
                "M=D\n";
        assertEquals(expected, p.initialize());
    }

    @Test
    void testParse() {
        var pAdd = new Parser("add/Add.asm");
        var pMax = new Parser("max/Max.asm");
        ArrayList<String> expectedAdd = new ArrayList<>();
        expectedAdd.add("@2");
        expectedAdd.add("D=A");
        expectedAdd.add("@3");
        expectedAdd.add("D=D+A");
        expectedAdd.add("@0");
        expectedAdd.add("M=D");

        ArrayList<String> expectedMax = new ArrayList<>(Arrays.asList(
                "@R0",
                "D=M",
                "@R1",
                "D=D-M",
                "@ITSR0",
                "D;JGT",
                "@R1",
                "D=M",
                "@OUTPUT_D",
                "0;JMP",
                "(ITSR0)",
                "@R0",
                "D=M",
                "(OUTPUT_D)",
                "@R2",
                "M=D",
                "(END)",
                "@END",
                "0;JMP"
        ));

        assertEquals(expectedAdd, pAdd.parse());
        assertEquals(expectedMax, pMax.parse());
    }
}