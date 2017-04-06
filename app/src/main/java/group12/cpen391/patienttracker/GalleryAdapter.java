package group12.cpen391.patienttracker;

import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private static ArrayList<ImageItem> imageList;

    public GalleryAdapter(Context context, int textViewResourceId) {
        mContext = context;
        imageList = getSavedImageList();
//        imageList = new ArrayList<ImageItem>();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    public void addItem(ImageItem item) { imageList.add(item); }

    public void updateJsonMetadata() {
        JSONObject metadata = new JSONObject();

        for (ImageItem i : imageList){
            JSONObject item = new JSONObject();
            try {
                String tag = Integer.toString(i.hashCode());
                item.put("id", i.id);
                item.put("date", i.date);
                item.put("filename", i.filename);
                metadata.put(tag, item);
            } catch (JSONException e){
                Log.e("GALLERY", e.toString());
            }
        }

        try {
            FileOutputStream os = mContext.openFileOutput("metadata.json", Context.MODE_PRIVATE);
            Log.v("GALLERY", "Updating JSON metadata: " + metadata.toString());
            os.write(metadata.toString().getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean containsId(int id){
        for (ImageItem i : imageList){
            if (i.id == id) return true;
        }
        return false;
    }

    @Override
    public Object getItem(int position) { return imageList.get(position); }

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

        File f = new File(mContext.getFilesDir(), item.filename);
        Log.v("GALLERY", "Retrieving image from path: " + mContext.getFilesDir() + item.filename);
        Picasso.with(mContext).load(f).placeholder(R.drawable.ic_error).into(holder.image);

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        ImageView image;
    }

    private ArrayList<ImageItem> getSavedImageList(){
        ArrayList<ImageItem> imageList = new ArrayList<ImageItem>();
        try {
            File f = new File(mContext.getFilesDir(), "metadata.json");
            if (f.exists()){
                FileInputStream is = new FileInputStream(f);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                String jsonString = new String(buffer, "UTF-8");
                Log.v("GALLERY", "Read saved json metadata: " + jsonString);
                System.out.println(jsonString);
                JSONObject jsonObject = new JSONObject(jsonString.trim());
                Iterator<?> keys = jsonObject.keys();

                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    Object item = jsonObject.get(key);
                    if ( item instanceof JSONObject ) {
                        int id = ((JSONObject) item).getInt("id");
                        String date = ((JSONObject) item).getString("date");
                        String filename = ((JSONObject) item).getString("filename");
                        imageList.add(new ImageItem(id, date, filename));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("GALLERY", e.toString());
        }
        return imageList;
    }
}