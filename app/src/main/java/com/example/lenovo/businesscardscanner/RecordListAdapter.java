package com.example.lenovo.businesscardscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecordListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int layout;
    ArrayList<Model> recordList,tempArray;
    CustomFilter cs;
    public RecordListAdapter(Context context, int layout, ArrayList<Model> recordList)
    {
        this.context = context;
        this.layout = layout;
        this.recordList = recordList;
        this.tempArray = recordList;
    }

    @Override
    public int getCount()
    {
        return recordList.size();
    }

    @Override
    public Object getItem(int i)
    {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }
    private class ViewHolder
    {
        ImageView imageView;
        TextView txtName,txtCompany,txtStatus;

    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View row = view;
        ViewHolder holder = new ViewHolder();
        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.txtName = row.findViewById(R.id.txtName);
            holder.txtCompany = row.findViewById(R.id.txtComp);
          //  holder.imageView = row.findViewById(R.id.imgView_Icon);
            holder.txtStatus = row.findViewById(R.id.txtStatus);
            row.setTag(holder);

        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }
        Model model = recordList.get(i);
        holder.txtName.setText(model.getName());
        holder.txtCompany.setText(model.getCompany());
        holder.txtStatus.setText(model.getStatus());
      //  byte[] recordImage = model.getImage();
      //  Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage,0,recordImage.length);
       // holder.imageView.setImageBitmap(bitmap);
        return row;
    }


    public Filter getFilter()
    {
        if(cs == null)
        {
            cs = new CustomFilter();
        }
        return cs;
    }
    class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint!=null && constraint.length()>0)
            {
                constraint = constraint.toString().toUpperCase();

            ArrayList<Model> filters = new ArrayList<>();
            for(int i = 0; i < tempArray.size(); i++)
            {
                if(tempArray.get(i).getName().toUpperCase().contains(constraint) || tempArray.get(i).getCompany().toUpperCase().contains(constraint) ||  tempArray.get(i).getStatus().toUpperCase().contains(constraint) )
                {
                    Model model = new Model(tempArray.get(i).getName(),tempArray.get(i).getCompany(),tempArray.get(i).getStatus());
                    filters.add(model);
                }
            }
            results.count = filters.size();
            results.values = filters;
            }
            else
            {
                results.count = tempArray.size();
                results.values = tempArray;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                recordList = (ArrayList<Model>)results.values;
                notifyDataSetChanged();
        }
    }
}
