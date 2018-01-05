package com.example.user.ex_inmobiles;

/**
 * Created by user on 1/3/2018.
 */

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ex_inmobiles.entity.ImageEntity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {



        private List<ImageEntity> imgs;
        private Context context;
        private RecyclerViewClickListener mListener;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title, desc;
            public ImageView imageView;
            private RecyclerViewClickListener mListener;

            public MyViewHolder(View view,RecyclerViewClickListener listener) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                desc = (TextView) view.findViewById(R.id.description);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                mListener = listener;
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mListener.onClick(view, getAdapterPosition());
            }
        }


        public ListAdapter(List<ImageEntity> imgs,RecyclerViewClickListener listener) {
            this.imgs = imgs;
            mListener = listener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            context = parent.getContext();
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);

            return new MyViewHolder(itemView,mListener);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
                ImageEntity img = imgs.get(position);
                holder.title.setText(img.getTitle());
                holder.desc.setText(img.getDescription());
                try{
                    Picasso.with(context).load(img.getLink()).resize(150, 150)
                            .placeholder(R.mipmap.logo)
                            .error(R.mipmap.logo).into(holder.imageView);
                }catch(Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                }


        }

        @Override
        public int getItemCount() {
            return imgs.size();
        }

        public List<ImageEntity> getList(){return imgs;}

    }

