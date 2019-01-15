package trackpad.paduch.com.trackpaddemo.trackpad.view;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import trackpad.paduch.com.trackpaddemo.trackpad.R;
import trackpad.paduch.com.trackpaddemo.trackpad.contract.VibratorContract;
import trackpad.paduch.com.trackpaddemo.trackpad.data.CoordinatesDaoImpl;
import trackpad.paduch.com.trackpaddemo.trackpad.presenter.VibratorPresenter;
import trackpad.paduch.com.trackpaddemo.trackpad.ui.TrackPadView;

/**
 * Main activity which is also View of MVP
 */
public class MainActivity extends AppCompatActivity implements VibratorContract.View {
    private Vibrator vibrator;
    private VibratorContract.Presenter presenter;
    private TrackPadView trackPadView;
    private TextView xView;
    private TextView yView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackPadView = findViewById(R.id.trackPadView);
        xView = findViewById(R.id.normalized_x);
        yView = findViewById(R.id.normalized_y);
        trackPadView.setOnChangeListener(new TrackPadView.OnChangeListener() {
            @Override
            public void onValueChange(float x, float y) {
                presenter.onTrackPadChange(x, y);
            }
        });
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        presenter = new VibratorPresenter(new CoordinatesDaoImpl(getPreferences(MODE_PRIVATE)), this);
        presenter.init();
    }

    /**
     * Save coordinates in onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        presenter.saveCoordinates(trackPadView.getX(), trackPadView.getY());
    }

    /**
     * Updates textviews with new coordinates
     *
     * @param x
     * @param y
     */
    @Override
    public void updateCoordinatesText(float x, float y) {
        xView.setText(getString(R.string.display_x, x));
        yView.setText(getString(R.string.display_y, y));
    }

    /**
     * Restore previously saved coordinates from memory
     *
     * @param x
     * @param y
     */
    @Override
    public void restoreCoordinates(float x, float y) {
        trackPadView.setX(x);
        trackPadView.setY(y);
    }

    /**
     * Vibrate device motor
     *
     * @param time      in ms
     * @param amplitude strength (1-255)
     */
    @Override
    public void vibrate(long time, int amplitude) {
        vibrator.vibrate(VibrationEffect.createOneShot(time, amplitude));
    }

    /**
     * @return true if device has vibrator
     */
    @Override
    public boolean checkVibrator() {
        return vibrator.hasVibrator();
    }

    /**
     * Shows toast message
     *
     * @param msg message
     */
    @Override
    public void onError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
