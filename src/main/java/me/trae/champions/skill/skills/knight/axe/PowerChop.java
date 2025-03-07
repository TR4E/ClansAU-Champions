package me.trae.champions.skill.skills.knight.axe;

import me.trae.api.damage.events.damage.CustomDamageEvent;
import me.trae.champions.role.types.Knight;
import me.trae.champions.skill.data.SkillData;
import me.trae.champions.skill.enums.SkillType;
import me.trae.champions.skill.types.ActiveSkill;
import me.trae.champions.skill.types.enums.ActiveSkillType;
import me.trae.core.config.annotations.ConfigInject;
import me.trae.core.utility.UtilMessage;
import me.trae.core.utility.UtilString;
import me.trae.core.utility.objects.SoundCreator;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Collections;

public class PowerChop extends ActiveSkill<Knight, SkillData> implements Listener {

    @ConfigInject(type = Float.class, path = "Energy", defaultValue = "25.0")
    private float energy;

    @ConfigInject(type = Long.class, path = "Recharge", defaultValue = "12_000")
    private long recharge;

    @ConfigInject(type = Long.class, path = "Prepare-Duration", defaultValue = "1_000")
    private long prepareDuration;

    public PowerChop(final Knight module) {
        super(module, ActiveSkillType.AXE);
    }

    @Override
    public Class<SkillData> getClassOfData() {
        return SkillData.class;
    }

    private double getDamage(final int level) {
        return level * 0.75D;
    }

    @Override
    public String[] getDescription(final int level) {
        return new String[]{
                "Right-Click with an Axe to Activate.",
                "",
                "Put more strength into your next axe attack,",
                UtilString.format("causing it to deal %s bonus damage.", this.getValueString(Double.class, this::getDamage, level)),
                "",
                "Attack must be made within",
                UtilString.format("%s of being used.", this.getValueString(Long.class, this.prepareDuration)),
                "",
                UtilString.pair("<gray>Recharge", UtilString.format("<green>%s", this.getRechargeString(level))),
                UtilString.pair("<gray>Energy", UtilString.format("<green>%s", this.getEnergyString(level)))
        };
    }

    @Override
    public void onActivate(final Player player, final int level) {
        this.addUser(new SkillData(player, level, this.prepareDuration));

        UtilMessage.simpleMessage(player, this.getModule().getName(), "You have prepared <green><var></green>.", Collections.singletonList(this.getDisplayName(level)));
    }

    @EventHandler
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!(event.getDamagee() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = event.getDamagerByClass(Player.class);

        if (!(SkillType.AXE.isItemStack(damager.getEquipment().getItemInHand()))) {
            return;
        }

        final SkillData data = this.getUserByPlayer(damager);
        if (data == null) {
            return;
        }

        final LivingEntity damagee = event.getDamageeByClass(LivingEntity.class);

        event.setDamage(event.getDamage() + this.getDamage(data.getLevel()));

        event.setReason(this.getDisplayName(data.getLevel()), 1000L);

        new SoundCreator(Sound.IRONGOLEM_HIT, 1.0F, 2.0F).play(damagee.getLocation());

        if (damagee instanceof Player) {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
            UtilMessage.simpleMessage(damagee, this.getModule().getName(), "<var> hit you with <green><var></green>.", Arrays.asList(event.getDamagerName(), this.getDisplayName(data.getLevel())));
        } else {
            UtilMessage.simpleMessage(damager, this.getModule().getName(), "You hit a <var> with <green><var></green>.", Arrays.asList(event.getDamageeName(), this.getDisplayName(data.getLevel())));
        }

        this.removeUser(damager);
    }

    @Override
    public float getEnergy(final int level) {
        final int value = (level - 1) * 2;

        return this.energy - value;
    }

    @Override
    public long getRecharge(final int level) {
        final int value = (int) ((level - 1) * 1.5D);

        return this.recharge - (value * 1000L);
    }
}