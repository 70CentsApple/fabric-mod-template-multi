package net.apple70cents.templatemod.utils;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static net.apple70cents.templatemod.TemplateMod.CONFIG;
import static net.apple70cents.templatemod.TemplateMod.DEFAULT_CONFIG;
import static net.apple70cents.templatemod.utils.TextUtils.trans;

/**
 * @author 70CentsApple
 */
public class ConfigScreenUtils {
    public static Text getTooltip(String key, String variableType) {
        return getTooltip(key, variableType, DEFAULT_CONFIG.get(key));
    }

    public static Text getTooltip(String key, String variableType, Object defaultVal) {
        boolean isNull = (defaultVal == null || defaultVal.toString().isBlank());
        String defaultValue = isNull ? "NULL" : defaultVal.toString();
        // check if F3+H is on
        if (MinecraftClient.getInstance().options.advancedItemTooltips) {
            try {
                if (variableType.endsWith("List")) {
                    if (!((List<?>) DEFAULT_CONFIG.get(key)).isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("[");
                        for (int i = 0; i < ((List<?>) DEFAULT_CONFIG.get(key)).size(); i++) {
                            String ele = ((List<?>) DEFAULT_CONFIG.get(key)).get(i).toString();
                            // if this is not the first element, we add a comma to the front
                            if (i != 0) sb.append(",");
                            // check if the list's type is raw string
                            if ("StringList".equals(variableType)) {
                                sb.append("\n  §r§f" + ele + "§r§7");
                            } else {
                                // we need to do pretty-printing further
                                sb.append("\n  {");
                                String[] keyAndValuePairs = ele.substring(1, ele.length() - 1).split(", ");
                                for (int j = 0; j < keyAndValuePairs.length; j++) {
                                    // if (j != 0) sb.append(",");
                                    String ele2 = keyAndValuePairs[j];
                                    int idx = ele2.indexOf("=");
                                    sb.append("\n    §e" + ele2.substring(0, idx) + "§r§7 = §f" + ele2.substring(idx + 1) + "§r§7");
                                }
                                sb.append("\n  }");
                            }
                        }
                        sb.append("\n]");
                        defaultValue = sb.toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Text defaults = ((MutableText) TextUtils.trans("gui.defaultValue", defaultValue)).formatted(Formatting.GRAY);

            Text keyName = ((MutableText) TextUtils.of(key)).formatted(Formatting.GOLD);
            Text main = ((MutableText) trans(key + ".@Tooltip")).formatted(Formatting.WHITE);
            Text type = ((MutableText) TextUtils.trans("gui.variableType", variableType)).formatted(Formatting.GRAY);
            MutableText tooltip = (MutableText) TextUtils.empty();
            tooltip.append(keyName).append("§r\n").append(main).append("§r\n").append(type).append("§r\n")
                   .append(defaults);
            return tooltip;
        } else {
            return trans(key + ".@Tooltip");
        }
    }

    // the `args` are only for `min` and `max` value for int sliders (recently)
    public static TooltipListEntry getEntryBuilder(ConfigEntryBuilder eb, String type, String key, int... args) {
        Text tooltip = getTooltip(key, type);
        switch (type) {
            case "boolean":
                return eb.startBooleanToggle(trans(key), (boolean) CONFIG.get(key))
                         .setDefaultValue((boolean) DEFAULT_CONFIG.get(key)).setTooltip(tooltip)
                         .setSaveConsumer(v -> CONFIG.set(key, v)).build();
            case "String":
                return eb.startStrField(trans(key), (String) CONFIG.get(key))
                         .setDefaultValue((String) DEFAULT_CONFIG.get(key)).setTooltip(tooltip)
                         .setSaveConsumer(v -> CONFIG.set(key, v)).build();
            case "intSlider":
                return eb.startIntSlider(trans(key), ((Number) CONFIG.get(key)).intValue(), args[0], args[1])
                         .setDefaultValue(((Number) DEFAULT_CONFIG.get(key)).intValue()).setTooltip(tooltip)
                         .setSaveConsumer(v -> CONFIG.set(key, (Number) v)).build();
            case "keycode":
                return eb.startKeyCodeField(trans(key), InputUtil.fromTranslationKey((String) CONFIG.get(key)))
                         .setDefaultValue(InputUtil.fromTranslationKey((String) DEFAULT_CONFIG.get(key)))
                         .setTooltip(tooltip)
                         //#if MC>=11800
                        .setKeySaveConsumer
                        //#elseif MC>=11700
                        // In MC 1.17.X, we use ClothConfig v5, where the discontinued version uses `setSaveConsumer()` method.
                        //$$ .setSaveConsumer
                        //#else
                        //$$ .setKeySaveConsumer
                        //#endif
                                (keybind -> CONFIG.set(key, keybind.getTranslationKey())).build();
            case "StringList":
                return eb.startStrList(trans(key), (List<String>) CONFIG.get(key))
                         .setDefaultValue((List<String>) DEFAULT_CONFIG.get(key)).setTooltip(tooltip)
                         .setSaveConsumer(v -> CONFIG.set(key, v)).build();
            default:
                return null;
        }
    }

    public static final Function<String, Optional<Text>> REGEX_COMPILE_ERROR_SUPPLIER = (v) -> {
        try {
            Pattern.compile(v);
            return Optional.empty();
        } catch (PatternSyntaxException e) {
            return Optional.of(TextUtils.of(e.getDescription()));
        }
    };

    public static final Function<String, Optional<Text>> REGEX_COMPILE_ERROR_SUPPLIER_ALLOW_STAR = (v) -> {
        if ("*".equals(v)) {
            return Optional.empty();
        }
        try {
            Pattern.compile(v);
            return Optional.empty();
        } catch (PatternSyntaxException e) {
            return Optional.of(TextUtils.of(e.getDescription()));
        }
    };
}
