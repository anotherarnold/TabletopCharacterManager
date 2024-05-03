package net.chrisarnold.tabletopcharactermanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Notes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        //Setting up sharedPrefs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText playerNotes = (EditText) findViewById(R.id.PlayerNotes);
        playerNotes.setText(sharedPref.getString("notes", ""));

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Putting the notes into sharedPrefs
                String notes = playerNotes.getText().toString();
                editor.putString("notes", notes);
                editor.apply();
            }
        });
    }
}