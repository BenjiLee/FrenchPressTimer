package benji.app.frenchpresstimer;

import java.util.concurrent.TimeUnit;


public class utils {
    static String formatTime(long millis) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    static int toMilliseconds(CharSequence cs) {
        String[] minutesSeconds = cs.toString().split(":");
        int minutes = Integer.parseInt(minutesSeconds[0]);
        int seconds = Integer.parseInt(minutesSeconds[1]);
        int minutesInSeconds = minutes * 60;
        return (minutesInSeconds + seconds) * 1000;
    }
}
