import java.io.BufferedReader;
import java.io.InputStreamReader;

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

public class Main {

	//static Parser parser = new XmlFile();
	static Parser parser = new JsonFile();

	static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	static boolean exit = false;

	public static void main(String[] args) {

		try {

			System.out.println("Приветствуем вас в игре крестики-нолики!");
			String choice;
			Rating.readFile();

			while (!exit) {
				System.out.println(
					"Начать новую игру? Введите: «н»\n" +
					"Посмотреть записи игр? Введите «з»\n" +
					"Посмотреть рейтинг игроков? Введите: «р»\n" +
					"Выйти из игры? Введите: «в»");

				choice = reader.readLine();

				switch (choice) {

				case "н":
					TicTacToe.start();
					TicTacToe.game();
					Rating.writeFile();
					parser.write();
					break;

				case "р":
					Rating.show();
					System.out.println(
						"Удалить игрока? Введите: «у»\n" +
						"Обнулить рейтинг? Введите: «о»\n" +
						"Выйти в меню? Введите любой другой символ.");

					choice = reader.readLine();

					if (choice.equals("у"))
						Rating.deletePlayer();
					else if (choice.equals("о"))
						Rating.reset();
					break;

				case "з":
					parser.replay();
					break;

				case "в":
					exit = true;
					reader.close();
					break;

				default:
					System.out.println("Неверный выбор! Попробуйте ещё раз.");
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Упс! Что-то пошло не так!");
		}
	}
}