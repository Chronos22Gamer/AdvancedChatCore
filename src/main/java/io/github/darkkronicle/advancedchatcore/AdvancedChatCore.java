package io.github.darkkronicle.advancedchatcore;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import io.github.darkkronicle.advancedchatcore.util.SyncTaskQueue;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class AdvancedChatCore implements ClientModInitializer {

    public static final String MOD_ID = "advancedchatcore";

    public static final boolean FORWARD_TO_HUD = true;

    private final static Random RANDOM = new Random();

    private final static String[] RANDOM_STRINGS = {"yes", "maybe", "no", "potentially", "hello", "goodbye", "tail", "pop", "water", "headphone", "head", "scissor", "paper", "burger", "clock", "peg", "speaker", "computer", "mouse", "mat", "keyboard", "soda", "mac", "cheese", "home", "pillow", "couch", "drums", "drumstick", "math", "Euler", "Chronos", "DarkKronicle", "Kron", "pain", "suffer", "bridge", "Annevdl", "MaLiLib", "pog", "music", "pants", "glockenspiel", "marimba", "chimes", "vibraphone", "vibe", "snare", "monkeymode", "shades", "cactus", "shaker", "pit", "band", "percussion", "foot", "leg", "Kurt", "bruh", "gamer", "gaming"};

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        KeyBinding keyBinding = new KeyBinding(
                "advancedchat.key.openlog",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                "advancedchat.category.keys"
        );
        KeyBindingHelper.registerKeyBinding(keyBinding);
        ClientTickEvents.START_CLIENT_TICK.register(s -> {
            if (keyBinding.wasPressed()) {
                GuiBase.openGui(GuiConfigHandler.getInstance().getDefaultScreen());
            }
            SyncTaskQueue.getInstance().update(s.inGameHud.getTicks());
        });
    }

    /**
     * Get's a resource from src/resources. Works in a emulated environment.
     * @param path Path from the resources to get
     * @return Stream of the resource
     *
     * @throws URISyntaxException If the resource doesn't exist
     * @throws IOException Can't be opened
     */
    public static InputStream getResource(String path) throws URISyntaxException, IOException {
        URI uri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();
        if(uri.getScheme().contains("jar")){
            // Not IDE
            //jar.toString() begins with file:
            //i want to trim it out...
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        } else{
            // IDE
            return new FileInputStream(Paths.get(uri).toFile());

        }
    }

    /**
     * Get's a random string.
     * @return Random generated string.
     */
    public static String getRandomString() {
        return RANDOM_STRINGS[RANDOM.nextInt(RANDOM_STRINGS.length)];
    }

}
