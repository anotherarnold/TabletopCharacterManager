package net.chrisarnold.tabletopcharactermanager;

import java.io.Serializable;

//Needs to implement Serializable to serialize for saving to file
public class PlayerCharacter implements Serializable {
    //Vital statistics
    public String name;
    public int hpMax;
    public int hpCurrent;
    public int strength;
    public int dexterity;
    public int constitution;
    public int intelligence;
    public int wisdom;
    public int charisma;
    public int strMod;
    public int dexMod;
    public int conMod;
    public int intMod;
    public int wisMod;
    public int chaMod;
    public String playerNotes;

    //Default constructor
    public PlayerCharacter() {
        name = "Character";
        hpMax = 10;
        hpCurrent = 10;
        strength = 10;
        dexterity = 10;
        constitution = 10;
        intelligence = 10;
        wisdom = 10;
        charisma = 10;
        strMod = 0;
        dexMod = 0;
        conMod = 0;
        intMod = 0;
        wisMod = 0;
        chaMod = 0;
        playerNotes = "";
    }

    //Getting modifiers from attributes. Subtract 10, divide by 2, get the floor value
    public int modCalc(int Stat) {
        double statDub = Stat;
        double modDouble = (statDub - 10)/2;
        return (int)Math.floor(modDouble);
    }

    //Updates all the modifiers for all the attributes
    public void updateMods(){
        strMod = modCalc(strength);
        dexMod = modCalc(dexterity);
        conMod = modCalc(constitution);
        intMod = modCalc(intelligence);
        wisMod = modCalc(wisdom);
        chaMod = modCalc(charisma);
    }
}
