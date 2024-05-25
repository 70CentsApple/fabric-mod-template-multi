package net.apple70cents.templatemod;

import net.apple70cents.templatemod.config.ConfigStorage;
import net.apple70cents.templatemod.utils.LoggerUtils;
import net.fabricmc.api.ModInitializer;

/**
 * @author 70CentsApple
 */
public class TemplateMod implements ModInitializer {

    public final static ConfigStorage DEFAULT_CONFIG = new ConfigStorage(true);
    public static ConfigStorage CONFIG;

    @Override
    public void onInitialize() {
        LoggerUtils.init();

        if (!ConfigStorage.configFileExists()) {
            // if the config file doesn't exist, create a new one with the default settings.
            DEFAULT_CONFIG.save();
        }

        CONFIG = new ConfigStorage(false).withDefault(DEFAULT_CONFIG.getHashmap());

        LoggerUtils.info("Successfully started Template Mod");
    }
}
