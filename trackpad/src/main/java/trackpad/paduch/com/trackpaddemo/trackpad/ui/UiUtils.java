package trackpad.paduch.com.trackpaddemo.trackpad.ui;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Helper class for ui utilites
 */
public final class UiUtils {
    private UiUtils() {
    }

    public static float convertDpToPixel(Context context, float dp) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}

