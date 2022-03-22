import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Rating {
    
    final static ArrayList<Player> list = new ArrayList<>();
    
    public static void show() {
        
        System.out.println("Рейтинг игроков:");
        
        for(Player person: list) {
            System.out.println("Имя-" + person.name 
                               + "; Побед-" + person.victories
                               + "; Поражений-" + person.defeats
                               + "; Ничьих-" + person.draws);
        }
        
        System.out.println("***");
    }
    
    public static void deletePlayer() {
        
        try {
            
            System.out.println("Введите имя удаляемого игрока.");
            String name = Main.reader.readLine();
            boolean ok = false;
        
        
            for(Iterator<Player> it = list.listIterator(); it.hasNext();) {
                if(it.next().name.equals(name)) {
                    it.remove();
                    ok = true;
                    writeFile();
                
                    System.out.println("Игрок успешно удален.");
                }
            }
        
            if(!ok) 
                System.out.println("Игрок не найден.");
        } 
        catch (Exception e) {e.printStackTrace();}
    }
    
    public static void reset() {
        
        list.clear();
        writeFile();
        
        System.out.println("Рейтинг очищен.");
    }
    
    public static void readFile() {
        
        list.clear();
        
        try(BufferedReader fileReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/rating.txt"))) {
            while(fileReader.ready()) {
                
                String[] line = fileReader.readLine().split(";");
                list.add(new Player(line[0].split("-")[1],
                                    Integer.parseInt(line[1].split("-")[1]),
                                    Integer.parseInt(line[2].split("-")[1]),
                                    Integer.parseInt(line[3].split("-")[1])));
        
            }
        } catch(Exception e) {
            System.out.println("Файл рейтинга не был загружен.");
            //e.printStackTrace();
        }
    }
    
    public static void writeFile() {
        
        try(BufferedWriter fileWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/rating.txt"))) {
            for(Player person: list) {
                fileWriter.write("Имя-" + person.name 
                               + "; Побед-" + person.victories
                               + "; Поражений-" + person.defeats
                               + "; Ничьих-" + person.draws + "\n");
            }
        
        } catch(Exception e) {
            System.out.println("Ошибка записи файла");
            e.printStackTrace();}
    }
}
