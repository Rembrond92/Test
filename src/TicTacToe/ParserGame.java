package TicTacToe;

interface ParserGame {
    
    void setPlayers(String one, String two);
    
    void setStep(int count, int id, String text);
    
    void gameResult(int res, String name);
    
    void write();
    
    void replay();
    
    void read(String name);
    
    void readNames();
    
}
