package GameOfLife;

import javax.sound.midi.*;
import processing.core.PApplet;

public class CellularAutomata {
    private int nrows;
    private int ncols;
    private Cell[][] cells;
    private int cellWidth, cellHeight;
    private PApplet p;

    private Synthesizer synthesizer;
    private MidiChannel[] channels;
    private final int INSTRUMENT = 1;


    public CellularAutomata(PApplet p, int nrows, int ncols) {
        this.p = p;
        this.nrows = nrows;
        this.ncols = ncols;
        cells = new Cell[nrows][ncols];
        cellWidth = p.width / ncols;
        cellHeight = p.height / nrows;
        createCells();
        initMIDI();
    }

    private void initMIDI() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channels = synthesizer.getChannels();
            channels[0].programChange(INSTRUMENT);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playNote(int note, int velocity) {
        channels[0].noteOn(note, velocity);
    }

    private int getPentatonicNoteFromRow(int row) {
        int[] pentatonicScale = {60, 62, 64, 65, 67, 69};
        return pentatonicScale[row % pentatonicScale.length];
    }

    public void initFromFile(String filePath) {
        try {

            String[] lines = p.loadStrings(filePath);


            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncols; j++) {
                    cells[i][j].setState(0);
                    cells[i][j].setColor(p.color(255));
                }
            }


            for (String line : lines) {
                String[] values = line.split(" ");
                if (values.length == 3) {
                    int row = Integer.parseInt(values[0]);
                    int col = Integer.parseInt(values[1]);
                    int state = Integer.parseInt(values[2]);


                    if (row >= 0 && row < nrows && col >= 0 && col < ncols) {
                        cells[row][col].setState(state);

                        if (state == 1) {

                            cells[row][col].setColor(p.color(p.random(255), p.random(255), p.random(255)));
                        } else {
                            cells[row][col].setColor(p.color(255));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar o arquivo");
            e.printStackTrace();
        }
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public PApplet getPApplet() {
        return p;
    }

    private void createCells() {
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                cells[i][j] = new Cell(this, i, j);
            }
        }
        setMooreNeighbors();
    }

    private void setMooreNeighbors() {
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell[] neigh = new Cell[8];
                int n = 0;
                for (int ii = -1; ii <= 1; ii++) {
                    for (int jj = -1; jj <= 1; jj++) {
                        if (ii == 0 && jj == 0) continue;
                        int row = (i + ii + nrows) % nrows;
                        int col = (j + jj + ncols) % ncols;
                        neigh[n++] = cells[row][col];
                    }
                }
                cells[i][j].setNeighbors(neigh);
            }
        }
    }

    public void update() {
        int[][] newStates = new int[nrows][ncols];
        int[][] newColors = new int[nrows][ncols];

        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell cell = cells[i][j];
                int aliveNeighbors = cell.countAliveNeighbors();

                if (cell.getState() == 1) {
                    if (aliveNeighbors < 2 || aliveNeighbors > 3) {
                        newStates[i][j] = 0;
                        newColors[i][j] = p.color(255);
                    } else {
                        newStates[i][j] = 1;
                        newColors[i][j] = cell.getColor();
                    }
                } else {
                    if (aliveNeighbors == 3 || aliveNeighbors == 6) {
                        newStates[i][j] = 1;
                        newColors[i][j] = cell.mostFrequentNeighborColor();


                        int note = getPentatonicNoteFromRow(i);
                        int velocity = (int) p.random(50, 127);
                        playNote(note, velocity);
                    } else {
                        newStates[i][j] = 0;
                        newColors[i][j] = p.color(255);
                    }
                }
            }
        }


        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                cells[i][j].setState(newStates[i][j]);
                cells[i][j].setColor(newColors[i][j]);
            }
        }
    }

    public void update2() {
        int[][] newStates = new int[nrows][ncols];
        int[][] newColors = new int[nrows][ncols];

        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell cell = cells[i][j];
                int vote0Count = 0;
                int vote1Count = 0;


                for (Cell neighbor : cell.getNeighbors()) {
                    if (neighbor.getState() == 0) {
                        vote0Count++;
                    } else if (neighbor.getState() == 1) {
                        vote1Count++;
                    }
                }


                if (vote0Count > vote1Count) {
                    newStates[i][j] = 0;
                    newColors[i][j] = p.color(255);
                }

                else if (vote1Count > vote0Count) {
                    newStates[i][j] = 1;
                    newColors[i][j] = cell.mostFrequentNeighborColor();


                    int note = getPentatonicNoteFromRow(i);
                    int velocity = (int) p.random(50, 127);
                    playNote(note, velocity);
                }
                else {
                    newStates[i][j] = cell.getState();
                    newColors[i][j] = cell.getColor();
                }
            }
        }

        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                cells[i][j].setState(newStates[i][j]);
                cells[i][j].setColor(newColors[i][j]);
            }
        }
    }



    public void initRandom() {
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                int state = Math.random() < 0.5 ? 0 : 1;
                cells[i][j].setState(state);
                if (state == 1) {
                    cells[i][j].setColor(p.color(p.random(255), p.random(255), p.random(255)));
                } else {
                    cells[i][j].setColor(p.color(255));
                }
            }
        }
    }

    public void display(PApplet p) {
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                cells[i][j].display(p);
            }
        }
    }
}
