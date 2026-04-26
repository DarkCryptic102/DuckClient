package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.ClientEvent;
import me.alpha432.oyvey.event.system.Subscribe;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import org.lwjgl.glfw.GLFW;
import java.awt.*;
import static me.alpha432.oyvey.features.commands.MessageSignatures.GENERAL;

public class ClickGuiModule extends Module {
    private static ClickGuiModule INSTANCE;

    public final Setting<String> prefix = str("Prefix", ".");

    // Duck theme: yellow/orange palette instead of blue
    public final Setting<Color> color = color("Color", 255, 200, 0, 180);         // duck yellow
    public final Setting<Color> topColor = color("TopColor", 230, 120, 0, 240);   // duck orange (beak/feet)

    public final Setting<Boolean> rainbow = bool("Rainbow", false);
    public final Setting<Integer> rainbowHue = num("Delay", 240, 0, 600);
    public final Setting<Float> rainbowBrightness = num("Brightness", 150.0f, 1.0f, 255.0f);
    public final Setting<Float> rainbowSaturation = num("Saturation", 150.0f, 1.0f, 255.0f);

    // Duck Inc branding
    private static final String DUCK_TITLE = "\uD83E\uDD86 Duck Inc";  // 🦆 Duck Inc

    public ClickGuiModule() {
        super("ClickGui", "Opens the Duck Inc ClickGui", Module.Category.CLIENT);
        setBind(GLFW.GLFW_KEY_RIGHT_SHIFT);
        rainbowHue.setVisibility(v -> rainbow.getValue());
        rainbowBrightness.setVisibility(v -> rainbow.getValue());
        rainbowSaturation.setVisibility(v -> rainbow.getValue());
        INSTANCE = this;
    }

    @Subscribe
    public void onSettingChange(ClientEvent event) {
        if (event.getType() == ClientEvent.Type.SETTING_UPDATE && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                OyVey.commandManager.setCommandPrefix(this.prefix.getPlannedValue());
                Command.sendMessage(DUCK_TITLE + " | Prefix set to {global} %s", GENERAL,
                        OyVey.commandManager.getCommandPrefix());
            }
            if (event.getSetting().equals(this.color)) {
                OyVey.colorManager.setColor(this.color.getPlannedValue());
            }
        }
    }

    @Override
    public void onEnable() {
        if (nullCheck()) {
            return;
        }
        mc.setScreen(OyVeyGui.getClickGui());
    }

    @Override
    public void onLoad() {
        OyVey.colorManager.setColor(this.color.getValue());
        OyVey.commandManager.setCommandPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGuiModule.mc.screen instanceof OyVeyGui)) {
            this.disable();
        }
    }

    public static ClickGuiModule getInstance() {
        return INSTANCE;
    }

    /** Helper: returns the Duck Inc branding string for use in GUI renderers */
    public static String getDuckTitle() {
        return DUCK_TITLE;
    }
}
