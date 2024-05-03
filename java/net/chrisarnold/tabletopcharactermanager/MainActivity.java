package net.chrisarnold.tabletopcharactermanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    //Declaring object variables to default values; we'll try to load from file below
    PlayerCharacter character = new PlayerCharacter();
    Weapon weapon = new Weapon(character, 6, 1, 0, 0, "", false, false, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //Attempts to read files to pull stored objects from
            //This could maybe all be one input stream, but it worked with two
            //and I fought with this a long time to get it to work in the first place
            File path = getApplicationContext().getFilesDir();
            File readCharacter = new File(path, "character.dat");
            File readWeapon = new File(path, "weapon.dat");
            FileInputStream characterInput = new FileInputStream(readCharacter);
            ObjectInputStream characterOIS = new ObjectInputStream(characterInput);
            //Creating character from the saved object
            character = (PlayerCharacter) characterOIS.readObject();
            characterInput.close();
            characterOIS.close();

            FileInputStream weaponInput = new FileInputStream(readWeapon);
            ObjectInputStream weaponOIS = new ObjectInputStream(weaponInput);
            //Creating weapon from the saved object
            weapon = (Weapon) weaponOIS.readObject();
            weaponInput.close();
            weaponOIS.close();

            //Notification of character being loaded and refreshing the page to make text consistent
            Toast.makeText(getApplicationContext(), "Character loaded: " + character.name, Toast.LENGTH_LONG).show();
            refreshPage(character);


        } catch (Exception e)
        {
            //If there's nothing to load, just say no character loaded
            Toast.makeText(getApplicationContext(), "No character loaded", Toast.LENGTH_SHORT).show();
        }

        //Making sharedpreferences to shuffle values between activities
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("dieSize", weapon.wpnDieSize);
        editor.putInt("dieAmount", weapon.wpnDieAmount);
        editor.putInt("modAttack", weapon.wpnModAttack);
        editor.putInt("modDamage", weapon.wpnModDamage);
        editor.putString("wpnName", weapon.wpnName);
        editor.putBoolean("finesse", weapon.isFinesse);
        editor.putBoolean("missile", weapon.isMissile);
        editor.putBoolean("handed", weapon.isTwoHanded);
        editor.putString("notes", character.playerNotes);
        editor.apply();

        //Button to save and update values.
        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call update method
                updateCharacter(character);
                //Pull player notes from sharedpref
                character.playerNotes = sharedPref.getString("notes", "");
                //Pull weapon from sharedpref
                weapon = new Weapon(character, sharedPref.getInt("dieSize", 0), sharedPref.getInt("dieAmount", 0), sharedPref.getInt("modAttack", 0),
                        sharedPref.getInt("modDamage", 0), sharedPref.getString("wpnName", ""), sharedPref.getBoolean("finesse", true),
                        sharedPref.getBoolean("missile", true), sharedPref.getBoolean("handed", true));

                //Attempt to write character and weapon objects to file
                try {
                    //Getting file path
                    File path = getApplicationContext().getFilesDir();
                    FileOutputStream fileOutCharacter = new FileOutputStream(new File(path, "character.dat"));
                    ObjectOutputStream outCharacter = new ObjectOutputStream(fileOutCharacter);
                    //Writing object
                    outCharacter.writeObject(character);
                    outCharacter.close();
                    fileOutCharacter.close();

                    FileOutputStream fileOutWeapon = new FileOutputStream(new File(path, "weapon.dat"));
                    ObjectOutputStream outWeapon = new ObjectOutputStream(fileOutWeapon);
                    outWeapon.writeObject(weapon);
                    outWeapon.close();
                    fileOutWeapon.close();
                } catch (IOException i)
                {
                    //Toast saying there's a problem. It should work, but it could possibly not work
                    Toast.makeText(getApplicationContext(), "Unable to save to file", Toast.LENGTH_LONG).show();
                }

            }
        });

        //I wanted this to be a DialogAlert, but it was just more practical to make it an activity
        Button btnNotes = (Button) findViewById(R.id.btnNotes);
        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Notes.class));
            }
        });

        //Same as above. These two are why I needed to bother with sharedPrefs
        Button btnWeapon = (Button) findViewById(R.id.btnInventory);
        btnWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SetWeapon.class));
            }
        });

        //Button to attack with, the exciting button
        Button btnAttack = (Button) findViewById(R.id.btnAttack);
        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Remaking the weapon object ensures consistency each time, even if it can be redundant.
                //This way the player can edit their weapon and have the changes reflect without having to hit the
                //update button, useful for weapon buffs.
                weapon = new Weapon(character, sharedPref.getInt("dieSize", 0), sharedPref.getInt("dieAmount", 0), sharedPref.getInt("modAttack", 0),
                        sharedPref.getInt("modDamage", 0), sharedPref.getString("wpnName", ""), sharedPref.getBoolean("finesse", true),
                        sharedPref.getBoolean("missile", true), sharedPref.getBoolean("handed", true));

                //Getting attack and damage rolls
                int attack = weapon.attack();
                int damage = weapon.damage();
                //Getting the die roll without attack modifier attached, used to determine critical hits
                int dieRoll = attack - weapon.wpnModAttackTotal;
                //AlertDialog to display the rolls
                AlertDialog.Builder attackDialog = new AlertDialog.Builder(MainActivity.this);
                final TextView attackRoll = new TextView(MainActivity.this);
                String attackToDisplay = "Die Roll: " + dieRoll + "\nAttack Roll: " + attack + "\nDamage Roll: " + damage;
                attackRoll.setText(attackToDisplay);
                attackRoll.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
                attackRoll.setGravity(Gravity.CENTER);
                attackDialog.setView(attackRoll);
                attackDialog.show();
            }
        });
    }

    //Pulls info from main activity, loads it into the character sheet, then writes
    //the derived info from the sheet to the activity
    public void updateCharacter(PlayerCharacter c){
        //Declaring edittexts
        final EditText name = (EditText) findViewById(R.id.playerName);
        final EditText hpCurrent = (EditText) findViewById(R.id.hpCurrent);
        final EditText hpMax =  (EditText) findViewById(R.id.hpMax);
        final EditText strength =  (EditText) findViewById(R.id.numStr);
        final EditText dexterity =  (EditText) findViewById(R.id.numDex);
        final EditText constitution =  (EditText) findViewById(R.id.numCon);
        final EditText intelligence =  (EditText) findViewById(R.id.numInt);
        final EditText wisdom =  (EditText) findViewById(R.id.numWis);
        final EditText charisma =  (EditText) findViewById(R.id.numCha);

        //Pulling values from the edittexts
        //Uses input validation method instead for integers since they can crash
        c.name = name.getText().toString();
        c.hpCurrent = intValidator(hpCurrent);
        c.hpMax = intValidator(hpMax);
        c.strength = intValidator(strength);
        c.dexterity = intValidator(dexterity);
        c.constitution = intValidator(constitution);
        c.intelligence = intValidator(intelligence);
        c.wisdom = intValidator(wisdom);
        c.charisma = intValidator(charisma);
        //Updating modifiers with function call
        c.updateMods();

        //More edittexts, for the modifiers
        final EditText modStr = (EditText) findViewById(R.id.modStr);
        final EditText modDex = (EditText) findViewById(R.id.modDex);
        final EditText modCon = (EditText) findViewById(R.id.modCon);
        final EditText modInt = (EditText) findViewById(R.id.modInt);
        final EditText modWis = (EditText) findViewById(R.id.modWis);
        final EditText modCha = (EditText) findViewById(R.id.modCha);

        //Setting the values
        modStr.setText(String.valueOf(c.strMod));
        modDex.setText(String.valueOf(c.dexMod));
        modCon.setText(String.valueOf(c.conMod));
        modInt.setText(String.valueOf(c.intMod));
        modWis.setText(String.valueOf(c.wisMod));
        modCha.setText(String.valueOf(c.chaMod));
    }

    //Refreshes the various text boxes by repopulating them with proper values
    //Ensures things stay up to date
    //Close to the above, but kind of in reverse. Sets the values from the character
    public void refreshPage(PlayerCharacter c){
        final EditText name = (EditText) findViewById(R.id.playerName);
        final EditText hpCurrent = (EditText) findViewById(R.id.hpCurrent);
        final EditText hpMax =  (EditText) findViewById(R.id.hpMax);
        final EditText strength =  (EditText) findViewById(R.id.numStr);
        final EditText dexterity =  (EditText) findViewById(R.id.numDex);
        final EditText constitution =  (EditText) findViewById(R.id.numCon);
        final EditText intelligence =  (EditText) findViewById(R.id.numInt);
        final EditText wisdom =  (EditText) findViewById(R.id.numWis);
        final EditText charisma =  (EditText) findViewById(R.id.numCha);

        name.setText(c.name);
        hpCurrent.setText(String.valueOf(c.hpCurrent));
        hpMax.setText(String.valueOf(c.hpMax));
        strength.setText(String.valueOf(c.strength));
        dexterity.setText(String.valueOf(c.dexterity));
        constitution.setText(String.valueOf(c.constitution));
        intelligence.setText(String.valueOf(c.intelligence));
        wisdom.setText(String.valueOf(c.wisdom));
        charisma.setText(String.valueOf(c.charisma));

        final EditText modStr = (EditText) findViewById(R.id.modStr);
        final EditText modDex = (EditText) findViewById(R.id.modDex);
        final EditText modCon = (EditText) findViewById(R.id.modCon);
        final EditText modInt = (EditText) findViewById(R.id.modInt);
        final EditText modWis = (EditText) findViewById(R.id.modWis);
        final EditText modCha = (EditText) findViewById(R.id.modCha);

        modStr.setText(String.valueOf(c.strMod));
        modDex.setText(String.valueOf(c.dexMod));
        modCon.setText(String.valueOf(c.conMod));
        modInt.setText(String.valueOf(c.intMod));
        modWis.setText(String.valueOf(c.wisMod));
        modCha.setText(String.valueOf(c.chaMod));
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