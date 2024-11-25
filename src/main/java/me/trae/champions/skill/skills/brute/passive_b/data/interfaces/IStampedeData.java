package me.trae.champions.skill.skills.brute.passive_b.data.interfaces;

public interface IStampedeData {

    int getAmplifier();

    void setAmplifier(final int amplifier);

    boolean hasAmplifier();

    long getLastUpdated();

    void updateLastUpdated();
}