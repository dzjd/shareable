package byow.TileEngine;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePaths {
    static String join(String first, String...others){
        return Paths.get(first,others).toString();
    }

    public static void main(String[] args) {
        System.out.println(join(".", "avatar.png"));
    }
}
