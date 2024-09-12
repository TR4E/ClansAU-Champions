package me.trae.champions.skill.interfaces;

import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface ISkill<D extends SkillData> {

    SkillType getType();

    Class<D> getClassOfData();

    Map<UUID, D> getUsers();

    void addUser(final D data);

    void removeUser(final Player player);

    D getUserByUUID(final UUID uuid);

    D getUserByPlayer(final Player player);

    boolean isUserByUUID(final UUID uuid);

    boolean isUserByPlayer(final Player player);

    int getLevel(final Player player);

    void reset(final Player player);

    void onExpire(final Player player, final D data);
}