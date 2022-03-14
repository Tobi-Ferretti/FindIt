package com.FindIt.finditpflanzenfinden;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.FindIt.finditpflanzenfinden.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<String> nameList = null;  //Namen
    //private ArrayList<String> arraylist;          //Objekte
    private HashMap<String, ArrayList<Integer>> serachMap;
    List<String> list;

    public ListViewAdapter(Context context, HashMap<String, ArrayList<Integer>> serachMap) {
        mContext = context;
        this.nameList = new ArrayList<String>(serachMap.keySet());
        inflater = LayoutInflater.from(mContext);
        //this.arraylist = new ArrayList<String>();
        //this.arraylist.addAll(nameList);
        this.list= new ArrayList<>(serachMap.keySet());
        this.serachMap=serachMap;
    }

    public void updateAdapter(HashMap<String, ArrayList<Integer>> serachMap){
        this.nameList = new ArrayList<String>(serachMap.keySet());
        this.list= new ArrayList<>(serachMap.keySet());
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public String getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(nameList.get(position));
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        nameList.clear();
        if (charText.length() == 0) {
            nameList.addAll(list);
        } else {
            for (int i=0; i<list.size(); i++) {
                if (list.get(i).toLowerCase(Locale.getDefault()).contains(charText)) {
                    nameList.add(list.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }
}
