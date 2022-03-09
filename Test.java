import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

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

public class Test {

    private static BufferedReader reader;
    private final int[][] map = new int[3][3];
    private static Player playerOne, playerTwo;
    private static final ArrayList<Player> list = new ArrayList<>();
    private static Test game;
    private static boolean exit = false, end = false;

    private Test(Player one, Player two) {
        playerOne = one;
        playerTwo = two;
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
                                "Посмотреть рейтинг игроков? Введите: «р»\n"+
                                "Выйти из игры? Введите: «в»");

                choice = reader.readLine();

                switch (choice) {
                    case "н":
                        end = false;
                        start();
                        game();
                        writeFile();
                        break;
                    case "р":
                        rating();
                        System.out.println(
                                "Удалить игрока? Введите: «у»\n" +
                                        "Обнулить рейтинг? Введите: «о»\n" +
                                        "Выйти в меню? Введите любой другой символ.");

                        choice = reader.readLine();

                        if (choice.equals("у"))
                            delete();
                        else if (choice.equals("о"))
                            reset();
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
        game = new Test(playerOne, playerTwo);
    }

    private static void login() throws Exception {

        System.out.println("Введите имя первого игрока.");
        String nameOne = reader.readLine();
        System.out.println("Введите имя второго игрока.");
        String nameTwo = reader.readLine();

        for(Player person: list) {
            if(person.name.equals(nameOne))
                playerOne = person;

            if(person.name.equals(nameTwo))
                playerTwo = person;
        }

        if(playerOne == null) {
            playerOne = new Player(nameOne, 0, 0, 0);
            list.add(playerOne);
        }
        if(playerTwo == null) {
            playerTwo = new Player(nameTwo, 0, 0, 0);
            list.add(playerTwo);
        }
    }

    protected static void game() {

        showMap();
        int count = 0;
        while(!end) {
            for(int i = 1; i <= 2 && !end; i++) {
                if(i == 1)
                    System.out.println("Первый игрок - " + playerOne.name + " делает ход. Введите номер свободной ячейки.");
                else
                    System.out.println("Второй игрок - " + playerTwo.name + " делает ход. Введите номер свободной ячейки.");
                move(i);

                if(++count == 9 && !end) {
                    System.out.println("Ничья!");

                    playerOne.draws += 1;
                    playerTwo.draws += 1;

                    end = true;
                    break;
                }
            }
        }
    }

    private static void move(int player) {

        setMap(player);
        showMap();
        testMap(player);
        if(end) {
            if(player == 1) {
                System.out.println("Первый игрок " + playerOne.name + " победил!");
                playerOne.victories += 1;
                playerTwo.defeats += 1;
            }
            if(player == 2) {
                System.out.println("Второй игрок - " + playerTwo.name + " победил!");
                playerTwo.victories += 1;
                playerOne.defeats += 1;
            }
        }
    }

    private static void showMap() {

        int count = 1;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                System.out.print("[");
                if(game.map[i][j] == 0)
                    System.out.print(count);
                if(game.map[i][j] == 1)
                    System.out.print("X");
                if(game.map[i][j] == 2)
                    System.out.print("O");
                System.out.print("]");
                count++;
            }
            System.out.println();
        }
    }

    private static void setMap(int player) {

        try{
            int set = Integer.parseInt(reader.readLine());
            int count = 1;

            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if(set == count) {
                        if(game.map[i][j] == 0) {
                            game.map[i][j] = player;
                            return;
                        }
                        else {
                            System.out.println("Ячейка уже занята!");
                        }
                    }
                    count++;
                }
            }
            if(set > 9 || set < 1)
                System.out.println("Неверный номер ячейки!");

        } catch (Exception e) {
            System.out.println("Неверный номер ячейки!");
        }
        System.out.println("Попробуйте ещё раз.");
        setMap(player);
    }

    private static void testMap(int player) {

        if(game.map[0][0] == player &&
                game.map[1][1] == player &&
                game.map[2][2] == player) {

            end = true;
            return;
        }

        if(game.map[0][2] == player &&
                game.map[1][1] == player &&
                game.map[2][0] == player) {

            end = true;
            return;
        }

        for(int i = 0; i < 3; i++) {
            if(game.map[0][i] == player &&
                    game.map[1][i] == player &&
                    game.map[2][i] == player) {

                end = true;
                return;
            }

            if(game.map[i][0] == player &&
                    game.map[i][1] == player &&
                    game.map[i][2] == player) {

                end = true;
                return;
            }
        }
    }

    private static void rating() {

        System.out.println("Рейтинг игроков:");

        for(Player person: list) {
            System.out.println("Имя-" + person.name
                    + "; Побед-" + person.victories
                    + "; Поражений-" + person.defeats
                    + "; Ничьих-" + person.draws);
        }

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
        } catch(Exception ignored) {
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

class Player {

    protected String name;
    protected int victories;
    protected int defeats;
    protected int draws;

    protected Player(String name, int v, int def, int dr) {
        this.name = name;
        this.victories = v;
        this.defeats = def;
        this.draws = dr;
    }
}
