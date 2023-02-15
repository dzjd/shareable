package byow.InputDemo;

import edu.princeton.cs.algs4.StdDraw;

/**
 * Created by hug.
 */
public class StringInputDevice implements InputSource  {
    private String input;
    private int index;

    public StringInputDevice(String s) {
        index = 0;
        input = s;
    }

    public char getNextKey() {
        char returnChar = input.charAt(index);
        returnChar = Character.toUpperCase(returnChar);
        index += 1;
        return returnChar;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
