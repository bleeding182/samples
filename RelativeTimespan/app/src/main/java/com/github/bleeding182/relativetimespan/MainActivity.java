package com.github.bleeding182.relativetimespan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by David on 22.02.2016.
 */
public class MainActivity extends AppCompatActivity {

    private TimeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mAdapter = new TimeAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final long min;
        switch (item.getItemId()) {
            case R.id.min_0:
                min = 0l;
                break;
            case R.id.min_min:
                min = DateUtils.MINUTE_IN_MILLIS;
                break;
            case R.id.min_hour:
                min = DateUtils.HOUR_IN_MILLIS;
                break;
            case R.id.min_day:
                min = DateUtils.DAY_IN_MILLIS;
                break;
            default:
                min = -1;
                break;
        }

        if (min >= 0) {
            item.setChecked(true);
            mAdapter.setMinResolution(min);
            return true;
        }

        item.setChecked(!item.isChecked());
        final int flag;
        switch (item.getItemId()) {
            case R.id.FORMAT_SHOW_TIME:
                flag = DateUtils.FORMAT_SHOW_TIME;
                break;
            case R.id.FORMAT_SHOW_WEEKDAY:
                flag = DateUtils.FORMAT_SHOW_WEEKDAY;
                break;
            case R.id.FORMAT_SHOW_YEAR:
                flag = DateUtils.FORMAT_SHOW_YEAR;
                break;
            case R.id.FORMAT_NO_YEAR:
                flag = DateUtils.FORMAT_NO_YEAR;
                break;
            case R.id.FORMAT_SHOW_DATE:
                flag = DateUtils.FORMAT_SHOW_DATE;
                break;
            case R.id.FORMAT_NO_MONTH_DAY:
                flag = DateUtils.FORMAT_NO_MONTH_DAY;
                break;
            case R.id.FORMAT_NO_NOON:
                flag = DateUtils.FORMAT_NO_NOON;
                break;
            case R.id.FORMAT_NO_MIDNIGHT:
                flag = DateUtils.FORMAT_NO_MIDNIGHT;
                break;
            case R.id.FORMAT_ABBREV_TIME:
                flag = DateUtils.FORMAT_ABBREV_TIME;
                break;
            case R.id.FORMAT_ABBREV_WEEKDAY:
                flag = DateUtils.FORMAT_ABBREV_WEEKDAY;
                break;
            case R.id.FORMAT_ABBREV_MONTH:
                flag = DateUtils.FORMAT_ABBREV_MONTH;
                break;
            case R.id.FORMAT_NUMERIC_DATE:
                flag = DateUtils.FORMAT_NUMERIC_DATE;
                break;
            case R.id.FORMAT_ABBREV_RELATIVE:
                flag = DateUtils.FORMAT_ABBREV_RELATIVE;
                break;
            case R.id.FORMAT_ABBREV_ALL:
                flag = DateUtils.FORMAT_ABBREV_ALL;
                break;
            default:
                throw new UnsupportedOperationException("unknown id");
        }

        if (item.isChecked()) {
            mAdapter.addFlag(flag);
        } else {
            mAdapter.removeFlag(flag);
        }

        return true;
    }
}
