package me.trae.champions.skill.data;

import me.trae.champions.skill.data.interfaces.ISkillData;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkillData implements ISkillData {

    private final UUID uuid;

    private long systemTime, duration;

    public SkillData(final Player player, final long duration) {
        this.uuid = player.getUniqueId();
        this.systemTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public SkillData(final Player player) {
        this(player, -1L);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
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