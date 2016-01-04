package at.bleeding182.samples.spinningstar;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author David Medenjak on 24.12.2015.
 */
public class TestImageView extends ImageView {
    public TestImageView(Context context) {
        super(context);
        init();
    }

    public TestImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TestImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setBackground(new RectProgressDrawable());
    }
}
