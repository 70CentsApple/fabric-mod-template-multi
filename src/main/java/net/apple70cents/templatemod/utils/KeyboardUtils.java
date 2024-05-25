package net.apple70cents.templatemod.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * @author 70CentsApple
 */
public class KeyboardUtils {
    /**
     * check if is a key is being pressed while the modifier key is pressed as well
     *
     * @param translationKey the key such as 'key.mouse.4'
     * @param modifier       the modifier
     * @param mode           the macro mode
     * @return success or not
     */
    public static boolean isKeyPressingWithModifier(String translationKey, String modifier, String mode) {
        // @formatter:off
        if (!"ALT".equalsIgnoreCase(modifier) && !"SHIFT".equalsIgnoreCase(modifier)
            && !"CTRL".equalsIgnoreCase(modifier) && !"NONE".equalsIgnoreCase(modifier)) {
            LoggerUtils.warn("Illegal modifier: " + modifier);
            modifier = "NONE";
        }
        // @formatter:on
        if (!"GREEDY".equalsIgnoreCase(modifier) && !"LAZY".equalsIgnoreCase(modifier)) {
            LoggerUtils.warn("Illegal pressing mode: " + mode);
            mode = "LAZY";
        }
        if (InputUtil.UNKNOWN_KEY.getTranslationKey().equals(translationKey)) {
            return false;
        }
        long handle = MinecraftClient.getInstance().getWindow().getHandle();
        InputUtil.Key key = InputUtil.fromTranslationKey(translationKey);
        int keyCode = key.getCode();

        // @formatter:off
        // This check is GREEDY, which means if `key` = D, `modifier` = Alt, it just cares whether these two keys are both activated.
        if (("ALT".equalsIgnoreCase(modifier) &&
                !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_ALT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT))
        ) || ("SHIFT".equalsIgnoreCase(modifier) &&
                !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT))
        ) || ("CTRL".equalsIgnoreCase(modifier) &&
                !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL))
        )) {
            return false;
        }
        // @formatter:on
        if ("LAZY".equalsIgnoreCase(mode)) {
            // Here we deal with LAZY mode if needed.
            // It is so stupid, but it works
            boolean lazyModePass;
            switch (modifier) {
                // @formatter:off
                case "NONE":
                    lazyModePass = !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_ALT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT));
                    break;
                case "SHIFT":
                    lazyModePass = !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_ALT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL));
                    break;
                case "ALT":
                    lazyModePass = !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT));
                    break;
                case "CTRL":
                    lazyModePass = !(InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_ALT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                            InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT));
                    break;
                default:
                    lazyModePass = true;
                // @formatter:on
            }
            lazyModePass = lazyModePass & !InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_F3);
            if (!lazyModePass) {
                return false;
            }
        }
        if (key.getCategory().equals(InputUtil.Type.KEYSYM)) {
            return InputUtil.isKeyPressed(handle, keyCode);
        } else if (key.getCategory().equals(InputUtil.Type.MOUSE)) {
            return GLFW.glfwGetMouseButton(handle, keyCode) == GLFW.GLFW_PRESS;
        }
        return false;
    }
}
