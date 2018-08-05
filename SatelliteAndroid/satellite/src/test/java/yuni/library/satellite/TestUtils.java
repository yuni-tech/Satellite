package yuni.library.satellite;

public class TestUtils {

    public static void setup() {
        Satellite.shared.setLogger(new Satellite.Logger() {
            @Override
            public void log(int level, String tag, String message) {
                System.out.println(tag + " " + message);
            }
        });
    }
}
