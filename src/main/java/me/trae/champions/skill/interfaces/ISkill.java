package me.trae.champions.skill.interfaces;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface ISkill<D extends SkillData> {

    SkillType getType();

    Class<D> getClassOfData();

    ConcurrentHashMap<UUID, D> getUsers();

    void addUser(final D data);

    void removeUser(final Player player);

    D getUserByUUID(final UUID uuid);

    D getUserByPlayer(final Player player);

    boolean isUserByUUID(final UUID uuid);

    boolean isUserByPlayer(final Player player);

    int getLevel(final Player player);

    String getDisplayName(final int level);

    String[] getDescription(final int level);

    void reset(final Player player);

    void onExpire(final Player player, final D data);
}