package view;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages themes for the Minesweeper game.
 * Singleton pattern to ensure consistent theme across the application.
 * 
 * @author Team Sloth
 */
public class ThemeManager {
    
    private static ThemeManager instance;
    private Theme currentTheme;
    private List<Theme> availableThemes;
    
    private ThemeManager() {
        availableThemes = new ArrayList<>();
        availableThemes.add(Theme.createClassic());
        availableThemes.add(Theme.createDark());
        availableThemes.add(Theme.createNeon());
        availableThemes.add(Theme.createNature());
        availableThemes.add(Theme.createHighContrast());
        
        // Set default theme
        currentTheme = Theme.createClassic();
    }
    
    /**
     * Gets the singleton instance of ThemeManager.
     * 
     * @return The ThemeManager instance
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * Gets the current theme.
     * 
     * @return The current theme
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Sets the current theme by type.
     * 
     * @param themeType The theme type to set
     */
    public void setTheme(Theme.ThemeType themeType) {
        for (Theme theme : availableThemes) {
            if (theme.getType() == themeType) {
                currentTheme = theme;
                return;
            }
        }
    }
    
    /**
     * Sets the current theme directly.
     * 
     * @param theme The theme to set
     */
    public void setTheme(Theme theme) {
        if (theme != null) {
            currentTheme = theme;
        }
    }
    
    /**
     * Gets all available themes.
     * 
     * @return List of all available themes
     */
    public List<Theme> getAvailableThemes() {
        return new ArrayList<>(availableThemes);
    }
    
    /**
     * Gets a theme by its type.
     * 
     * @param themeType The theme type
     * @return The theme, or null if not found
     */
    public Theme getTheme(Theme.ThemeType themeType) {
        for (Theme theme : availableThemes) {
            if (theme.getType() == themeType) {
                return theme;
            }
        }
        return null;
    }
}

