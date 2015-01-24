package fr.isen.shazamphoto;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.isen.shazamphoto.database.Monument;


public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Monument> movieItems;

    public CustomListAdapter(Activity activity, List<Monument> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            if(position%2 == 0){
                convertView = inflater.inflate(R.layout.list_row_right, null);
            }else{
                convertView = inflater.inflate(R.layout.list_row_left, null);
            }

        }


        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Monument m = movieItems.get(position);

        title.setText(m.getName());
        year.setText(String.valueOf(m.getYear()));
        int id = activity.getResources().getIdentifier("res:drawable/monument_1.jpg", null, null);
        image.setImageResource(id);

        return convertView;
    }

}