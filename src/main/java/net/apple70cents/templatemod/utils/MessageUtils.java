package net.apple70cents.templatemod.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 70CentsApple
 */
public class MessageUtils {
    public static void sendToActionbar(Text text) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(text, true);
        }
    }

    public static void sendToNonPublicChat(Text text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }

    public static void sendToPublicChat(String text) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        //#if MC>=11900
        String text2 = StringHelper.truncateChat(StringUtils.normalizeSpace(text.trim()));
        if (!text2.isEmpty()) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(text);
            if (text2.startsWith("/")) {
                player.networkHandler.sendChatCommand(text2.substring(1));
            } else {
                player.networkHandler.sendChatMessage(text2);
            }
        }
        //#else
        //$$ player.sendChatMessage(text);
        //#endif
    }
}
