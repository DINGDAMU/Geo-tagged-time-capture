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
    ArrayList<String> compass=new ArrayList<>();

    public PostAdapter(Context c,int layoutResourceId,ArrayList<String> uri,ArrayList<String> coordinate,ArrayList<String> address,ArrayList<String> time,ArrayList<String> compass)
    {

        this.context = c;
        this.layoutResourceId = layoutResourceId;
        this.uri = uri;
        this.coordinate = coordinate;
        this.address = address;
        this.time = time;
        this.compass=compass;
    }
    //public int getCount() 得到数据的行数

    @Override
    public int getCount() {
        return uri.size();
    }

    //public Object getItem(int position) 根据position得到某一行的记录
    @Override
    public Object getItem(int position) {
        return uri.get(position);
    }

    // public long getItemId(int position) 得到某一条记录的ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    //自己的适配器，可以自定义布局
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PostHolder holder = null;
        if(row == null)
        {
            //在实际开发中LayoutInflater这个类还是非常有用的，它的作用类似于findViewById()。
            // 不同点是LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化；
            // 而findViewById()是找xml布局文件下的具体widget控件(如Button、TextView等)。
            //1、对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入；
            //2、对于一个已经载入的界面，就可以使用Activiy.findViewById()方法来获得其中的界面元素。
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PostHolder();
            holder.postImage = (ImageView)row.findViewById(R.id.listImage);
            holder.txtTitle = (TextView)row.findViewById(R.id.listCoordinates);
            holder.txtTitle2 = (TextView)row.findViewById(R.id.listAddress);
            holder.txtTitle3 = (TextView)row.findViewById(R.id.listTime);
            holder.txtTitle4 =(TextView)row.findViewById(R.id.listCompass);
            row.setTag(holder);
        }
        else
        {
            holder = (PostHolder)row.getTag();
        }

        Picasso.with(this.context).load(Uri.parse(uri.get(position)).toString()).placeholder(R.drawable.placeholder).resize(1000,1000).into(holder.postImage);
        holder.txtTitle.setText(coordinate.get(position));
        holder.txtTitle2.setText(address.get(position));
        holder.txtTitle3.setText(time.get(position));
        holder.txtTitle4.setText(compass.get(position));

        return row;
    }

    /*View holder design pattern to allow for recycling of list items*/
    static class PostHolder
    {
        ImageView postImage;
        TextView txtTitle;
        TextView txtTitle2;
        TextView txtTitle3;
        TextView txtTitle4;
    }
}
