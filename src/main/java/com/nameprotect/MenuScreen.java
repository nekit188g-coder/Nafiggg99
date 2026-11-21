package com.nameprotect;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Меню на Right Shift. Тексти — картинки з шрифтом arialmt.ttf. */
public class MenuScreen extends Screen {
    private static final float S = 0.2f;   // масштаб текстових картинок
    private int mx, my;
    private final List<Item> items = new ArrayList<>();

    private record Item(int x, int y, int w, int h, Runnable action) {}

    public MenuScreen() {
        super(Component.literal("LoLHelper"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int bg(int x, int y, int w, int h, boolean selected) {
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        return selected ? 0xFF5B3FC4 : (hover ? 0xFF2E2E3E : 0xFF1C1C26);
    }

    /** Кнопка з текстом зліва (і статусом ON/OFF справа). */
    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean selected, String status, Runnable action) {
        g.fill(x, y, x + w, y + h, bg(x, y, w, h, selected));
        int ty = y + Math.max(0, (h - (int) (Labels.H * S)) / 2);
        Labels.draw(g, label, x + 4, ty, S);
        if (status != null) {
            Labels.draw(g, status, x + w - (int) (Labels.width(status) * S) - 4, ty, S);
        }
        items.add(new Item(x, y, w, h, action));
    }

    private void toggle(GuiGraphics g, int x, int y, int w, String label, boolean on, Runnable action) {
        button(g, x, y, w, 11, label, on, on ? "on" : "off", action);
    }

    /** Мала кнопка з текстом по центру. */
    private void small(GuiGraphics g, int x, int y, int w, int h, String label, boolean selected, Runnable action) {
        g.fill(x, y, x + w, y + h, bg(x, y, w, h, selected));
        int ty = y + Math.max(0, (h - (int) (Labels.H * S)) / 2);
        Labels.draw(g, label, x + (w - (int) (Labels.width(label) * S)) / 2, ty, S);
        items.add(new Item(x, y, w, h, action));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        mx = mouseX;
        my = mouseY;
        items.clear();

        g.fill(0, 0, this.width, this.height, 0x66000000);

        final int pw = 330, ph = 150;
        int x0 = (this.width - pw) / 2;
        int y0 = (this.height - ph) / 2;
        g.fill(x0, y0, x0 + pw, y0 + ph, 0xF0121218);
        g.fill(x0, y0, x0 + pw, y0 + 2, 0xFF8B5CF6);
        Labels.draw(g, "wm", x0 + 8, y0 + 6, 0.3f);

        // ---------- колонка A: TargetESP + режими
        int ax = x0 + 8, aw = 100;
        toggle(g, ax, y0 + 22, aw, "targetesp", Config.targetEsp, () -> Config.targetEsp = !Config.targetEsp);
        Labels.draw(g, "mode", ax + 1, y0 + 36, S);
        String[] modes = {"m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8"};
        for (int i = 0; i < modes.length; i++) {
            final int idx = i;
            button(g, ax, y0 + 48 + i * 11, aw, 10, modes[i], Config.mode == i, null, () -> Config.mode = idx);
        }

        // ---------- колонка B: вигляд цілі + ефекти
        int bx = x0 + 116, bw = 100;
        Labels.draw(g, "size", bx + 1, y0 + 24, S);
        String[] sz = {"s", "m", "l"};
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            small(g, bx + i * 34, y0 + 35, 32, 10, sz[i], Config.size == i, () -> Config.size = idx);
        }
        toggle(g, bx, y0 + 50, bw, "spin", Config.spin, () -> Config.spin = !Config.spin);
        toggle(g, bx, y0 + 66, bw, "killeffect", Config.killEffect, () -> Config.killEffect = !Config.killEffect);
        toggle(g, bx, y0 + 82, bw, "killsound", Config.killSound, () -> Config.killSound = !Config.killSound);
        toggle(g, bx, y0 + 98, bw, "nameprotect", Config.nameProtect, () -> Config.nameProtect = !Config.nameProtect);

        // ---------- колонка C: Hitsounds + гучність + Watermark
        int cx = x0 + 224, cw = 100;
        Labels.draw(g, "hitsounds", cx + 1, y0 + 24, S);
        String[] hs = {"h0", "h1", "h2"};
        for (int i = 0; i < hs.length; i++) {
            final int idx = i;
            button(g, cx, y0 + 35 + i * 11, cw, 10, hs[i], Config.hitSound == i, null, () -> {
                Config.hitSound = idx;
                HitSounds.play(idx, 1.0f);
            });
        }
        Labels.draw(g, "volume", cx + 1, y0 + 74, S);
        String[] vol = {"v25", "v50", "v100"};
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            small(g, cx + i * 34, y0 + 85, 32, 10, vol[i], Config.volume == i, () -> {
                Config.volume = idx;
                HitSounds.play(Config.hitSound, 1.0f);
            });
        }
        toggle(g, cx, y0 + 100, cw, "watermark", Config.watermark, () -> Config.watermark = !Config.watermark);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (Item it : items) {
            if (mx >= it.x() && mx < it.x() + it.w() && my >= it.y() && my < it.y() + it.h()) {
                it.action().run();
                Config.save();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }
}
