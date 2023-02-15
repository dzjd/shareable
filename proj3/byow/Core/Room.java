package byow.Core;

public class Room {
    private int width;
    private int height;
    private int x1;
    private int y1;

    private int x2;
    private int y2;
    private int[] center;

    public Room(int x, int y, int w, int h) {
        this.width = w;
        this.height = h;
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + w;
        this.y2 = y + h;
        center = findCenter();
    }

    public boolean isOverlapping(Room other) {
        if (y2 < other.y1
                || y1 > other.y2) {
            return false;
        }
        if (x2 < other.x1
                || x1 > other.x2) {
            return false;
        }
        return true;
    }

    public int[] findCenter() {
        int[] centerArray = new int[2];
        centerArray[0] = (x1 + x2) / 2;
        centerArray[1] = (y1 + y2) / 2;
        return centerArray;
    }


}
