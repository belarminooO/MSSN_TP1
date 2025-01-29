package GameOfLife;

import processing.core.PApplet;

public class Cell {
    private int row, col;
    private int state;
    private int color;
    private Cell[] neighbors;
    private CellularAutomata ca;

    public Cell(CellularAutomata ca, int row, int col) {
        this.ca = ca;
        this.row = row;
        this.col = col;
        this.state = 0;
        this.color = ca.getPApplet().color(255);
        this.neighbors = null;
    }

    public void setNeighbors(Cell[] neigh) {
        this.neighbors = neigh;
    }

    public Cell[] getNeighbors() {
        return neighbors;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int countAliveNeighbors() {
        int aliveCount = 0;
        for (Cell neighbor : neighbors) {
            if (neighbor != this && neighbor.getState() == 1) {
                aliveCount++;
            }
        }
        return aliveCount;
    }

    public int mostFrequentNeighborColor() {
        int[] neighborColors = new int[8];
        int[] colorCounts = new int[8];
        int colorCountIndex = 0;

        for (Cell neighbor : neighbors) {
            if (neighbor != this && neighbor.getState() == 1) {
                int neighborColor = neighbor.getColor();
                boolean found = false;

                for (int i = 0; i < colorCountIndex; i++) {
                    if (neighborColors[i] == neighborColor) {
                        colorCounts[i]++;
                        found = true;
                        break;
                    }
                }

                if (!found && colorCountIndex < 8) {
                    neighborColors[colorCountIndex] = neighborColor;
                    colorCounts[colorCountIndex] = 1;
                    colorCountIndex++;
                }
            }
        }


        int maxCount = 0;
        int mostFrequentColor = ca.getPApplet().color(255);

        for (int i = 0; i < colorCountIndex; i++) {
            if (colorCounts[i] > maxCount) {
                maxCount = colorCounts[i];
                mostFrequentColor = neighborColors[i];
            }
        }

        return mostFrequentColor;
    }

    public void display(PApplet p) {
        if (state == 1) {
            p.fill(color);
        } else {
            p.fill(255);
        }
        p.rect(col * ca.getCellWidth(), row * ca.getCellHeight(), ca.getCellWidth(), ca.getCellHeight());
    }
}
