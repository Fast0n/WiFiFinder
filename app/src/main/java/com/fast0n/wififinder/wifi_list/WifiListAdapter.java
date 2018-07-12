package com.fast0n.wififinder.wifi_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fast0n.wififinder.R;

import java.util.List;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.MyViewHolder> {

    private List<WifiList> wifiListList;

    public WifiListAdapter(List<WifiList> wifiListList) {
        this.wifiListList = wifiListList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WifiList c = wifiListList.get(position);
        holder.nome.setText(c.name_wifi);
    }

    @Override
    public int getItemCount() {
        return wifiListList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wifi, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;

        MyViewHolder(View view) {
            super(view);
            nome = view.findViewById(R.id.tv_ssid);
        }
    }
}
