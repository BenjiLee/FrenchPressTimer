package benji.app.frenchpresstimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String WAKE = "Wake up";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, FrenchPressTimerActivity.class);
        i.putExtra(WAKE, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(i);
    }
}
