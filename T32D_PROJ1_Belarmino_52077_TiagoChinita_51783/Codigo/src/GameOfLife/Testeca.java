package GameOfLife;

import Pro.IProcessingApp;
import processing.core.PApplet;

public class Testeca implements IProcessingApp {

    private int nrows = 50;
    private int ncols = 50;
    private CellularAutomata ca;

    @Override
    public void setup(PApplet p) {
        ca = new CellularAutomata(p, nrows, ncols);
        //ca.initRandom();
        ca.initFromFile("C:\\Users\\belar\\Desktop\\AAA\\src\\ca\\teste.txt");
    }

    @Override
    public void draw(PApplet p, float dt) {
        ca.display(p);
        ca.update();
    }

    @Override
    public void mousePressed(PApplet p) {

    }

    @Override
    public void keyPressed(PApplet p) {

    }
}


