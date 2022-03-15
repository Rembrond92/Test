
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static java.lang.Thread.sleep;

/*
 * Игра крестики-нолики
 * Шаги:
 * Приветствие;
 * Логин игроков;
 * Ход игрока + вывод поля;
 * Объявление победителя;
 * Занесение в файл;
 * Предложение сыграть ещё раз;
 * Запрос на изменение имён игроков;
 */

public class TicTacToe {

    static BufferedReader reader;
    static int count = 0;
    static int[][] map;
    static Player playerOne, playerTwo;
    final static ArrayList<Player> list = new ArrayList<>();
    static TicTacToe game;
    static boolean exit = false, end = false;

    private TicTacToe(Player one, Player two) {
        playerOne = one;
        playerTwo = two;

        map = new int[3][3];
    }

    public static void main(String[] args) {

        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Приветствуем вас в игре крестики-нолики!");
            String choice;
            readFile();

            while(!exit) {
                System.out.println(
                        "Начать новую игру? Введите: «н»\n"+
                                "Посмотреть записи игр? Введите «з»\n"+
                                "Посмотреть рейтинг игроков? Введите: «р»\n"+
                                "Выйти из игры? Введите: «в»");

                choice = reader.readLine();

                switch (choice) {

                    case "н":
                        end = false;
                        start();
                        game();
                        writeFile();
                        XmlFile.writeXml();
                        break;

                    case "р":
                        rating();
                        System.out.println(
                                "Удалить игрока? Введите: «у»\n"+
                                        "Обнулить рейтинг? Введите: «о»\n"+
                                        "Выйти в меню? Введите любой другой символ.");

                        choice = reader.readLine();

                        if(choice.equals("у"))
                            delete();
                        else if(choice.equals("о"))
                            reset();
                        break;

                    case "з":
                        XmlFile.replay();
                        break;

                    case "в":
                        exit = true;
                        break;

                    default:
                        System.out.println("Неверный выбор! Попробуйте ещё раз.");
                        break;
                }
            }

            reader.close();

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Упс! Что-то пошло не так!");
        }
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    private static void start() throws Exception {

        if(game == null) {
            login();
        }

        else {
            System.out.println(
                    "Хотите поменять игроков? \n"+
                            "Да? Введите: «д»\n"+
                            "Нет? Введите любой другой символ.");
            if(reader.readLine().equals("д")) {
                playerOne = null;
                playerTwo = null;
                login();
            }
        }
    game = new TicTacToe(playerOne, playerTwo);
    XmlFile.setPlayers(playerOne.name, playerTwo.name);
}

    private static void login() throws Exception {

        final String nameOne, nameTwo;

        System.out.println("Введите имя первого игрока.");
        nameOne = reader.readLine();
        System.out.println("Введите имя второго игрока.");
        nameTwo = reader.readLine();

        for(Player person: list) {
            if(person.name.equals(nameOne))
                playerOne = person;//new Player(nameOne, person.victories, person.defeats, person.draws);

            if(person.name.equals(nameTwo))
                playerTwo = person;//new Player(nameTwo, person.victories, person.defeats, person.draws);
        }

        if(playerOne == null) {
            playerOne = new Player(nameOne, 0, 0, 0);
            list.add(playerOne);
        }
        if(playerTwo == null) {
            playerTwo = new Player(nameTwo,
                    0,
                    0,
                    0);
            list.add(playerTwo);
        }
    }

    private static void game() {

        showMap();
        count = 0;
        while(!end) {
            for(int i = 1; i <= 2 && !end; i++) {

                if(i == 1)
                    System.out.println("Первый игрок - " + playerOne.name + " делает ход. Введите номер свободной ячейки.");
                else
                    System.out.println("Второй игрок - " + playerTwo.name + " делает ход. Введите номер свободной ячейки.");

                XmlFile.setStep(count+1, i);
                move(i);

                if(++count == 9 && !end) {
                    System.out.println("Ничья!");

                    playerOne.draws += 1;
                    playerTwo.draws += 1;

                    XmlFile.gameResult(0, "");

                    end = true;
                    break;
                }
            }
        }
    }

    private static void move(int player) {

        setValue(player);
        showMap();
        testMap(player);
        if(end) {
            if(player == 1) {
                System.out.println("Первый игрок " + playerOne.name + " победил!");
                playerOne.victories += 1;
                playerTwo.defeats += 1;

                XmlFile.gameResult(1, playerOne.name);
            }

            if(player == 2) {
                System.out.println("Второй игрок - " + playerTwo.name + " победил!");
                playerTwo.victories += 1;
                playerOne.defeats += 1;

                XmlFile.gameResult(2, playerTwo.name);
            }
        }
    }

    public static void showMap() {

        int count = 1;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                System.out.print("[");
                if(map[i][j] == 0)
                    System.out.print(count);
                if(map[i][j] == 1)
                    System.out.print("X");
                if(map[i][j] == 2)
                    System.out.print("O");
                System.out.print("]");
                count++;
            }
            System.out.println();
        }
    }

    private static void setValue(int player) {

        try{
            int set = Integer.parseInt(reader.readLine());
            int count = 1;

            if(set > 9 || set < 1)
                System.out.println("Неверный номер ячейки!");

            else {

                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        if(set == count) {
                            if(map[i][j] == 0) {
                                map[i][j] = player;

                                XmlFile.step.setTextContent(String.valueOf(set));
                                return;
                            }
                            else {
                                System.out.println("Ячейка уже занята!");

                            }
                        }
                        count++;
                    }
                }
            }

        } catch (Exception e) {
//e.printStackTrace();
            System.out.println("Неверный номер ячейки!");
        }

        System.out.println("Попробуйте ещё раз.");
        setValue(player);
    }

    private static void testMap(int player) {

        if(map[0][0] == player &&
                map[1][1] == player &&
                map[2][2] == player) {

            end = true;
            return;
        }

        if(map[0][2] == player &&
                map[1][1] == player &&
                map[2][0] == player) {

            end = true;
            return;
        }

        for(int i = 0; i < 3; i++) {
            if(map[0][i] == player &&
                    map[1][i] == player &&
                    map[2][i] == player) {

                end = true;
                return;
            }

            if(map[i][0] == player &&
                    map[i][1] == player &&
                    map[i][2] == player) {

                end = true;
                return;
            }
        }
    }

    private static void rating() {

        System.out.println("Рейтинг игроков:");

        for(Player person: list)
            System.out.println("Имя-" + person.name
                    + "; Побед-" + person.victories
                    + "; Поражений-" + person.defeats
                    + "; Ничьих-" + person.draws);

        System.out.println("***");

    }

    private static void delete() throws Exception {

        System.out.println("Введите имя удаляемого игрока.");
        String name = reader.readLine();
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

    private static void reset() {

        list.clear();
        writeFile();

        System.out.println("Рейтинг очищен.");
    }

    private static void readFile() {

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
            System.out.println("Ошибка чтения файла!");
            e.printStackTrace();
        }
    }

    private static void writeFile() {

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
