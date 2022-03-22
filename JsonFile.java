import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFile implements Parser {
    
    File file;
    ArrayList<String> listNames;
    
    JSONArray players;
    JSONArray steps;
    JSONObject gameResult;
    
    public void setPlayers(String one, String two) {
        
        try {
            
            players = new JSONArray();
            
            for(int i = 0; i < 2; i++) {
            
                JSONObject player = new JSONObject();
                player.put("id", i == 0? "1": "2");
                player.put("name", i == 0? one: two);
                player.put("symbol", i == 0? "X": "O");
                
                players.put(player);
            } 
            
            steps = new JSONArray();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setStep(int count, int id, String text) {
        
        try {
            
            JSONObject step = new JSONObject();
            step.put("num", count);
            step.put("playerId", id);
            step.put("text", text);
                
            steps.put(step);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void gameResult(int res, String name) {
        
        try {
        
            gameResult = new JSONObject();
        
            if(res != 0) {
                JSONObject player = new JSONObject();
                player.put("id", res == 1? "1": "2");
                player.put("name", name);
                player.put("symbol", res == 1? "X": "O");
            
                gameResult.put("Player", player);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void write() {
        
        this.readNames();
        
        String fileName = "game_" + (listNames.size() + 1);
        
        try{
            
            JSONObject root = new JSONObject();
            JSONObject gameplay = new JSONObject();
            gameplay.put("Player", players);
            JSONObject game = new JSONObject();
            game.put("Step", steps);
            gameplay.put("Game", game);
            gameplay.put("GameResult", gameResult);
            root.put("Gameplay", gameplay);

            String result = prettyJson(root.toString());
            
            PrintWriter out = new PrintWriter(new FileOutputStream(file.getPath() + "/" + fileName + ".json"));
            out.print(result);
            out.close();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private String prettyJson(String text) {
        
        StringBuilder result = new StringBuilder();
        StringBuilder tab = new StringBuilder();
        
        for(int i = 0; i < text.length(); i++) {
            
            String symbol = text.substring(i, i+1);
            
            switch (symbol) {
                
                case "{":
                    tab.append("\t");
                    result.append(symbol)
                          .append("\n")
                          .append(tab);
                    break;
                
                case "[":
                    tab.append("\t");
                    result.append(symbol)
                          .append("\n")
                          .append(tab);
                    break;
                
                case "}":
                    tab.deleteCharAt(0);
                    result.append("\n")
                          .append(tab)
                          .append(symbol);
                    break;
                
                case "]":
                    tab.deleteCharAt(0);
                    result.append("\n")
                          .append(tab)
                          .append(symbol);
                    break;
                
                case ":":
                    result.append(symbol)
                          .append(" ");
                    break;
                
                case ",":
                    result.append(symbol)
                          .append("\n")
                          .append(tab);
                    break;
                
                default:
                    result.append(symbol);
            }
        }
        
        return result.toString();
    }
    
    public void replay() {
        
        this.readNames();
        System.out.println("Список доступных реплеев:");
        
        Collections.sort(listNames);
        
        for(String name: listNames)
            System.out.println(name);
        
        System.out.println("Введите имя реплея. Выйти в меню? Введите «в»");
        
        while(true) {
            try {
                String name = Main.reader.readLine();
            
                if(listNames.contains(name)) {
                
                    this.read(name);
                    break;
                }
                
                else if(name.equals("в")) return;
                
                else {
                    System.out.println("Неверное имя реплея, попробуйте ещё раз. Выйти в меню? Введите «в»");
                }
            
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void read(String name) {
        
        try(BufferedReader reader = new BufferedReader(new FileReader(file.getPath() + "/" + name + ".json"))) {
            
            StringBuilder text = new StringBuilder();
            
            while(reader.ready()) {
                text.append(reader.readLine());
            }
            
            JSONObject root = new JSONObject(text.toString());
            JSONObject gameplay = new JSONObject(root.getString("Gameplay"));

            players = new JSONArray(gameplay.getString("Player"));
            
            JSONObject game = new JSONObject(gameplay.getString("Game"));
            steps = new JSONArray(game.getString("Step"));
            gameResult = new JSONObject(gameplay.getString("GameResult"));
            
            JSONObject playerOne = new JSONObject(players.getString(0));
            JSONObject playerTwo = new JSONObject(players.getString(1));
            
            String namePlayerOne = playerOne.getString("name");
            String namePlayerTwo = playerTwo.getString("name");
            String idPlayerOne = playerOne.getString("id");
            String idPlayerTwo = playerTwo.getString("id");
            String symbolPlayerOne = playerOne.getString("symbol");
            String symbolPlayerTwo = playerTwo.getString("symbol");
            
            System.out.println("Player " + idPlayerOne + " " + namePlayerOne + " as " + symbolPlayerOne 
                               + " vs player "+ idPlayerTwo + " " + namePlayerTwo + " as " + symbolPlayerTwo);

            TicTacToe.map = new int[3][3];

            Pattern p = Pattern.compile("([0-9])");
            
            for(int i = 0; i < steps.length(); i++) {

                JSONObject step = new JSONObject(steps.getString(i));
                
                String playerId = step.getString("playerId");
                String stepText = step.getString("text");
                
                try {
                    
                    Matcher m = p.matcher(stepText);
                    int count = 0, x = 0, y = 0;
                    boolean set = false;
                    
                    while(m.find()) {
                        ++count;
                        
                        if(count == 1)
                            x = Integer.parseInt(m.group());
                        else y = Integer.parseInt(m.group());
                    }
                    System.out.println();
                    if(count == 1) {
                        for(int a = 0; a < 3 && !set; a++) {
                            for(int j = 0; j < 3 && !set; j++) {
                                if(x == count) {
                                    TicTacToe.map[a][j] = Integer.parseInt(playerId);
                                    set = true;
                                }
                                else count++;
                            }
                        }
                    }
                        
                    else TicTacToe.map[x-1][y-1] = Integer.parseInt(playerId);
                    
                    TicTacToe.showMap();
                    Thread.sleep(1000);
                    
                } catch (Exception e) {e.printStackTrace();}
            }
            
            try {
                
                JSONObject winner = new JSONObject(gameResult.getString("Player"));
                
                String nameWinner = winner.getString("name");
                String symbolWinner = winner.getString("symbol");
                String idWinner = winner.getString("id");
            
                System.out.println("Player " + idWinner + " -> " + nameWinner + " is winner as '" + symbolWinner + "'!");
          
            } catch(Exception e) {
                System.out.println("Draw!");
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка чтения файла. Попробуйте ещё раз.");
            return;
        }
        
    }
    
    public void readNames() {
        
        listNames = new ArrayList<>();
        file = new File(System.getProperty("user.dir") + "/replay");
     
        if(file.exists()) {
            if(file.listFiles() != null) {
                
                File[] list = file.listFiles();
                
                for(int i = 0; i < list.length; i++) {
                    
                    String s = list[i].getName();
                    
                    if(s.endsWith(".json"))
                        listNames.add(s.split("\\.")[0]);
                }
            }
            
        } else file.mkdir();
    }
}
