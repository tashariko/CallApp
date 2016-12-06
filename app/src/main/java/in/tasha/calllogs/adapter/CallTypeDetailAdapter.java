package in.tasha.calllogs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.tasha.calllogs.R;
import in.tasha.calllogs.model.CallModel;
import in.tasha.calllogs.util.TimeSpentManager;

/**
 * Created by Puru Chauhan on 29/11/16.
 */

public class CallTypeDetailAdapter extends ArrayAdapter<CallModel> {

    private LayoutInflater inflater;
    private ArrayList<CallModel> list;
    private View view;
    private ViewHolder holder;

    public CallTypeDetailAdapter(Context context, int resource, ArrayList<CallModel> adapterList) {
        super(context, resource);
        this.list=adapterList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if(view==null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item_detail, null);
            holder.contactNumber = (TextView) view.findViewById(R.id.contactNumber);
            holder.time= (TextView) view.findViewById(R.id.time);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }


        long time = list.get(position).time;
        holder.contactNumber.setText(String.valueOf(list.get(position).otherNumber)+" - "+
        TimeSpentManager.setTimeAgo(view.getContext(),list.get(position).date));
        holder.time.setText(String.format("%.02f",((float)time/60))+" min");
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private class ViewHolder{
        TextView contactNumber,time;
    }
}
