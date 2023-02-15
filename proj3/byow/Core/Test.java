package byow.Core;
import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.*;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

import java.util.ArrayList;
import java.util.Random;

public class Test {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 35;
    public static final int WINDOW_WIDTH = WIDTH + 4;
    public static final int WINDOW_HEIGHT = HEIGHT + 6;
    public static void main(String[] args) {
        Engine eng = new Engine();
        eng.ter.initialize(WINDOW_WIDTH, WINDOW_HEIGHT);
        eng.interactWithInputString("LSS");
        eng.drawGameScreen();
    }
}
