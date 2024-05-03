package net.chrisarnold.tabletopcharactermanager;

import java.io.Serializable;

//Needs to implement Serializable to serialize for saving to file
public class Weapon implements Serializable {
    //Weapon attributes
    String wpnName;
    int wpnDieSize;
    int wpnDieAmount;
    int wpnModAttack;
    int wpnModDamage;
    int wpnModAttackTotal;
    int wpnModDamageTotal;
    boolean isFinesse;
    boolean isMissile;
    boolean isTwoHanded;

    //Constructor. Uses a few if elses to decide what damage statistics need to be, using DnD's esoteric rules
    public Weapon(PlayerCharacter c, int dieSize, int dieAmount,int modAttack, int modDamage, String wn, boolean finesse, boolean missile, boolean handed){
        wpnName = wn;
        wpnDieSize = dieSize;
        wpnDieAmount = dieAmount;
        isFinesse = finesse;
        isMissile = missile;
        isTwoHanded = handed;
        wpnModAttack = modAttack;
        wpnModDamage = modDamage;
        wpnModAttackTotal = wpnModAttack;
        wpnModDamageTotal = wpnModDamage;
        //Finesse and missile weapons to dexterity to attack, everything else uses strength
        if (isFinesse || isMissile){
            wpnModAttackTotal += c.dexMod;
        } else {
            wpnModAttackTotal += c.strMod;
        }
        //Missile weapon's don't get strength to damage, all else does
        if (isMissile){
            wpnModDamageTotal = modDamage;
        //Using two hands increases strength to damage, if strength is positive
        } else if (isTwoHanded) {
            if (c.strMod < 0) {
                wpnModDamageTotal = modDamage + c.strMod;
            } else {
                //Getting a double and casting to int, also rounding down
                double damage = modDamage + (c.strMod * 1.5);
                wpnModDamageTotal = (int)Math.floor(damage);
            }
        } else {
            wpnModDamageTotal = modDamage + c.strMod;
        }
    }

    //Rolls dice for attack roll. Rolls a number between 1 and 20 and adds modifiers
    public int attack(){
        int roll = (int)(Math.random()*20)+1;
        return roll + wpnModAttackTotal;

    }

    //Rolls dice for damage roll. Rolls a number of dice dependent on the weapon and adds modifiers.
    public int damage(){
        int damage = wpnModDamageTotal;
        for (int i = 0; i< wpnDieAmount; i++){
            int roll = (int)(Math.random()*wpnDieSize)+1;
            damage+= roll;
        }
        return damage;
    }
}
