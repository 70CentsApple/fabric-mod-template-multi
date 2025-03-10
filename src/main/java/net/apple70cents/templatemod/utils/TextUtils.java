package net.apple70cents.templatemod.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.text.*;

import java.util.Map;
import java.util.regex.Pattern;

//#if MC>=12005
import net.minecraft.registry.BuiltinRegistries;
//#endif

/**
 * @author 70CentsApple
 */
public class TextUtils {
    public static final String PREFIX = "key.template_mod.";

    public static Text literal(String str) {
        //#if MC>=11900
        return Text.literal(str);
        //#else
        //$$return new LiteralText(str);
        //#endif
    }

    public static Text transWithPrefix(String str, String prefix) {
        //#if MC>=11900
        return Text.translatable(prefix + str);
        //#else
        //$$return new TranslatableText(prefix + str);
        //#endif
    }

    public static Text transWithPrefix(String str, String prefix, Object... args) {
        //#if MC>=11900
        return Text.translatable(prefix + str, args);
        //#else
        //$$return new TranslatableText(prefix + str, args);
        //#endif
    }

    public static Text trans(String str, Object... args) {
        return of(transWithPrefix(str, PREFIX, args).getString().strip());
    }

    public static Text trans(String str) {
        return of(transWithPrefix(str, PREFIX).getString().strip());
    }

    public static Text of(String str) {
        return Text.of(str);
    }

    public static Text empty() {
        //#if MC>=11900
        return Text.empty();
        //#else
        //$$return of("");
        //#endif
    }

    /**
     * removes color codes in the string
     *
     * @param str the string
     * @return string with no color codes
     */
    public static String wash(String str) {
        return Pattern.compile("§.").matcher(str).replaceAll("");
    }

    public static String escapeColorCodes(String str) {
        return str.replace('&', '§').replace("\\§", "&");
    }

    public static String backEscapeColorCodes(String str) {
        return str.replace('§', '&');
    }

    /**
     * replace a {@link MutableText}
     *
     * @param text      the text
     * @param oldString old string
     * @param newString new string
     * @return text after replacement
     */
    public static MutableText replaceText(MutableText text, String oldString, String newString) {
        //#if MC>=12005
        JsonElement jsonElement = new Text.Serializer(BuiltinRegistries.createWrapperLookup()).serialize(text, null, null);
        //#else
        //$$ JsonElement jsonElement = Text.Serialization.toJsonTree(text);
        //#endif
        replaceFieldValue(jsonElement, oldString, newString);
        //#if MC>=12005
        return new Text.Serializer(BuiltinRegistries.createWrapperLookup()).deserialize(jsonElement, null, null);
        //#else
        //$$ return Text.Serialization.fromJsonTree(jsonElement);
        //#endif
    }

    private static void replaceFieldValue(JsonElement jsonElement, String oldValue, String newValue) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> ele : jsonObject.entrySet()) {
                String key = ele.getKey();
                JsonElement value = ele.getValue();
                if (value.isJsonPrimitive() && value.getAsString().contains(oldValue)) {
                    jsonObject.addProperty(key, value.getAsString().replace(oldValue, newValue));
                } else {
                    replaceFieldValue(value, oldValue, newValue);
                }
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                replaceFieldValue(element, oldValue, newValue);
            }
        }
    }
}
