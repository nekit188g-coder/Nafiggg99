package com.nameprotect;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class Config {
    public static boolean targetEsp = true;
    public static int mode = 0;          // 0..7 = Radar..Sardelki
    public static int size = 1;          // 0 = S, 1 = M, 2 = L
    public static boolean spin = true;
    public static int hitSound = 0;      // 0 = Off, 1 = Undertale, 2 = Bubble
    public static int volume = 2;        // 0 = 25%, 1 = 50%, 2 = 100%
    public static boolean killEffect = true;
    public static boolean killSound = true;
    public static boolean nameProtect = true;
    public static boolean watermark = true;

    private Config() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("nameprotect.properties");
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static boolean b(Properties p, String k, boolean d) {
        return Boolean.parseBoolean(p.getProperty(k, String.valueOf(d)));
    }

    private static int i(Properties p, String k, int d, int lo, int hi) {
        try {
            return clamp(Integer.parseInt(p.getProperty(k, String.valueOf(d))), lo, hi);
        } catch (Exception e) {
            return d;
        }
    }

    public static void load() {
        try {
            Path p = file();
            if (!Files.exists(p)) return;
            Properties pr = new Properties();
            try (InputStream in = Files.newInputStream(p)) {
                pr.load(in);
            }
            targetEsp = b(pr, "targetEsp", true);
            mode = i(pr, "mode", 0, 0, 7);
            size = i(pr, "size", 1, 0, 2);
            spin = b(pr, "spin", true);
            hitSound = i(pr, "hitSound", 0, 0, 2);
            volume = i(pr, "volume", 2, 0, 2);
            killEffect = b(pr, "killEffect", true);
            killSound = b(pr, "killSound", true);
            nameProtect = b(pr, "nameProtect", true);
            watermark = b(pr, "watermark", true);
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            Properties pr = new Properties();
            pr.setProperty("targetEsp", String.valueOf(targetEsp));
            pr.setProperty("mode", String.valueOf(mode));
            pr.setProperty("size", String.valueOf(size));
            pr.setProperty("spin", String.valueOf(spin));
            pr.setProperty("hitSound", String.valueOf(hitSound));
            pr.setProperty("volume", String.valueOf(volume));
            pr.setProperty("killEffect", String.valueOf(killEffect));
            pr.setProperty("killSound", String.valueOf(killSound));
            pr.setProperty("nameProtect", String.valueOf(nameProtect));
            pr.setProperty("watermark", String.valueOf(watermark));
            try (OutputStream out = Files.newOutputStream(file())) {
                pr.store(out, "NameProtect / LoLHelper");
            }
        } catch (Exception ignored) {
        }
    }
}
