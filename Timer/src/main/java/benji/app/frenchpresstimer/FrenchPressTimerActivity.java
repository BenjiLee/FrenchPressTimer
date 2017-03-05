package benji.app.frenchpresstimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import benji.app.frenchpresstimer.databinding.ActivityMainBinding;

public class FrenchPressTimerActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Boolean timerReady = true;
    private CharSequence step2StartTime;
    private CharSequence step3StartTime;
    private Ringtone ringtoneShort;
    private Ringtone ringtoneLong;
    private CountDownTimer step2CountDownTimer;
    private CountDownTimer step3CountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        step2StartTime = binding.step2Timer.getText();
        step3StartTime = binding.step3Timer.getText();

        binding.step2.setText(getResources().getString(R.string.step2, binding.step2Timer.getText()));
        binding.step3.setText(getResources().getString(R.string.step3, binding.step3Timer.getText()));
        binding.startButton.setOnClickListener(startButtonOnClickListener());

        ringtoneShort = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        ringtoneLong = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        step2CountDownTimer = setUpCountdownTimer(binding.step2Timer, ringtoneShort);
        step3CountDownTimer = setUpCountdownTimer(binding.step3Timer, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(AlarmReceiver.WAKE)) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            timeUpAlert(ringtoneLong);
            showDismissAlarmDialog();
        }
    }

    public View.OnClickListener startButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerReady) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timerReady = false;
                    binding.startButton.setText(R.string.reset_timer);
                    step2CountDownTimer.start();
                    step3CountDownTimer.start();
                    setupAlarm(utils.toMilliseconds(binding.step3Timer.getText()));
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timerReady = true;
                    binding.startButton.setText(R.string.start_timer);
                    binding.step2Timer.setText(step2StartTime);
                    binding.step3Timer.setText(step3StartTime);
                    step2CountDownTimer.cancel();
                    step3CountDownTimer.cancel();
                    cancelAlarm();
                }
            }
        };
    }

    private CountDownTimer setUpCountdownTimer(@NonNull final Button buttonText, @Nullable final Ringtone ringtone) {
        return new CountDownTimer(utils.toMilliseconds(buttonText.getText()), 1000) {
            public void onTick(long millisUntilFinished) {
                String formattedTime = utils.formatTime(millisUntilFinished + 1);
                buttonText.setText(formattedTime);
            }
            public void onFinish() {
                buttonText.setText(utils.formatTime(0));
                if (ringtone != null) {
                    timeUpAlert(ringtone);
                }
            }
        };
    }

    private void setupAlarm(int milliseconds) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milliseconds, pendingIntent);
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void showDismissAlarmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Your coffee is ready!")
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clearAll();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        clearAll();
                    }
                })
                .show();
    }

    private void clearAll() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ringtoneLong.stop();
        ringtoneShort.stop();
    }

    private void timeUpAlert(Ringtone ringtone) {
        ringtone.play();
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
}
