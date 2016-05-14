package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProdukAdapter extends ArrayAdapter<Produk>{

    TextView namaproduk;
    Context context;

    public ProdukAdapter (Context context, ArrayList<Produk> produks){
        super(context, R.layout.list_produk , produks);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Produk produk = getItem(position);

        View rowView = inflater.inflate(R.layout.list_produk, parent, false);

        namaproduk = (TextView) rowView.findViewById(R.id.list_produk);

        namaproduk.setText(produk.getNamaProduk());

        return rowView;
    }
}
