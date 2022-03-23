package TicTacToeGame;

public class TicTacToe {
    
    static int countStep = 0;
    static int[][] map;
    static Player playerOne, playerTwo;
    static TicTacToe game;
    static boolean exit = false, end = false;
    
    public TicTacToe(Player one, Player two) {
        playerOne = one;
        playerTwo = two;
        
        map = new int[3][3];
        exit = false; end = false;
    }
    
    public static void start() throws Exception {
       
        try {
            
            if(game == null) {
                login();
            } 
        
            else {
                System.out.println(
                "Хотите поменять игроков? \n"+
                "Да? Введите: «д»\n"+
                "Нет? Введите любой другой символ.");
                
                if(Main.reader.readLine().equals("д")) {
                    playerOne = null;
                    playerTwo = null;
                    login();
                }
            }
            game = new TicTacToe(playerOne, playerTwo);
        
            Main.parser.setPlayers(playerOne.name, playerTwo.name);
    
        } catch(Exception e) {e.printStackTrace();}
    }
    
    public static void login() {
          
        try {
            
            final String nameOne, nameTwo;
        
            System.out.println("Введите имя первого игрока.");
            nameOne = Main.reader.readLine();
            System.out.println("Введите имя второго игрока.");
            nameTwo = Main.reader.readLine();
        
            for(Player person: Rating.list) {
                if(person.name.equals(nameOne)) 
                    playerOne = person;//new Player(nameOne, person.victories, person.defeats, person.draws);
                
                if(person.name.equals(nameTwo))
                    playerTwo = person;//new Player(nameTwo, person.victories, person.defeats, person.draws);
            }
        
            if(playerOne == null) {
                playerOne = new Player(nameOne, 0, 0, 0);
                Rating.list.add(playerOne);
            }
            if(playerTwo == null) {
                playerTwo = new Player(nameTwo, 0, 0, 0);
                Rating.list.add(playerTwo);
            }
        }
        catch(Exception e) {e.printStackTrace();}
    }
    
    public static void game() throws Exception {
        
        showMap();
        countStep = 0;
        while(!end) {
            for(int i = 1; i <= 2 && !end; i++) {
                
                if(i == 1) 
                    System.out.println("Первый игрок - " + playerOne.name + " делает ход. Введите номер свободной ячейки.");
                else
                    System.out.println("Второй игрок - " + playerTwo.name + " делает ход. Введите номер свободной ячейки.");
                
                move(i);
                
                if(++countStep == 9 && !end) {
                    System.out.println("Ничья!");
                    
                    playerOne.draws += 1;
                    playerTwo.draws += 1;
                    
                    Main.parser.gameResult(0, "");
                    
                    end = true;
                    break;
                }
            }
        }
    }
    
    public static void move(int player) {
        
        setValue(player);
        showMap();
        testMap(player);
        if(end) {
            if(player == 1) {
                System.out.println("Первый игрок " + playerOne.name + " победил!");
                playerOne.victories += 1;
                playerTwo.defeats += 1;
                
                Main.parser.gameResult(1, playerOne.name);
            }
            
            if(player == 2) {
                System.out.println("Второй игрок - " + playerTwo.name + " победил!");
                playerTwo.victories += 1;
                playerOne.defeats += 1;
                
                Main.parser.gameResult(2, playerTwo.name);
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
    
    public static void setValue(int player) {
        
        try {
            
            int set = Integer.parseInt(Main.reader.readLine());
            int count = 1;
            
            if(set > 9 || set < 1)
                System.out.println("Неверный номер ячейки!");
            
            else {
                
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        if(set == count) {
                            if(map[i][j] == 0) {
                                map[i][j] = player;
                            
                                Main.parser.setStep(countStep + 1, player, String.valueOf(set));
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
    
    public static void testMap(int player) {
        
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
}
