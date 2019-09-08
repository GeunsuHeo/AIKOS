package com.example.mycuk;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener{
    private List<String> mDataset, urlList;
    private String baseUrl;
    static int position;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(android.R.id.text1);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<String> list, List<String> urlList) {
        mDataset = list;
        this.urlList = urlList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        position = 0;
        baseUrl = "http://www.catholic.ac.kr";

        Context context = parent.getContext() ;
        LayoutInflater inflater = LayoutInflater.from(context);
        // create a new view
        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent,false);
        MyViewHolder vh = new MyAdapter.MyViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try{
            holder.textView.setText(mDataset.get(position));
            holder.textView.setOnClickListener(this);
            holder.textView.setTag(position);
        }catch (Exception e){
            holder.textView.setText("데이터를 가져올 수 없습니다. 인터넷 상태를 확인해주세요.");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<String> newlist, List<String> newUrlList) {
        mDataset = newlist;
        urlList = newUrlList;
        this.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        String url = baseUrl+urlList.get(position);
        System.out.println("position : "+position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        v.getContext().startActivity(intent);
    }
}

class MyltemDecoration extends RecyclerView.ItemDecoration{
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int index = parent.getChildAdapterPosition(view)+1;
        outRect.set(20,20,20,20);

        view.setBackgroundColor(0x2200498C);
        ViewCompat.setElevation(view, 20.0f);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}

