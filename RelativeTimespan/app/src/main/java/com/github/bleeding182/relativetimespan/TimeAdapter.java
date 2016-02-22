package com.github.bleeding182.relativetimespan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by David on 22.02.2016.
 */
public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {


    public static final long[] mTimestamps = new long[]{
            0,
            DateUtils.SECOND_IN_MILLIS,
            2 * DateUtils.SECOND_IN_MILLIS,
            5 * DateUtils.SECOND_IN_MILLIS,
            10 * DateUtils.SECOND_IN_MILLIS,
            30 * DateUtils.SECOND_IN_MILLIS,
            DateUtils.MINUTE_IN_MILLIS,
            2 * DateUtils.MINUTE_IN_MILLIS,
            5 * DateUtils.MINUTE_IN_MILLIS,
            10 * DateUtils.MINUTE_IN_MILLIS,
            30 * DateUtils.MINUTE_IN_MILLIS,
            DateUtils.HOUR_IN_MILLIS,
            2 * DateUtils.HOUR_IN_MILLIS,
            5 * DateUtils.HOUR_IN_MILLIS,
            10 * DateUtils.HOUR_IN_MILLIS,
            24 * DateUtils.HOUR_IN_MILLIS,
            36 * DateUtils.HOUR_IN_MILLIS,
            72 * DateUtils.HOUR_IN_MILLIS,
            4 * DateUtils.DAY_IN_MILLIS,
            5 * DateUtils.DAY_IN_MILLIS,
            7 * DateUtils.DAY_IN_MILLIS,
            14 * DateUtils.DAY_IN_MILLIS,
            21 * DateUtils.DAY_IN_MILLIS,
            DateUtils.DAY_IN_MILLIS * 30,
            2 * DateUtils.DAY_IN_MILLIS * 30,
            3 * DateUtils.DAY_IN_MILLIS * 30,
            DateUtils.DAY_IN_MILLIS * 30 * 12,
            2 * DateUtils.DAY_IN_MILLIS * 30 * 12
    };
    private int mFlags;
    private long mMinResolution;
    private long mTransitionResolution;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_list_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        long now = System.currentTimeMillis();
        holder.mTextView1.setText(DateUtils.getRelativeDateTimeString(context,
                now - mTimestamps[position], mMinResolution, mTransitionResolution, mFlags));
    }

    @Override
    public int getItemCount() {
        return mTimestamps.length;
    }

    public void addFlag(int flag) {
        mFlags |= flag;
        notifyDataSetChanged();
    }

    public void removeFlag(int flag) {
        mFlags &= ~flag;
        notifyDataSetChanged();
    }

    public void setMinResolution(long minResolution) {
        mMinResolution = minResolution;
        notifyDataSetChanged();
    }

    public void setTransitionResolution(long transitionResolution) {
        mTransitionResolution = transitionResolution;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView1;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView1 = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

}
