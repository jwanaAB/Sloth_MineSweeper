package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import javax.swing.*;
import java.awt.Window;
import model.*;
import view.GamePanel;
import view.QuestionDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller for managing game logic and interactions between the Game model
 * and GamePanel view. Handles cell reveals, flagging, turn switching, and
 * question cell interactions.
 * 
 * @author Team Sloth
 */
public class GameController {
    
    private final Game game;
    private final GamePanel gamePanel;
    private final ScoringService scoringService;
    @SuppressWarnings("unused")
    private final Runnable onReturnToMainMenu;
    private final SoundManager soundManager;
    private MinesweeperBot aiBot; // AI bot for playing when player 2 is AI
    private Timer aiMoveTimer; // Timer for scheduling AI moves with delay
    private Timer aiCheckTimer; // Periodic timer to check if AI should move (fallback)
    private final java.util.Random random = new java.util.Random(); // Random for AI decision making
    private boolean gameOver = false;
    private final LocalDateTime gameStartTime; // Track when game started
    private Timer gameTimer; // Timer that updates every second
    private LocalDateTime pauseStartTime; // When the game was paused
    private long totalPausedDurationSeconds = 0; // Total time paused
    private boolean isPaused = false;
    
    /**
     * Constructs a new GameController.
     * 
     * @param game The Game model instance
     * @param gamePanel The GamePanel view instance
     * @param questionLogic The QuestionLogic instance for loading questions
     * @param onReturnToMainMenu Callback to return to main menu when game ends
     */
    public GameController(Game game, GamePanel gamePanel, QuestionLogic questionLogic, Runnable onReturnToMainMenu) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.scoringService = new ScoringService(SysData.getInstance());
        this.onReturnToMainMenu = onReturnToMainMenu;
        this.soundManager = SoundManager.getInstance();
        this.gameStartTime = LocalDateTime.now(); // Record game start time
        
        // Initialize AI bot if player 2 is AI
        if (game.isPlayer2AI()) {
            this.aiBot = new MinesweeperBot();
        }
        
        // Initialize the game panel
        gamePanel.initializeGame(game, this);
        
        // Add observer to detect when it's AI's turn
        game.addObserver(new GameObserver() {
            @Override
            public void onTurnChanged(int currentPlayer, String playerName) {
                // Use SwingUtilities to ensure this runs on EDT
                SwingUtilities.invokeLater(() -> {
                    // Check if it's AI's turn and trigger AI move
                    if (game.isCurrentPlayerAI() && !gameOver && !isPaused && aiBot != null) {
                        scheduleAIMove();
                    }
                });
            }
            
            @Override
            public void onCellRevealed(int row, int col, int player) {
                // Not needed for AI, but required by interface
            }
            
            @Override
            public void onScoreChanged(int score) {
                // Not needed for AI, but required by interface
            }
            
            @Override
            public void onLivesChanged(int lives, int totalLives) {
                // Not needed for AI, but required by interface
            }
            
            @Override
            public void onGameOver(boolean won, int winner) {
                // Stop AI timer when game ends
                if (aiMoveTimer != null) {
                    aiMoveTimer.stop();
                }
            }
        });
        
        // Start the game timer
        startGameTimer();
        
        // If AI mode, set up periodic check to ensure AI moves when it should (fallback mechanism)
        if (game.isPlayer2AI()) {
            // Initial check if it's AI's turn
            SwingUtilities.invokeLater(() -> {
                if (game.isCurrentPlayerAI() && !gameOver && aiBot != null) {
                    scheduleAIMove();
                }
            });
            
            // Add a periodic check every 1 second to ensure AI moves if stuck
            // This is a fallback in case the observer doesn't fire or timer gets cancelled
            aiCheckTimer = new Timer(1000, e -> {
                if (game.isCurrentPlayerAI() && !gameOver && !isPaused && aiBot != null) {
                    // Check if AI timer is not running and it's AI's turn
                    // If timer is null or not running, schedule a move immediately
                    boolean shouldMove = (aiMoveTimer == null || !aiMoveTimer.isRunning());
                    if (shouldMove) {
                        // Schedule immediately with a short delay
                        scheduleAIMove();
                    }
                }
            });
            aiCheckTimer.start();
        }
    }
    
    /**
     * Starts the game timer that updates every second.
     */
    private void startGameTimer() {
        // Stop any existing timer first
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
        
        // Reset timer state
        totalPausedDurationSeconds = 0;
        isPaused = false;
        pauseStartTime = null;
        
        // Create and start new timer
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Only update if this controller's game is still active
                if (gamePanel.getGameController() == GameController.this && !gameOver && !isPaused) {
                    updateTimerDisplay();
                }
            }
        });
        
        // Immediately update display to show 0:00
        updateTimerDisplay();
        
        // Start the timer
        gameTimer.start();
    }
    
    /**
     * Updates the timer display in the game panel.
     */
    private void updateTimerDisplay() {
        long elapsedSeconds = getElapsedTimeSeconds();
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        String timeString = String.format("%d:%02d", minutes, seconds);
        gamePanel.updateTimerDisplay(timeString);
    }
    
    /**
     * Gets the elapsed game time in seconds (excluding paused time).
     * 
     * @return Elapsed time in seconds
     */
    public long getElapsedTimeSeconds() {
        if (isPaused) {
            // If currently paused, return time up to when pause started
            return Duration.between(gameStartTime, pauseStartTime).getSeconds() 
                   - totalPausedDurationSeconds;
        } else {
            // Calculate total elapsed time minus total paused time
            long totalElapsed = Duration.between(gameStartTime, LocalDateTime.now()).getSeconds();
            return totalElapsed - totalPausedDurationSeconds;
        }
    }
    
    /**
     * Pauses the game timer.
     */
    public void pauseTimer() {
        if (!isPaused) {
            isPaused = true;
            pauseStartTime = LocalDateTime.now();
            if (gameTimer != null) {
                gameTimer.stop();
            }
        }
    }
    
    /**
     * Resumes the game timer.
     */
    public void resumeTimer() {
        if (isPaused) {
            // Calculate how long we were paused and add to total paused duration
            long pauseDuration = Duration.between(pauseStartTime, LocalDateTime.now()).getSeconds();
            totalPausedDurationSeconds += pauseDuration;
            isPaused = false;
            if (gameTimer != null) {
                gameTimer.start();
            }
        }
    }
    
    /**
     * Schedules an AI move with a delay to make it feel more natural.
     * The AI will make exactly one move per turn.
     */
    private void scheduleAIMove() {
        // Don't schedule if already scheduled and running (prevent duplicates)
        if (aiMoveTimer != null && aiMoveTimer.isRunning()) {
            return;
        }
        
        // Stop any existing timer
        if (aiMoveTimer != null) {
            aiMoveTimer.stop();
        }
        
        // Increased delay of 1500-2500ms to make AI moves feel more natural and give human time to see
        int delay = 1500 + (int)(Math.random() * 1000);
        
        aiMoveTimer = new Timer(delay, e -> {
            // Double-check conditions before making move
            if (game.isCurrentPlayerAI() && !gameOver && !isPaused && aiBot != null) {
                makeAIMove();
            }
        });
        aiMoveTimer.setRepeats(false);
        aiMoveTimer.start();
    }
    
    /**
     * Makes a move for the AI bot.
     * The bot will:
     * 1. First check for unactivated surprise cells (activate these)
     * 2. Otherwise, select and reveal a cell using bot logic
     * 
     * Note: The bot does NOT automatically open question cells. If it reveals a question cell,
     * the turn switches and the human can click on it later when it's their turn.
     */
    private void makeAIMove() {
        if (gameOver || !game.isCurrentPlayerAI() || aiBot == null) {
            return;
        }
        
        GameBoard aiBoard = game.getCurrentBoard(); // Player 2's board
        
        // Check if game is already won (all non-mine cells revealed)
        if (aiBoard.isGameWon()) {
            // Game is won, but let the normal win detection handle it
            return;
        }
        
        // Priority 1: Check for unactivated surprise cells (these can be activated immediately)
        int[] surpriseCell = aiBot.findUnactivatedSurpriseCell(aiBoard);
        if (surpriseCell != null) {
            // Verify the cell is still unrevealed before trying to reveal it
            Cell cell = aiBoard.getCell(surpriseCell[0], surpriseCell[1]);
            if (cell != null && !cell.isRevealed()) {
                handleCellReveal(surpriseCell[0], surpriseCell[1], 2);
                return;
            }
        }
        
        // Priority 2: Sometimes open revealed question cells for the human to answer
        // Check if there are any revealed but unopened question cells
        // Open them with 30% probability to make it feel natural (not always, not never)
        int[] questionCell = aiBot.findUnopenedQuestionCell(aiBoard);
        if (questionCell != null && random.nextDouble() < 0.30) {
            // Open the question cell directly (bypassing normal validation since it's AI's turn)
            // This will show the question dialog to the human, and the turn will switch after
            Cell cell = aiBoard.getCell(questionCell[0], questionCell[1]);
            if (cell != null && cell instanceof QuestionCell) {
                QuestionCell qCell = (QuestionCell) cell;
                if (!qCell.isQuestionOpened()) {
                    // Switch turn to human - AI's turn ends when opening question
                    game.switchTurn();
                    // Now open the question for the human to answer
                    openQuestion(qCell, 2);
                    return;
                }
            }
        }
        
        // Priority 3: Select and reveal a cell using bot logic
        // The bot will NOT prioritize question cells - it will reveal them normally
        // and let the human click on them later
        // Try up to 5 times to find a valid unrevealed cell (in case of race conditions)
        for (int attempt = 0; attempt < 5; attempt++) {
            int[] cellToReveal = aiBot.selectCellToReveal(aiBoard);
            if (cellToReveal != null) {
                // Verify the cell is still unrevealed before trying to reveal it
                Cell cell = aiBoard.getCell(cellToReveal[0], cellToReveal[1]);
                if (cell != null && !cell.isRevealed() && !cell.isFlagged()) {
                    handleCellReveal(cellToReveal[0], cellToReveal[1], 2);
                    return; // Successfully made a move
                }
                // Cell was already revealed or flagged, try again
            } else {
                break; // Bot couldn't find a cell, use fallback
            }
        }
        
        // Fallback: if bot couldn't find a valid cell, manually find any unrevealed cell
        int[] fallbackCell = findAnyUnrevealedCell(aiBoard);
        if (fallbackCell != null) {
            handleCellReveal(fallbackCell[0], fallbackCell[1], 2);
        } else {
            // No unrevealed cells - game should be won, but just in case
            System.out.println("AI: No unrevealed cells found - game may be won");
        }
    }
    
    /**
     * Fallback method to find any unrevealed, unflagged cell on the board.
     * Used when the bot's logic fails to find a cell.
     * 
     * @param board The game board
     * @return An array [row, col] of an unrevealed cell, or null if none found
     */
    private int[] findAnyUnrevealedCell(GameBoard board) {
        if (board == null) {
            return null;
        }
        
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = board.getCell(i, j);
                if (cell != null && cell.isHidden() && !cell.isFlagged()) {
                    return new int[]{i, j};
                }
            }
        }
        
        return null;
    }
    
    /**
     * Stops the game timer (called when game ends).
     */
    private void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
        if (aiMoveTimer != null) {
            aiMoveTimer.stop();
            aiMoveTimer = null;
        }
        if (aiCheckTimer != null) {
            aiCheckTimer.stop();
            aiCheckTimer = null;
        }
    }
    
    /**
     * Stops the game timer for cleanup (called when a new game starts).
     * This is public so GamePanel can call it to clean up old controllers.
     */
    public void stopTimerForCleanup() {
        stopGameTimer();
    }
    
    /**
     * Handles a cell reveal action initiated by the user.
     * 
     * @param row The row index of the cell
     * @param col The column index of the cell
     * @param player The player number (1 or 2) attempting the action
     */
    public void handleCellReveal(int row, int col, int player) {
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Validate that it's the current player's turn
        if (!game.canRevealCell(row, col, player)) {
            // Show visual indicator instead of message
            gamePanel.showWrongTurnIndicator();
            return;
        }
        
        // Check if it's a question cell that needs special handling (already revealed)
        Cell cell = game.getBoard(player).getCell(row, col);
        if (cell != null && cell.isRevealed() && 
            cell instanceof QuestionCell && !((QuestionCell) cell).isQuestionOpened()) {
            // Question cell already revealed - offer to open question
            // If AI revealed it, the human will answer
            handleQuestionCellClick(row, col, player);
            return;
        }
        
        // Don't process if cell is already revealed (except question cells handled above)
        if (cell != null && cell.isRevealed()) {
            // If it's the AI's turn and it tried to reveal an already-revealed cell,
            // trigger another AI move attempt (the retry logic in makeAIMove should handle this)
            if (game.isCurrentPlayerAI() && player == 2) {
                // Schedule another AI move attempt after a short delay
                SwingUtilities.invokeLater(() -> {
                    if (game.isCurrentPlayerAI() && !gameOver && !isPaused && aiBot != null) {
                        // Try again with a new cell selection
                        makeAIMove();
                    }
                });
            }
            return;
        }
        
        // Reveal the cell (will unflag if needed)
        boolean mineHit = game.revealCell(row, col);
        
        // Observer pattern will automatically update UI when cell is revealed
        
        // Get the revealed cell to check its type
        Cell revealedCell = game.getBoard(player).getCell(row, col);
        String playerName = game.getCurrentPlayerName();
        int gameDifficulty = convertDifficultyToInt(game.getDifficulty());
        
        if (mineHit) {
            // Mine hit - play bomb sound and score the mine hit
            soundManager.playSound("bomb");
            scoringService.scoreMineHit(game, playerName);
            showMessage(
                playerName + " hit a mine!",
                "Mine Hit!",
                JOptionPane.WARNING_MESSAGE
            );
            
            // Check if game is over due to lives running out
            if (game.getSharedLives() <= 0) {
                handleGameOver(false);
                return;
            }
            
            // Switch turn after mine hit
            game.switchTurn();
        } else {
            // Score the revealed cell (only for the initially clicked cell, not cascade reveals)
            if (revealedCell != null) {
                scoreCellReveal(revealedCell, playerName, gameDifficulty);
            }
            
            // Check if current player won (BEFORE switching turn for non-mine cells)
            // Only check if ALL non-mine cells are revealed
            GameBoard currentBoard = game.getCurrentBoard();
            if (currentBoard.isGameWon()) {
                // Set game over in the model
                game.setGameOver(true);
                showMessage(
                    "Congratulations! Both players won!\n" +
                    game.getPlayer1Name() + " and " + game.getPlayer2Name() + 
                    " successfully revealed all cells on their boards!",
                    "Game Won!",
                    JOptionPane.INFORMATION_MESSAGE
                );
                handleGameOver(true);
                return; // Don't continue with turn switching
            }
            
            // Handle non-mine cells
            if (revealedCell != null) {
                // Check if it's a question or surprise cell
                if (revealedCell instanceof QuestionCell) {
                    // Question cell revealed - just switch turn
                    // The human can click on it later when it's their turn to open the question
                    // The bot does NOT automatically open question cells
                    game.switchTurn();
                } else if (revealedCell instanceof SurpriseCell) {
                    // Surprise cell revealed - switch turn (no message)
                    game.switchTurn();
                } else {
                    // Regular cell (number, empty) - switch turn
                    game.switchTurn();
                }
            } else {
                // No cell revealed (shouldn't happen, but just in case)
                game.switchTurn();
            }
        }
        
        // Observer pattern will automatically update UI when turn changes
    }
    
    /**
     * Handles a cell flag action initiated by the user.
     * 
     * @param row The row index of the cell
     * @param col The column index of the cell
     * @param player The player number (1 or 2) attempting the action
     */
    public void handleCellFlag(int row, int col, int player) {
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Check if it's the current player's turn
        if (player != game.getCurrentPlayer()) {
            // Show visual indicator instead of message
            gamePanel.showWrongTurnIndicator();
            return;
        }
        
        // Get the cell to check if it can be flagged
        Cell cell = game.getBoard(player).getCell(row, col);
        if (cell == null) {
            return;
        }
        
        // Check if the cell is revealed (cannot flag revealed cells)
        if (cell.isRevealed()) {
            showMessage("Cannot flag revealed cells!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the flag state before toggling
        boolean wasFlagged = cell.isFlagged();
        
        // Toggle flag
        game.flagCell(row, col);
        
        // Play flag sound only when placing a flag (not removing)
        if (!wasFlagged && cell.isFlagged()) {
            soundManager.playSound("flag");
        }
        
        // Flags should not affect scoring - no scoring logic for flags
        
        // Update UI
        gamePanel.updateUI();
    }
    
    /**
     * Handles clicking on a revealed question cell.
     * Can be called from GamePanel when user clicks on an already-revealed question cell.
     * 
     * @param row The row index of the question cell
     * @param col The column index of the question cell
     * @param player The player number (1 or 2)
     */
    public void handleQuestionCellClick(int row, int col, int player) {
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Validate that it's the current player's turn
        if (player != game.getCurrentPlayer()) {
            // Show visual indicator instead of message
            gamePanel.showWrongTurnIndicator();
            return;
        }
        
        Cell cell = game.getBoard(player).getCell(row, col);
        
        if (cell == null || !(cell instanceof QuestionCell)) {
            return;
        }
        
        QuestionCell questionCell = (QuestionCell) cell;
        
        // Check if question already opened
        if (questionCell.isQuestionOpened()) {
            showMessage("This question has already been opened.", "Question Already Used", 
                       JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Directly open the question without asking for confirmation
        openQuestion(questionCell, player);
    }
    
    /**
     * Handles clicking on a revealed surprise cell.
     * Can be called from GamePanel when user clicks on an already-revealed surprise cell.
     * 
     * @param row The row index of the surprise cell
     * @param col The column index of the surprise cell
     * @param player The player number (1 or 2)
     */
    public void handleSurpriseCellClick(int row, int col, int player) {
        // Don't allow actions if game is over
        if (gameOver) {
            return;
        }
        
        // Validate that it's the current player's turn
        if (player != game.getCurrentPlayer()) {
            // Show visual indicator instead of message
            gamePanel.showWrongTurnIndicator();
            return;
        }
        
        Cell cell = game.getBoard(player).getCell(row, col);
        
        if (cell == null || !(cell instanceof SurpriseCell)) {
            return;
        }
        
        SurpriseCell surpriseCell = (SurpriseCell) cell;
        
        // Check if surprise already activated
        if (surpriseCell.isSurpriseActivated()) {
            showMessage("This surprise cell has already been activated.", "Surprise Already Used", 
                       JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Directly activate the surprise cell without asking for confirmation
        activateSurpriseCell(surpriseCell, player);
    }
    
    /**
     * Opens a question for the player to answer.
     * 
     * @param questionCell The question cell
     * @param player The player number
     */
    private void openQuestion(QuestionCell questionCell, int player) {
        Question question = questionCell.getQuestion();
        
        if (question == null) {
            showMessage("No question available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show question dialog and only mark opened after an answer is provided
        boolean answered = showQuestionDialog(question, player);
        if (answered) {
            questionCell.markQuestionOpened();
            gamePanel.updateUI();
        }
    }
    
    /**
     * Shows a dialog with a question for the player to answer.
     * If the AI revealed the question cell, the human player (Player 1) will answer it.
     * 
     * @param question The Question object
     * @param player The player number (the player whose board the question is on)
     */
    private boolean showQuestionDialog(Question question, int player) {
        // If AI (player 2) revealed a question cell, the human (player 1) answers it
        // Otherwise, the player who revealed it answers it
        final String playerName;
        if (player == 2 && game.isPlayer2AI()) {
            // AI revealed it - human answers
            playerName = game.getPlayer1Name();
        } else {
            // Normal case - the player who revealed it answers
            playerName = player == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
        }

        // Get the parent frame for the dialog
        JFrame parentFrame = null;
        Window parentWindow = SwingUtilities.getWindowAncestor(gamePanel);
        if (parentWindow instanceof JFrame) {
            parentFrame = (JFrame) parentWindow;
        }

        Integer choice = null;
        while (choice == null) {
            QuestionDialog dialog = new QuestionDialog(parentFrame, question, playerName);
            choice = dialog.showDialog();

            if (choice == null) {
                // Dialog was closed without selection - show warning and loop again
                JOptionPane.showMessageDialog(
                    gamePanel,
                    "You must answer the question before closing.",
                    "Answer Required",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }

        String selectedAnswer = "";
        if (choice == 0) selectedAnswer = "A";
        else if (choice == 1) selectedAnswer = "B";
        else if (choice == 2) selectedAnswer = "C";
        else if (choice == 3) selectedAnswer = "D";
        
        // Check if correct
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);
        
        // Score the question activation
        // The scoring player is the one whose board the question is on (the one who revealed it)
        String scoringPlayerName = player == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
        int gameDifficulty = convertDifficultyToInt(game.getDifficulty());
        int questionType = question.getDifficulty(); // Question difficulty maps to question type (1-4)
        
        scoringService.scoreQuestionCellActivated(game, scoringPlayerName, gameDifficulty, questionType, isCorrect);
        
        if (isCorrect) {
            soundManager.playSound("correct-answer");
            showMessage("Correct! Well done!", "Correct Answer", JOptionPane.INFORMATION_MESSAGE);
        } else {
            soundManager.playSound("wrong-answer");
            showMessage("Incorrect answer.", 
                       "Wrong Answer", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Handle turn switching after answering question
        // If AI (player 2) opened the question, the human answered it, so turn should stay with human
        // If human opened their own question, switch turn normally
        if (player == 2 && game.isPlayer2AI()) {
            // AI opened the question - human answered it
            // Turn should already be with human (switched before opening question)
            // Don't switch turn - human should play next on their board
            // Just ensure turn is with human (Player 1)
            if (game.getCurrentPlayer() != 1) {
                game.switchTurn(); // Ensure it's human's turn
            }
        } else {
            // Human opened their own question - switch turn normally
            game.switchTurn();
        }
        
        // Observer pattern will automatically update UI when turn changes

        return true;
    }
    
    /**
     * Handles game over scenarios (win or lose).
     * 
     * @param won true if the game was won, false if lives ran out
     */
    private void handleGameOver(boolean won) {
        // Mark game as over to prevent further actions
        gameOver = true;
        
        // Disable all game interactions
        gamePanel.setGameOver(true);
        
        // Convert remaining lives to points
        int pointsAdded = scoringService.convertRemainingLivesToPoints(game);
        
        String message;
        String title;
        
        if (won) {
            soundManager.playSound("victory");
            message = "Congratulations! Both " + game.getPlayer1Name() + " and " + 
                     game.getPlayer2Name() + " won together!\n" +
                     "Final Score: " + game.getCombinedScore() + " points";
            title = "Game Won";
        } else {
            soundManager.playSound("game-over");
            message = "Game Over! Both " + game.getPlayer1Name() + " and " + 
                     game.getPlayer2Name() + " lost.\n" +
                     "Shared lives ran out.\n" +
                     "Final Score: " + game.getCombinedScore() + " points";
            if (pointsAdded > 0) {
                message += "\n" + pointsAdded + " points added from remaining lives.";
            }
            title = "Game Over";
        }
        
        // Reveal all cells for both players
        game.revealAllCells();
        
        // Show game over message
        JOptionPane.showMessageDialog(
            gamePanel,
            message,
            title,
            won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
        
        // Update UI to show final state with all cells revealed
        gamePanel.updateUI();
        
        // Save game history
        saveGameHistory(won);
        
        // Stay on game board view - do not return to main menu
    }
    
    /**
     * Saves the completed game to history.
     * 
     * @param won true if the game was won, false otherwise
     */
    private void saveGameHistory(boolean won) {
        try {
            // Stop the timer
            stopGameTimer();
            
            // Calculate game duration (use elapsed time which accounts for pauses)
            long durationSeconds = getElapsedTimeSeconds();
            
            // Get game data
            Game.Difficulty difficulty = game.getDifficulty();
            LocalDate date = LocalDate.now();
            String player1Name = game.getPlayer1Name();
            String player2Name = game.getPlayer2Name();
            int combinedScore = game.getCombinedScore();
            int remainingHearts = game.getSharedLives(); // Remaining shared lives
            
            // Create and save game history entry
            GameHistory history = new GameHistory(
                difficulty,
                date,
                durationSeconds,
                player1Name,
                player2Name,
                combinedScore,
                remainingHearts
            );
            
            System.out.println("Saving game history: " + player1Name + " vs " + player2Name + 
                             ", Score: " + combinedScore + ", Hearts: " + remainingHearts);
            
            SysData.getInstance().addGameHistory(history);
            
            System.out.println("Game history saved successfully. Total games: " + 
                             SysData.getInstance().getGameHistory().size());
        } catch (Exception e) {
            System.err.println("Error saving game history: " + e.getMessage());
            e.printStackTrace();
            // Show error to user
            JOptionPane.showMessageDialog(
                gamePanel,
                "Error saving game history: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Shows a message dialog.
     * 
     * @param message The message to display
     * @param title The dialog title
     * @param messageType The message type (from JOptionPane constants)
     */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(gamePanel, message, title, messageType);
    }
    
    /**
     * Gets the Game instance.
     * 
     * @return The Game instance
     */
    public Game getGame() {
        return game;
    }
    
    /**
     * Scores a cell reveal based on the cell type.
     * 
     * @param cell The cell that was revealed
     * @param playerName The name of the player who revealed it
     * @param gameDifficulty The game difficulty (1=Easy, 2=Medium, 3=Hard)
     */
    private void scoreCellReveal(Cell cell, String playerName, int gameDifficulty) {
        if (cell == null) return;
        
        if (cell instanceof MineCell) {
            // Mine hit is handled separately in handleCellReveal
        } else if (cell instanceof NumberCell) {
            scoringService.scoreNumberedCellRevealedCorrectly(game, playerName, ((NumberCell) cell).getAdjacentMines());
        } else if (cell instanceof EmptyCell) {
            scoringService.scoreEmptyCellRevealedCorrectly(game, playerName);
        } else if (cell instanceof QuestionCell) {
            scoringService.scoreQuestionCellRevealedCorrectly(game, playerName);
        } else if (cell instanceof SurpriseCell) {
            scoringService.scoreSurpriseCellRevealedCorrectly(game, playerName);
        }
    }
    
    /**
     * Activates a surprise cell and applies its effects.
     * 
     * @param surpriseCell The surprise cell to activate
     * @param player The player number
     */
    private void activateSurpriseCell(SurpriseCell surpriseCell, int player) {
        if (surpriseCell == null) {
            return;
        }
        
        // Mark surprise as activated
        surpriseCell.markSurpriseActivated();
        
        // Play surprise sound
        soundManager.playSound("surprise");
        
        String playerName = player == 1 ? game.getPlayer1Name() : game.getPlayer2Name();
        int gameDifficulty = convertDifficultyToInt(game.getDifficulty());
        
        // Score the surprise cell activation and get the surprise details
        String surpriseMessage = scoringService.scoreSurpriseCellActivated(game, playerName, gameDifficulty);
        
        // Show result message with surprise details
        showMessage(
            surpriseMessage,
            "Surprise!",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // Check if game is over due to lives running out (bad surprise can decrease lives)
        if (game.getSharedLives() <= 0) {
            handleGameOver(false);
            return;
        }
        
        // Switch turn after activating surprise
        game.switchTurn();
        
        // Observer pattern will automatically update UI when turn changes
    }
    
    /**
     * Converts Game.Difficulty enum to integer (1=Easy, 2=Medium, 3=Hard).
     * 
     * @param difficulty The Game.Difficulty enum value
     * @return Integer representation (1, 2, or 3)
     */
    private int convertDifficultyToInt(Game.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return 1;
            case MEDIUM:
                return 2;
            case HARD:
                return 3;
            default:
                return 1;
        }
    }
}

