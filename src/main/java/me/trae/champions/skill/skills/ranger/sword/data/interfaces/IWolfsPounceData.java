package me.trae.champions.skill.skills.ranger.sword.data.interfaces;

public interface IWolfsPounceData {

    long getLastUpdated();

    void updateLastUpdated();

    int getCharges();

    void setCharges(final int charges);

    boolean isPounced();

    void setPounced(final boolean pounced);
}