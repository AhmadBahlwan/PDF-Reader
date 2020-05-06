package com.bhlwan.pdfreader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFAdapter extends ArrayAdapter<File> implements Filterable {

    Context context;
    ViewHolder viewHolder;
    ArrayList<File>pdf_files;
    ArrayList<File>search_pdf_files;

    public PDFAdapter(@NonNull Context context, ArrayList<File> pdf_files) {
        super(context, R.layout.pdf_adapter,pdf_files);
        this.context = context;
        search_pdf_files = pdf_files;
        this.pdf_files = new ArrayList<>(pdf_files);
    }

    public void update(ArrayList<File>files){
        pdf_files.clear();
        pdf_files.addAll(files);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        if (pdf_files.size() > 0){
            return pdf_files.size();
        } else
            return 1;
    }




    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pdf_adapter,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.tv_fileName =  convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        File currentFile = search_pdf_files.get(position);
        viewHolder.tv_fileName.setText(currentFile.getName());
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filesFilter;
    }

    private Filter filesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<File> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(pdf_files);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (File item : pdf_files) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            search_pdf_files.clear();
            search_pdf_files.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder{
       TextView tv_fileName;
    }
}
