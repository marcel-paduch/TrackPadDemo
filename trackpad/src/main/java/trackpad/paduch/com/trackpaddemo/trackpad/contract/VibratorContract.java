package trackpad.paduch.com.trackpaddemo.trackpad.contract;

/**
 * Contains interfaces for View and Presenter of MVP, each method is commented in the implementation
 */
public interface VibratorContract {
    interface View {
        void restoreCoordinates(float x, float y);

        void vibrate(long time, int amplitude);

        void onError(String msg);

        boolean checkVibrator();

        void updateCoordinatesText(float x, float y);
    }

    interface Presenter {
        void saveCoordinates(float x, float y);

        void onTrackPadChange(float x, float y);

        void init();
    }

}
