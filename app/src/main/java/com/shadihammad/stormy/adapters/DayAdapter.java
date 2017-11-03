package com.shadihammad.stormy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shadihammad.stormy.R;
import com.shadihammad.stormy.weather.Day;

import org.w3c.dom.Text;

/**
 * Created by fadir on 10/12/2017.
 */

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_list_item,
                parent, false);

        DayViewHolder viewHolder = new DayViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        holder.bindDay(mDays[position]);
    }

    @Override
    public int getItemCount() {
        return mDays.length;
    }


    public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTemperatureLabel;
        public TextView mDayNameLabel;
        public ImageView mIconLabel;


        public DayViewHolder(View itemView) {
            super(itemView);

            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mDayNameLabel = (TextView) itemView.findViewById(R.id.dayNameLabel);
            mIconLabel = (ImageView) itemView.findViewById(R.id.iconImageView);

            itemView.setOnClickListener(this);
        }

        public void bindDay(Day day) {
            mTemperatureLabel.setText(day.getTemperatureMax() + "");
            mDayNameLabel.setText(day.getDayOfTheWeek());
            mIconLabel.setImageResource(day.getIconId());
        }

        @Override
        public void onClick(View v) {
            String highTemp = (String) mTemperatureLabel.getText();
            String day = (String) mDayNameLabel.getText();
            String mSummary = mDays[getAdapterPosition()].getSummary();
            String message = String.format("On %s, it will be %s with a high of %s degrees",
                    day, mSummary.toLowerCase().replaceAll("\\.", ""), highTemp);
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}

























