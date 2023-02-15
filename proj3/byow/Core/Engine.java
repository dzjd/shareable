package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.awt.*;
import java.util.*;


public class Engine {
    public TERenderer ter;

    public static final int TILE_SIZE = 16;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 35;
    public static final int WINDOW_WIDTH = WIDTH + 4;
    public static final int WINDOW_HEIGHT = HEIGHT + 6;

    public TETile[][] world;
    public TETile[][] spotlightWorld;
    public ArrayList<Room> rooms;
    public static final int ROOM_MIN_SIZE = 5;
    public static final int ROOM_MAX_SIZE = 15;
    public static final int MAX_ROOMS = 100;
    public static final int MAX_TREES = 50;
    public Random rand;
    public int[] avatarCoord;

    public static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 40);
    public static final Font NORMAL_FONT = new Font("Georgia", Font.BOLD, 18);
    public Long seed;
    public boolean gameStarted = false;
    public boolean gameOver = false;
    public boolean replayMode = false;
    public int replayPause = 500;
    public boolean spotlightMode = false;
    public int treesDefeated = 0;
    public int numberOfTrees = 0;
    public boolean quitWatch = false;

    private static final java.util.List<Character> directionKeys = Arrays.asList(new Character[]{'W', 'A', 'S', 'D'});
    private static final java.util.List<Character> validMenuKeys = Arrays.asList(new Character[]{'N', 'Q', 'L', 'P', 'R'});
    private static final String FILE_NAME = "saveFile.txt";
    private String userInput = "";
    private boolean language = true;

    public Engine() {
        initializeBoard();
        ter = new TERenderer();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WINDOW_WIDTH, WINDOW_HEIGHT);
        String seed = "";
        InputSource inputSource = new KeyboardInputSource();
        startUpScreen();
        gameLoop:
        while (inputSource.possibleNextInput()) {
            mainMenuLoop:
            while (!gameStarted) {
                char nextKey = inputSource.getNextKey();
                if (!validMenuKeys.contains(nextKey)) {
                    invalidKeyScreen();
                    while (inputSource.possibleNextInput()) {
                        char response = inputSource.getNextKey();
                        if (response == 'B') {
                            startUpScreen();
                            continue mainMenuLoop;
                        }
                    }
                }
                switch (nextKey) {
                    case 'N':
                        userInput += nextKey;
                        promptSeedScreen(seed);
                        nextKey = inputSource.getNextKey();
                        while (nextKey != 'S') {
                            seed += nextKey;
                            userInput += nextKey;
                            promptSeedScreen(seed);
                            nextKey = inputSource.getNextKey();
                        }
                        userInput += nextKey;
                        this.seed = Long.parseLong(seed);
                        this.rand = new Random(Long.parseLong(seed));
                        startNewInteractiveGame();
                        showHUD();
                        continue gameLoop;
                    case 'Q':
                        System.exit(0);
                        break;
                    case 'P':
                        language = !language;
                        startUpScreen();
                        continue gameLoop;
                    case 'L':
                        try {
                            loadPrevInteractiveGame();
                            continue gameLoop;
                            } catch (IllegalArgumentException e) {
                                System.exit(0);
                            }
                            case 'R':
                                replayMenu();
                                while (inputSource.possibleNextInput()) {
                                    char speed = inputSource.getNextKey();
                                    if (speed == '1' || speed == '2' || speed == '3') {
                                        switch (speed) {
                                            case '1':
                                                replayPause = 450;
                                                break;
                                            case '2':
                                                replayPause = 100;
                                                break;
                                            case '3':
                                                replayPause = 10;
                                                break;
                                        }
                                        break;
                                    }
                                }
                                replaySaved();
                                replayOverHUD();
                                replayMode = false;
                                gameStarted = true;
                                continue gameLoop;
                        }

                }
                while (!StdDraw.hasNextKeyTyped() && !gameOver) {
                    showMouseTile();
                    showHUD();
                }
                handleKey(inputSource.getNextKey(), true);
            }
        }


    public void replayMenu() {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 0, 153));
        StdDraw.filledRectangle((double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2, (double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(NORMAL_FONT);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 4, "Choose your replay speed:");
            StdDraw.text(WINDOW_WIDTH / 2, 20, "Enter (1) for slow replay");
            StdDraw.text(WINDOW_WIDTH / 2, 18, "Enter (2) for regular replay");
            StdDraw.text(WINDOW_WIDTH / 2, 16, "Enter (3) for RAPID replay");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 4, "请选择您的回放速度：");
            StdDraw.text(WINDOW_WIDTH / 2, 20, "慢回放：请按（1)");
            StdDraw.text(WINDOW_WIDTH / 2, 18, "正常回放：请按（2)");
            StdDraw.text(WINDOW_WIDTH / 2, 16, "极快回放：请按（3)");
        }
        StdDraw.show();
    }

    public void replayOverHUD () {
        StdDraw.clear();
        drawGameScreen();
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1, WINDOW_WIDTH / 2, 1.5);
        StdDraw.setPenColor(Color.white);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1.5, "Replay over! Continue playing!");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1.5, "回放结束！游戏继续!");
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        StdDraw.pause(1000);
    }

    public void replaySaved() {
        replayMode = true;
        String prevGameState = loadGame();
        userInput = prevGameState;
        InputSource inputDevice = new StringInputDevice(userInput);
        String seed = "";
        char nextKey = inputDevice.getNextKey();
        while (nextKey != 'S') {
            if (nextKey == 'N') {
                nextKey = inputDevice.getNextKey();
                continue;
            }
            seed += nextKey;
            nextKey = inputDevice.getNextKey();
        }
        this.seed = Long.valueOf(seed);
        this.rand = new Random(this.seed);
        generateWorld();
        placeAvatar();
        placeTrees();
        while (inputDevice.possibleNextInput()) {
            nextKey = inputDevice.getNextKey();
            handleKey(nextKey, true);
        }
    }


    public void showSpotlight() {
        int avatarXLow = avatarCoord[0] - 2;
        int avatarXHigh = avatarCoord[0] + 2;
        int avatarYLow = avatarCoord[1] - 2;
        int avatarYHigh = avatarCoord[1] + 2;
        for (int x = avatarXLow, i = avatarXLow; x < avatarXHigh + 1; x++, i++) {
            for (int y = avatarYLow, j = avatarYLow; y < avatarYHigh + 1; y++, j++) {
                spotlightWorld[i][j] = world[x][y];
            }
        }
        changedToBlack();
    }

    public void changedToBlack() {
        for (int x = 2; x < WINDOW_WIDTH - 2; x++) {
            for (int y = 3; y < WINDOW_HEIGHT - 3; y++) {
                if (surroundedByWalls(x, y)) {
                    changeTileToSpotLightWorld(x, y, Tileset.NOTHING);
                }
            }
        }
        for (int x = avatarCoord[0] - 2; x > 0; x--) {
            for (int y = 0; y < WINDOW_HEIGHT; y++) {
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);
            }
        }
        for (int x = avatarCoord[0] + 2; x < WINDOW_WIDTH; x++) {
            for (int y = 0; y < WINDOW_HEIGHT; y++) {
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);
            }
        }
        for (int y = avatarCoord[1] + 2; y < WINDOW_HEIGHT; y++) {
            for (int x = 0; x < WINDOW_WIDTH; x++) {
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);
            }
        }
        for (int y = avatarCoord[1] - 2; y > 0; y--) {
            for (int x = 0; x < WINDOW_WIDTH; x++) {
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);
            }
        }
    }

    public void saveGame(String string) {
        Out saveTo = new Out(FILE_NAME);
        saveTo.println(string);
        saveTo.close();
    }

    public String loadGame() {
        In loadFrom = new In(FILE_NAME);
        String s = loadFrom.readLine();
        return s;
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        rooms.clear();
        userInput = "";
        StringInputDevice inputDevice = new StringInputDevice(input);
        char firstKey = inputDevice.getNextKey();
        if (firstKey == 'N') {
            userInput += firstKey;
            char nextKey = inputDevice.getNextKey();
            String seed = "";
            while (nextKey != 'S') {
                seed += nextKey;
                userInput += nextKey;
                nextKey = inputDevice.getNextKey();
            }
            userInput += nextKey;
            this.seed = Long.parseLong(seed);
            this.rand = new Random(this.seed);
            generateWorld();
            placeAvatar();
            placeTrees();
            while (inputDevice.possibleNextInput()) {
                nextKey = inputDevice.getNextKey();
                if (nextKey != ':') {
                    handleKey(nextKey, false);
                }
                if (nextKey == ':') {
                    if (inputDevice.getNextKey() == 'Q') {
                        saveGame(userInput);
                    }
                }
            }
        } else if (firstKey == 'L') {
            String prevInputs = loadGame();
            while (inputDevice.possibleNextInput()) {
                char nextKey = inputDevice.getNextKey();
                if (nextKey != ':') {
                    prevInputs += nextKey;
                } else if (nextKey == ':') {
                    saveGame(prevInputs);
                }
            }
            interactWithInputString(prevInputs);
        }
        return world;
    }

    public void initializeBoard() {
        rooms = new ArrayList<>();
        world = new TETile[WINDOW_WIDTH][WINDOW_HEIGHT];
        spotlightWorld = new TETile[WINDOW_WIDTH][WINDOW_HEIGHT];
        for (int x = 0; x < WINDOW_WIDTH; x += 1) {
            for (int y = 0; y < WINDOW_HEIGHT; y += 1) {
                changeTileTo(x, y, Tileset.CUSTOM_WALL);
            }
        }
    }

    public void create_h_hall(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            changeTileTo(x, y, Tileset.CUSTOM_FLOOR);
        }
    }

    public void create_v_hall(int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            changeTileTo(x, y, Tileset.CUSTOM_FLOOR);
        }
    }


    public void makeRoom(int x, int y, int w, int h) {
        for (int x1 = x + 1; x1 < x + w; x1 += 1) {
            for (int y1 = y + 1; y1 < y + h; y1++) {
                changeTileTo(x1, y1, Tileset.CUSTOM_FLOOR);
            }
        }
    }

    public void generateWorld() {
        outerloop:
        for (int i = 0; i < MAX_ROOMS; i++) {
            int width = RandomUtils.uniform(rand, ROOM_MIN_SIZE, ROOM_MAX_SIZE);
            int height = RandomUtils.uniform(rand, ROOM_MIN_SIZE, ROOM_MAX_SIZE);
            int x = RandomUtils.uniform(rand, 2, WIDTH - width - 1);
            int y = RandomUtils.uniform(rand, 3, HEIGHT - height - 1);
            Room newRoom = new Room(x, y, width, height);

            for (Room room : rooms) {
                if (newRoom.isOverlapping(room)) {
                    continue outerloop;
                }
            }
            makeRoom(x, y, width, height);
            if (rooms.size() > 0) {
                int[] newRoomCenter = newRoom.findCenter();
                int new_x = newRoomCenter[0];
                int new_y = newRoomCenter[1];
                int[] lastRoomCenter = rooms.get(rooms.size() - 1).findCenter();
                int prev_x = lastRoomCenter[0];
                int prev_y = lastRoomCenter[1];
                if (RandomUtils.bernoulli(rand)) {
                    create_h_hall(prev_x, new_x, prev_y);
                    create_v_hall(prev_y, new_y, new_x);
                } else {
                    create_v_hall(prev_y, new_y, prev_x);
                    create_h_hall(prev_x, new_x, new_y);
                }
            }


            rooms.add(newRoom);
        }
        cleanWorld();
    }

    public void placeTrees() {
        for (int i = 0; i < MAX_TREES; i++) {
            int x = RandomUtils.uniform(rand, 2, WIDTH);
            int y = RandomUtils.uniform(rand, 3, HEIGHT);
            TETile currTile = world[x][y];
            if (currTile.equals(Tileset.CUSTOM_FLOOR)) {
                world[x][y] = Tileset.TREE;
                numberOfTrees++;
            }
        }
    }

    public boolean surroundedByWalls(int x, int y) {
        return (world[x + 1][y] == Tileset.CUSTOM_WALL || world[x + 1][y] == Tileset.GRASS) &&
                (world[x + 1][y + 1] == Tileset.CUSTOM_WALL || world[x + 1][y + 1] == Tileset.GRASS) &&
                (world[x + 1][y - 1] == Tileset.CUSTOM_WALL || world[x + 1][y - 1] == Tileset.GRASS) &&
                (world[x][y + 1] == Tileset.CUSTOM_WALL || world[x][y + 1] == Tileset.GRASS) &&
                (world[x][y - 1] == Tileset.CUSTOM_WALL || world[x][y - 1] == Tileset.GRASS) &&
                (world[x - 1][y] == Tileset.CUSTOM_WALL || world[x - 1][y] == Tileset.GRASS) &&
                (world[x - 1][y + 1] == Tileset.CUSTOM_WALL || world[x - 1][y + 1] == Tileset.GRASS) &&
                (world[x - 1][y - 1] == Tileset.CUSTOM_WALL || world[x - 1][y - 1] == Tileset.GRASS);
    }

    public void cleanWorld() {
        for (int x = 2; x < WINDOW_WIDTH - 2; x++) {
            for (int y = 3; y < WINDOW_HEIGHT - 3; y++) {
                if (surroundedByWalls(x, y)) {

                    changeTileTo(x, y, Tileset.NOTHING);
                    changeTileToSpotLightWorld(x, y, Tileset.NOTHING);

                    changeTileTo(x, y, Tileset.GRASS);
                    ;

                }
            }
        }


        for (int x = 0; x < WINDOW_WIDTH; x++) {
            for (int y = 0; y < 3; y++) {

                changeTileTo(x, y, Tileset.NOTHING);
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);

                changeTileTo(x, y, Tileset.GRASS);

            }
        }
        for (int x = 0; x < WINDOW_WIDTH; x++) {
            for (int y = HEIGHT + 1; y < HEIGHT + 6; y++) {

                changeTileTo(x, y, Tileset.NOTHING);
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);

                changeTileTo(x, y, Tileset.GRASS);

            }
        }
        for (int y = 0; y < WINDOW_HEIGHT; y++) {
            for (int x = 0; x < 2; x++) {

                changeTileTo(x, y, Tileset.NOTHING);
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);

                changeTileTo(x, y, Tileset.GRASS);

            }
        }
        for (int y = 0; y < WINDOW_HEIGHT; y++) {
            for (int x = WIDTH + 1; x < WINDOW_WIDTH; x++) {

                changeTileTo(x, y, Tileset.NOTHING);
                changeTileToSpotLightWorld(x, y, Tileset.NOTHING);

                changeTileTo(x, y, Tileset.GRASS);

            }
        }
    }

    public TETile tileAbove(int x, int y) {
        return world[x][y + 1];
    }

    public TETile tileBelow(int x, int y) {
        return world[x][y - 1];
    }

    public TETile tileLeft(int x, int y) {
        return world[x - 1][y];
    }

    public TETile tileRight(int x, int y) {
        return world[x + 1][y];
    }

    public void moveAvatar(Character direction, boolean render) {
        switch (direction) {
            case 'W':
                TETile tileUp = tileAbove(avatarX(), avatarY());
                if (tileUp.equals(Tileset.CUSTOM_FLOOR) || tileUp.equals(Tileset.TREE)) {
                    changeTileTo(avatarX(), avatarY() + 1, Tileset.AVATAR);
                    changeTileTo(avatarX(), avatarY(), Tileset.CUSTOM_FLOOR);
                    avatarCoord[1]++;


                    if (tileUp.equals(Tileset.TREE)) {
                        treesDefeated++;
                    }

                }

                break;
            case 'S':
                TETile tileDown = tileBelow(avatarX(), avatarY());
                if (tileDown.equals(Tileset.CUSTOM_FLOOR) || tileDown.equals(Tileset.TREE)) {
                    changeTileTo(avatarX(), avatarY() - 1, Tileset.AVATAR);
                    changeTileTo(avatarX(), avatarY(), Tileset.CUSTOM_FLOOR);
                    avatarCoord[1]--;


                    if (tileDown.equals(Tileset.TREE)) {
                        treesDefeated++;
                    }

                }

                break;
            case 'A':
                TETile tileLeft = tileLeft(avatarX(), avatarY());
                if (tileLeft.equals(Tileset.CUSTOM_FLOOR) || tileLeft.equals(Tileset.TREE)) {
                    changeTileTo(avatarX() - 1, avatarY(), Tileset.AVATAR);
                    changeTileTo(avatarX(), avatarY(), Tileset.CUSTOM_FLOOR);
                    avatarCoord[0]--;
                    if (tileLeft.equals(Tileset.TREE)) {
                        treesDefeated++;
                    }
                }

                break;
            case 'D':
                TETile tileRight = tileRight(avatarX(), avatarY());
                if (tileRight.equals(Tileset.CUSTOM_FLOOR) || tileRight.equals(Tileset.TREE)) {
                    changeTileTo(avatarX() + 1, avatarY(), Tileset.AVATAR);
                    changeTileTo(avatarX(), avatarY(), Tileset.CUSTOM_FLOOR);
                    avatarCoord[0]++;
                    if (tileRight.equals(Tileset.TREE)) {
                        treesDefeated++;
                    }
                }

                break;
        }

        if (treesDefeated == numberOfTrees) {
            gameOver = true;
            drawWinningScreen();
            InputSource inputSource = new KeyboardInputSource();
            while (inputSource.possibleNextInput()) {
                if (inputSource.getNextKey() == 'Q') {
                    gameOver = true;
                    System.exit(0);
                }
            }
        }
        if (render) {
            drawGameScreen();
        }
    }


    public void changeTileTo(int x, int y, TETile changeTo) {
        world[x][y] = changeTo;
    }

    public void changeTileToSpotLightWorld(int x, int y, TETile changeTo) {
        spotlightWorld[x][y] = changeTo;
    }

    public int avatarX() {
        return avatarCoord[0];
    }

    public int avatarY() {
        return avatarCoord[1];
    }

    public void handleKey(char key, boolean render) {
        Character c = Character.toUpperCase(key);
        if (directionKeys.contains(c)) {
            if (!replayMode) {
                userInput += c;
                quitWatch = false;
            }
            moveAvatar(c, render);
        }
        if (c == ':') {
            quitWatch = true;
        }
        if (c == 'Q' && quitWatch) {
            saveGame(userInput);
            System.exit(0);
        }
        if (c == 'H') {
            showHelpMenu();
            InputSource keyboard = new KeyboardInputSource();
            while (keyboard.possibleNextInput()) {
                char nextKey = keyboard.getNextKey();
                if (nextKey == 'B') {
                    drawGameScreen();
                    break;
                }
                if (nextKey == 'P') {
                    language = !language;
                    showHelpMenu();
                }
            }
        }
        if (c == 'P') {
            language = !language;
        }
        if (c == 'K') {
            spotlightMode = !spotlightMode;
            drawGameScreen();
        }
    }


    private void showMouseTile() {
        double nonFloorX = StdDraw.mouseX();
        double nonFloorY = StdDraw.mouseY();
        int x = (int) Math.floor(nonFloorX);
        int y = (int) Math.floor(nonFloorY);
        String mouseStr = null;

        if (x < WINDOW_WIDTH && x >= 0 && y < WINDOW_HEIGHT && y >= 0) {
            TETile currTile = world[x][y];
            if (!currTile.equals(Tileset.AVATAR) && !currTile.equals(Tileset.GRASS)) {
                if (language) {
                    mouseStr = "This is a " + currTile.description();
                } else {
                    mouseStr = "这是" + translateTileToChinese(currTile.description());
                }
            } else if (currTile.equals(Tileset.AVATAR)) {
                if (language) {
                    mouseStr = "This is you :D";
                } else {
                    mouseStr = "这是你!";
                }
            } else if (currTile.equals(Tileset.GRASS)) {
                if (nonFloorY > WINDOW_HEIGHT - 2.5 && y < WINDOW_HEIGHT) {
                    if (language) {
                        mouseStr = "This is the HUD";
                    } else {
                        mouseStr = "这是平视显示器";
                    }
                } else {
                    if (language) {
                        mouseStr = "This is just grass...";
                    } else {
                        mouseStr = "这是草...";
                    }
                }
            }
        } else {
            if (language) {
                mouseStr = "Come back you're looking too far!";
            } else {
                mouseStr = "你看太远了";
            }
        }
        StdDraw.text(10, WINDOW_HEIGHT - 1.5, mouseStr);
    }

    public void showTime() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        StdDraw.text(WINDOW_WIDTH - 8, WINDOW_HEIGHT - 1.5, formattedDate);
    }


    public void showHUD() {
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1, WINDOW_WIDTH / 2, 1.5);
        StdDraw.setPenColor(Color.white);
        showTime();
        showMouseTile();
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1.5, "Enter (H) to open help menu");
            StdDraw.setPenColor(Color.GREEN);
            StdDraw.text(60, WINDOW_HEIGHT - 1.5, "Trees left: " + (numberOfTrees - treesDefeated));
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1.5, "按 (H) 打开说明页面");
            StdDraw.setPenColor(Color.GREEN);
            StdDraw.text(60, WINDOW_HEIGHT - 1.5, "还剩 " + (numberOfTrees - treesDefeated) + " 棵树");
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
        StdDraw.pause(20);
    }


    public void placeAvatar() {
        Room randomStartingRoom = rooms.get(RandomUtils.uniform(rand, 0, rooms.size()));
        int[] randomStartingPoint = randomStartingRoom.findCenter();
        avatarCoord = randomStartingPoint;
        world[randomStartingPoint[0]][randomStartingPoint[1]] = Tileset.AVATAR;
    }

    public void startNewInteractiveGame() {
        generateWorld();
        placeAvatar();
        placeTrees();
        gameStarted = true;
        drawGameScreen();
    }

    public void loadPrevInteractiveGame() {
        String prevGameState = loadGame();
        userInput = prevGameState;
        gameStarted = true;
        interactWithInputString(userInput);
        drawGameScreen();
    }

    public void drawGameScreen() {
        if (!replayMode) {
            showHUD();
            if (spotlightMode) {
                showSpotlight();
                ter.renderFrame(spotlightWorld);
            } else {
                ter.renderFrame(world);
            }
        } else {
            ter.renderFrame(world);
            currentlyReplayingHUD();
            StdDraw.pause(replayPause);
        }
    }

    public void currentlyReplayingHUD() {
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1, WINDOW_WIDTH / 2, 1.5);
        StdDraw.setPenColor(Color.white);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1.5, "Replaying...");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 1.5, "回放中...");
        }
        StdDraw.show();
    }

    public void invalidKeyScreen() {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 0, 153));
        StdDraw.filledRectangle((double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2, (double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(NORMAL_FONT);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "INVALID KEY PRESS");
            StdDraw.text(WINDOW_WIDTH / 2, 20, "PLEASE ENTER EITHER (N), (L), OR (Q)");
            StdDraw.text(WINDOW_WIDTH / 2, 6, "PRESS (B) TO RETURN TO MAIN MENU");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "不支持您的输入");
            StdDraw.text(WINDOW_WIDTH / 2, 20, "请输入 (N), (L), 或 (Q)");
            StdDraw.text(WINDOW_WIDTH / 2, 6, "按 (B) 返回主页");
        }
        StdDraw.show();
    }

    public void startUpScreen() {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 0, 153));
        StdDraw.filledRectangle((double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2, (double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2);
        StdDraw.picture(WINDOW_WIDTH / 2 - 20, WINDOW_HEIGHT / 2 + 2, "oski.png");
        StdDraw.picture(WINDOW_WIDTH / 2 + 20, WINDOW_HEIGHT / 2 + 2, "stanfordtree.png");
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(TITLE_FONT);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 2, "Defeat the Tree");
            StdDraw.setFont(NORMAL_FONT);
            StdDraw.text(WINDOW_WIDTH / 2, 14, "New Game: (N)");
            StdDraw.text(WINDOW_WIDTH / 2, 12, "Load Game: (L)");
            StdDraw.text(WINDOW_WIDTH / 2, 10, "Replay last save: (R)");
            StdDraw.text(WINDOW_WIDTH / 2, 8, "Switch to Chinese: (P)");
            StdDraw.text(WINDOW_WIDTH / 2, 6, "Quit: (Q)");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 2, "打败斯坦福");
            StdDraw.setFont(NORMAL_FONT);
            StdDraw.text(WINDOW_WIDTH / 2, 14, "新游戏: (N)");
            StdDraw.text(WINDOW_WIDTH / 2, 12, "恢复存档: (L)");
            StdDraw.text(WINDOW_WIDTH / 2, 10, "回放存档: (R)");
            StdDraw.text(WINDOW_WIDTH / 2, 8, "切换到英文 (P)");
            StdDraw.text(WINDOW_WIDTH / 2, 6, "退出: (Q)");
        }
        StdDraw.show();
    }


    public void promptSeedScreen(String seed) {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 0, 153));
        StdDraw.filledRectangle((double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2, (double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(NORMAL_FONT);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "Enter Random Number:");
            StdDraw.text(WINDOW_WIDTH / 2, 6, "Press 'S' to Create World");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "随机输入数字:");
            StdDraw.text(WINDOW_WIDTH / 2, 6, "按 (S) 开始游戏");
        }
        StdDraw.text(WINDOW_WIDTH / 2, 20, seed);
        StdDraw.show();
    }


    public void drawWinningScreen() {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 0, 153));
        StdDraw.picture(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, "win.jpg");
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(TITLE_FONT);
        if (language) {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "CONGRATULATIONS YOU BEAT THE TREE!");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 1, "Press (Q) to quit");
        } else {
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "恭喜你打败了斯坦福!");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 1, "按 (Q) 键退出游戏");
        }
        StdDraw.show();
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            if (inputSource.getNextKey() == 'Q') {
                System.exit(0);
                Engine engine = new Engine();
                engine.interactWithKeyboard();

            }
        }
    }

    public String translateTileToChinese(String tileName) {
        switch (tileName) {
            case "wall":
                return "墙";
            case "floor":
                return "地板";
        }
        return tileName;
    }

    public void showHelpMenu() {
        StdDraw.clear();
        StdDraw.setPenColor(new Color(0, 0, 153));
        StdDraw.filledRectangle((double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2, (double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2);
        StdDraw.setFont(NORMAL_FONT);
        if (language) {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 5, "Defeat all the Stanford Trees (by collecting them) to win!");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "Press (K) to toggle on/off line of sight during gameplay");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 1, "Type :Q to save and quit during gameplay");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 1, "Press (P) to switch to Chinese");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 7, "Press (B) to return to gameplay");
        } else {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 5, "打败所有斯坦福树赢得游戏!");
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 3, "游戏中按 (K) 键打开或关闭视线功能");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 1, "游戏中输入 :Q 存档并退出游戏");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 1, "随时按 (P) 键切换到英语");
            StdDraw.text(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 7, "按 (B) 键返回游戏");
        }
        StdDraw.show();
    }
}
