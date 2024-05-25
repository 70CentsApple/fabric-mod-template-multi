package net.apple70cents.templatemod.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author 70CentsApple
 */
@Environment(EnvType.CLIENT)
public class ModMenuScreen implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreenGenerator.getConfigBuilder().setParentScreen(parent).build();
    }
}