package com.example.camera_activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Suggestion_dialogbox extends AppCompatDialogFragment  {

    private Suggestion_dialogbox listener;
    String suggestions;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogbox = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.suggestion_dialogbox, null);

        final EditText edit_suggestion = view.findViewById(R.id.suggestion);

        dialogbox.setView(view)
                .setTitle(" Oops, unable to detect it. \n Suggest name for this location")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        suggestions = edit_suggestion.getText().toString();
                    }
                });

        return dialogbox.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface DialogListener {
        void applyTexts(String suggestion);
    }
}

