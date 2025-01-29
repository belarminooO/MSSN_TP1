package DLA;

import Pro.IProcessingApp;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class DLA implements IProcessingApp {
    private List<Walker> walkers;
    private int NUM_WALKERS = 200;
    private int NUM_STEPS_PER_FRAME = 10;
    private float speed = 1.0f;
    private float stickinessProbability = 1.0f;

    @Override
    public void setup(PApplet p) {
        walkers = new ArrayList<>();


        Walker w = new Walker(p, new PVector(p.width / 2, p.height / 2));
        walkers.add(w);


        for (int i = 0; i < NUM_WALKERS; i++) {
            w = new Walker(p);
            walkers.add(w);
        }
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(190);

        List<Walker> newWalkers = new ArrayList<>();

        for (int i = 0; i < NUM_STEPS_PER_FRAME; i++) {
            for (Walker w : walkers) {
                if (w.getState() == Walker.State.WANDER) {
                    w.wander(p, speed);
                    w.updateState(p, walkers, stickinessProbability);
                    if (w.getState() == Walker.State.STOPPED) {
                        Walker newWalker = new Walker(p);
                        newWalkers.add(newWalker);
                    }
                }
            }
            System.out.println("Stopped = " + Walker.num_stopped + " Wander = " + Walker.num_wanders);
        }

        walkers.addAll(newWalkers);

        for (Walker w : walkers) {
            w.display(p);
        }
    }

    @Override
    public void mousePressed(PApplet p) {

    }

    @Override
    public void keyPressed(PApplet p) {
        if (p.key == ' ') {
            walkers.clear();
            setup(p);
            System.out.println("Simulation restarted!");
        }

        if (p.key == '+') {
            NUM_STEPS_PER_FRAME += 1;
            System.out.println("Speed increased: " + NUM_STEPS_PER_FRAME + " fps");
        }

        if (p.key == '-') {
            NUM_STEPS_PER_FRAME = Math.max(1, NUM_STEPS_PER_FRAME - 1);
            System.out.println("Speed decreased: " + NUM_STEPS_PER_FRAME + " fps");
        }

        if (p.key == 'M' || p.key == 'm') {
            stickinessProbability = Math.min(1.0f, stickinessProbability + 0.1f);
            System.out.println("Stickiness increased: " + stickinessProbability);
        }

        if (p.key == 'L' || p.key == 'l') {
            stickinessProbability = Math.max(0.0f, stickinessProbability - 0.1f);
            System.out.println("Stickiness decreased: " + stickinessProbability);
        }
    }
}
