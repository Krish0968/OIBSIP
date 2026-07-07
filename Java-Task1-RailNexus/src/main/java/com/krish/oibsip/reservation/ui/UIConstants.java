package com.krish.oibsip.reservation.ui;

import java.awt.Color;
import java.awt.Font;

public class UIConstants {
    // Spacing (in pixels)
    public static final int SPACING_XS = 8;
    public static final int SPACING_S = 12;
    public static final int SPACING_M = 16;
    public static final int SPACING_L = 24;
    public static final int SPACING_XL = 32;

    // Component Dimensions
    public static final int CONTROL_HEIGHT = 36;
    public static final int SIDEBAR_WIDTH = 220;

    // Substantial Surface Color Palette
    public static final Color COLOR_BG_WINDOW = new Color(24, 24, 28);   // Dark main window background
    public static final Color COLOR_BG_CARD = new Color(34, 34, 42);     // Lighter floating card background
    public static final Color COLOR_BG_SIDEBAR = new Color(18, 18, 22);   // Deep obsidian sidebar background
    
    // Inputs & Borders
    public static final Color COLOR_INPUT_BG = new Color(44, 44, 54);    // Custom input background
    public static final Color COLOR_BORDER = new Color(54, 54, 66);      // Muted border color
    
    // Semantic Accent Colors (Clean, muted tones for dark mode)
    public static final Color COLOR_PRIMARY = new Color(33, 150, 243);   // Vibrant Accent Blue
    public static final Color COLOR_SUCCESS = new Color(76, 175, 80);    // Success Green
    public static final Color COLOR_DANGER = new Color(244, 67, 54);     // Danger Red
    
    // Status Highlights
    public static final Color BG_SUCCESS_MUTED = new Color(76, 175, 80, 30); // 12% opacity green
    public static final Color BG_DANGER_MUTED = new Color(244, 67, 54, 30);   // 12% opacity red

    // Typography
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 12);
    
    public static final Font FONT_SECTION = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_MUTED = new Font(FONT_FAMILY, Font.PLAIN, 12);

    // Card Specific
    public static final Font FONT_CARD_TITLE = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_CARD_VALUE = new Font(FONT_FAMILY, Font.BOLD, 32);
}
