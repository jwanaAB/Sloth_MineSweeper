package controller;

import model.Game;
import model.PlayerState;
import model.SysData;
import java.util.Random;

/**
 * ScoringService handles all scoring logic based on game actions.
 * Implements the scoring rules as specified in the requirements:
 * - Players share score and lives
 * - Mine cells: +1pt if flagged correctly, -1 shared life if hit
 * - Numbered cells: +1pt if revealed correctly, -3pts if flagged incorrectly
 * - Empty cells: +1pt if revealed correctly, -3pts if flagged incorrectly
 * - Question cells: +1pt if revealed correctly, -3pts if flagged incorrectly, activation cost (5/8/12pts by difficulty) then scoring based on question type and correctness
 * - Surprise cells: +1pt if revealed correctly, -3pts if flagged incorrectly, activation with 50-50 chance (cost and rewards vary by difficulty)
 */
public class ScoringService {
    private final SysData sysData;
    private final Random random;

    public ScoringService(SysData sysData) {
        this.sysData = sysData;
        this.random = new Random();
    }

    /**
     * Handles scoring when a mine cell is flagged correctly.
     * Awards +1 point to shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who flagged the mine
     */
    public void scoreMineFlaggedCorrectly(Game game, String playerName) {
        game.addSharedScore(1);
        sysData.addHistoryEntry(String.format("%s flagged a mine correctly (+1pt)", playerName));
    }

    /**
     * Handles scoring when a mine cell is flagged incorrectly.
     * This method is for when a player flags a cell thinking it's a mine, but it's actually not.
     * Note: This is different from flagging empty/question cells incorrectly.
     *
     * @param game The game instance
     * @param playerName The name of the player who incorrectly flagged a non-mine cell as a mine
     */
    public void scoreMineFlaggedIncorrectly(Game game, String playerName) {
        game.addSharedScore(-1);
        sysData.addHistoryEntry(String.format("%s flagged a non-mine cell incorrectly (-1pt)", playerName));
    }

    /**
     * Handles scoring when a mine cell is revealed (hit).
     * Shared lives decrease by 1 and no points are awarded/penalized.
     *
     * @param game The game instance
     * @param playerName The name of the player who hit the mine
     */
    public void scoreMineHit(Game game, String playerName) {
        game.decreaseSharedLives();
        sysData.addHistoryEntry(String.format("%s hit a mine! Lost 1 shared life (Lives: %d)", 
                playerName, game.getSharedLives()));
    }

    /**
     * Handles scoring when a numbered cell (1-8) is revealed correctly.
     * Awards +1 point to shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who revealed the cell correctly
     * @param cellValue The number on the cell (1-8)
     */
    public void scoreNumberedCellRevealedCorrectly(Game game, String playerName, int cellValue) {
        game.addSharedScore(1);
        sysData.addHistoryEntry(String.format("%s revealed numbered cell %d correctly (+1pt)", 
                playerName, cellValue));
    }

    /**
     * Handles scoring when a numbered cell is flagged incorrectly.
     * Penalizes -3 points from shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who flagged the numbered cell incorrectly
     * @param cellValue The number on the cell (1-8)
     */
    public void scoreNumberedCellFlaggedIncorrectly(Game game, String playerName, int cellValue) {
        game.addSharedScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged numbered cell %d incorrectly (-3pts)", 
                playerName, cellValue));
    }

    /**
     * Handles scoring when a question cell is activated (to answer the question).
     * Can only be activated after the cell has been revealed.
     * Charges activation cost based on difficulty, then applies answer scoring based on question type:
     * - Easy: 5pts activation cost, then scoring based on question type and correctness
     * - Medium: 8pts activation cost, then scoring based on question type and correctness
     * - Hard: 12pts activation cost, then scoring based on question type and correctness
     *
     * @param game The game instance
     * @param playerName The name of the player activating the question cell
     * @param difficulty The current game difficulty (1=Easy, 2=Medium, 3=Hard)
     * @param questionType The type of question (1=Easy Question, 2=Medium Question, 3=Hard Question, 4=Expert Question)
     * @param isCorrect Whether the answer was correct
     */
    public void scoreQuestionCellActivated(Game game, String playerName, int difficulty, int questionType, boolean isCorrect) {
        int cost = calculateQuestionActivationCost(difficulty);
        game.addSharedScore(-cost);
        
        // Apply answer scoring based on question type using calculateQuestionScore
        int[] scoreChange = calculateQuestionScore(difficulty, questionType, isCorrect);
        game.addSharedScore(scoreChange[0]); // Points change
        
        // Apply life changes if any
        if (scoreChange[1] != 0) {
            if (scoreChange[1] > 0) {
                for (int i = 0; i < scoreChange[1]; i++) {
                    game.addSharedLife();
                }
            } else {
                for (int i = 0; i < -scoreChange[1]; i++) {
                    game.decreaseSharedLives();
                }
            }
        }
        
        String difficultyName = difficulty == 1 ? "Easy" : difficulty == 2 ? "Medium" : "Hard";
        String questionTypeName = getQuestionTypeName(questionType);
        String answerResult = isCorrect ? "correctly" : "incorrectly";
        String lifeChange = scoreChange[1] != 0 ? String.format(", %s%d shared life", scoreChange[1] > 0 ? "+" : "", scoreChange[1]) : "";
        sysData.addHistoryEntry(String.format("%s activated question cell (%s: -%dpts cost, %s answered %s %s%dpts%s)", 
                playerName, difficultyName, cost, questionTypeName, answerResult, 
                scoreChange[0] >= 0 ? "+" : "", scoreChange[0], lifeChange));
    }

    /**
     * Calculates the cost of activating a question cell based on difficulty.
     * Same cost as surprise cell activation.
     *
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return The cost in points
     */
    private int calculateQuestionActivationCost(int difficulty) {
        // Same cost as surprise cell activation
        return calculateSurpriseActivationCost(difficulty);
    }

    /**
     * Handles scoring when an empty cell is revealed correctly.
     * Awards +1 point to shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who revealed the empty cell correctly
     */
    public void scoreEmptyCellRevealedCorrectly(Game game, String playerName) {
        game.addSharedScore(1);
        sysData.addHistoryEntry(String.format("%s revealed empty cell correctly (+1pt)", playerName));
    }

    /**
     * Handles scoring when an empty cell is flagged incorrectly.
     * Penalizes -3 points from shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who incorrectly flagged an empty cell
     */
    public void scoreEmptyCellFlaggedIncorrectly(Game game, String playerName) {
        game.addSharedScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged empty cell incorrectly (-3pts)", playerName));
    }

    /**
     * Handles scoring when a question cell is revealed correctly.
     * Awards +1 point to shared score.
     * Note: This is separate from answering the question correctly.
     *
     * @param game The game instance
     * @param playerName The name of the player who revealed the question cell correctly
     */
    public void scoreQuestionCellRevealedCorrectly(Game game, String playerName) {
        game.addSharedScore(1);
        sysData.addHistoryEntry(String.format("%s revealed question cell correctly (+1pt)", playerName));
    }

    /**
     * Handles scoring when a question cell is flagged incorrectly.
     * Penalizes -3 points from shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who incorrectly flagged a question cell
     */
    public void scoreQuestionCellFlaggedIncorrectly(Game game, String playerName) {
        game.addSharedScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged question cell incorrectly (-3pts)", playerName));
    }

    /**
     * Handles scoring when a surprise cell is revealed correctly.
     * Awards +1 point to shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who revealed the surprise cell correctly
     */
    public void scoreSurpriseCellRevealedCorrectly(Game game, String playerName) {
        game.addSharedScore(1);
        sysData.addHistoryEntry(String.format("%s revealed surprise cell correctly (+1pt)", playerName));
    }

    /**
     * Handles scoring when a surprise cell is flagged incorrectly.
     * Penalizes -3 points from shared score.
     *
     * @param game The game instance
     * @param playerName The name of the player who incorrectly flagged a surprise cell
     */
    public void scoreSurpriseCellFlaggedIncorrectly(Game game, String playerName) {
        game.addSharedScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged surprise cell incorrectly (-3pts)", playerName));
    }

    /**
     * Handles scoring when a surprise cell is activated.
     * Can only be activated after the cell has been revealed.
     * Cost and rewards vary by difficulty:
     * - Easy: 5pts cost, 50% chance: +1 shared life +8pts OR -1 shared life -8pts
     * - Medium: 8pts cost, 50% chance: +1 shared life +12pts OR -1 shared life -12pts
     * - Hard: 12pts cost, 50% chance: +1 shared life +16pts OR -1 shared life -16pts
     *
     * @param game The game instance
     * @param playerName The name of the player activating the surprise cell
     * @param difficulty The current game difficulty (1=Easy, 2=Medium, 3=Hard)
     * @return A string describing the surprise outcome
     */
    public String scoreSurpriseCellActivated(Game game, String playerName, int difficulty) {
        int cost = calculateSurpriseActivationCost(difficulty);
        game.addSharedScore(-cost);
        
        // 50-50 chance for good or bad surprise
        boolean goodSurprise = random.nextBoolean();
        int[] reward = calculateSurpriseReward(difficulty, goodSurprise);
        
        game.addSharedScore(reward[0]); // Points change (can be positive or negative)
        if (reward[1] > 0) {
            game.addSharedLife();
        } else if (reward[1] < 0) {
            game.decreaseSharedLives();
        }
        
        String outcome = goodSurprise ? "good" : "bad";
        String lifeChange = reward[1] > 0 ? String.format("+%d shared life", reward[1]) : 
                           reward[1] < 0 ? String.format("%d shared life", reward[1]) : "";
        String pointsChange = reward[0] >= 0 ? String.format("+%dpts", reward[0]) : 
                             String.format("%dpts", reward[0]);
        
        sysData.addHistoryEntry(String.format("%s activated surprise cell (%s: -%dpts cost, %s, %s)", 
                playerName, outcome, cost, pointsChange, lifeChange));
        
        // Build and return the surprise message
        String surpriseType = goodSurprise ? "Good Surprise!" : "Bad Surprise!";
        String message = String.format(
            "Surprise cell activated!\n\n" +
            "Type: %s\n" +
            "Activation Cost: -%d points\n" +
            "Points Change: %s\n" +
            "Lives Change: %s",
            surpriseType, cost, pointsChange, lifeChange
        );
        
        return message;
    }

    /**
     * Calculates the cost of activating a surprise cell based on difficulty.
     *
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return The cost in points
     */
    private int calculateSurpriseActivationCost(int difficulty) {
        switch (difficulty) {
            case 1: return 5;   // Easy
            case 2: return 8;    // Medium
            case 3: return 12;   // Hard
            default: return 5;
        }
    }

    /**
     * Calculates the reward/penalty for activating a surprise cell.
     *
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @param goodSurprise Whether it's a good surprise (true) or bad surprise (false)
     * @return Array with [pointsChange, lifeChange]
     */
    private int[] calculateSurpriseReward(int difficulty, boolean goodSurprise) {
        int pointsChange;
        int lifeChange;
        
        if (goodSurprise) {
            // Good surprise: +1 life and positive points
            lifeChange = 1;
            switch (difficulty) {
                case 1: pointsChange = 8; break;   // Easy
                case 2: pointsChange = 12; break;  // Medium
                case 3: pointsChange = 16; break;  // Hard
                default: pointsChange = 8; break;
            }
        } else {
            // Bad surprise: -1 life and negative points
            lifeChange = -1;
            switch (difficulty) {
                case 1: pointsChange = -8; break;   // Easy
                case 2: pointsChange = -12; break;  // Medium
                case 3: pointsChange = -16; break;  // Hard
                default: pointsChange = -8; break;
            }
        }
        
        return new int[]{pointsChange, lifeChange};
    }

    /**
     * Calculates the scoring for answering a question based on difficulty and question type.
     *
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @param questionType The type of question (1=Easy Question, 2=Medium Question, 3=Hard Question, 4=Expert Question)
     * @param isCorrect Whether the answer was correct
     * @return Array with [pointsChange, lifeChange]
     */
    private int[] calculateQuestionScore(int difficulty, int questionType, boolean isCorrect) {
        int pointsChange = 0;
        int lifeChange = 0;
        
        if (isCorrect) {
            // Correct usage - positive scoring
            switch (difficulty) {
                case 1: // Easy game
                    switch (questionType) {
                        case 1: // Easy Question
                            pointsChange = 3;
                            lifeChange = 1;
                            break;
                        case 2: // Medium Question - Uncover a mine AND +6pts
                            pointsChange = 6;
                            // Note: Mine uncovering is handled elsewhere in the game logic
                            break;
                        case 3: // Hard Question - Display 3x3 random cells AND +10pts
                            pointsChange = 10;
                            // Note: 3x3 cell reveal is handled elsewhere in the game logic
                            break;
                        case 4: // Expert Question
                            pointsChange = 15;
                            lifeChange = 2;
                            break;
                    }
                    break;
                case 2: // Medium game
                    switch (questionType) {
                        case 1: // Easy Question
                            pointsChange = 8;
                            lifeChange = 1;
                            break;
                        case 2: // Medium Question
                            pointsChange = 10;
                            lifeChange = 1;
                            break;
                        case 3: // Hard Question
                            pointsChange = 15;
                            lifeChange = 1;
                            break;
                        case 4: // Expert Question
                            pointsChange = 20;
                            lifeChange = 2;
                            break;
                    }
                    break;
                case 3: // Hard game
                    switch (questionType) {
                        case 1: // Easy Question
                            pointsChange = 10;
                            lifeChange = 1;
                            break;
                        case 2: // Medium Question - 50% chance: (+1 life and +15pts) OR (+2 lives and +15pts)
                            pointsChange = 15;
                            lifeChange = random.nextBoolean() ? 1 : 2; // 50% chance for +1 or +2 lives
                            break;
                        case 3: // Hard Question
                            pointsChange = 20;
                            lifeChange = 2;
                            break;
                        case 4: // Expert Question
                            pointsChange = 40;
                            lifeChange = 3;
                            break;
                    }
                    break;
            }
        } else {
            // Incorrect usage - negative scoring (with chance of "nothing" for some cases)
            switch (difficulty) {
                case 1: // Easy game
                    switch (questionType) {
                        case 1: // Easy Question - 50% chance for (-3pts) OR nothing
                            if (random.nextBoolean()) {
                                pointsChange = -3;
                            }
                            else {pointsChange = 0;}
                            break;
                        case 2: // Medium Question - 50% chance for (-6pts) OR nothing
                            if (random.nextBoolean()) {
                                pointsChange = -6;
                            }
                            else {pointsChange = 0;}
                            break;
                        case 3: // Hard Question
                            pointsChange = -10;
                            break;
                        case 4: // Expert Question
                            pointsChange = -15;
                            lifeChange = -1;
                            break;
                    }
                    break;
                case 2: // Medium game
                    switch (questionType) {
                        case 1: // Easy Question
                                pointsChange = -8;
                            break;
                        case 2: // Medium Question
                        if (random.nextBoolean()) {
                            pointsChange = -10;
                            lifeChange = -1;
                        }else{
                            pointsChange = 0;
                            lifeChange = 0;
                        }
                            break;
                        case 3: // Hard Question 
                            pointsChange = -15;
                            lifeChange = -1;
                            break;
                        case 4: // Expert Question
                            pointsChange = -20;
                            lifeChange = random.nextBoolean() ? -1 : -2; // 50% chance for -1 or -2 lives
                            break;
                    }
                    break;
                case 3: // Hard game
                    switch (questionType) {
                        case 1: // Easy Question 
                            pointsChange = -10;
                            lifeChange = -1;
                            break;
                        case 2: // Medium Question - 50% chance: (-1 life and -15pts) OR (-2 lives and -15pts)
                            pointsChange = -15;
                            lifeChange = random.nextBoolean() ? -1 : -2; // 50% chance for -1 or -2 lives
                            break;
                        case 3: // Hard Question
                            pointsChange = -20;
                            lifeChange = -2;
                            break;
                        case 4: // Expert Question
                            pointsChange = -40;
                            lifeChange = -3;
                            break;
                    }
                    break;
            }
        }
        
        return new int[]{pointsChange, lifeChange};
    }
    
    /**
     * Gets the name of a question type.
     *
     * @param questionType The question type (1=Easy Question, 2=Medium Question, 3=Hard Question, 4=Expert Question)
     * @return The name of the question type
     */
    private String getQuestionTypeName(int questionType) {
        switch (questionType) {
            case 1: return "Easy Question";
            case 2: return "Medium Question";
            case 3: return "Hard Question";
            case 4: return "Expert Question";
            default: return "Unknown";
        }
    }

    /**
     * Converts remaining shared lives to points at game end.
     * Formula: points += lives × activationCost(difficulty)
     * 
     * @param game The game instance
     * @return The points added from remaining lives
     */
    public int convertRemainingLivesToPoints(Game game) {
        int remainingLives = game.getSharedLives();
        if (remainingLives <= 0) {
            return 0;
        }
        
        // Get difficulty (1=Easy, 2=Medium, 3=Hard)
        int difficulty;
        switch (game.getDifficulty()) {
            case EASY:
                difficulty = 1;
                break;
            case MEDIUM:
                difficulty = 2;
                break;
            case HARD:
                difficulty = 3;
                break;
            default:
                difficulty = 1;
                break;
        }
        
        int activationCost = calculateSurpriseActivationCost(difficulty);
        int pointsToAdd = remainingLives * activationCost;
        
        game.addSharedScore(pointsToAdd);
        sysData.addHistoryEntry(String.format("Game ended: %d remaining shared lives converted to %d points (%d lives × %d activation cost)", 
                remainingLives, pointsToAdd, remainingLives, activationCost));
        
        return pointsToAdd;
    }

    /**
     * Initializes player states for a new game based on difficulty.
     * According to requirements:
     * Easy: 10 shared lives, 6 questions, 2 surprises
     * Medium: 8 shared lives, 7 questions, 3 surprises
     * Hard: 6 shared lives, 11 questions, 4 surprises
     * 
     * Note: Lives and score are shared between players in the Game object.
     * This method returns PlayerState objects that may be used for tracking player-specific data.
     *
     * @param player1Name Name of player 1
     * @param player2Name Name of player 2
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return Array with [player1State, player2State]
     */
    public PlayerState[] initializePlayerStates(String player1Name, String player2Name, int difficulty) {
        int lives, questions, surprises;
        
        switch (difficulty) {
            case 1: // Easy
                lives = 10;
                questions = 6;
                surprises = 2;
                break;
            case 2: // Medium
                lives = 8;
                questions = 7;
                surprises = 3;
                break;
            case 3: // Hard
                lives = 6;
                questions = 11;
                surprises = 4;
                break;
            default:
                lives = 10;
                questions = 6;
                surprises = 2;
                break;
        }

        PlayerState player1 = new PlayerState(player1Name, lives, questions, surprises);
        PlayerState player2 = new PlayerState(player2Name, lives, questions, surprises);

        return new PlayerState[]{player1, player2};
    }
}

