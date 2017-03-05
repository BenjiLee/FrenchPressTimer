package benji.app.frenchpresstimer;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import benji.app.frenchpresstimer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Boolean timerReady = true;
    CharSequence step2StartTime;
    CharSequence step3StartTime;
    Ringtone currentRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        step2StartTime = binding.step2Timer.getText();
        step3StartTime = binding.step3Timer.getText();


        binding.step2.setText(getResources().getString(R.string.step2, binding.step2Timer.getText()));
        binding.step3.setText(getResources().getString(R.string.step3, binding.step3Timer.getText()));
        binding.startButton.setOnClickListener(startButtonOnClickListener());
    }

    public View.OnClickListener startButtonOnClickListener() {
        final CountDownTimer step2Timer = setupStep2CountDownTimer();
        final CountDownTimer step3Timer = setupStep3CountDownTimer();

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerReady) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timerReady = false;
                    binding.startButton.setText(R.string.reset_timer);
                    step2Timer.start();
                    step3Timer.start();
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    step2Timer.cancel();
                    step3Timer.cancel();
                    timerReady = true;
                    binding.step2Timer.setText(step2StartTime);
                    binding.step3Timer.setText(step3StartTime);
                    binding.startButton.setText(R.string.start_timer);
                }
            }
        };
    }

    private CountDownTimer setupStep2CountDownTimer() {
        final int milliseconds = toMilliseconds(binding.step2Timer.getText());
        return new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                String formattedTime = formatTime(millisUntilFinished + 1);
                binding.step2Timer.setText(formattedTime);
            }

            public void onFinish() {
                binding.step2Timer.setText(formatTime(0));
                timeUpAlert(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
        };
    }

    private CountDownTimer setupStep3CountDownTimer() {
        final int milliseconds = toMilliseconds(binding.step3Timer.getText());
        return new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                String formattedTime = formatTime(millisUntilFinished + 1);
                binding.step3Timer.setText(formattedTime);
            }

            public void onFinish() {
                binding.step3Timer.setText(formatTime(0));
                timeUpAlert(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                showDismissAlarmDialog();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearAll();
    }

    private void clearAll() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (currentRingtone != null && currentRingtone.isPlaying()) {
            currentRingtone.stop();
        }
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

    private void timeUpAlert(Uri sound) {
        currentRingtone = RingtoneManager.getRingtone(getApplicationContext(), sound);
        currentRingtone.play();
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private String formatTime(long millis) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );

    }

    private static int toMilliseconds(CharSequence cs) {
        String[] minutesSeconds = cs.toString().split(":");
        int minutes = Integer.parseInt(minutesSeconds[0]);
        int seconds = Integer.parseInt(minutesSeconds[1]);
        int minutesInSeconds = minutes * 60;
        return (minutesInSeconds + seconds) * 1000;
    }
}
