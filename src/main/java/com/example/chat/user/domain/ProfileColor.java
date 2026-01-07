package com.example.chat.user.domain;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum ProfileColor {

    RED("#EF4444"),
    ORANGE("#F97316"),
    AMBER("#F59E0B"),
    YELLOW("#EAB308"),
    LIME("#84CC16"),
    GREEN("#22C55E"),
    EMERALD("#10B981"),
    TEAL("#14B8A6"),
    CYAN("#06B6D4"),
    SKY("#0EA5E9"),
    BLUE("#3B82F6"),
    INDIGO("#6366F1"),
    VIOLET("#8B5CF6"),
    PURPLE("#A855F7"),
    FUCHSIA("#D946EF"),
    PINK("#EC4899"),
    ROSE("#F43F5E");

    private final String hexCode;

    ProfileColor(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getHexCode() {
        return hexCode;
    }

    private static final List<ProfileColor> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();

    public static String getRandomHexCode() {
        int randomIndex = ThreadLocalRandom.current().nextInt(SIZE);
        return VALUES.get(randomIndex).getHexCode();
    }
}
