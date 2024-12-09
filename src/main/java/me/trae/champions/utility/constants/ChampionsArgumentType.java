package me.trae.champions.utility.constants;

import me.trae.api.champions.role.Role;
import me.trae.champions.Champions;
import me.trae.champions.role.RoleManager;
import me.trae.core.utility.UtilPlugin;
import me.trae.core.utility.constants.CoreArgumentType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChampionsArgumentType {

    public static Function<String, List<String>> ROLES = (arg) -> CoreArgumentType.CUSTOM.apply(UtilPlugin.getInstance(Champions.class).getManagerByClass(RoleManager.class).getModulesByClass(Role.class).stream().map(Role::getName).collect(Collectors.toList()), arg);
}