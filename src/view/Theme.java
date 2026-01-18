package view;

import java.awt.Color;

/**
 * Represents a visual theme for the Minesweeper game.
 * Contains all color definitions for different cell types and UI elements.
 * 
 * @author Team Sloth
 */
public class Theme {
    
    public enum ThemeType {
        CLASSIC("Classic"),
        DARK("Dark Mode"),
        NEON("Neon"),
        NATURE("Nature"),
        HIGH_CONTRAST("High Contrast");
        
        private final String displayName;
        
        ThemeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private final ThemeType type;
    private final String name;
    
    // Cell colors
    private final Color hiddenCellColor;
    private final Color revealedCellColor;
    private final Color flaggedCellColor;
    private final Color mineCellColor;
    private final Color questionCellColor;
    private final Color surpriseCellColor;
    private final Color emptyCellColor;
    
    // Number colors (for NumberCell)
    private final Color[] numberColors;
    
    // UI colors
    private final Color backgroundColor;
    private final Color boardBackgroundColor;
    private final Color activeBorderColor;
    private final Color inactiveBorderColor;
    
    /**
     * Creates a theme with the specified colors.
     */
    private Theme(ThemeType type, String name,
                  Color hiddenCellColor, Color revealedCellColor, Color flaggedCellColor,
                  Color mineCellColor, Color questionCellColor, Color surpriseCellColor,
                  Color emptyCellColor, Color[] numberColors,
                  Color backgroundColor, Color boardBackgroundColor,
                  Color activeBorderColor, Color inactiveBorderColor) {
        this.type = type;
        this.name = name;
        this.hiddenCellColor = hiddenCellColor;
        this.revealedCellColor = revealedCellColor;
        this.flaggedCellColor = flaggedCellColor;
        this.mineCellColor = mineCellColor;
        this.questionCellColor = questionCellColor;
        this.surpriseCellColor = surpriseCellColor;
        this.emptyCellColor = emptyCellColor;
        this.numberColors = numberColors;
        this.backgroundColor = backgroundColor;
        this.boardBackgroundColor = boardBackgroundColor;
        this.activeBorderColor = activeBorderColor;
        this.inactiveBorderColor = inactiveBorderColor;
    }
    
    /**
     * Creates the Classic theme (default Windows Minesweeper style).
     */
    public static Theme createClassic() {
        Color[] numberColors = {
            new Color(0, 0, 255),      // 1 - Blue
            new Color(0, 150, 0),      // 2 - Green
            new Color(255, 0, 0),      // 3 - Red
            new Color(0, 0, 150),      // 4 - Dark Blue
            new Color(150, 0, 0),      // 5 - Dark Red
            new Color(0, 150, 150),    // 6 - Teal
            new Color(0, 0, 0),       // 7 - Black
            new Color(100, 100, 100)  // 8 - Gray
        };
        
        return new Theme(
            ThemeType.CLASSIC, "Classic",
            new Color(140, 140, 140),  // Hidden
            new Color(250, 250, 250),  // Revealed
            new Color(255, 180, 180),  // Flagged
            new Color(255, 120, 120),   // Mine
            new Color(255, 255, 150),  // Question
            new Color(255, 180, 255),  // Surprise
            new Color(250, 250, 250),  // Empty
            numberColors,
            new Color(240, 240, 250),  // Background
            new Color(255, 255, 255),   // Board background
            new Color(91, 161, 255),    // Active border
            new Color(200, 200, 200)   // Inactive border
        );
    }
    
    /**
     * Creates the Dark Mode theme.
     */
    public static Theme createDark() {
        Color[] numberColors = {
            new Color(100, 150, 255),  // 1 - Light Blue
            new Color(100, 255, 100),  // 2 - Light Green
            new Color(255, 100, 100),  // 3 - Light Red
            new Color(150, 150, 255),  // 4 - Light Purple
            new Color(255, 150, 100),  // 5 - Orange
            new Color(100, 255, 255),  // 6 - Cyan
            new Color(255, 255, 255),  // 7 - White
            new Color(200, 200, 200)   // 8 - Light Gray
        };
        
        return new Theme(
            ThemeType.DARK, "Dark Mode",
            new Color(60, 60, 70),     // Hidden - Dark gray
            new Color(35, 35, 45),     // Revealed - Very dark gray
            new Color(200, 100, 100),  // Flagged - Dark red
            new Color(220, 60, 60),    // Mine - Bright red
            new Color(255, 200, 100),  // Question - Dark yellow
            new Color(200, 100, 200),  // Surprise - Dark magenta
            new Color(70, 70, 85),     // Empty - Lighter than hidden to show it's revealed
            numberColors,
            new Color(30, 30, 40),     // Background - Very dark
            new Color(40, 40, 50),     // Board background
            new Color(100, 150, 255),  // Active border - Light blue
            new Color(80, 80, 90)      // Inactive border - Dark gray
        );
    }
    
    /**
     * Creates the Neon theme (cyberpunk style).
     */
    public static Theme createNeon() {
        Color[] numberColors = {
            new Color(0, 255, 255),    // 1 - Cyan
            new Color(0, 255, 0),      // 2 - Green
            new Color(255, 0, 255),    // 3 - Magenta
            new Color(255, 255, 0),    // 4 - Yellow
            new Color(255, 100, 0),    // 5 - Orange
            new Color(255, 0, 100),    // 6 - Pink
            new Color(100, 0, 255),    // 7 - Purple
            new Color(255, 255, 255)   // 8 - White
        };
        
        return new Theme(
            ThemeType.NEON, "Neon",
            new Color(20, 20, 40),     // Hidden - Dark purple
            new Color(25, 25, 35),     // Revealed - Dark gray
            new Color(255, 0, 100),    // Flagged - Neon pink
            new Color(255, 0, 0),      // Mine - Bright red
            new Color(255, 255, 0),    // Question - Neon yellow
            new Color(255, 0, 255),    // Surprise - Neon magenta
            new Color(45, 45, 65),     // Empty - Lighter purple-gray to show it's revealed
            numberColors,
            new Color(5, 5, 20),       // Background - Very dark blue
            new Color(10, 10, 30),     // Board background
            new Color(0, 255, 255),    // Active border - Cyan
            new Color(50, 50, 80)     // Inactive border - Dark purple
        );
    }
    
    /**
     * Creates the Nature theme (green/earth tones).
     */
    public static Theme createNature() {
        Color[] numberColors = {
            new Color(50, 150, 255),   // 1 - Sky Blue
            new Color(50, 200, 50),    // 2 - Green
            new Color(200, 100, 50),   // 3 - Brown
            new Color(100, 150, 50),   // 4 - Olive
            new Color(150, 100, 50),   // 5 - Tan
            new Color(100, 200, 150),  // 6 - Mint
            new Color(50, 100, 50),    // 7 - Dark Green
            new Color(100, 100, 100)   // 8 - Gray
        };
        
        return new Theme(
            ThemeType.NATURE, "Nature",
            new Color(120, 150, 100),  // Hidden - Moss green
            new Color(240, 250, 230),  // Revealed - Light green
            new Color(255, 200, 150),  // Flagged - Peach
            new Color(200, 80, 80),     // Mine - Red
            new Color(255, 220, 100),  // Question - Yellow
            new Color(200, 150, 255),  // Surprise - Lavender
            new Color(245, 255, 240),  // Empty - Very light green
            numberColors,
            new Color(230, 245, 220),  // Background - Light green tint
            new Color(250, 255, 245),  // Board background
            new Color(50, 150, 50),     // Active border - Green
            new Color(180, 200, 170)   // Inactive border - Gray-green
        );
    }
    
    /**
     * Creates the High Contrast theme (accessibility).
     */
    public static Theme createHighContrast() {
        Color[] numberColors = {
            Color.BLUE,                // 1
            Color.GREEN,               // 2
            Color.RED,                 // 3
            new Color(0, 0, 128),      // 4 - Navy
            new Color(128, 0, 0),      // 5 - Maroon
            new Color(0, 128, 128),    // 6 - Teal
            Color.BLACK,               // 7
            Color.GRAY                 // 8
        };
        
        return new Theme(
            ThemeType.HIGH_CONTRAST, "High Contrast",
            Color.BLACK,               // Hidden
            Color.WHITE,               // Revealed
            Color.RED,                 // Flagged
            Color.RED,                 // Mine
            Color.YELLOW,              // Question
            Color.MAGENTA,             // Surprise
            Color.WHITE,               // Empty
            numberColors,
            Color.WHITE,               // Background
            Color.WHITE,               // Board background
            Color.BLUE,                // Active border
            Color.BLACK                // Inactive border
        );
    }
    
    // Getters
    public ThemeType getType() { return type; }
    public String getName() { return name; }
    public Color getHiddenCellColor() { return hiddenCellColor; }
    public Color getRevealedCellColor() { return revealedCellColor; }
    public Color getFlaggedCellColor() { return flaggedCellColor; }
    public Color getMineCellColor() { return mineCellColor; }
    public Color getQuestionCellColor() { return questionCellColor; }
    public Color getSurpriseCellColor() { return surpriseCellColor; }
    public Color getEmptyCellColor() { return emptyCellColor; }
    public Color getNumberColor(int number) {
        if (number >= 1 && number <= 8) {
            return numberColors[number - 1];
        }
        return numberColors[7]; // Default to last color
    }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getBoardBackgroundColor() { return boardBackgroundColor; }
    public Color getActiveBorderColor() { return activeBorderColor; }
    public Color getInactiveBorderColor() { return inactiveBorderColor; }
}

