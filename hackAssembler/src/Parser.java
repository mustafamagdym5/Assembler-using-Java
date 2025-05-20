import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.spec.ECField;
import java.util.ArrayList;

// convert the asm file to list of commands
public class Parser {
    private String fileName;

    public Parser(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String newFileName) {
        this.fileName = newFileName;
    }

    public String initialize() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
            String line;
            StringBuilder file = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//")) continue;
                file.append(line);
                file.append("\n");
            }
            return file.toString();

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public ArrayList<String> parse() {
        String file = this.initialize();
        if (file == null) return new ArrayList<>();
        String[] commandsArr =  file.split("\n");
        ArrayList<String> commandsList = new ArrayList<>();

        for (String cmd: commandsArr) {
            String trimmedCmd = cmd.trim();
            if (trimmedCmd.startsWith("//") || trimmedCmd.isEmpty()) continue;
            commandsList.add(trimmedCmd);
        }
        return commandsList;
    }

    public void writeFile() {
        String hackFileName = fileName.substring(0, fileName.length() - 4) + ".hack";

        Code c = new Code(this.parse());
        ArrayList<String> listBinaryCode = c.translateCode();

        try {
            BufferedWriter writeFile = new BufferedWriter(new FileWriter(hackFileName));
            for (String lineCode: listBinaryCode) {
                writeFile.write(lineCode + "\n");
            }
            writeFile.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
