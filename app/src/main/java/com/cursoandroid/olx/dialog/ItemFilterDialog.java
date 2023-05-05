package com.cursoandroid.olx.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.cursoandroid.olx.adapter.OnItemClickListener;


public class ItemFilterDialog {
    @NonNull
    public static AlertDialog newDialog(Context context, String title, String[] itemList, OnItemClickListener<String> onClickListener){
        final Spinner spinner = new Spinner(context);
        spinner.setLayoutParams(new ViewGroup.LayoutParams(220,54));
        spinner.setGravity(Gravity.CENTER);
        ArrayAdapter<String> estadosAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, itemList);
        estadosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(estadosAdapter);
        return new AlertDialog.Builder(context)
                .setTitle(title).setCancelable(false).setView(spinner)
                .setPositiveButton(android.R.string.ok, (d,w)-> onClickListener.onItemClick(spinner.getSelectedItem().toString()))
                .setNegativeButton(android.R.string.cancel,null)
                .create();
    }
}
