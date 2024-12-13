package me.trae.champions.skill.types;

import me.trae.api.champions.role.Role;
import me.trae.api.damage.events.damage.CustomPostDamageEvent;
import me.trae.champions.skill.types.data.BowSkillData;
import me.trae.champions.skill.types.enums.PassiveSkillType;
import me.trae.champions.skill.types.interfaces.IPassiveBowSkill;
import me.trae.core.utility.UtilJava;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class PassiveBowSkill<R extends Role, D extends BowSkillData> extends PassiveSkill<R, D> implements IPassiveBowSkill<D> {

    public PassiveBowSkill(final R module, final PassiveSkillType passiveSkillType) {
        super(module, passiveSkillType);
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(UtilJava.matchlessObjectCast(this.getClassOfData(), new BowSkillData(player, level)));
    }

    @Override
    public void onFire(final Player player, final D data) {
    }

    @Override
    public void onHitByLocation(final Player player, final Location location, final D data) {
    }

    @Override
    public void onHitByEntity(final Player damager, final Entity damagee, final CustomPostDamageEvent event, final D data) {
    }

    @Override
    public void onUpdater(final Player player, final D data) {
    }
}