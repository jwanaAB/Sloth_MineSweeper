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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;

/**
 * The main gameplay panel displaying two gameboards side-by-side for two
 * players.
 * Shows player information, turn indicator, and handles cell interactions.
 * 
 * @author Team Sloth
 */
public class GamePanel extends JPanel {

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

    /**
     * Constructs a new GamePanel.
     */
    public GamePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 250));

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
        
        this.game = game;
        this.gameController = gameController;
        this.gameOver = false; // Reset game over state for new game
        this.flagModeEnabled = false; // Reset Flag Mode to OFF for new game
        this.isPaused = false; // Reset pause state for new game

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
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setBackground(Color.WHITE);

        // Top bar with home button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        homeButton = new JButton("\u2190 Home");
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        homeButton.setBackground(new Color(231, 76, 60)); // Prominent red/orange color
        homeButton.setForeground(Color.WHITE); // White text for contrast
        homeButton.setOpaque(true);
        homeButton.setBorderPainted(false);
        homeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
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

        topBar.add(homeButton, BorderLayout.WEST);
        
        // Create Pause button in the center
        pauseButton = new JButton("⏸ Pause");
        pauseButton.setFocusPainted(false);
        pauseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pauseButton.setBackground(new Color(255, 193, 7)); // Yellow/amber color
        pauseButton.setForeground(Color.BLACK);
        pauseButton.setOpaque(true);
        pauseButton.setBorderPainted(false);
        pauseButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 170, 0), 2),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
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
        
        // Add Pause button to center
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(pauseButton);
        topBar.add(centerPanel, BorderLayout.CENTER);
        
        // Create Flag Mode button
        flagModeButton = new JButton("Flag Mode: OFF");
        flagModeButton.setFocusPainted(false);
        flagModeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        flagModeButton.setBackground(new Color(200, 200, 200)); // Gray when OFF
        flagModeButton.setForeground(Color.BLACK);
        flagModeButton.setOpaque(true);
        flagModeButton.setBorderPainted(false);
        flagModeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
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
        
        // Add Flag Mode button to top bar on the right side
        topBar.add(flagModeButton, BorderLayout.EAST);
        
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
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        } else {
            flagModeButton.setText("Flag Mode: OFF");
            flagModeButton.setBackground(new Color(200, 200, 200)); // Gray when OFF
            flagModeButton.setForeground(Color.BLACK);
            flagModeButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));
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
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)));

        // Player names panel
        JPanel namesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        namesPanel.setOpaque(false);

        player1NameLabel = new JLabel("Player 1: ");
        player1NameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        player1NameLabel.setForeground(new Color(91, 161, 255));

        player2NameLabel = new JLabel("Player 2: ");
        player2NameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        player2NameLabel.setForeground(new Color(196, 107, 255));

        namesPanel.add(player1NameLabel);
        namesPanel.add(player2NameLabel);

        // Shared lives label (above combined score)
        sharedLivesLabel = new JLabel("Shared Lives: 0/0");
        sharedLivesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sharedLivesLabel.setForeground(new Color(78, 214, 137)); // Green color
        sharedLivesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Combined score label
        combinedScoreLabel = new JLabel("Combined Score: 0");
        combinedScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        combinedScoreLabel.setForeground(new Color(78, 214, 137));
        combinedScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Turn indicator
        turnIndicatorLabel = new JLabel("Current Turn: Player 1");
        turnIndicatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        turnIndicatorLabel.setForeground(new Color(140, 70, 215));
        turnIndicatorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Timer label
        timerLabel = new JLabel("Time: 0:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(new Color(255, 193, 7)); // Yellow/amber to match pause button
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(sharedLivesLabel, BorderLayout.NORTH);
        centerPanel.add(combinedScoreLabel, BorderLayout.CENTER);
        centerPanel.add(timerLabel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Add turn indicator below timer
        JPanel bottomInfoPanel = new JPanel(new BorderLayout());
        bottomInfoPanel.setOpaque(false);
        bottomInfoPanel.add(turnIndicatorLabel, BorderLayout.CENTER);
        bottomInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JPanel infoContainer = new JPanel(new BorderLayout());
        infoContainer.setOpaque(false);
        infoContainer.add(centerPanel, BorderLayout.CENTER);
        infoContainer.add(bottomInfoPanel, BorderLayout.SOUTH);

        infoPanel.add(namesPanel, BorderLayout.NORTH);
        infoPanel.add(infoContainer, BorderLayout.CENTER);

        // Get the north container that was created in buildTopBar
        JPanel northContainer = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);

        // Add to the north container instead of directly to the main panel
        northContainer.add(infoPanel);
    }

    /**
     * Builds the gameboards panel with two boards side-by-side.
     */
    private void buildGameBoards() {
        JPanel boardsContainer = new JPanel(new BorderLayout());
        boardsContainer.setBackground(new Color(240, 240, 250));
        boardsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Container for both boards
        JPanel boardsWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsWrapper.setOpaque(false);
        
        // Player 1 board container with label
        JPanel player1Container = new JPanel(new BorderLayout(0, 10));
        player1Container.setOpaque(false);
        player1BoardLabel = new JLabel("Player 1's Board", SwingConstants.CENTER);
        player1BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        player1BoardLabel.setForeground(new Color(91, 161, 255));
        player1Container.add(player1BoardLabel, BorderLayout.NORTH);
        
        // Player 1 board panel with custom border
        player1BoardPanel.setBackground(new Color(255, 255, 255));
        player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(10, 10, 10, 10)));
        player1Container.add(player1BoardPanel, BorderLayout.CENTER);
        
        // Player 2 board container with label
        JPanel player2Container = new JPanel(new BorderLayout(0, 10));
        player2Container.setOpaque(false);
        player2BoardLabel = new JLabel("Player 2's Board", SwingConstants.CENTER);
        player2BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        player2BoardLabel.setForeground(new Color(196, 107, 255));
        player2Container.add(player2BoardLabel, BorderLayout.NORTH);
        
        // Player 2 board panel with custom border
        player2BoardPanel.setBackground(new Color(255, 255, 255));
        player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 2),
                new EmptyBorder(10, 10, 10, 10)));
        player2Container.add(player2BoardPanel, BorderLayout.CENTER);
        
        boardsWrapper.add(player1Container);
        boardsWrapper.add(player2Container);
        
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
        
        int currentPlayer = game.getCurrentPlayer();
        Color player1Color = new Color(91, 161, 255); // Blue
        Color player2Color = new Color(196, 107, 255); // Purple
        Color inactiveColor = new Color(200, 200, 200); // Gray
        Color inactiveTextColor = new Color(150, 150, 150); // Light gray for inactive text
        
        // Update Player 1 board border and label
        if (currentPlayer == 1) {
            // Active: thick colored border with glow effect
            player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(player1Color, 4),
                    new EmptyBorder(8, 8, 8, 8)));
            player1BoardPanel.setBackground(new Color(245, 250, 255)); // Very light blue tint
            // Make label bold and colored
            if (player1BoardLabel != null) {
                player1BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                player1BoardLabel.setForeground(player1Color);
            }
        } else {
            // Inactive: thin gray border
            player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(inactiveColor, 2),
                    new EmptyBorder(10, 10, 10, 10)));
            player1BoardPanel.setBackground(new Color(255, 255, 255)); // White
            // Make label less prominent
            if (player1BoardLabel != null) {
                player1BoardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                player1BoardLabel.setForeground(inactiveTextColor);
            }
        }
        
        // Update Player 2 board border and label
        if (currentPlayer == 2) {
            // Active: thick colored border with glow effect
            player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(player2Color, 4),
                    new EmptyBorder(8, 8, 8, 8)));
            player2BoardPanel.setBackground(new Color(255, 245, 255)); // Very light purple tint
            // Make label bold and colored
            if (player2BoardLabel != null) {
                player2BoardLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                player2BoardLabel.setForeground(player2Color);
            }
        } else {
            // Inactive: thin gray border
            player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(inactiveColor, 2),
                    new EmptyBorder(10, 10, 10, 10)));
            player2BoardPanel.setBackground(new Color(255, 255, 255)); // White
            // Make label less prominent
            if (player2BoardLabel != null) {
                player2BoardLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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
            int fontSize = (int) (18 * scaleFactor);
            fontSize = Math.max(14, Math.min(24, fontSize));
            player1NameLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (player2NameLabel != null) {
            int fontSize = (int) (18 * scaleFactor);
            fontSize = Math.max(14, Math.min(24, fontSize));
            player2NameLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (sharedLivesLabel != null) {
            int fontSize = (int) (16 * scaleFactor);
            fontSize = Math.max(12, Math.min(22, fontSize));
            sharedLivesLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (combinedScoreLabel != null) {
            int fontSize = (int) (16 * scaleFactor);
            fontSize = Math.max(12, Math.min(22, fontSize));
            combinedScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }
        if (turnIndicatorLabel != null) {
            int fontSize = (int) (14 * scaleFactor);
            fontSize = Math.max(11, Math.min(20, fontSize));
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
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        } else {
            pauseButton.setText("⏸ Pause");
            pauseButton.setBackground(new Color(255, 193, 7)); // Yellow/amber when not paused
            pauseButton.setForeground(Color.BLACK);
            pauseButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 170, 0), 2),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        }
    }
    
    /**
     * Shows a semi-transparent overlay indicating the game is paused.
     * This overlay is added on top of the existing layout without modifying board structure.
     */
    private void showPauseOverlay() {
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(true);
            pauseOverlay.repaint();
            return;
        }
        
        // Create overlay panel
        pauseOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Pause message
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
                String text = "PAUSED";
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                
                // Subtitle
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                String subtitle = "Click Resume to continue";
                fm = g2.getFontMetrics();
                textWidth = fm.stringWidth(subtitle);
                x = (getWidth() - textWidth) / 2;
                y += textHeight + 20;
                g2.drawString(subtitle, x, y);
                
                g2.dispose();
            }
        };
        pauseOverlay.setOpaque(false);
        pauseOverlay.setLayout(new BorderLayout());
        
        // Add overlay as a glass pane using the root pane
        JFrame rootFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (rootFrame != null) {
            pauseOverlay.setSize(rootFrame.getSize());
            rootFrame.setGlassPane(pauseOverlay);
            pauseOverlay.setVisible(true);
        } else {
            // Fallback: add directly to this panel's center (will cover content)
            add(pauseOverlay, BorderLayout.CENTER);
            pauseOverlay.setVisible(true);
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
            } else {
                // If added to center, remove it
                if (pauseOverlay.getParent() == this) {
                    remove(pauseOverlay);
                }
            }
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

            // Add mouse listeners for left and right click
            addMouseListener(new MouseAdapter() {
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

            // Calculate font size based on button size for proportional scaling
            int buttonSize = Math.min(Math.max(getWidth(), 20), Math.max(getHeight(), 20)); // Ensure minimum size
            int emojiFontSize = Math.max(10, Math.min(buttonSize * 3 / 4, 24)); // Proportional to button size
            int numberFontSize = Math.max(8, Math.min(buttonSize * 2 / 3, 20));
            int emptyFontSize = Math.max(6, Math.min(buttonSize / 3, 14));
            
            // Set font that supports Unicode symbols (for emoji) - default for flagged/mine cells
            setFont(new Font("Segoe UI Emoji", Font.BOLD, emojiFontSize));

            if (cell.isFlagged()) {
                // Flagged state - raised appearance, pink/red background
                setText("🚩");
                setBackground(new Color(255, 180, 180));
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
                    setBackground(new Color(255, 120, 120)); // Bright red
                    setForeground(Color.BLACK);
                } else if (cell instanceof NumberCell) {
                    NumberCell numberCell = (NumberCell) cell;
                    // Use calculated number font size
                    setFont(new Font("Segoe UI", Font.BOLD, numberFontSize));
                    setText(String.valueOf(numberCell.getAdjacentMines()));
                    setBackground(new Color(250, 250, 250)); // Very light gray/almost white - same as empty
                    // Color code numbers for better visibility
                    int num = numberCell.getAdjacentMines();
                    if (num == 1)
                        setForeground(new Color(0, 0, 255)); // Blue
                    else if (num == 2)
                        setForeground(new Color(0, 150, 0)); // Green
                    else if (num == 3)
                        setForeground(new Color(255, 0, 0)); // Red
                    else if (num == 4)
                        setForeground(new Color(0, 0, 150)); // Dark blue
                    else if (num == 5)
                        setForeground(new Color(150, 0, 0)); // Dark red
                    else if (num == 6)
                        setForeground(new Color(0, 150, 150)); // Teal
                    else if (num == 7)
                        setForeground(new Color(0, 0, 0)); // Black
                    else
                        setForeground(new Color(100, 100, 100)); // Gray
                } else if (cell instanceof QuestionCell) {
                    QuestionCell questionCell = (QuestionCell) cell;
                    setText("?");
                    setBackground(new Color(255, 255, 150)); // Bright yellow
                    setForeground(Color.BLACK);
                    // Question cells should be clickable if not opened yet
                    if (!questionCell.isQuestionOpened()) {
                        setEnabled(true); // Enable so user can click to open question
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLoweredBevelBorder(),
                                BorderFactory.createCompoundBorder(
                                        new LineBorder(new Color(255, 200, 0), 2),
                                        new EmptyBorder(0, 0, 0, 0))));
                    } else {
                        setEnabled(false); // Disable if already opened
                    }
                } else if (cell instanceof SurpriseCell) {
                    SurpriseCell surpriseCell = (SurpriseCell) cell;
                    setText("✨");
                    setBackground(new Color(255, 180, 255)); // Bright magenta
                    setForeground(Color.BLACK);
                    // Surprise cells should be clickable when revealed (if not already activated)
                    if (!surpriseCell.isSurpriseActivated()) {
                        setEnabled(true); // Enable so user can click to activate surprise
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLoweredBevelBorder(),
                                BorderFactory.createCompoundBorder(
                                        new LineBorder(new Color(255, 100, 255), 2),
                                        new EmptyBorder(0, 0, 0, 0))));
                    } else {
                        setEnabled(false); // Disable if already activated
                    }
                } else if (cell instanceof EmptyCell) {
                    // Empty revealed cells - very light/white to show they're opened
                    setFont(new Font("Segoe UI", Font.PLAIN, emptyFontSize));
                    setText("");
                    setBackground(new Color(250, 250, 250)); // Very light gray/almost white - clearly different
                                                             // from hidden
                    setForeground(Color.BLACK);
                }
            } else {
                // Hidden state - raised 3D appearance, darker gray background
                setText("");
                setBackground(new Color(140, 140, 140)); // Dark gray - clearly different from revealed
                setForeground(Color.BLACK);
                setEnabled(isCurrentPlayer); // Only enable if it's current player's turn
                // Raised border to show it's clickable/unopened
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            }
        }
    }
}
