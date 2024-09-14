package me.trae.champions.skill.data;

import me.trae.champions.skill.data.interfaces.ISkillData;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkillData implements ISkillData {

    private final UUID uuid;
    private final int level;

    private long systemTime, duration;

    public SkillData(final Player player, final int level, final long duration) {
        this.uuid = player.getUniqueId();
        this.level = level;
        this.systemTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public SkillData(final Player player, final int level) {
        this(player, level, -1L);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public long getSystemTime() {
        return this.systemTime;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(final long duration) {
        this.systemTime = System.currentTimeMillis();
        this.duration = duration;
    }
}