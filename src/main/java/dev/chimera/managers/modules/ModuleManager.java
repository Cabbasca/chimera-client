package dev.chimera.managers.modules;

import dev.chimera.managers.modules.combat.KillAuraModule;
import dev.chimera.managers.modules.common.ClickGUIModule;
import dev.chimera.managers.modules.common.FarmAuraModule;
import dev.chimera.managers.modules.misc.TestModule;
import dev.chimera.managers.modules.player.FlightModule;
import dev.chimera.managers.modules.player.NoFallModule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Constructor;

public class ModuleManager {
    @Getter
    private static final List<AbstractModule> modules = new ArrayList<>();

    public static void initializeModules() {
        registerModule(TestModule.class);
        registerModule(KillAuraModule.class);
        registerModule(ClickGUIModule.class);
        registerModule(FarmAuraModule.class);
        registerModule(FlightModule.class);
        registerModule(NoFallModule.class);
        ModuleManager.findModule(TestModule.class).setEnabled(true);
    }


    public static void registerModule(Class<? extends AbstractModule> clazz) {
        // Def not stolen from coffee
        AbstractModule instance = null;
        for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
            if (declaredConstructor.getParameterCount() != 0) {
                throw new IllegalArgumentException(clazz.getName() + " has invalid constructor: expected " + clazz.getName() + "(), got " + declaredConstructor);
            }
            try {
                instance = (AbstractModule) declaredConstructor.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to make instance of " + clazz.getName(), e);
            }
        }
        if (instance == null) {
            throw new IllegalArgumentException("Failed to make instance of " + clazz.getName());
        }

        modules.add(instance);
    }

    public static List<AbstractModule> getEnabledModuleList() {
        return modules.stream()
                .filter(AbstractModule::isEnabled)
                .collect(Collectors.toList());
    }

    public static AbstractModule findModule(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractModule> T findModule(Class<T> clazz) {
        return (T) modules.stream()
                .filter(m -> m.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }
}

