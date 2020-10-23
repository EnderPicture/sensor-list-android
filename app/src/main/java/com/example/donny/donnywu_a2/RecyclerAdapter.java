package com.example.donny.donnywu_a2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    // the sensor list
    private List<Sensor> mSensorList;

    // main activity's context
    private Context mContext;

    /**
     * sensor recycler view
     * @param context       context of the main activity
     * @param sensorList    the list of sensors inside the
     */
    public RecyclerAdapter(Context context, List<Sensor> sensorList) {
        mSensorList = sensorList;
        mContext = context;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate the view item
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.recycler_item, parent, false);

        // makes a new holder item
        return new ViewHolder (itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // set the values for the specific sensor based on index
        holder.onSet(mSensorList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mSensorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mView;
        private View mRoot;
        private Sensor mSensor;
        private int mIndex;

        public ViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
            mView = itemView.findViewById(R.id.itemText);

            mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, SensorDetails.class);
                    i.putExtra("Sensor Name", mSensor.getName());
                    i.putExtra("Sensor Index", mIndex);
                    mContext.startActivity(i);
                }
            });
        }

        public void onSet(Sensor sensor, int index) {
            mSensor = sensor;
            mIndex = index;

            mView.setText(mSensor.getName());
        }
    }
}
