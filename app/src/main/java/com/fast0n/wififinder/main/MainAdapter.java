package com.fast0n.wififinder.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fast0n.wififinder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private List<Main> mainList;
    private Context context;

    public MainAdapter(List<Main> mainList, Context context) {
        this.mainList = mainList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Main c = mainList.get(position);
        holder.tv_counter.setText(c.counter);
        holder.tv_ssid.setText(c.ssid);
        holder.tv_location.setText(c.nameLocation);

        if (c.counter.equals("1")){
            holder.tv_country.setVisibility(View.VISIBLE);
            holder.tv_country.setText(c.nameLocation.split(",")[3]);
        }
        else{
            holder.tv_country.setVisibility(View.GONE);
        }

        switch (c.pwdtype) {
            case "WI-FI APERTO":
                holder.tv_password.setText(context.getString(R.string.radioButton2).toUpperCase());
                break;
            case "NON FUNZIONA":
                holder.tv_password.setText(context.getString(R.string.radioButton3).toUpperCase());
                break;
            default:
                holder.tv_password.setText(c.pwdtype);
                break;
        }

        holder.tv_datetime.setText(String.valueOf(c.datetime));

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), c.datetime.replaceAll("\\s+", ""), timeStamp);

        if (dateDifference < 2){
            Glide.with(context).load(context.getDrawable(R.drawable.ic_new)).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(holder.imageNew);
            holder.imageNew.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageNew.setVisibility(View.GONE);
        }


        holder.map.setOnClickListener(view -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?q=" + c.location.replaceAll("\\s+", "")))));


    }
    private static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception ignoring) {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_counter, tv_ssid, tv_location, tv_password, tv_datetime, tv_country;
        ImageButton map;
        ImageView imageNew;

        MyViewHolder(View view) {
            super(view);
            tv_country = view.findViewById(R.id.tv_country);
            tv_counter = view.findViewById(R.id.tv_counter);
            tv_ssid = view.findViewById(R.id.tv_ssid);
            tv_location = view.findViewById(R.id.tv_location);
            tv_password = view.findViewById(R.id.tv_password);
            tv_datetime = view.findViewById(R.id.tv_datetime);
            map = view.findViewById(R.id.imageButton);
            imageNew = view.findViewById(R.id.imageNew);
        }
    }
}
