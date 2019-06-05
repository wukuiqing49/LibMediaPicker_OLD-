package com.wkq.media.ui.camera;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wkq.media.R;

import java.util.List;

/**
 * Author by Yuansiwen.com, Date on 2018/12/25.
 * PS: Not easy to write code, please indicate.
 */
public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<CircleData.ReleaseVideoTypeListBean> data;
    private MyClickListener mListener;

    @Override
    public void onClick(View v) {
        mListener.clickListener(v);
    }

    public interface MyClickListener{
        public void clickListener(View v);
    }

    public RecyclerviewAdapter(Context context, List<CircleData.ReleaseVideoTypeListBean> data, MyClickListener listener){
        this.context = context;
        this.data = data;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_circl,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).load(data.get(position).getPic()).into(holder.userimage);
        holder.userimage.setOnClickListener(this);
        holder.userimage.setTag(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView userimage;

        public ViewHolder(View itemView) {
            super(itemView);
            userimage = itemView.findViewById(R.id.userImage);
        }
    }
}
