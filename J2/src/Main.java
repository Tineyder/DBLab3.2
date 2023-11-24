import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;


public class Main {

    public static void main(String[] args) {

        Scanner console = new Scanner(System.in);
        Scanner scanDir = new Scanner(System.in);
        Scanner filePath = new Scanner(System.in);
        Scanner fiPp = new Scanner(System.in);
        Scanner pos = new Scanner(System.in);
        String text="Вставлений текст";
        String param = "";
        String currentDirectoryPath = System.getProperty("user.dir");
        File originalFile = new File(currentDirectoryPath, "a.txt");

        for ( ; ; ) {
            System.out.print("команда===>");
            String inp = console.nextLine();
            System.out.println("Виконуємо: " + inp);

            if (inp.equals("pwd")) { printCurrentDirectory();}
            else if (inp.equals("ls")) { printCurFolders(originalFile);    }
            else if (inp.equals("cd")) { System.out.print("Введіть новий шлях до каталогу: ");
                String pathD = scanDir.nextLine();
                changeDirectory(pathD);    }
            else if (inp.equals("touch")) { System.out.print("Введіть назву файлу: ");
                String fP = filePath.nextLine();
                createFile(fP);    }
            else if (inp.equals("cat")) { System.out.print("Введіть назву файлу: ");
                String fP = fiPp.nextLine();
                System.out.print("Введіть параметр до файлу (Enter - якщо не буде параметру): ");
                String fPp = fiPp.nextLine();
                if (fP.endsWith("csv")&&fPp.equals("-t")) {
                    catCSV(fP);        } else   if (fPp.equals(""))        {  printFileContent(fP);        }
                else {
                    System.out.println("Помилка: Некоректний формат введення.");
                }
            }
            else if (inp.equals("insert")) { System.out.print("Введіть назву файлу: ");
                String fP = filePath.nextLine();
                System.out.print("Введіть позицію вставки в файл (натрульне число або 0 в початок файла чи end в кінець файла): ");
                String pS = pos.nextLine();
                insertTextIntoFile(fP,text, pS);}
            else if (inp.equals("chmod")) { System.out.print("Введіть назву файлу: ");
                String fP = filePath.nextLine();
                String mode = getPermissionFromUser();
                chmode(fP, mode);         }
            else if (inp.equals("e")) { System.exit(0);   }
            else {System.out.println("Помилка: Команда не дійсна ");  }

            currentDirectoryPath = System.getProperty("user.dir");
            originalFile = new File(currentDirectoryPath, "a.txt");
        }

    }

    public static void changeDirectory(String newDirectoryPath) {
        File newDirectory = new File(newDirectoryPath);

        if (newDirectory.isDirectory() && newDirectory.exists()) {
            System.setProperty("user.dir", newDirectory.getAbsolutePath());
            System.out.println("Директорія змінена на: " + newDirectory.getAbsolutePath());
        } else {
            System.out.println("Помилка: Директорія не існує.");
        }
    }

    public static void printCurFolders(File originalFile) {

        File folder = originalFile.getParentFile();
        for (File file : folder.listFiles())
        {
            if (file.isDirectory()) {
                System.out.println("-> " + file.getAbsolutePath());
            } else {
                System.out.println(file.getAbsolutePath() +
                        " (Розмір у байтах: " + file.length() +
                        ", Час останнього редагування: " + file.lastModified() + " )"
                        +", Видимість (hidden)- "+ file.isHidden()
                        +" Права доступу (r)"+file.canRead()+"/ (w)/"+file.canWrite()+" (e)"+file.canExecute());
            }
        }
    }

    public static void printCurrentDirectory() {
        File currentDirectory = new File(System.getProperty("user.dir"));
        System.out.println("Поточна директорія: " + currentDirectory.getAbsolutePath());
    }
    public static void createFile(String filePath) {

        File file = new File(filePath);

        try {
            if (file.createNewFile()) {
                System.out.println("Файл успішно створен!");
            } else {
                System.out.println("Такий файл вже існує");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void printFileContent(String filePath) {
        Path path = Paths.get(filePath);
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void insertTextIntoFile(String fileName, String text, String pos) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(fileName));


            int position;
            switch (pos.toLowerCase()) {
                case "end":
                    position = fileBytes.length;
                    break;
                case "0":
                    position = 0;
                    break;
                default:
                    try {
                        position = Integer.parseInt(pos);
                        if (position < 0 || position > fileBytes.length) {
                            System.out.println("Помилка: Позиція вказана за межами файлу.");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка: Неправильне значення параметра pos.");
                        return;
                    }
            }

            byte[] newTextBytes = text.getBytes();

            byte[] resultBytes = new byte[fileBytes.length + newTextBytes.length];

            System.arraycopy(fileBytes, 0, resultBytes, 0, position);

            System.arraycopy(newTextBytes, 0, resultBytes, position, newTextBytes.length);

            System.arraycopy(fileBytes, position, resultBytes, position + newTextBytes.length, fileBytes.length - position);

            Files.write(Paths.get(fileName), resultBytes, StandardOpenOption.WRITE);

            System.out.println("Текст вставлено успішно!");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    public static void chmode(String fileName, String mode) {

        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(mode);

        try {
            Path path = FileSystems.getDefault().getPath(fileName);

            Files.setPosixFilePermissions(path, permissions);

            System.out.println("Права доступу змінено успішно!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPermissionFromUser() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Введіть восьмирічну форму прав доступу (наприклад, 777): ");
            return reader.readLine();
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }
    public static void catCSV(String csvFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                printTableRow(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTableRow(String row) {
        StringTokenizer tokenizer = new StringTokenizer(row, ",;");
        while (tokenizer.hasMoreTokens()) {
            System.out.printf("%-15s|", tokenizer.nextToken().trim());
        }
        System.out.println();
    }

}
