package io.github.darkkronicle.advancedchatcore.chat;

import io.github.darkkronicle.advancedchatcore.AdvancedChatCore;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageProcessor;
import io.github.darkkronicle.advancedchatcore.mixin.MixinChatHudInvoker;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Environment(EnvType.CLIENT)
public class ChatHistoryProcessor implements IMessageProcessor {

    private static boolean sendToHud(Text text) {
        if (AdvancedChatCore.FORWARD_TO_HUD) {
            ((MixinChatHudInvoker) MinecraftClient.getInstance().inGameHud.getChatHud()).invokeAddMessage(text, 0, MinecraftClient.getInstance().inGameHud.getTicks(), false);
            return true;
        }
        return false;
    }

    @Override
    public boolean process(FluidText text, @Nullable FluidText unfiltered) {
        if (unfiltered == null) {
            unfiltered = text;
        }
        ColorUtil.SimpleColor backcolor = text.getBackgroundColor();

        // Put the time in
        LocalTime time = LocalTime.now();
        boolean showtime = ConfigStorage.General.SHOW_TIME.config.getBooleanValue();
        // Store original so we can get stuff without the time
        Text original = text.copy();
        if (showtime) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(ConfigStorage.General.TIME_FORMAT.config.getStringValue());
            text.addTime(format, time);
        }

        int width = 0;
        // Find player
        MessageOwner player = SearchUtils.getAuthor(MinecraftClient.getInstance().getNetworkHandler(), unfiltered.getString());
        ChatMessage line = ChatMessage.builder()
                .displayText(text)
                .originalText(original)
                .owner(player)
                .id(0)
                .width(width)
                .creationTick(MinecraftClient.getInstance().inGameHud.getTicks())
                .time(time)
                .background(backcolor)
                .build();
        if (ChatHistory.getInstance().add(line)) {
            sendToHud(line.getDisplayText());
        }
        return true;
    }

}
