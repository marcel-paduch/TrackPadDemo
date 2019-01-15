package trackpad.paduch.com.trackpaddemo.trackpad.data;

import android.content.SharedPreferences;

/**
 * This class stores coordinates in the shared preferences, all methods are self explanatory
 */
public class CoordinatesDaoImpl implements CoordinatesDao {
    private static final String X_PREF = "x." + CoordinatesDaoImpl.class.getCanonicalName();
    private static final String Y_PREF = "y." + CoordinatesDaoImpl.class.getCanonicalName();
    private SharedPreferences preferences;

    public CoordinatesDaoImpl(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public float getX() {
        return preferences.getFloat(X_PREF, 0);
    }

    @Override
    public float getY() {
        return preferences.getFloat(Y_PREF, 0);
    }

    @Override
    public void saveX(float x) {
        preferences.edit().putFloat(X_PREF, x).apply();
    }

    @Override
    public void saveY(float y) {
        preferences.edit().putFloat(Y_PREF, y).apply();
    }
}
