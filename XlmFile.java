import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

class XmlFile {

    static File file;
    static ArrayList<String> listNames;

    static DocumentBuilderFactory dbf;
    static DocumentBuilder db;
    static Document doc;

    static Element root;
    static Element player;
    static Element game;
    static Element step;
    static Element gameResult;

    static void setPlayers(String one, String two) {

        try{

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.newDocument();

            root = doc.createElement("Gameplay");
            doc.appendChild(root);

        } catch(Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 2; i++) {

            player = doc.createElement("Player");
            root.appendChild(player);
            player.setAttribute("id", i == 0? "1": "2");
            player.setAttribute("name", i == 0? one: two);
            player.setAttribute("symbol", i == 0? "X": "O");
        }

        game = doc.createElement("Game");
        root.appendChild(game);
    }

    static void setStep(int count, int id) {

        step = doc.createElement("Step");
        game.appendChild(step);
        step.setAttribute("num", String.valueOf(count));
        step.setAttribute("playerId", String.valueOf(id));
    }

    static void gameResult(int res, String name) {

        gameResult = doc.createElement("GameResult");
        root.appendChild(gameResult);

        if(res != 0) {
            player = doc.createElement("Player");
            gameResult.appendChild(player);
            player.setAttribute("id", res == 1? "1": "2");
            player.setAttribute("name", name);
            player.setAttribute("symbol", res == 1? "X": "O");
        }

        else
            gameResult.setTextContent("Draw!");
    }

    static void writeXml() {

        readXmlNames();

        String fileName = "game_" + (listNames.size() + 1);

        try{

            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            DOMSource src = new DOMSource(doc);
            FileOutputStream fos = new FileOutputStream(file.getPath() + "/" + fileName + ".xml");

            StreamResult result = new StreamResult(fos);

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(src, result);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static void replay() {

        readXmlNames();
        System.out.println("Список доступных реплеев:");

        for(String name: listNames)
            System.out.println(name);

        System.out.println("Введите имя реплея. Выйти в меню? Введите «в»");

        while(true) {
            try {
                String name = TicTacToe.reader.readLine();

                if(listNames.contains(name)) {

                    readXml(name);
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

    static void readXml(String name) {

        try {

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = null;

            try(FileInputStream fis = new FileInputStream(file.getPath() + "/" + name + ".xml")) {

                doc = db.parse(fis);

            } catch(Exception e) {

                e.printStackTrace();
                System.out.println("Ошибка чтения файла. Попробуйте ещё раз.");
                return;
            }

            doc.getDocumentElement().normalize();

            NodeList players = doc.getElementsByTagName("Player");
            NodeList steps = doc.getElementsByTagName("Step");

            Node playerOne = players.item(0);
            Node playerTwo = players.item(1);

            NamedNodeMap attOne = playerOne.getAttributes();
            NamedNodeMap attTwo = playerTwo.getAttributes();

            String namePlayerOne = attOne.getNamedItem("name").getNodeValue();
            String namePlayerTwo = attTwo.getNamedItem("name").getNodeValue();
            String idPlayerOne = attOne.getNamedItem("id").getNodeValue();
            String idPlayerTwo = attTwo.getNamedItem("id").getNodeValue();
            String symbolPlayerOne = attOne.getNamedItem("symbol").getNodeValue();
            String symbolPlayerTwo = attTwo.getNamedItem("symbol").getNodeValue();

            System.out.println("Player " + idPlayerOne + " " + namePlayerOne + " as " + symbolPlayerOne
                    + " vs player "+ idPlayerTwo + " " + namePlayerTwo + " as " + symbolPlayerTwo);

            TicTacToe.map = new int[3][3];

            Pattern p = Pattern.compile("([0-9])");

            for(int i = 0; i < steps.getLength(); i++) {

                Node step = steps.item(i);
                NamedNodeMap att = step.getAttributes();

                String playerId = att.getNamedItem("playerId").getNodeValue();
                String text = step.getTextContent();

                try {

                    Matcher m = p.matcher(text);
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
                    sleep(1000);

                } catch (Exception e) {e.printStackTrace();}
            }

            Node winner = players.item(2);

            try {

                NamedNodeMap attWinner = winner.getAttributes();

                String nameWinner = attWinner.getNamedItem("name").getNodeValue();
                String symbolWinner = attWinner.getNamedItem("symbol").getNodeValue();
                String idWinner = attWinner.getNamedItem("id").getNodeValue();

                System.out.println("Player " + idWinner + " -> " + nameWinner + " is winner as '" + symbolWinner + "'!");

            } catch(Exception e) {
                System.out.println("Draw!");
            }

        } catch(Exception e) {e.printStackTrace();}
    }

    static void readXmlNames() {

        listNames = new ArrayList<>();
        file = new File(System.getProperty("user.dir") + "/replay");

        if(file.exists()) {
            if(file.listFiles() != null) {

                File[] list = file.listFiles();

                assert list != null;
                for (File value : list) {

                    String s = value.getName();

                    if (s.endsWith(".xml"))
                        listNames.add(s.split("\\.")[0]);
                }
            }

        } else file.mkdir();
    }
}
