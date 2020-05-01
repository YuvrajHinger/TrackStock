package com.example.trackstock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> implements Filterable {

    List<MyItem> myItems;
    List<MyItem> myItemAll;

    public HomeAdapter(ArrayList<MyItem> myItems) {
        this.myItems = myItems;
        this.myItemAll = new ArrayList<>(myItems);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_recycler_view, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MyItem myItem = myItems.get(i);
        byte[] itemPic = myItem.getItemImage();
        if(itemPic==null){
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(itemPic,0,itemPic.length);
        viewHolder.homeImage.setImageBitmap(bitmap);
        viewHolder.homeItemName.setText(myItem.getItemName());
        viewHolder.homeStock.setText("Stock: "+String.valueOf(myItem.getCurrentStock()));
        viewHolder.homePrice.setText("Price: "+String.valueOf(myItem.getPrice()));
        viewHolder.homItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), MyItemView.class).putExtra("object", myItem));
            }
        });
    }

    @Override
    public int getItemCount() {
        return myItems.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyItem> filterList = new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filterList.addAll(myItemAll);
            }
            else{
                String pattern = constraint.toString().toLowerCase().trim();
                for(MyItem item: myItemAll){
                    if(item.getItemName().toLowerCase().contains(pattern)){
                        filterList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myItems.clear();
            myItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView homeImage;
        TextView homeItemName;
        TextView homePrice;
        TextView homeStock;
        LinearLayout homItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeImage = itemView.findViewById(R.id.home_item_image);
            homeItemName = itemView.findViewById(R.id.home_item_name);
            homePrice = itemView.findViewById(R.id.home_item_price);
            homeStock = itemView.findViewById(R.id.home_item_stock);
            homItem = itemView.findViewById(R.id.home_item);
        }
    }
}
