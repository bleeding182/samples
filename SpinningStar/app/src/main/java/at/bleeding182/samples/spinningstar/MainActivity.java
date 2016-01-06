package at.bleeding182.samples.spinningstar;

import android.graphics.drawable.Animatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        PathProgressDrawable pathProgressDrawable = new PathProgressDrawable();
        pathProgressDrawable.setAnimatedPoints(10);
        progressBar.setIndeterminateDrawable(pathProgressDrawable);
        progressBar.setIndeterminate(true);


        RectProgressDrawable drawable = new RectProgressDrawable();
        drawable.setPoints(7);
        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress2);
        progressBar2.setIndeterminateDrawable(drawable);
        progressBar2.setIndeterminate(true);

        CircleProgressDrawable drawableCircle = new CircleProgressDrawable();
        drawableCircle.setPoints(6);
        drawableCircle.setAnimatedPoints(20);
        ProgressBar progressBar3 = (ProgressBar) findViewById(R.id.progress3);
        progressBar3.setIndeterminateDrawable(drawableCircle);
        progressBar3.setIndeterminate(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItem = menu.add("Spinning");
        menuItem.setIcon(new SpinningStarDrawable());
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((Animatable) menuItem.getIcon()).start();
        return true;
    }
}
