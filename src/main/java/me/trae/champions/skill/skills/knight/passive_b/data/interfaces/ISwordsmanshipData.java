package me.trae.champions.skill.skills.knight.passive_b.data.interfaces;

public interface ISwordsmanshipData {

    int getCharges();

    void setCharges(final int charges);

    boolean hasCharges();

    long getLastUpdated();

    void updateLastUpdated();
}