package group12.cpen391.patienttracker;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private static ArrayList<ImageItem> mDataSource;

    public GalleryAdapter(Context context, int textViewResourceId, ArrayList<ImageItem> items) {
        mContext = context;
        mDataSource = items;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    public void addItem(ImageItem item) { mDataSource.add(item); }

    public boolean containsId(int id){
        for (ImageItem i : mDataSource){
            if (i.id == id) return true;
        }
        return false;
    }

    @Override
    public Object getItem(int position) { return mDataSource.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        ImageItem item = (ImageItem) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.grid_item_text);
            holder.image = (ImageView) convertView.findViewById(R.id.grid_item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set text/image for views
        holder.title.setText(item.date);
        holder.image.setImageBitmap(item.image);

//          Picasso.with(mContext).load(item.image).placeholder(R.mipmap.ic_launcher).into(holder.image);
//        Picasso.with(mContext).load("file:///android_asset/testimage.jpg").placeholder(R.drawable.ic_error).into(holder.image);

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        ImageView image;
    }
}