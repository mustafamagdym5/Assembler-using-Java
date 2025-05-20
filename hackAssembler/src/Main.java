import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        Parser p1 = new Parser("/home/mustafa/courses/nand2tetris/nand2tetris/projects/6/assembler_java/hackAssembler/add/Add.asm");
        p1.writeFile();

        Parser p2 = new Parser("/home/mustafa/courses/nand2tetris/nand2tetris/projects/6/assembler_java/hackAssembler/max/MaxL.asm");
        p2.writeFile();

        Parser p3 = new Parser("/home/mustafa/courses/nand2tetris/nand2tetris/projects/6/assembler_java/hackAssembler/max/Max.asm");
        p3.writeFile();

        Parser p4 = new Parser("/home/mustafa/courses/nand2tetris/nand2tetris/projects/6/assembler_java/hackAssembler/rect/Rect.asm");
        p4.writeFile();

        Parser p5 = new Parser("/home/mustafa/courses/nand2tetris/nand2tetris/projects/6/assembler_java/hackAssembler/pong/Pong.asm");
        p5.writeFile();
    }
}
