package me.trae.champions.skill.skills.brute.sword.data.interfaces;

public interface IFleshHookData {

    long getDelay();

    long getLastUpdated();

    void updateLastUpdated();

    int getCharges();

    void setCharges(final int charges);
}