package com.bhlwan.pdfreader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;

public class SortingFragmentDialog extends DialogFragment {
    boolean is_Descending;
    RadioButton option;
    CheckBox checkBox;
    Button ok_button;
    Button cancel_button;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sorting_options, container, false);
        final Dialog dialog = this.getDialog();
        option = view.findViewById(getChosenRadioButton("CHOSEN_OPTION"));
        if (option == null){
            option = view.findViewById(R.id.sort_by_name);
        }
        checkBox = view.findViewById(R.id.checkbox);
        ok_button = view.findViewById(R.id.ok_button);
        cancel_button = view.findViewById(R.id.cancel_button);
        is_Descending = getCheckBoxState("IS_DESCENDING");
        Log.d("checkbox",String.valueOf(is_Descending));
        checkBox.setChecked(is_Descending);
        //Log.d("option",String.valueOf(option.getId()));
        option.setChecked(true);
        dialog.setTitle("Sort By");
        final SharedPreferences.Editor editor = getContext().getSharedPreferences("option id", Context.MODE_PRIVATE).edit();
        final RadioGroup group = view.findViewById(R.id.sorting_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                option.setChecked(false);
                option = group.findViewById(checkedId);
                Log.d("option2",String.valueOf(checkedId));
                option.setChecked(true);
            }
        });
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChosenRadioButton("CHOSEN_OPTION", option.getId());
                is_Descending = checkBox.isChecked();
                editor.putInt("sorting_option", option.getId());
                editor.putBoolean("IS_CHECKED",is_Descending);
                editor.apply();
                saveCheckBoxState("IS_DESCENDING",is_Descending);
                MainActivity.is_Descending = is_Descending;
                MainActivity.sortBy(option.getId());
                MainActivity.obj_adapter.update(MainActivity.fileList);
                dialog.dismiss();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dialog.dismiss();
            }
        });
        return view;
    }


    private boolean getCheckBoxState(String key) {
        return getContext().getSharedPreferences("PROJECT_NAME", Context.MODE_PRIVATE).getBoolean(key, false);
    }


    private void saveCheckBoxState(String key, boolean isChecked) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PROJECT_NAME", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, isChecked);
        editor.commit();
    }

    private int getChosenRadioButton(String key) {
        return getContext().getSharedPreferences("PROJECT_NAME", Context.MODE_PRIVATE).getInt(key, R.id.sort_by_name);
    }

    // save the chosen option of sorting
    private void saveChosenRadioButton(String key, int id) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PROJECT_NAME", Context.MODE_PRIVATE).edit();
        editor.putInt(key, id);
        editor.commit();
    }
}

