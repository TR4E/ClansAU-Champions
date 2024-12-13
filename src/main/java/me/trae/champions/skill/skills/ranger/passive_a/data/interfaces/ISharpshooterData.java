package me.trae.champions.skill.skills.ranger.passive_a.data.interfaces;

public interface ISharpshooterData {

    int getCharges();

    void addCharge();

    long getLastHit();

    void updateLastHit();
}