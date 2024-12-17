package me.trae.champions.skill.skills.assassin.axe.data.interfaces;

public interface IFlashData {

    long getLastUpdated();

    void updateLastUpdated();

    int getCharges();

    void setCharges(final int charges);
}