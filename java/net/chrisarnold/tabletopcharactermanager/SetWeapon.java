package net.chrisarnold.tabletopcharactermanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SetWeapon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_weapon);

        //Getting sharedPrefs set up
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        //Making the editTexts for the activity
        EditText txtWpnName = (EditText) findViewById(R.id.txtWpnName);
        EditText txtAttackMod = (EditText) findViewById(R.id.txtAttackMod);
        EditText txtDamageMod = (EditText) findViewById(R.id.txtDamageMod);
        EditText txtDieSize = (EditText) findViewById(R.id.txtDieSize);
        EditText txtDieAmount = (EditText) findViewById(R.id.txtDieAmount);

        //Toggles proved very easy to work with
        ToggleButton toggleFinesse = (ToggleButton) findViewById(R.id.toggleFinesse);
        ToggleButton toggleHanded = (ToggleButton) findViewById(R.id.toggleHanded);
        ToggleButton toggleMissile = (ToggleButton) findViewById(R.id.toggleMissile);

        //Pulling from sharedPrefs
        txtWpnName.setText(sharedPref.getString("wpnName", ""));
        txtAttackMod.setText(String.valueOf(sharedPref.getInt("modAttack", 0)));
        txtDamageMod.setText(String.valueOf(sharedPref.getInt("modDamage", 0)));
        txtDieSize.setText((String.valueOf(sharedPref.getInt("dieSize", 0))));
        txtDieAmount.setText(String.valueOf((sharedPref.getInt("dieAmount", 0))));

        toggleFinesse.setChecked(sharedPref.getBoolean("finesse", false));
        toggleHanded.setChecked(sharedPref.getBoolean("handed", false));
        toggleMissile.setChecked(sharedPref.getBoolean("missile", false));

        //Button to save changes
        Button btnWpnSave = (Button) findViewById(R.id.btnWpnSave);
        btnWpnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //All it needs to do is jam things into sharedPrefs
                editor.putString("wpnName", txtWpnName.getText().toString());
                editor.putInt("modAttack", intValidator(txtAttackMod));
                editor.putInt("modDamage", intValidator(txtDamageMod));
                editor.putInt("dieSize", intValidator(txtDieSize));
                editor.putInt("dieAmount", intValidator(txtDieAmount));
                editor.putBoolean("finesse", toggleFinesse.isChecked());
                editor.putBoolean("handed", toggleHanded.isChecked());
                editor.putBoolean("missile", toggleMissile.isChecked());
                editor.apply();

            }
        });
    }
    //Little method to prevent empty or too big fields from crashing
    public int intValidator (EditText t) {
        int x = 0;
        try {
            x = Integer.parseInt(t.getText().toString());
        } catch (NumberFormatException e)
        {
            Toast.makeText(getApplicationContext(), "Oops! Bad number!", Toast.LENGTH_SHORT).show();
            t.setText(String.valueOf(0));
        }
        return x;
    }
}