package controller;

import model.PlayerState;
import model.SysData;

/**
 * GameController handles game initialization and setup.
 * Receives player name and difficulty from the setup dialog,
 * stores them in the SysData singleton, and initializes the game board.
 */
public class GameController {
    private final SysData sysData;
    private final ScoringService scoringService;
    private PlayerState currentPlayer;

    public GameController(SysData sysData) {
        this.sysData = sysData;
        this.scoringService = new ScoringService(sysData);
        this.currentPlayer = null;
    }

    /**
     * Initializes the game with two player names and difficulty.
     * This method receives setup data and stores it in the SysData singleton,
     * then initializes the game board based on the selected difficulty.
     *
     * @param player1Name The name of player 1
     * @param player2Name The name of player 2
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     */
    public void initializeGame(String player1Name, String player2Name, int difficulty) {
        // Store setup data in singleton
        sysData.setPlayer1Name(player1Name);
        sysData.setPlayer2Name(player2Name);
        sysData.setCurrentDifficulty(difficulty);

        // Initialize player states with lives and resources based on difficulty
        PlayerState[] playerStates = scoringService.initializePlayerStates(player1Name, player2Name, difficulty);
        sysData.setPlayer1State(playerStates[0]);
        sysData.setPlayer2State(playerStates[1]);
        currentPlayer = playerStates[0]; // Start with player 1

        // Initialize game board based on difficulty
        initializeGameBoard(difficulty);
    }

    /**
     * Initializes the game board according to the selected difficulty level.
     * This method will be extended when the actual game board logic is implemented.
     *
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     */
    private void initializeGameBoard(int difficulty) {
        // TODO: Implement actual game board initialization
        // For now, this is a placeholder that will be extended with:
        // - Board size calculation based on difficulty
        // - Mine/Question distribution
        // - Board state initialization
        
        System.out.println("Initializing game board...");
        System.out.println("Player 1: " + sysData.getPlayer1Name());
        System.out.println("Player 2: " + sysData.getPlayer2Name());
        System.out.println("Difficulty: " + difficulty);
        
        // Example: Difficulty-based board configuration
        int boardSize = calculateBoardSize(difficulty);
        int questionCount = calculateQuestionCount(difficulty);
        
        System.out.println("Board Size: " + boardSize + "x" + boardSize);
        System.out.println("Question Count: " + questionCount);
        
        // Future implementation will create the actual game board here
    }

    /**
     * Calculates the board size based on difficulty level.
     * According to project requirements:
     * Easy: 9x9 board (81 cells, 9 rows, 9 columns)
     * Medium: 13x13 board (169 cells, 13 rows, 13 columns)
     * Hard: 16x16 board (256 cells, 16 rows, 16 columns)
     * 
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return The board size (width/height)
     */
    private int calculateBoardSize(int difficulty) {
        switch (difficulty) {
            case 1: return 9;   // Easy: 9x9
            case 2: return 13;  // Medium: 13x13
            case 3: return 16;  // Hard: 16x16
            default: return 9;  // Default to Easy
        }
    }

    /**
     * Calculates the number of mines based on difficulty level.
     * According to project requirements:
     * Easy: 9 mines (matching board size)
     * Medium: 13 mines (matching board size)
     * Hard: 16 mines (matching board size)
     * 
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return The number of mines
     */
    private int calculateQuestionCount(int difficulty) {
        switch (difficulty) {
            case 1: return 9;   // Easy: 9 mines
            case 2: return 13;  // Medium: 13 mines
            case 3: return 16;  // Hard: 16 mines
            default: return 9;  // Default to Easy
        }
    }

    /**
     * Gets player 1 name from SysData.
     * 
     * @return Player 1 name, or null if not set
     */
    public String getPlayer1Name() {
        return sysData.getPlayer1Name();
    }

    /**
     * Gets player 2 name from SysData.
     * 
     * @return Player 2 name, or null if not set
     */
    public String getPlayer2Name() {
        return sysData.getPlayer2Name();
    }

    /**
     * Gets the current difficulty level from SysData.
     * 
     * @return The current difficulty level (1=Easy, 2=Medium, 3=Hard)
     */
    public int getCurrentDifficulty() {
        return sysData.getCurrentDifficulty();
    }

    /**
     * Gets the scoring service instance.
     * 
     * @return The ScoringService instance
     */
    public ScoringService getScoringService() {
        return scoringService;
    }

    /**
     * Gets the current active player state.
     * 
     * @return The current player's state
     */
    public PlayerState getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets player 1's state.
     * 
     * @return Player 1's state
     */
    public PlayerState getPlayer1State() {
        return sysData.getPlayer1State();
    }

    /**
     * Gets player 2's state.
     * 
     * @return Player 2's state
     */
    public PlayerState getPlayer2State() {
        return sysData.getPlayer2State();
    }

    /**
     * Switches to the next player.
     */
    public void switchPlayer() {
        if (currentPlayer == sysData.getPlayer1State()) {
            currentPlayer = sysData.getPlayer2State();
        } else {
            currentPlayer = sysData.getPlayer1State();
        }
    }

    /**
     * Updates the score display. This method will be called after each move
     * to update the UI with the current scores and lives.
     * TODO: This will be integrated with the game board view when it's implemented.
     */
    public void updateScoreDisplay() {
        // This method will trigger UI updates when the game board is implemented
        // For now, it's a placeholder that can be called after scoring operations
        PlayerState p1 = sysData.getPlayer1State();
        PlayerState p2 = sysData.getPlayer2State();
        
        if (p1 != null && p2 != null) {
            System.out.println("Score Update:");
            System.out.println(p1.getPlayerName() + " - Score: " + p1.getScore() + ", Lives: " + p1.getLives());
            System.out.println(p2.getPlayerName() + " - Score: " + p2.getScore() + ", Lives: " + p2.getLives());
        }
    }
}

