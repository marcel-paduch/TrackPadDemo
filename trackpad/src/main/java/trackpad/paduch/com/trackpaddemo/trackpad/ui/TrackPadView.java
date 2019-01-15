package trackpad.paduch.com.trackpaddemo.trackpad.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This custom view draws track pad and handles touch events.
 * It should take in and return only normalized coordinates <0,1>
 */
public class TrackPadView extends View {
    public static final int MAX_NORMALIZED = 1;
    public static final float MIDDLE_POS = 0.5f;
    public static final int RECT_ROUNDNESS = 15;
    private static final int DEFAULT_BOUNDARY_COLOR = Color.BLACK;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_POINTER_COLOR = Color.BLACK;
    private static final float DEFAULT_RADIUS = 10;
    private static final float MIN_WIDTH_DP = 200;
    private static final float MIN_HEIGHT_DP = 200;
    private static final int DEFAULT_BOUNDARY_STROKE_WIDTH = 8;
    private static final int DEFAULT_HELPER_LINES_WIDTH = 1;
    private static final float MIN_NORMALIZED = 0;
    private Paint boundaryPaint = new Paint();
    private Paint pointerPaint = new Paint();
    private Paint linesPaint = new Paint();
    private RectF boundaryBox = new RectF();
    private Pointer pointer = new Pointer();
    private OnChangeListener onChangeListener;

    public TrackPadView(Context context) {
        super(context);
        init();
    }

    public TrackPadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * init all default values
     */
    private void init() {
        boundaryPaint.setColor(DEFAULT_BOUNDARY_COLOR);
        boundaryPaint.setStyle(Paint.Style.STROKE);
        boundaryPaint.setStrokeWidth(DEFAULT_BOUNDARY_STROKE_WIDTH);
        boundaryPaint.setAntiAlias(true);
        pointerPaint.setColor(DEFAULT_POINTER_COLOR);
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setAntiAlias(true);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setStrokeWidth(DEFAULT_HELPER_LINES_WIDTH);
        linesPaint.setColor(DEFAULT_BOUNDARY_COLOR);
        pointer.setRadius(DEFAULT_RADIUS);
        pointer.setX(MIDDLE_POS);
        pointer.setY(MIDDLE_POS);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
        int measuredWidth = measureDimension(desiredWidth, widthMeasureSpec);
        int measuredHeight = measureDimension(desiredHeight, heightMeasureSpec);

        measuredWidth = Math.max(measuredWidth, (int) UiUtils.convertDpToPixel(getContext(), MIN_WIDTH_DP));
        measuredHeight = Math.max(measuredHeight, (int) UiUtils.convertDpToPixel(getContext(), MIN_HEIGHT_DP));

        setMeasuredDimension(measuredWidth, measuredHeight);
        setInitialPosition(measuredWidth, measuredHeight);
    }

    /**
     * Set boundary size right after view size is calculated
     *
     * @param w
     * @param h
     */
    private void setInitialPosition(int w, int h) {
        boundaryBox.set(getPaddingLeft(), getPaddingTop(), w, h);
    }

    /**
     * Draw background, helper lines, pointer and boundary rectangle
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(DEFAULT_BACKGROUND_COLOR);
        drawHelperLines(canvas);
        canvas.drawCircle(denormalizeX(pointer.getX()), denormalizeY(pointer.getY()), pointer.getRadius(), pointerPaint);
        canvas.drawRoundRect(boundaryBox, RECT_ROUNDNESS, RECT_ROUNDNESS, boundaryPaint);
    }

    /**
     * Helper method for drawing helper lines
     *
     * @param canvas
     */
    private void drawHelperLines(Canvas canvas) {
        float x = denormalizeX(pointer.getX());
        float y = denormalizeY(pointer.getY());
        canvas.drawLine(x, boundaryBox.top, x, boundaryBox.bottom, linesPaint);
        canvas.drawLine(boundaryBox.left, y, boundaryBox.right, y, linesPaint);
    }

    @Override
    public float getX() {
        return pointer.getX();
    }

    public void setX(float x) {
        pointer.setX(x);
        invalidate();
    }

    /**
     * Return inverted Y, so 1,1 is in the upper right corner instead of the lower right
     *
     * @return
     */
    @Override
    public float getY() {
        return MAX_NORMALIZED - pointer.getY();
    }

    /**
     * Sets inverted y coordinate so we have 1,1 in upper right corner
     *
     * @param y
     */
    public void setY(float y) {
        pointer.setY(MAX_NORMALIZED - y);
        invalidate();
    }

    /**
     * Transform value <0,1> to cooresponding pixel based on view size
     *
     * @param x
     * @return
     */
    private float denormalizeX(float x) {
        return x * (boundaryBox.right - boundaryBox.left) + boundaryBox.left;
    }

    private float denormalizeY(float y) {
        return y * (boundaryBox.bottom - boundaryBox.top) + boundaryBox.top;
    }

    /**
     * Transform pixel position to <0,1>
     *
     * @param x
     * @return
     */
    private float normalizeX(float x) {
        return (x - boundaryBox.left) / (boundaryBox.right - boundaryBox.left);
    }

    private float normalizeY(float y) {
        return (y - boundaryBox.top) / (boundaryBox.bottom - boundaryBox.top);
    }

    /**
     * On touch or finger move checks if action was in the boundaries of track pad and moves the pointer
     * and calls callback function(if any)
     *
     * @param event touch event
     * @return was consumed
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();

        float x = normalizeX(event.getX());
        float y = normalizeY(event.getY());
        //this is checked before so it doesnt have to be reapeted for every eventAction later

        switch (eventAction) {
            //intentional fall through
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (x > MIN_NORMALIZED && x < MAX_NORMALIZED && y > MIN_NORMALIZED && y < MAX_NORMALIZED) {
                    pointer.setX(x);
                    pointer.setY(y);
                    if (onChangeListener != null) {
                        onChangeListener.onValueChange(getX(), getY());
                    }
                    invalidate();
                    return true;
                }
        }
        return false;
    }

    /**
     * Callback interface for trackpad
     */
    public interface OnChangeListener {
        void onValueChange(float x, float y);
    }

    /**
     * Helper class for finger pointer
     */
    private static class Pointer {
        private float x;
        private float y;
        private float radius;

        private Pointer() {
        }

        private float getX() {
            return x;
        }

        private void setX(float x) {
            this.x = x;
        }

        private float getY() {
            return y;
        }

        private void setY(float y) {
            this.y = y;
        }

        private float getRadius() {
            return radius;
        }

        private void setRadius(float radius) {
            this.radius = radius;
        }
    }
}
