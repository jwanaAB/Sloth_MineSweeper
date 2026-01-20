package view;

import controller.GameController;
import model.Cell;
import model.EmptyCell;
import model.MineCell;
import model.NumberCell;
import model.QuestionCell;
import model.SurpriseCell;
import model.Game;
import model.GameBoard;
import model.GameObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.util.List;

/**
 * The main gameplay panel displaying two gameboards side-by-side for two
 * players.
 * Shows player information, turn indicator, and handles cell interactions.
 * Implements GameObserver to automatically update UI when game state changes.
 * 
 * @author Team Sloth
 */
public class GamePanel extends JPanel implements GameObserver {

    private JButton homeButton;
    private JButton pauseButton;
    private JButton flagModeButton;
    private JLabel player1NameLabel;
    private JLabel player2NameLabel;
    private JLabel sharedLivesLabel;
    private JLabel combinedScoreLabel;
    private JLabel turnIndicatorLabel;
    private JLabel timerLabel;
    private final JPanel player1BoardPanel;
    private final JPanel player2BoardPanel;
    private JLabel player1BoardLabel;
    private JLabel player2BoardLabel;
    private final CellButton[][] player1Cells;
    private final CellButton[][] player2Cells;
    private GameController gameController;
    private Game game;
    private boolean gameOver = false;
    private boolean flagModeEnabled = false;
    private boolean isPaused = false;
    private JPanel pauseOverlay;
    private JButton themeButton;
    private ThemeManager themeManager;
    private JPanel topBar;
    private JPanel infoPanel;
    private JPanel boardsContainer;
    private JPanel northContainer;

    /**
     * Constructs a new GamePanel.
     */
    public GamePanel() {
        setLayout(new BorderLayout());
        
        // Initialize theme manager
        themeManager = ThemeManager.getInstance();
        applyTheme();
        
        // Initialize board panels
        player1BoardPanel = new JPanel();
        player2BoardPanel = new JPanel();
        player1Cells = new CellButton[16][16]; // Max size for hard difficulty
        player2Cells = new CellButton[16][16];

        // Build UI components
        buildTopBar();
        buildPlayerInfoPanel();
        buildGameBoards();
        buildBottomPanel();
        
        // Add resize listeners to board panels
        player1BoardPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateCellSizes();
            }
        });
        player2BoardPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateCellSizes();
            }
        });
    }

    /**
     * Initializes the game panel with a game instance.
     * 
     * @param game           The Game instance to display
     * @param gameController The GameController to handle interactions
     */
    public void initializeGame(Game game, GameController gameController) {
        // Stop any existing timer from previous game controller
        if (this.gameController != null && this.gameController != gameController) {
            this.gameController.stopTimerForCleanup();
        }
        
        // Remove observer from previous game if it exists
        if (this.game != null) {
            this.game.removeObserver(this);
        }
        
        this.game = game;
        this.gameController = gameController;
        this.gameOver = false; // Reset game over state for new game
        this.flagModeEnabled = false; // Reset Flag Mode to OFF for new game
        this.isPaused = false; // Reset pause state for new game

        // Register this panel as an observer of the game
        game.addObserver(this);

        // Update button appearances
        updateFlagModeButtonAppearance();
        updatePauseButtonAppearance();
        hidePauseOverlay();
        
        // Reset timer display
        updateTimerDisplay("0:00");

        // Update player names
        player1NameLabel.setText("Player 1: " + game.getPlayer1Name());
        player2NameLabel.setText("Player 2: " + game.getPlayer2Name());
        
        // Update board labels with player names
        if (player1BoardLabel != null) {
            player1BoardLabel.setText(game.getPlayer1Name() + "'s Board");
        }
        if (player2BoardLabel != null) {
            player2BoardLabel.setText(game.getPlayer2Name() + "'s Board");
        }

        // Initialize cell buttons for both boards
        initializeBoard(player1BoardPanel, player1Cells, game.getPlayer1Board(), 1);
        initializeBoard(player2BoardPanel, player2Cells, game.getPlayer2Board(), 2);

        // Update cell sizes after initialization
        SwingUtilities.invokeLater(() -> {
            updateCellSizes();
        });

        // Update UI
        updateUI();
    }

    /**
     * Builds the top bar with home button.
     */
    private void buildTopBar() {
        // Create a container panel for both top bar and player info
        northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        Theme theme = themeManager.getCurrentTheme();
        northContainer.setBackground(theme.getBoardBackgroundColor());

        // Top bar with GridBagLayout for proper centering
        topBar = new JPanel(new GridBagLayout());
        topBar.setBackground(theme.getBoardBackgroundColor());
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 4, 12));
        GridBagConstraints gbc = new GridBagConstraints();

        homeButton = new JButton("\u2190 Home");
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        homeButton.setBackground(new Color(231, 76, 60)); // Prominent red/orange color
        homeButton.setForeground(Color.WHITE); // White text for contrast
        homeButton.setOpaque(true);
        homeButton.setBorderPainted(false);
        homeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(new Color(192, 57, 43)); // Darker on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(new Color(231, 76, 60)); // Original color
            }
        });

        // Home button on the left
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        topBar.add(homeButton, gbc);
        
        // Create Flag Mode button
        flagModeButton = new JButton("Flag Mode: OFF");
        flagModeButton.setFocusPainted(false);
        flagModeButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        flagModeButton.setBackground(new Color(200, 200, 200)); // Gray when OFF
        flagModeButton.setForeground(Color.BLACK);
        flagModeButton.setOpaque(true);
        flagModeButton.setBorderPainted(false);
        flagModeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        flagModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        flagModeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (flagModeEnabled) {
                    flagModeButton.setBackground(new Color(200, 255, 200)); // Light green on hover when ON
                } else {
                    flagModeButton.setBackground(new Color(180, 180, 180)); // Darker gray on hover when OFF
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateFlagModeButtonAppearance();
            }
        });
        
        // Add click handler
        flagModeButton.addActionListener(e -> toggleFlagMode());
        
        // Create Theme button
        themeButton = new JButton("Theme");
        themeButton.setFocusPainted(false);
        themeButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        themeButton.setBackground(new Color(150, 150, 200));
        themeButton.setForeground(Color.WHITE);
        themeButton.setOpaque(true);
        themeButton.setBorderPainted(false);
        themeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 120, 180), 2),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        themeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        themeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                themeButton.setBackground(new Color(120, 120, 180));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                themeButton.setBackground(new Color(150, 150, 200));
            }
        });
        
        // Add click handler to show theme selector
        themeButton.addActionListener(e -> showThemeSelector());
        
        // Create mute button for background music
        JButton muteButton = createMuteButton();
        
        // Create right panel for Mute, Theme, and Flag Mode buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(muteButton);
        rightPanel.add(themeButton);
        rightPanel.add(flagModeButton);
        
        // Right panel on the right side
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Take up remaining space
        gbc.anchor = GridBagConstraints.EAST;
        topBar.add(rightPanel, gbc);
        
        northContainer.add(topBar);

        // Add the combined north container to the main panel
        add(northContainer, BorderLayout.NORTH);
    }
    
    /**
     * Toggles the Flag Mode state and updates the button appearance.
     */
    private void toggleFlagMode() {
        flagModeEnabled = !flagModeEnabled;
        updateFlagModeButtonAppearance();
    }
    
    /**
     * Updates the Flag Mode button appearance based on current state.
     */
    private void updateFlagModeButtonAppearance() {
        if (flagModeButton == null) {
            return;
        }
        
        if (flagModeEnabled) {
            flagModeButton.setText("Flag Mode: ON");
            flagModeButton.setBackground(new Color(144, 238, 144)); // Light green when ON
            flagModeButton.setForeground(new Color(0, 100, 0)); // Dark green text
            flagModeButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 150, 0), 2),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        } else {
            flagModeButton.setText("Flag Mode: OFF");
            flagModeButton.setBackground(new Color(200, 200, 200)); // Gray when OFF
            flagModeButton.setForeground(Color.BLACK);
            flagModeButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        }
    }
    
    /**
     * Gets the current Flag Mode state.
     * 
     * @return true if Flag Mode is enabled, false otherwise
     */
    public boolean isFlagModeEnabled() {
        return flagModeEnabled;
    }

    /**
     * Builds the player information panel showing names and combined score.
     */
    private void buildPlayerInfoPanel() {
        Theme theme = themeManager.getCurrentTheme();
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(theme.getBoardBackgroundColor());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(theme.getInactiveBorderColor(), 1),
                new EmptyBorder(8, 20, 8, 20)));

        // Player names panel
        JPanel namesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        namesPanel.setOpaque(false);

        player1NameLabel = new JLabel("Player 1: ");
        player1NameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        player1NameLabel.setForeground(new Color(91, 161, 255));

        player2NameLabel = new JLabel("Player 2: ");
        player2NameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        player2NameLabel.setForeground(new Color(196, 107, 255));

        namesPanel.add(player1NameLabel);
        namesPanel.add(player2NameLabel);

        // Shared lives label (above combined score)
        sharedLivesLabel = new JLabel("Shared Lives: 0/0");
        sharedLivesLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sharedLivesLabel.setForeground(new Color(78, 214, 137)); // Green color
        sharedLivesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Combined score label
        combinedScoreLabel = new JLabel("Combined Score: 0");
        combinedScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        combinedScoreLabel.setForeground(new Color(78, 214, 137));
        combinedScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Turn indicator
        turnIndicatorLabel = new JLabel("Current Turn: Player 1");
        turnIndicatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        turnIndicatorLabel.setForeground(new Color(140, 70, 215));
        turnIndicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Timer label
        timerLabel = new JLabel("Time: 0:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        timerLabel.setForeground(new Color(255, 193, 7)); // Yellow/amber to match pause button
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(sharedLivesLabel, BorderLayout.NORTH);
        centerPanel.add(combinedScoreLabel, BorderLayout.CENTER);
        centerPanel.add(timerLabel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        
        // Add turn indicator below timer
        JPanel bottomInfoPanel = new JPanel(new BorderLayout());
        bottomInfoPanel.setOpaque(false);
        bottomInfoPanel.add(turnIndicatorLabel, BorderLayout.CENTER);
        bottomInfoPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        
        JPanel infoContainer = new JPanel(new BorderLayout());
        infoContainer.setOpaque(false);
        infoContainer.add(centerPanel, BorderLayout.CENTER);
        infoContainer.add(bottomInfoPanel, BorderLayout.SOUTH);

        infoPanel.add(namesPanel, BorderLayout.NORTH);
        infoPanel.add(infoContainer, BorderLayout.CENTER);

        // Create Pause button in the center, above the boards
        pauseButton = new JButton("⏸ Pause");
        pauseButton.setFocusPainted(false);
        pauseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pauseButton.setBackground(new Color(255, 193, 7)); // Yellow/amber color
        pauseButton.setForeground(Color.BLACK);
        pauseButton.setOpaque(true);
        pauseButton.setBorderPainted(false);
        pauseButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 170, 0), 2),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        pauseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isPaused) {
                    pauseButton.setBackground(new Color(230, 170, 0)); // Darker on hover
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isPaused) {
                    pauseButton.setBackground(new Color(255, 193, 7)); // Original color
                }
            }
        });
        
        // Add click handler
        pauseButton.addActionListener(e -> togglePause());
        
        // Center the pause button in its own panel
        JPanel pauseButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        pauseButtonPanel.setOpaque(false);
        pauseButtonPanel.setBackground(theme.getBoardBackgroundColor());
        pauseButtonPanel.add(pauseButton);
        
        // Get the north container that was created in buildTopBar
        JPanel northContainer = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);

        // Add to the north container instead of directly to the main panel
        northContainer.add(infoPanel);
        northContainer.add(pauseButtonPanel);
    }

    /**
     * Builds the gameboards panel with two boards side-by-side.
     */
    private void buildGameBoards() {
        Theme theme = themeManager.getCurrentTheme();
        // Create a wrapper panel with BorderLayout to hold boards and overlay
        boardsContainer = new JPanel(new BorderLayout());
        boardsContainer.setBackground(theme.getBackgroundColor());
        boardsContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Container for both boards
        JPanel boardsWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsWrapper.setOpaque(false);
        
        // Player 1 board container with label
        JPanel player1Container = new JPanel(new BorderLayout(0, 5));
        player1Container.setOpaque(false);
        player1BoardLabel = new JLabel("Player 1's Board", SwingConstants.CENTER);
        player1BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        player1BoardLabel.setForeground(new Color(91, 161, 255));
        player1Container.add(player1BoardLabel, BorderLayout.NORTH);
        
        // Player 1 board panel with custom border
        Theme currentTheme = themeManager.getCurrentTheme();
        player1BoardPanel.setBackground(currentTheme.getBoardBackgroundColor());
        player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(currentTheme.getInactiveBorderColor(), 2),
                new EmptyBorder(10, 10, 10, 10)));
        player1Container.add(player1BoardPanel, BorderLayout.CENTER);
        
        // Player 2 board container with label
        JPanel player2Container = new JPanel(new BorderLayout(0, 5));
        player2Container.setOpaque(false);
        player2BoardLabel = new JLabel("Player 2's Board", SwingConstants.CENTER);
        player2BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        player2BoardLabel.setForeground(new Color(196, 107, 255));
        player2Container.add(player2BoardLabel, BorderLayout.NORTH);
        
        // Player 2 board panel with custom border
        player2BoardPanel.setBackground(currentTheme.getBoardBackgroundColor());
        player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(currentTheme.getInactiveBorderColor(), 2),
                new EmptyBorder(10, 10, 10, 10)));
        player2Container.add(player2BoardPanel, BorderLayout.CENTER);
        
        boardsWrapper.add(player1Container);
        boardsWrapper.add(player2Container);
        
        // Add boardsWrapper to boardsContainer
        boardsContainer.add(boardsWrapper, BorderLayout.CENTER);
        add(boardsContainer, BorderLayout.CENTER);
    }
    
    /**
     * Updates the board borders and labels to highlight the active player's board.
     */
    private void updateBoardBorders() {
        if (game == null) {
            return;
        }
        
        Theme theme = themeManager.getCurrentTheme();
        int currentPlayer = game.getCurrentPlayer();
        Color player1Color = new Color(91, 161, 255); // Blue
        Color player2Color = new Color(196, 107, 255); // Purple
        Color inactiveColor = theme.getInactiveBorderColor();
        Color inactiveTextColor = new Color(150, 150, 150); // Light gray for inactive text
        
        // Update Player 1 board border and label
        if (currentPlayer == 1) {
            // Active: thick colored border with glow effect
            player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(player1Color, 4),
                    new EmptyBorder(8, 8, 8, 8)));
            player1BoardPanel.setBackground(theme.getBoardBackgroundColor());
            // Make label bold and colored
            if (player1BoardLabel != null) {
                player1BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                player1BoardLabel.setForeground(player1Color);
            }
        } else {
            // Inactive: thin gray border
            player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(inactiveColor, 2),
                    new EmptyBorder(10, 10, 10, 10)));
            player1BoardPanel.setBackground(theme.getBoardBackgroundColor());
            // Make label less prominent
            if (player1BoardLabel != null) {
                player1BoardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                player1BoardLabel.setForeground(inactiveTextColor);
            }
        }
        
        // Update Player 2 board border and label
        if (currentPlayer == 2) {
            // Active: thick colored border with glow effect
            player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(player2Color, 4),
                    new EmptyBorder(8, 8, 8, 8)));
            player2BoardPanel.setBackground(theme.getBoardBackgroundColor());
            // Make label bold and colored
            if (player2BoardLabel != null) {
                player2BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                player2BoardLabel.setForeground(player2Color);
            }
        } else {
            // Inactive: thin gray border
            player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(inactiveColor, 2),
                    new EmptyBorder(10, 10, 10, 10)));
            player2BoardPanel.setBackground(theme.getBoardBackgroundColor());
            // Make label less prominent
            if (player2BoardLabel != null) {
                player2BoardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                player2BoardLabel.setForeground(inactiveTextColor);
            }
        }
        
        // Repaint both panels and labels to show changes
        player1BoardPanel.revalidate();
        player1BoardPanel.repaint();
        player2BoardPanel.revalidate();
        player2BoardPanel.repaint();
        if (player1BoardLabel != null) {
            player1BoardLabel.repaint();
        }
        if (player2BoardLabel != null) {
            player2BoardLabel.repaint();
        }
    }

    /**
     * Builds the bottom panel (currently empty, can be used for controls).
     */
    private void buildBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 250));
        bottomPanel.setPreferredSize(new Dimension(0, 20));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Initializes a gameboard with cell buttons.
     * 
     * @param boardPanel  The panel to add cells to
     * @param cellButtons The 2D array to store cell button references
     * @param board       The GameBoard model
     * @param player      The player number (1 or 2)
     */
    private void initializeBoard(JPanel boardPanel, CellButton[][] cellButtons,
            GameBoard board, int player) {
        int rows = board.getRows();
        int cols = board.getCols();

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols, 2, 2));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                CellButton cellButton = new CellButton(i, j, player);
                cellButtons[i][j] = cellButton;
                boardPanel.add(cellButton);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Updates the responsive layout based on scale factor or window size.
     * 
     * @param scaleFactor The scaling factor from MainView (can be ignored for GamePanel's own calculations)
     */
    public void updateResponsiveLayout(double scaleFactor) {
        // GamePanel handles its own sizing based on available space
        updateCellSizes();
        updateFonts(scaleFactor);
    }
    
    /**
     * Updates cell button sizes based on available board panel space.
     */
    private void updateCellSizes() {
        if (game == null) {
            return;
        }
        
        updateBoardCellSizes(player1BoardPanel, player1Cells, game.getPlayer1Board());
        updateBoardCellSizes(player2BoardPanel, player2Cells, game.getPlayer2Board());
    }
    
    /**
     * Updates cell sizes for a specific board.
     */
    private void updateBoardCellSizes(JPanel boardPanel, CellButton[][] cellButtons, GameBoard board) {
        if (board == null || boardPanel == null || cellButtons == null) {
            return;
        }
        
        int rows = board.getRows();
        int cols = board.getCols();
        
        if (rows == 0 || cols == 0) {
            return;
        }
        
        // Get available space (accounting for padding and gaps)
        int panelWidth = boardPanel.getWidth();
        int panelHeight = boardPanel.getHeight();
        
        // Account for padding (typically 10px on each side)
        int padding = 10;
        int gap = 2; // GridLayout gap
        int availableWidth = panelWidth - (padding * 2);
        int availableHeight = panelHeight - (padding * 2);
        
        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }
        
        // Calculate cell size to fit both width and height
        int cellWidth = (availableWidth - (gap * (cols - 1))) / cols;
        int cellHeight = (availableHeight - (gap * (rows - 1))) / rows;
        int cellSize = Math.min(cellWidth, cellHeight);
        
        // Set minimum and maximum cell size for usability
        cellSize = Math.max(20, Math.min(60, cellSize)); // Clamp between 20-60 pixels
        
        // Update all cell button preferred sizes
        for (int i = 0; i < rows && i < cellButtons.length; i++) {
            for (int j = 0; j < cols && j < cellButtons[i].length; j++) {
                if (cellButtons[i][j] != null) {
                    cellButtons[i][j].setPreferredSize(new Dimension(cellSize, cellSize));
                    cellButtons[i][j].setMinimumSize(new Dimension(cellSize, cellSize));
                    cellButtons[i][j].setMaximumSize(new Dimension(cellSize, cellSize));
                }
            }
        }
        
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    
    /**
     * Updates fonts based on scale factor.
     */
    private void updateFonts(double scaleFactor) {
        // Scale player info fonts
        if (player1NameLabel != null) {
            int fontSize = (int) (13 * scaleFactor);
            fontSize = Math.max(11, Math.min(18, fontSize));
            player1NameLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (player2NameLabel != null) {
            int fontSize = (int) (13 * scaleFactor);
            fontSize = Math.max(11, Math.min(18, fontSize));
            player2NameLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (sharedLivesLabel != null) {
            int fontSize = (int) (12 * scaleFactor);
            fontSize = Math.max(10, Math.min(16, fontSize));
            sharedLivesLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (combinedScoreLabel != null) {
            int fontSize = (int) (12 * scaleFactor);
            fontSize = Math.max(10, Math.min(16, fontSize));
            combinedScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (turnIndicatorLabel != null) {
            int fontSize = (int) (11 * scaleFactor);
            fontSize = Math.max(9, Math.min(15, fontSize));
            turnIndicatorLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Updates the UI to reflect the current game state.
     */
    public void updateUI() {
        if (game == null) {
            return;
        }

        // Update shared lives
        sharedLivesLabel.setText("Shared Lives: " + game.getSharedLives() + "/" + game.getTotalLives());

        // Update combined score
        combinedScoreLabel.setText("Combined Score: " + game.getCombinedScore());

        // Update turn indicator
        String currentPlayerName = game.getCurrentPlayerName();
        turnIndicatorLabel.setText("Current Turn: " + currentPlayerName);
        turnIndicatorLabel.setForeground(
                game.getCurrentPlayer() == 1 ? new Color(91, 161, 255) : new Color(196, 107, 255));

        // Update board borders to highlight active player
        updateBoardBorders();

        // Update player 1 board
        updateBoard(player1Cells, game.getPlayer1Board(), 1);

        // Update player 2 board
        updateBoard(player2Cells, game.getPlayer2Board(), 2);
    }

    /**
     * Updates the visual representation of a board.
     * 
     * @param cellButtons The 2D array of cell buttons
     * @param board       The GameBoard model
     * @param player      The player number
     */
    private void updateBoard(CellButton[][] cellButtons, GameBoard board, int player) {
        int rows = board.getRows();
        int cols = board.getCols();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                CellButton button = cellButtons[i][j];

                if (button != null && cell != null) {
                    button.updateCell(cell, game.getCurrentPlayer() == player);
                }
            }
        }
    }

    /**
     * Sets the action listener for the home button.
     * 
     * @param listener The action listener
     */
    public void setHomeAction(ActionListener listener) {
        for (ActionListener l : homeButton.getActionListeners()) {
            homeButton.removeActionListener(l);
        }
        if (listener != null) {
            homeButton.addActionListener(e -> {
                // Show confirmation dialog before executing the listener (unless game is over)
                if (gameOver || confirmReturnHome()) {
                    listener.actionPerformed(e);
                }
            });
        }
    }
    
    /**
     * Shows a visual indicator when the wrong board is clicked.
     * Flashes the correct board to indicate whose turn it is.
     */
    public void showWrongTurnIndicator() {
        if (game == null) {
            return;
        }
        
        int currentPlayer = game.getCurrentPlayer();
        JPanel activeBoardPanel = currentPlayer == 1 ? player1BoardPanel : player2BoardPanel;
        JLabel activeBoardLabel = currentPlayer == 1 ? player1BoardLabel : player2BoardLabel;
        
        // Flash color for visual indicator
        Color flashColor = new Color(255, 200, 0); // Bright yellow/orange for flash
        
        // Flash animation: quickly change border color and back
        Timer flashTimer = new Timer(100, null);
        int[] flashCount = {0};
        flashTimer.addActionListener(e -> {
            if (flashCount[0] < 3) { // Flash 3 times
                if (flashCount[0] % 2 == 0) {
                    // Flash on
                    activeBoardPanel.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(flashColor, 5),
                            new EmptyBorder(5, 5, 5, 5)));
                    if (activeBoardLabel != null) {
                        activeBoardLabel.setForeground(flashColor);
                        activeBoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    }
                } else {
                    // Flash off - restore original
                    updateBoardBorders();
                }
                flashCount[0]++;
            } else {
                // Restore original state
                updateBoardBorders();
                flashTimer.stop();
            }
            repaint();
        });
        flashTimer.start();
    }
    
    /**
     * Sets the game over state and disables all cell interactions.
     * 
     * @param gameOver true if the game is over, false otherwise
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        
        // Disable all cell buttons
        disableAllCells();
    }
    
    /**
     * Disables all cell buttons to prevent further interactions.
     */
    private void disableAllCells() {
        // Disable player 1 cells
        for (int i = 0; i < player1Cells.length; i++) {
            for (int j = 0; j < player1Cells[i].length; j++) {
                if (player1Cells[i][j] != null) {
                    player1Cells[i][j].setEnabled(false);
                }
            }
        }
        
        // Disable player 2 cells
        for (int i = 0; i < player2Cells.length; i++) {
            for (int j = 0; j < player2Cells[i].length; j++) {
                if (player2Cells[i][j] != null) {
                    player2Cells[i][j].setEnabled(false);
                }
            }
        }
    }
    
    /**
     * Enables cell buttons based on current game state (only if not game over and not paused).
     */
    private void enableAllCells() {
        if (game == null || gameOver || isPaused) {
            return;
        }
        
        // Re-enable cells by calling updateUI which will update cell states
        updateUI();
    }
    
    /**
     * Toggles the pause state and updates the UI accordingly.
     */
    private void togglePause() {
        isPaused = !isPaused;
        updatePauseButtonAppearance();
        
        if (isPaused) {
            disableAllCells();
            showPauseOverlay();
            // Pause the timer
            if (gameController != null) {
                gameController.pauseTimer();
            }
        } else {
            enableAllCells();
            hidePauseOverlay();
            // Resume the timer
            if (gameController != null) {
                gameController.resumeTimer();
            }
        }
    }
    
    /**
     * Updates the timer display with the given time string.
     * 
     * @param timeString The formatted time string (e.g., "5:23")
     */
    public void updateTimerDisplay(String timeString) {
        if (timerLabel != null) {
            timerLabel.setText("Time: " + timeString);
        }
    }
    
    /**
     * Gets the current game controller.
     * 
     * @return The current GameController instance
     */
    public GameController getGameController() {
        return gameController;
    }
    
    /**
     * Updates the Pause button appearance based on current state.
     */
    private void updatePauseButtonAppearance() {
        if (pauseButton == null) {
            return;
        }
        
        if (isPaused) {
            pauseButton.setText("▶ Resume");
            pauseButton.setBackground(new Color(40, 167, 69)); // Green when paused
            pauseButton.setForeground(Color.WHITE);
            pauseButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(30, 130, 50), 2),
                    BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        } else {
            pauseButton.setText("⏸ Pause");
            pauseButton.setBackground(new Color(255, 193, 7)); // Yellow/amber when not paused
            pauseButton.setForeground(Color.BLACK);
            pauseButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 170, 0), 2),
                    BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        }
    }
    
    /**
     * Shows a semi-transparent overlay indicating the game is paused.
     * This overlay is added on top of the existing layout without modifying board structure.
     */
    private void showPauseOverlay() {
        JFrame rootFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (pauseOverlay != null) {
            // Reattach to glass pane if it was removed
            if (rootFrame != null && rootFrame.getGlassPane() != pauseOverlay) {
                pauseOverlay.setSize(rootFrame.getSize());
                rootFrame.setGlassPane(pauseOverlay);
            }
            pauseOverlay.setVisible(true);
            pauseOverlay.repaint();
            
            // Re-add ESC key binding to root frame if needed
            if (rootFrame != null) {
                rootFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resumeGameFromFrame");
                rootFrame.getRootPane().getActionMap().put("resumeGameFromFrame", new AbstractAction() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (isPaused) {
                            togglePause();
                        }
                    }
                });
            }
            return;
        }
        
        // Create overlay panel with BorderLayout to hold content
        pauseOverlay = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background to highlight/darken the whole screen
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        pauseOverlay.setOpaque(false);
        
        // Create center panel with pause message and resume button
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        // Pause message label
        JLabel pauseLabel = new JLabel("PAUSED");
        pauseLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle label
        JLabel subtitleLabel = new JLabel("Press ESC or click Resume to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        // Resume button
        JButton resumeButton = new JButton("▶ Resume");
        resumeButton.setFocusPainted(false);
        resumeButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        resumeButton.setBackground(new Color(40, 167, 69)); // Green
        resumeButton.setForeground(Color.WHITE);
        resumeButton.setOpaque(true);
        resumeButton.setBorderPainted(false);
        resumeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 130, 50), 2),
                BorderFactory.createEmptyBorder(12, 32, 12, 32)));
        resumeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect to resume button
        resumeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                resumeButton.setBackground(new Color(30, 130, 50)); // Darker on hover
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                resumeButton.setBackground(new Color(40, 167, 69)); // Original color
            }
        });
        
        // Add click handler to resume button
        resumeButton.addActionListener(e -> togglePause());
        
        // Add components to center panel
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(pauseLabel);
        centerPanel.add(subtitleLabel);
        centerPanel.add(resumeButton);
        centerPanel.add(Box.createVerticalGlue());
        
        // Add center panel to overlay
        pauseOverlay.add(centerPanel, BorderLayout.CENTER);
        
        // Add ESC key binding to resume (works even when overlay doesn't have focus)
        pauseOverlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resumeGame");
        pauseOverlay.getActionMap().put("resumeGame", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (isPaused) {
                    togglePause();
                }
            }
        });
        
        // Add overlay as a glass pane using the root pane
        if (rootFrame != null) {
            pauseOverlay.setSize(rootFrame.getSize());
            rootFrame.setGlassPane(pauseOverlay);
            pauseOverlay.setVisible(true);
        } else {
            // Fallback: add directly to this panel's center (will cover content)
            add(pauseOverlay, BorderLayout.CENTER);
            pauseOverlay.setVisible(true);
        }
        
        // Also add ESC key binding to the root frame for better compatibility
        if (rootFrame != null) {
            rootFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resumeGameFromFrame");
            rootFrame.getRootPane().getActionMap().put("resumeGameFromFrame", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (isPaused) {
                        togglePause();
                    }
                }
            });
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Hides the pause overlay.
     */
    private void hidePauseOverlay() {
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
            JFrame rootFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (rootFrame != null && rootFrame.getGlassPane() == pauseOverlay) {
                rootFrame.setGlassPane(new JPanel()); // Reset to empty glass pane
                // Remove ESC key binding from root frame when unpaused
                rootFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
                rootFrame.getRootPane().getActionMap().remove("resumeGameFromFrame");
            } else {
                // If added to center, remove it
                if (pauseOverlay.getParent() == this) {
                    remove(pauseOverlay);
                }
            }
            // Return focus to the main panel
            requestFocusInWindow();
            revalidate();
            repaint();
        }
    }

    /**
     * Shows a confirmation dialog before returning to the main menu.
     * 
     * @return true if user confirms, false otherwise
     */
    private boolean confirmReturnHome() {
        final boolean[] confirmed = { false };

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Return to Main Menu?", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(24, 32, 24, 32));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Are you sure you want to return to the main menu?");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Your current game progress will be lost.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 20, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Create rounded buttons similar to MainView
        JButton noButton = createRoundedButton(
                "No",
                Color.WHITE,
                new Color(45, 45, 45),
                new Color(210, 210, 210));
        JButton yesButton = createRoundedButton(
                "Yes",
                new Color(15, 15, 20),
                Color.WHITE,
                new Color(15, 15, 20));

        noButton.addActionListener(e -> {
            confirmed[0] = false;
            dialog.dispose();
        });
        yesButton.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        buttonRow.add(noButton);
        buttonRow.add(yesButton);

        content.add(title);
        content.add(subtitle);
        content.add(buttonRow);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return confirmed[0];
    }
    
    /**
     * Shows an enhanced theme selector dialog with visual previews.
     */
    private void showThemeSelector() {
        List<Theme> themes = themeManager.getAvailableThemes();
        Theme currentTheme = themeManager.getCurrentTheme();
        
        // Create custom dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Select Theme", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Choose a Theme", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Theme selection panel with previews
        JPanel themesPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        themesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        themesPanel.setBackground(Color.WHITE);
        
        ButtonGroup themeGroup = new ButtonGroup();
        JRadioButton[] themeButtons = new JRadioButton[themes.size()];
        
        for (int i = 0; i < themes.size(); i++) {
            Theme theme = themes.get(i);
            boolean isSelected = theme.getType() == currentTheme.getType();
            
            // Create radio button with theme preview
            JRadioButton radioButton = new JRadioButton(theme.getName(), isSelected);
            radioButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            radioButton.setBackground(Color.WHITE);
            radioButton.setFocusPainted(false);
            
            // Create preview panel with fixed size
            JPanel previewPanel = createThemePreview(theme);
            previewPanel.setPreferredSize(new Dimension(500, 80));
            previewPanel.setMinimumSize(new Dimension(500, 80));
            previewPanel.setMaximumSize(new Dimension(500, 80));
            previewPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isSelected ? new Color(91, 161, 255) : Color.GRAY, 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            
            // Container for radio button and preview
            JPanel themeContainer = new JPanel(new BorderLayout(10, 5));
            themeContainer.setBackground(Color.WHITE);
            themeContainer.add(radioButton, BorderLayout.WEST);
            themeContainer.add(previewPanel, BorderLayout.CENTER);
            
            // Add click listener
            final int index = i;
            radioButton.addActionListener(e -> {
                Theme selectedTheme = themes.get(index);
                themeManager.setTheme(selectedTheme);
                applyTheme();
                updateUI();
                // Update preview borders
                for (int j = 0; j < themeButtons.length; j++) {
                    JPanel container = (JPanel) themesPanel.getComponent(j);
                    JPanel preview = (JPanel) container.getComponent(1);
                    preview.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    j == index ? new Color(91, 161, 255) : Color.GRAY, 2),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                }
            });
            
            themeGroup.add(radioButton);
            themeButtons[i] = radioButton;
            themesPanel.add(themeContainer);
        }
        
        // Scroll pane for themes
        JScrollPane scrollPane = new JScrollPane(themesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(650, 500));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(91, 161, 255));
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Creates a visual preview panel for a theme.
     */
    private JPanel createThemePreview(Theme theme) {
        JPanel preview = new JPanel(new GridLayout(2, 4, 3, 3));
        preview.setBackground(theme.getBackgroundColor());
        // Set fixed size to ensure all previews are exactly the same
        Dimension fixedSize = new Dimension(500, 80);
        preview.setPreferredSize(fixedSize);
        preview.setMinimumSize(fixedSize);
        preview.setMaximumSize(fixedSize);
        
        // Create sample cells
        Color[] sampleColors = {
            theme.getHiddenCellColor(),
            theme.getRevealedCellColor(),
            theme.getFlaggedCellColor(),
            theme.getMineCellColor(),
            theme.getQuestionCellColor(),
            theme.getSurpriseCellColor(),
            theme.getEmptyCellColor(),
            theme.getNumberColor(1)
        };
        
        String[] labels = {"Hidden", "Revealed", "Flagged", "Mine", "Question", "Surprise", "Empty", "Number"};
        
        for (int i = 0; i < sampleColors.length; i++) {
            JPanel cellPreview = new JPanel(new BorderLayout());
            cellPreview.setBackground(sampleColors[i]);
            cellPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            
            JLabel label = new JLabel(labels[i], SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            // Calculate brightness manually (0-1 scale)
            Color bgColor = theme.getBackgroundColor();
            double brightness = (bgColor.getRed() * 0.299 + bgColor.getGreen() * 0.587 + bgColor.getBlue() * 0.114) / 255.0;
            label.setForeground(brightness < 0.5 ? Color.WHITE : Color.BLACK);
            cellPreview.add(label, BorderLayout.CENTER);
            
            preview.add(cellPreview);
        }
        
        return preview;
    }
    
    /**
     * Applies the current theme to the entire UI panel.
     */
    private void applyTheme() {
        Theme theme = themeManager.getCurrentTheme();
        
        // Apply main background
        setBackground(theme.getBackgroundColor());
        
        // Apply top bar background
        if (topBar != null) {
            topBar.setBackground(theme.getBoardBackgroundColor());
        }
        
        // Apply north container background
        if (northContainer != null) {
            northContainer.setBackground(theme.getBoardBackgroundColor());
        }
        
        // Apply info panel background and border
        if (infoPanel != null) {
            infoPanel.setBackground(theme.getBoardBackgroundColor());
            infoPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(theme.getInactiveBorderColor(), 1),
                    new EmptyBorder(15, 20, 15, 20)));
        }
        
        // Update board backgrounds
        if (player1BoardPanel != null) {
            player1BoardPanel.setBackground(theme.getBoardBackgroundColor());
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.setBackground(theme.getBoardBackgroundColor());
        }
        
        // Update boards container background
        if (boardsContainer != null) {
            boardsContainer.setBackground(theme.getBackgroundColor());
        }
        
        // Update all cells to use theme
        if (game != null) {
            updateUI();
        }
        
        // Repaint everything
        revalidate();
        repaint();
    }

    /**
     * Creates a rounded button for the confirmation dialog.
     */
    private JButton createRoundedButton(String text, Color fillColor, Color textColor, Color borderColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Custom button class for individual cells in the gameboard.
     */
    @SuppressWarnings("unused")
    private class CellButton extends JButton {
        @SuppressWarnings("unused")
        private final int row;
        @SuppressWarnings("unused")
        private final int col;
        @SuppressWarnings("unused")
        private final int player;
        @SuppressWarnings("unused")
        private Cell currentCell;
        @SuppressWarnings("unused")
        private boolean isCurrentPlayer;
        private boolean wasRevealed = false;
        private float animationProgress = 0.0f;
        private Timer animationTimer;

        /**
         * Constructs a new CellButton.
         * 
         * @param row    The row index
         * @param col    The column index
         * @param player The player number (1 or 2)
         */
        public CellButton(int row, int col, int player) {
            this.row = row;
            this.col = col;
            this.player = player;
            this.currentCell = null;
            this.isCurrentPlayer = false;

            // Size will be set dynamically by updateCellSizes()
            setPreferredSize(new Dimension(30, 30)); // Default, will be updated
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            // Default border for hidden cells (raised appearance)
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));

            // Add mouse listeners for hover effects
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (currentCell != null && !currentCell.isRevealed() && !currentCell.isFlagged() && isCurrentPlayer) {
                        // Hover effect: slight scale and glow
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createRaisedBevelBorder(),
                                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2)));
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (currentCell != null && !currentCell.isRevealed() && !currentCell.isFlagged()) {
                        // Restore normal border
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createRaisedBevelBorder(),
                                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameController == null || game == null) {
                        return;
                    }
                    
                    // Don't process clicks if game is over or paused
                    if (gameOver || isPaused) {
                        return;
                    }

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // Check Flag Mode state
                        if (flagModeEnabled) {
                            // Flag Mode ON: left-click toggles flag
                            gameController.handleCellFlag(row, col, player);
                        } else {
                            // Flag Mode OFF: left-click reveals cell or handles question/surprise cell
                            Cell cell = game.getBoard(player).getCell(row, col);
                            if (cell != null && cell.isRevealed()) {
                                if (cell instanceof QuestionCell && !((QuestionCell) cell).isQuestionOpened()) {
                                    // Question cell already revealed - offer to open
                                    gameController.handleQuestionCellClick(row, col, player);
                                } else if (cell instanceof SurpriseCell && !((SurpriseCell) cell).isSurpriseActivated()) {
                                    // Surprise cell already revealed - offer to activate
                                    gameController.handleSurpriseCellClick(row, col, player);
                                } else {
                                    // Already revealed regular cell or already activated - do nothing
                                    return;
                                }
                            } else {
                                // Reveal cell
                                gameController.handleCellReveal(row, col, player);
                            }
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // Right click: flag cell (works regardless of Flag Mode state)
                        gameController.handleCellFlag(row, col, player);
                    }
                }
            });
        }

        /**
         * Updates the button's appearance based on the cell state.
         * Uses theme colors and triggers animations when cells are revealed.
         * 
         * @param cell            The Cell model
         * @param isCurrentPlayer Whether this is the current player's board
         */
        public void updateCell(Cell cell, boolean isCurrentPlayer) {
            this.currentCell = cell;
            this.isCurrentPlayer = isCurrentPlayer;

            if (cell == null) {
                return;
            }

            Theme theme = themeManager.getCurrentTheme();
            
            // Check if cell was just revealed (animation trigger)
            boolean justRevealed = cell.isRevealed() && !wasRevealed;
            wasRevealed = cell.isRevealed();
            
            // Start animation if cell was just revealed
            if (justRevealed) {
                startRevealAnimation();
            }

            // Calculate font size based on button size for proportional scaling
            int buttonSize = Math.min(Math.max(getWidth(), 20), Math.max(getHeight(), 20)); // Ensure minimum size
            int emojiFontSize = Math.max(10, Math.min(buttonSize * 3 / 4, 24)); // Proportional to button size
            int numberFontSize = Math.max(8, Math.min(buttonSize * 2 / 3, 20));
            int emptyFontSize = Math.max(6, Math.min(buttonSize / 3, 14));
            
            // Set font that supports Unicode symbols (for emoji) - default for flagged/mine cells
            setFont(new Font("Segoe UI Emoji", Font.BOLD, emojiFontSize));

            if (cell.isFlagged()) {
                // Flagged state - raised appearance
                setText("🚩");
                setBackground(theme.getFlaggedCellColor());
                setForeground(Color.BLACK);
                setEnabled(true);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            } else if (cell.isRevealed()) {
                // Revealed state - sunken/flat appearance to show it's been opened
                setEnabled(false);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLoweredBevelBorder(),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));

                if (cell instanceof MineCell) {
                    setText("💣");
                    setBackground(theme.getMineCellColor());
                    setForeground(Color.BLACK);
                } else if (cell instanceof NumberCell) {
                    NumberCell numberCell = (NumberCell) cell;
                    // Use calculated number font size
                    setFont(new Font("Segoe UI", Font.BOLD, numberFontSize));
                    setText(String.valueOf(numberCell.getAdjacentMines()));
                    setBackground(theme.getRevealedCellColor());
                    // Use theme number colors
                    int num = numberCell.getAdjacentMines();
                    setForeground(theme.getNumberColor(num));
                } else if (cell instanceof QuestionCell) {
                    QuestionCell questionCell = (QuestionCell) cell;
                    setText("?");
                    setBackground(theme.getQuestionCellColor());
                    setForeground(Color.BLACK);
                    // Question cells should be clickable if not opened yet
                    if (!questionCell.isQuestionOpened()) {
                        setEnabled(true); // Enable so user can click to open question
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLoweredBevelBorder(),
                                BorderFactory.createCompoundBorder(
                                        new LineBorder(theme.getQuestionCellColor().darker(), 2),
                                        new EmptyBorder(0, 0, 0, 0))));
                    } else {
                        setEnabled(false); // Disable if already opened
                    }
                } else if (cell instanceof SurpriseCell) {
                    SurpriseCell surpriseCell = (SurpriseCell) cell;
                    setText("✨");
                    setBackground(theme.getSurpriseCellColor());
                    setForeground(Color.BLACK);
                    // Surprise cells should be clickable when revealed (if not already activated)
                    if (!surpriseCell.isSurpriseActivated()) {
                        setEnabled(true); // Enable so user can click to activate surprise
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLoweredBevelBorder(),
                                BorderFactory.createCompoundBorder(
                                        new LineBorder(theme.getSurpriseCellColor().darker(), 2),
                                        new EmptyBorder(0, 0, 0, 0))));
                    } else {
                        setEnabled(false); // Disable if already activated
                    }
                } else if (cell instanceof EmptyCell) {
                    // Empty revealed cells
                    setFont(new Font("Segoe UI", Font.PLAIN, emptyFontSize));
                    setText("");
                    setBackground(theme.getEmptyCellColor());
                    setForeground(Color.BLACK);
                }
            } else {
                // Hidden state - raised 3D appearance
                setText("");
                setBackground(theme.getHiddenCellColor());
                setForeground(Color.BLACK);
                setEnabled(isCurrentPlayer); // Only enable if it's current player's turn
                // Raised border to show it's clickable/unopened
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            }
        }
        
        /**
         * Starts the reveal animation for this cell.
         */
        private void startRevealAnimation() {
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
            
            animationProgress = 0.0f;
            animationTimer = new Timer(16, e -> { // ~60 FPS
                animationProgress += 0.15f; // Increased from 0.05f for faster animation (3x speed)
                if (animationProgress >= 1.0f) {
                    animationProgress = 1.0f;
                    animationTimer.stop();
                }
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            // Apply animation effect if in progress
            if (animationProgress > 0.0f && animationProgress < 1.0f && currentCell != null && currentCell.isRevealed()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fade-in effect: interpolate opacity
                float alpha = animationProgress;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                
                // Scale effect: slight scale animation
                double scale = 0.8 + (0.2 * animationProgress); // Scale from 0.8 to 1.0
                int width = getWidth();
                int height = getHeight();
                int newWidth = (int) (width * scale);
                int newHeight = (int) (height * scale);
                int x = (width - newWidth) / 2;
                int y = (height - newHeight) / 2;
                
                // Save original clip
                Shape originalClip = g2.getClip();
                g2.setClip(x, y, newWidth, newHeight);
                
                // Paint normally
                super.paintComponent(g2);
                
                // Restore clip
                g2.setClip(originalClip);
                g2.dispose();
            } else {
                // Normal painting without animation
                super.paintComponent(g);
            }
        }
    }
    
    // ========== GameObserver Implementation ==========
    
    /**
     * Called when the game score changes.
     * Updates the score label in the UI.
     * 
     * @param newScore The new combined score
     */
    @Override
    public void onScoreChanged(int newScore) {
        SwingUtilities.invokeLater(() -> {
            if (combinedScoreLabel != null) {
                combinedScoreLabel.setText("Combined Score: " + newScore);
            }
        });
    }
    
    /**
     * Called when the shared lives change.
     * Updates the lives label in the UI.
     * 
     * @param newLives The new number of shared lives
     * @param totalLives The total number of lives
     */
    @Override
    public void onLivesChanged(int newLives, int totalLives) {
        SwingUtilities.invokeLater(() -> {
            if (sharedLivesLabel != null) {
                sharedLivesLabel.setText("Shared Lives: " + newLives + "/" + totalLives);
            }
        });
    }
    
    /**
     * Called when the turn changes to a different player.
     * Updates the turn indicator and board borders in the UI.
     * 
     * @param currentPlayer The current player number (1 or 2)
     * @param playerName The name of the current player
     */
    @Override
    public void onTurnChanged(int currentPlayer, String playerName) {
        SwingUtilities.invokeLater(() -> {
            if (turnIndicatorLabel != null) {
                turnIndicatorLabel.setText("Current Turn: " + playerName);
                turnIndicatorLabel.setForeground(
                        currentPlayer == 1 ? new Color(91, 161, 255) : new Color(196, 107, 255));
            }
            // Update board borders to highlight active player
            updateBoardBorders();
            // Update cell states to enable/disable based on current player
            updateUI();
        });
    }
    
    /**
     * Called when the game ends (either won or lost).
     * Updates the UI to show the final game state.
     * 
     * @param won true if the game was won, false if lost
     * @param winner The winner player number (1 or 2), or 0 if no winner (loss)
     */
    @Override
    public void onGameOver(boolean won, int winner) {
        SwingUtilities.invokeLater(() -> {
            gameOver = true;
            setGameOver(true);
            // The full UI update will be handled by the controller's handleGameOver method
            // but we ensure the UI reflects the game over state
            updateUI();
        });
    }
    
    /**
     * Called when a cell is revealed.
     * Updates the UI to reflect the revealed cell.
     * 
     * @param row The row index of the revealed cell
     * @param col The column index of the revealed cell
     * @param player The player number (1 or 2) who revealed the cell
     */
    @Override
    public void onCellRevealed(int row, int col, int player) {
        SwingUtilities.invokeLater(() -> {
            // Update the specific board that had a cell revealed
            if (player == 1 && game != null) {
                updateBoard(player1Cells, game.getPlayer1Board(), 1);
            } else if (player == 2 && game != null) {
                updateBoard(player2Cells, game.getPlayer2Board(), 2);
            }
        });
    }
    
    /**
     * Creates a mute button for background music control.
     */
    private JButton createMuteButton() {
        JButton button = new JButton("\uD83D\uDD0A"); // Speaker icon 🔊
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        button.setForeground(new Color(91, 161, 255));
        button.setBackground(new Color(240, 248, 255));
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(91, 161, 255), 2, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Mute/Unmute Background Music");
        button.addActionListener(e -> {
            controller.SoundManager.getInstance().toggleBackgroundMusic();
            updateMuteButtonIcon(button);
        });
        updateMuteButtonIcon(button);
        return button;
    }
    
    /**
     * Updates the mute button icon based on music state.
     */
    private void updateMuteButtonIcon(JButton button) {
        if (button != null) {
            boolean isMuted = !controller.SoundManager.getInstance().isBackgroundMusicEnabled();
            button.setText(isMuted ? "\uD83D\uDD07" : "\uD83D\uDD0A"); // 🔇 when muted, 🔊 when playing
            button.setToolTipText(isMuted ? "Unmute Background Music" : "Mute Background Music");
        }
    }
}
