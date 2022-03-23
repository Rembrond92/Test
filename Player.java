package TicTacToeGame;

public class Player {
       
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
