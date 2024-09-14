package me.trae.champions.skill.data.interfaces;

import me.trae.core.utility.components.*;

import java.util.UUID;

public interface ISkillData extends GetSystemTimeComponent, GetDurationComponent, SetDurationComponent, RemainingComponent, ExpiredComponent {

    UUID getUUID();

    int getLevel();
}