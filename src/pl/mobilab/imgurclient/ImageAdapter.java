package pl.mobilab.imgurclient;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ImageAdapter extends BaseAdapter {
	
    private LayoutInflater layoutInflater;
    private List<String> urls;
    private Context context;

    public ImageAdapter(Context context, List<String> content) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.urls = content;
    }

    public int getCount() {
        return urls.size();
    }

    public Object getItem(int position) {
        return urls.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	View v = convertView;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.image_item, parent, false);
        }
        SquareImageView imageView = (SquareImageView) v.findViewById(R.id.picture);

        ImageLoader.newInstance(context).loadBitmap(urls.get(position), imageView);
        
        return v;
    }
}
