package me.alpha432.oyvey.features.gui;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.gui.items.Item;
import me.alpha432.oyvey.features.gui.items.buttons.ModuleButton;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class OyVeyGui extends Screen {
    private static OyVeyGui INSTANCE;
    private static Color colorClipboard = null;

    // Duck Inc branding
    private static final String DUCK_WATERMARK = "\uD83E\uDD86 Duck Inc";  // 🦆 Duck Inc

    // Duck theme colors
    private static final Color DUCK_BG       = new Color(0,   0,   0,   120); // same dark overlay
    private static final Color DUCK_ACCENT   = new Color(255, 200, 0,   180); // duck yellow
    private static final Color DUCK_ACCENT2  = new Color(230, 120, 0,   200); // duck orange

    static {
        INSTANCE = new OyVeyGui();
    }

    private final ArrayList<Widget> widgets = new ArrayList<>();

    public OyVeyGui() {
        super(Component.literal(DUCK_WATERMARK));  // title bar now says 🦆 Duck Inc
        setInstance();
        load();
    }

    public static OyVeyGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OyVeyGui();
        }
        return INSTANCE;
    }

    public static OyVeyGui getClickGui() {
        return OyVeyGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (Module.Category category : OyVey.moduleManager.getCategories()) {
            if (category == Module.Category.HUD) continue;
            // Prefix each panel header with the duck emoji
            Widget panel = new Widget("\uD83E\uDD86 " + category.getName(), x += 90, 4, true);
            OyVey.moduleManager.stream()
                    .filter(m -> m.getCategory() == category && !m.hidden)
                    .map(ModuleButton::new)
                    .forEach(panel::addButton);
            this.widgets.add(panel);
        }
        this.widgets.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Item.context = context;

        // Duck-tinted background overlay (same structure, yellow-tinted instead of pure black)
        context.fill(0, 0, context.guiWidth(), context.guiHeight(), DUCK_BG.hashCode());

        // Draw a subtle duck-yellow gradient strip across the top as a header bar
        context.fill(0, 0, context.guiWidth(), 3, DUCK_ACCENT2.hashCode());
        context.fill(0, 0, context.guiWidth(), 1, DUCK_ACCENT.hashCode());

        this.widgets.forEach(components -> components.drawScreen(context, mouseX, mouseY, delta));

        // Draw "🦆 Duck Inc" watermark in the bottom-right corner
        int wmX = context.guiWidth()  - mc.font.width(DUCK_WATERMARK) - 4;
        int wmY = context.guiHeight() - mc.font.lineHeight - 4;
        context.fill(wmX - 3, wmY - 2, wmX + mc.font.width(DUCK_WATERMARK) + 3, wmY + mc.font.lineHeight + 2,
                new Color(0, 0, 0, 140).hashCode());
        context.drawString(mc.font, DUCK_WATERMARK, wmX, wmY, DUCK_ACCENT.hashCode());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        this.widgets.forEach(components -> components.mouseClicked((int) click.x(), (int) click.y(), click.button()));
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        this.widgets.forEach(components -> components.mouseReleased((int) click.x(), (int) click.y(), click.button()));
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            this.widgets.forEach(component -> component.setY(component.getY() - 10));
        } else if (verticalAmount > 0) {
            this.widgets.forEach(component -> component.setY(component.getY() + 10));
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        this.widgets.forEach(component -> component.onKeyPressed(input.input()));
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        this.widgets.forEach(component -> component.onKeyTyped(input.codepointAsString(), input.modifiers()));
        return super.charTyped(input);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    } // ignore 1.21.8 blur thing

    public final ArrayList<Widget> getComponents() {
        return this.widgets;
    }

    public int getTextOffset() {
        return -6;
    }

    public static Color getColorClipboard() {
        return colorClipboard;
    }

    public static void setColorClipboard(Color color) {
        colorClipboard = color;
    }
}
