package me.alpha432.oyvey.features.modules.hud;
 
import me.alpha432.oyvey.event.impl.render.Render2DEvent;
import me.alpha432.oyvey.features.modules.client.HudModule;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.BuildConfig;
import me.alpha432.oyvey.util.TextUtil;
 
public class WatermarkHudModule extends HudModule {
    public final Setting<String> text = str("Text", "Duck Inc");
    public final Setting<Boolean> fullVersion = new Setting<>("FullVersion", false);
 
    // Duck drawn with ASCII art using unicode chars
    private static final String DUCK = "\uD83E\uDD86"; // 🦆 emoji
 
    public WatermarkHudModule() {
        super("Watermark", "Display watermark", 100, 10);
        if (BuildConfig.USING_GIT) {
            register(fullVersion);
        }
    }
 
    @Override
    protected void render(Render2DEvent e) {
        super.render(e);
 
        String watermarkString = DUCK + " {global} %s {} %s";
        if (fullVersion.getValue() && BuildConfig.USING_GIT) {
            watermarkString += "/" + BuildConfig.BRANCH + "-" + BuildConfig.HASH;
        }
 
        // TextUtil.text() returns MutableComponent, not String
        net.minecraft.network.chat.MutableComponent rendered =
                TextUtil.text(watermarkString, text.getValue(), BuildConfig.VERSION);
 
        // Draw background pill for watermark
        int x = (int) getX();
        int y = (int) getY();
        int w = mc.font.width(rendered) + 6;
        int h = mc.font.lineHeight + 4;
 
        // Draw a simple dark background behind the watermark
        e.getContext().fill(x - 3, y - 2, x + w, y + h, 0xAA000000);
 
        // Draw the watermark text (with duck emoji prefix)
        e.getContext().drawString(mc.font, rendered, x, y, 0xFFFFD700); // gold color
 
        setWidth(w);
        setHeight(h);
    }
}
