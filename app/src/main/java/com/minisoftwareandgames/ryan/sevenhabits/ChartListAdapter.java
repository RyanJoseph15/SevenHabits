package com.minisoftwareandgames.ryan.sevenhabits;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ryan on 12/19/15.
 */
public class ChartListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QuadrantDetail> elements;
    private LayoutInflater mLayoutInflater = null;
    private int lastQuad = 1;                                   // last seen quadrant - for coloring

    public ChartListAdapter(Context context, ArrayList<QuadrantDetail> list) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        elements = list;
    }
    @Override
    public int getCount() {
        if (elements != null)
            return elements.size();
        else return 0;
    }
    @Override
    public Object getItem(int pos) {
        if (elements != null)
            return elements.get(pos);
        else return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        CompleteListViewHolder viewHolder;
        QuadrantDetail quadrantDetail = elements.get(position);
        lastQuad = quadrantDetail.getQuadrant();

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.list_view_item, null);
            viewHolder = new CompleteListViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (CompleteListViewHolder) view.getTag();
        }
        viewHolder.mTVItem.setText(quadrantDetail.getDetails());
        setBackgroundColor(view, lastQuad);
        return view;
    }

    private void setBackgroundColor(View view, int lastQuad) {
        if (lastQuad == 1) {
            view.setBackgroundColor(this.context.getResources().getColor(R.color.green));
        } else if (lastQuad == 2) {
            view.setBackgroundColor(this.context.getResources().getColor(R.color.blue));
        } else if (lastQuad == 3) {
            view.setBackgroundColor(this.context.getResources().getColor(R.color.yellow));
        } else {    // 4
            view.setBackgroundColor(this.context.getResources().getColor(R.color.red));
        }
    }

    class CompleteListViewHolder {
        public TextView mTVItem;
        public CompleteListViewHolder(View base) {
            mTVItem = (TextView) base.findViewById(R.id.list_view_item);
        }
    }

    public void clear() {
        elements = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<QuadrantDetail> elements) {
        this.elements.addAll(elements);
        order(elements.get(0));
        notifyDataSetChanged();
    }

    public void add(QuadrantDetail element) {
        this.elements.add(element);
        order(element);
        notifyDataSetChanged();
    }

    public void order(QuadrantDetail element) {
        SQLiteHelper helper = new SQLiteHelper(context);
        if (element != null)
            this.elements = helper.getDetails(element.getTitle(), Utilities.QUADRANT.ALL);
        else this.elements = new ArrayList<>();
    }

    public void newList(ArrayList<QuadrantDetail> elements) {
        this.elements = elements;
        notifyDataSetChanged();
    }

}
