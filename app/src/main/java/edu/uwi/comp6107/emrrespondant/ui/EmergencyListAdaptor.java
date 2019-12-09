package edu.uwi.comp6107.emrrespondant.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import edu.uwi.comp6107.emrrespondant.R;
import edu.uwi.comp6107.emrrespondant.model.Emergency;
import edu.uwi.comp6107.emrrespondant.model.Responder;
import edu.uwi.comp6107.emrrespondant.presenters.EmergencyListPresenter;

public class EmergencyListAdaptor extends RecyclerView.Adapter<EmergencyListAdaptor.ViewHolder> {

    private static final String TAG = "MDB:EmergencListAdaptor";


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView description;
        private TextView timestamp;
        private TextView latitude;
        private TextView longitude;
        private TextView distance;


        public ViewHolder(View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.emergency_description_textView);
            timestamp = itemView.findViewById(R.id.timestamp_textView);
            latitude = itemView.findViewById(R.id.latitude_textView);
            longitude = itemView.findViewById(R.id.longitude_textView);
            distance = itemView.findViewById(R.id.distance_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EmergencyActivity.class);
                    intent.putExtra("CALLER_ID", emergencies.get(getAdapterPosition()).callerId);
                    Log.d(TAG, "onClick: passing caller id of: " + emergencies.get(getAdapterPosition()).callerId);
                    context.startActivity(intent);
                }
            });

        }

        public void upDateUIUsing(Emergency emergency, Responder responder) {
            if(emergency == null) { return; }

            if(emergency != null) {
                if(emergency.description.isEmpty() || emergency.description == null) {
                    description.setText("No description provided.");
                } else {
                    description.setText(emergency.description);
                }


                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(emergency.timestamp);
                timestamp.setText(dateFormat.format(date));

                if(emergency.location != null){
                    latitude.setText(String.valueOf(emergency.location.getLatitude()));
                    longitude.setText(String.valueOf(emergency.location.getLongitude()));
                }
            }


            if(currentResponder != null){
                if(currentResponder.currentLocation != null && emergency.location != null) {
                    Float floatDistance = emergency.location.distanceTo(currentResponder.currentLocation);
                    Float distanceInKm = floatDistance/1000;
                    String stringDistance = String.format("%1.2f", distanceInKm);
                    distance.setText(stringDistance + "km away.");
                } else {
                    distance.setText("");
                }
            }

        }

    }

    private Context context;
    private ArrayList<Emergency> emergencies;
    private Responder currentResponder;

    public EmergencyListAdaptor(Context context) {
        this.context = context;
        emergencies = new ArrayList<>();
    }

    public void updateEmegencyList(final ArrayList<Emergency> emergencies) {
        this.emergencies = emergencies;
        sortEmergencies();
        notifyDataSetChanged();
    }


    private void sortEmergencies(){
        Collections.sort(this.emergencies, new Comparator<Emergency>() {
            @Override
            public int compare(Emergency o1, Emergency o2) {
                int result = 0;

                if(o1.location == null || o2.location == null) {
                    return 1;
                }

                if(currentResponder == null) {return  1;}

                if(o1.location.distanceTo(currentResponder.currentLocation) < o2.location.distanceTo(currentResponder.currentLocation)) {
                    result = -1;
                } else if(o1.location.distanceTo(currentResponder.currentLocation) > o2.location.distanceTo(currentResponder.currentLocation)) {
                    result = 1;
                }

                return result;
            }
        });
    }

    public void updateCurrentResponder(Responder responder) {
        this.currentResponder = responder;
        sortEmergencies();
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.upDateUIUsing(emergencies.get(position), currentResponder);
    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }
}
