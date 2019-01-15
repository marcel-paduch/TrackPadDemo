package trackpad.paduch.com.trackpaddemo.trackpad.presenter;

import trackpad.paduch.com.trackpaddemo.trackpad.contract.VibratorContract;
import trackpad.paduch.com.trackpaddemo.trackpad.data.CoordinatesDao;

/**
 * Presenter class for vibrator, acts as a glue between view and data
 */
public class VibratorPresenter implements VibratorContract.Presenter {
    public static final int MAX_AMPLITUDE = 255;
    public static final int TIME_MULTIPLIER = 100;
    public static final int MIN_TIME = 1;
    public static final int MIN_AMPLITUDE = 1;
    private CoordinatesDao coordinatesDao;
    private VibratorContract.View view;

    public VibratorPresenter(CoordinatesDao coordinatesDao, VibratorContract.View view) {
        this.coordinatesDao = coordinatesDao;
        this.view = view;
    }

    /**
     * Restores coordinates from memory and updates textviews
     */
    @Override
    public void init() {
        view.restoreCoordinates(coordinatesDao.getX(), coordinatesDao.getY());
        view.updateCoordinatesText(coordinatesDao.getX(), coordinatesDao.getY());
    }

    /**
     * Saves coordinates to dao
     *
     * @param x
     * @param y
     */
    @Override
    public void saveCoordinates(float x, float y) {
        coordinatesDao.saveX(x);
        coordinatesDao.saveY(y);
    }

    /**
     * If vibrator is present, vibrates and updates text, otherwise shows error
     *
     * @param x
     * @param y
     */
    @Override
    public void onTrackPadChange(float x, float y) {
        if (view.checkVibrator()) {
            long time = Math.max(Math.round(x * TIME_MULTIPLIER), MIN_TIME);
            int amplitude = Math.max(MIN_AMPLITUDE, Math.round(y * MAX_AMPLITUDE));
            view.updateCoordinatesText(x, y);
            view.vibrate(time, amplitude);
        } else {
            view.onError("Vibrator not supported");
        }
    }

}
