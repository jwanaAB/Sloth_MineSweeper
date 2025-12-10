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

/**
 * The main gameplay panel displaying two gameboards side-by-side for two
 * players.
 * Shows player information, turn indicator, and handles cell interactions.
 * 
 * @author Team Sloth
 */
public class GamePanel extends JPanel {

    private JButton homeButton;
    private JLabel player1NameLabel;
    private JLabel player2NameLabel;
    private JLabel sharedLivesLabel;
    private JLabel combinedScoreLabel;
    private JLabel turnIndicatorLabel;
    private final JPanel player1BoardPanel;
    private final JPanel player2BoardPanel;
    private JLabel player1BoardLabel;
    private JLabel player2BoardLabel;
    private final CellButton[][] player1Cells;
    private final CellButton[][] player2Cells;
    private GameController gameController;
    private Game game;
    private boolean gameOver = false;

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
    }

    /**
     * Initializes the game panel with a game instance.
     * 
     * @param game           The Game instance to display
     * @param gameController The GameController to handle interactions
     */
    public void initializeGame(Game game, GameController gameController) {
        this.game = game;
        this.gameController = gameController;
        this.gameOver = false; // Reset game over state for new game

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
        northContainer.add(topBar);

        // Add the combined north container to the main panel
        add(northContainer, BorderLayout.NORTH);
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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(sharedLivesLabel, BorderLayout.NORTH);
        centerPanel.add(combinedScoreLabel, BorderLayout.CENTER);
        centerPanel.add(turnIndicatorLabel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        infoPanel.add(namesPanel, BorderLayout.NORTH);
        infoPanel.add(centerPanel, BorderLayout.CENTER);

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

            setPreferredSize(new Dimension(30, 30));
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
                    
                    // Don't process clicks if game is over
                    if (gameOver) {
                        return;
                    }

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // Left click: reveal cell or handle question/surprise cell
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
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // Right click: flag cell
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

            // Set font that supports Unicode symbols
            setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));

            if (cell.isFlagged()) {
                // Flagged state - raised appearance, pink/red background
                setText("F");
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
                    setText("M");
                    setBackground(new Color(255, 120, 120)); // Bright red
                    setForeground(Color.BLACK);
                } else if (cell instanceof NumberCell) {
                    NumberCell numberCell = (NumberCell) cell;
                    setFont(new Font("Segoe UI", Font.BOLD, 16));
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
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
