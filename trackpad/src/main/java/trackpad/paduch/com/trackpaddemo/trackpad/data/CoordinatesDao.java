package trackpad.paduch.com.trackpaddemo.trackpad.data;

/**
 * Interface for data access object for coordinates storage
 */
public interface CoordinatesDao {
    float getX();

    float getY();

    void saveX(float x);

    void saveY(float y);
}
