package view;

import controller.GameController;
import model.Cell;
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
 * The main gameplay panel displaying two gameboards side-by-side for two players.
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
    private final CellButton[][] player1Cells;
    private final CellButton[][] player2Cells;
    private GameController gameController;
    private Game game;
    
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
     * @param game The Game instance to display
     * @param gameController The GameController to handle interactions
     */
    public void initializeGame(Game game, GameController gameController) {
        this.game = game;
        this.gameController = gameController;
        
        // Update player names
        player1NameLabel.setText("Player 1: " + game.getPlayer1Name());
        player2NameLabel.setText("Player 2: " + game.getPlayer2Name());
        
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
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        homeButton = new JButton("\u2190 Home");
        homeButton.setFocusPainted(false);
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        homeButton.setBackground(new Color(245, 245, 245));
        homeButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        topBar.add(homeButton, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
    }
    
    /**
     * Builds the player information panel showing names and combined score.
     */
    private void buildPlayerInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
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
        
        add(infoPanel, BorderLayout.NORTH);
    }
    
    /**
     * Builds the gameboards panel with two boards side-by-side.
     */
    private void buildGameBoards() {
        JPanel boardsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsContainer.setBackground(new Color(240, 240, 250));
        boardsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Player 1 board panel
        player1BoardPanel.setBackground(new Color(255, 255, 255));
        player1BoardPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Player 2 board panel
        player2BoardPanel.setBackground(new Color(255, 255, 255));
        player2BoardPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        boardsContainer.add(player1BoardPanel);
        boardsContainer.add(player2BoardPanel);
        
        add(boardsContainer, BorderLayout.CENTER);
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
     * @param boardPanel The panel to add cells to
     * @param cellButtons The 2D array to store cell button references
     * @param board The GameBoard model
     * @param player The player number (1 or 2)
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
            game.getCurrentPlayer() == 1 ? 
            new Color(91, 161, 255) : 
            new Color(196, 107, 255)
        );
        
        // Update player 1 board
        updateBoard(player1Cells, game.getPlayer1Board(), 1);
        
        // Update player 2 board
        updateBoard(player2Cells, game.getPlayer2Board(), 2);
    }
    
    /**
     * Updates the visual representation of a board.
     * 
     * @param cellButtons The 2D array of cell buttons
     * @param board The GameBoard model
     * @param player The player number
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
            homeButton.addActionListener(listener);
        }
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
         * @param row The row index
         * @param col The column index
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
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            
            // Add mouse listeners for left and right click
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameController == null || game == null) {
                        return;
                    }
                    
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // Left click: reveal cell or handle question cell
                        Cell cell = game.getBoard(player).getCell(row, col);
                        if (cell != null && cell.isRevealed() && 
                            cell.getType() == Cell.CellType.QUESTION && !cell.isQuestionOpened()) {
                            // Question cell already revealed - offer to open
                            gameController.handleQuestionCellClick(row, col, player);
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
         * @param cell The Cell model
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
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
            } else if (cell.isRevealed()) {
                // Revealed state - sunken/flat appearance to show it's been opened
                setEnabled(false);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
                Cell.CellType type = cell.getType();
                
                switch (type) {
                    case MINE:
                        setText("M");
                        setBackground(new Color(255, 120, 120)); // Bright red
                        setForeground(Color.BLACK);
                        break;
                    case NUMBER:
                        setFont(new Font("Segoe UI", Font.BOLD, 16));
                        setText(String.valueOf(cell.getAdjacentMines()));
                        setBackground(new Color(250, 250, 250)); // Very light gray/almost white - same as empty
                        // Color code numbers for better visibility
                        int num = cell.getAdjacentMines();
                        if (num == 1) setForeground(new Color(0, 0, 255)); // Blue
                        else if (num == 2) setForeground(new Color(0, 150, 0)); // Green
                        else if (num == 3) setForeground(new Color(255, 0, 0)); // Red
                        else if (num == 4) setForeground(new Color(0, 0, 150)); // Dark blue
                        else if (num == 5) setForeground(new Color(150, 0, 0)); // Dark red
                        else if (num == 6) setForeground(new Color(0, 150, 150)); // Teal
                        else if (num == 7) setForeground(new Color(0, 0, 0)); // Black
                        else setForeground(new Color(100, 100, 100)); // Gray
                        break;
                    case QUESTION:
                        setText("?");
                        setBackground(new Color(255, 255, 150)); // Bright yellow
                        setForeground(Color.BLACK);
                        // Question cells should be clickable if not opened yet
                        if (!cell.isQuestionOpened()) {
                            setEnabled(true); // Enable so user can click to open question
                            setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLoweredBevelBorder(),
                                BorderFactory.createCompoundBorder(
                                    new LineBorder(new Color(255, 200, 0), 2),
                                    new EmptyBorder(0, 0, 0, 0)
                                )
                            ));
                        } else {
                            setEnabled(false); // Disable if already opened
                        }
                        break;
                    case SURPRISE:
                        setText("✨");
                        setBackground(new Color(255, 180, 255)); // Bright magenta
                        setForeground(Color.BLACK);
                        break;
                    case EMPTY:
                    default:
                        // Empty revealed cells - very light/white to show they're opened
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        setText("");
                        setBackground(new Color(250, 250, 250)); // Very light gray/almost white - clearly different from hidden
                        setForeground(Color.BLACK);
                        break;
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
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
            }
        }
    }
}

