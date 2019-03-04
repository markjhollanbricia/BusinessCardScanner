package com.example.lenovo.businesscardscanner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {
public android.app.Dialog onCreateDialog(Bundle savedInstance)
{

   final  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Choose")
            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                 public void onClick(DialogInterface dialog, int which) {

                }
            })
    .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    });
    return builder.create();

}
}
