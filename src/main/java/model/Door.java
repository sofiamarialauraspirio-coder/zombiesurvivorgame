package model;

public class Door {
    private int x;
    private int y;
    private int gridColLeft;
    private int gridColRight;
    private int gridRow;
    
    // NUOVA VARIABILE: La porta nasce chiusa!
    private boolean open = false;

    public Door(int x, int y, int gridColLeft, int gridColRight, int gridRow) {
        this.x = x;
        this.y = y;
        this.gridColLeft = gridColLeft;
        this.gridColRight = gridColRight;
        this.gridRow = gridRow;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getGridColLeft() { return gridColLeft; }
    public int getGridColRight() { return gridColRight; }
    public int getGridRow() { return gridRow; }

    // METODI PER APRIRE E CHIUDERE
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
}