package me.trae.champions.skill.skills.ranger.passive_b.data.interfaces;

public interface IHuntersThrill {

    int getCharges();

    void addCharge();

    long getLastHit();

    void updateLastHit();
}