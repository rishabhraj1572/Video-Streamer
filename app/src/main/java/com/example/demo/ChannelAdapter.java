package com.example.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

    private Context mContext;
    private ArrayList<ChannelItem> mChannelList;
    private OnItemClickListener mListener;




    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public ChannelAdapter(Context context, ArrayList<ChannelItem> ChannelList){
        mContext=context;
        mChannelList=ChannelList;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.channel_item,parent,false);
        return new ChannelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        ChannelItem currentItem = mChannelList.get(position);

        String imageUrl =currentItem.getImageUrl();
        String channelName= currentItem.getChannelName();


        holder.clickToPlay.setOnClickListener(view->{

            if(mListener!=null){
                if(position!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        });

        holder.mChannelName.setText(channelName);

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .oval(false)
                .build();

        Picasso.get()
                .load(imageUrl)
                .fit()
                //.transform(transformation)
                .into(holder.mChannelImage);


    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder{
        public ImageView mChannelImage;
        public TextView mChannelName;
        public LinearLayout clickToPlay;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            mChannelImage=itemView.findViewById(R.id.channelImage);
            mChannelName=itemView.findViewById(R.id.channelName);
            clickToPlay=itemView.findViewById(R.id.clickToPlay);


        }
    }
}
