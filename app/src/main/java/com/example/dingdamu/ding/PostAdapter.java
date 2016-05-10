package com.example.dingdamu.ding;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dingdamu on 10/05/16.
 */
public class PostAdapter extends BaseAdapter {
    static Context context;
    static int layoutResourceId;
    ArrayList<String> uri = new ArrayList<String>();
    ArrayList<String> coordinate = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();

    public PostAdapter(Context c,int layoutResourceId,ArrayList<String> uri,ArrayList<String> coordinate,ArrayList<String> address,ArrayList<String> time)
    {

        this.context = c;
        this.layoutResourceId = layoutResourceId;
        this.uri = uri;
        this.coordinate = coordinate;
        this.address = address;
        this.time = time;
    }

    @Override
    public int getCount() {
        return uri.size();
    }

    @Override
    public Object getItem(int position) {
        return uri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PostHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PostHolder();
            holder.postImage = (ImageView)row.findViewById(R.id.listImage);
            holder.txtTitle = (TextView)row.findViewById(R.id.listCoordinates);
            holder.txtTitle2 = (TextView)row.findViewById(R.id.listAddress);
            holder.txtTitle3 = (TextView)row.findViewById(R.id.listTime);
            row.setTag(holder);
        }
        else
        {
            holder = (PostHolder)row.getTag();
        }

        Picasso.with(this.context).load(Uri.parse(uri.get(position)).toString()).placeholder(R.drawable.placeholder).resize(500,350).into(holder.postImage);
        holder.txtTitle.setText(coordinate.get(position));
        holder.txtTitle2.setText(address.get(position));
        holder.txtTitle3.setText(time.get(position));
        return row;
    }

    /*View holder design pattern to allow for recycling of list items*/
    static class PostHolder
    {
        ImageView postImage;
        TextView txtTitle;
        TextView txtTitle2;
        TextView txtTitle3;
    }
}
