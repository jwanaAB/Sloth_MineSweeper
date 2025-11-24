package controller;

import model.PlayerState;
import model.SysData;
import java.util.Random;

/**
 * ScoringService handles all scoring logic based on game actions.
 * Implements the scoring rules as specified in the requirements:
 * - Mine cells: +1pt if flagged correctly (uncovers mine in UI), -1 life if hit
 * - Numbered cells: +1pt if revealed correctly, -3pts if hit incorrectly
 * - Empty cells: +1pt if revealed correctly, -3pts if flagged incorrectly
 * - Question cells: +1pt if revealed correctly, -3pts if flagged incorrectly, activation cost (5/8/12pts by difficulty) then +1pt if answered correctly OR -3pts if answered incorrectly
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
     * Awards +1 point.
     *
     * @param player The player who flagged the mine
     */
    public void scoreMineFlaggedCorrectly(PlayerState player) {
        player.addScore(1);
        sysData.addHistoryEntry(String.format("%s flagged a mine correctly (+1pt)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a mine cell is flagged incorrectly.
     * This method is for when a player flags a cell thinking it's a mine, but it's actually not.
     * Note: This is different from flagging empty/question cells incorrectly.
     *
     * @param player The player who incorrectly flagged a non-mine cell as a mine
     */
    public void scoreMineFlaggedIncorrectly(PlayerState player) {
        player.addScore(-1);
        sysData.addHistoryEntry(String.format("%s flagged a non-mine cell incorrectly (-1pt)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a mine cell is revealed (hit).
     * Player loses 1 life and no points are awarded/penalized.
     *
     * @param player The player who hit the mine
     */
    public void scoreMineHit(PlayerState player) {
        player.loseLife();
        sysData.addHistoryEntry(String.format("%s hit a mine! Lost 1 life (Lives: %d)", 
                player.getPlayerName(), player.getLives()));
    }

    /**
     * Handles scoring when a numbered cell (1-8) is revealed correctly.
     * Awards +1 point.
     *
     * @param player The player who revealed the cell correctly
     * @param cellValue The number on the cell (1-8)
     */
    public void scoreNumberedCellRevealedCorrectly(PlayerState player, int cellValue) {
        player.addScore(1);
        sysData.addHistoryEntry(String.format("%s revealed numbered cell %d correctly (+1pt)", 
                player.getPlayerName(), cellValue));
    }

    /**
     * Handles scoring when a numbered cell is hit incorrectly.
     * Penalizes -3 points.
     *
     * @param player The player who hit the numbered cell incorrectly
     * @param cellValue The number on the cell (1-8)
     */
    public void scoreNumberedCellHitIncorrectly(PlayerState player, int cellValue) {
        player.addScore(-3);
        sysData.addHistoryEntry(String.format("%s hit numbered cell %d incorrectly (-3pts)", 
                player.getPlayerName(), cellValue));
    }

    /**
     * Handles scoring when a question cell is answered correctly.
     * Awards +1 point.
     * Note: This method does NOT charge the activation cost. Use scoreQuestionCellActivated() for full activation handling.
     *
     * @param player The player who answered correctly
     */
    public void scoreQuestionAnsweredCorrectly(PlayerState player) {
        player.addScore(1);
        sysData.addHistoryEntry(String.format("%s answered question correctly (+1pt)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a question cell is answered incorrectly.
     * Penalizes -3 points.
     * Note: This method does NOT charge the activation cost. Use scoreQuestionCellActivated() for full activation handling.
     *
     * @param player The player who answered incorrectly
     */
    public void scoreQuestionAnsweredIncorrectly(PlayerState player) {
        player.addScore(-3);
        sysData.addHistoryEntry(String.format("%s answered question incorrectly (-3pts)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a question cell is activated (to answer the question).
     * Can only be activated after the cell has been revealed.
     * Charges activation cost based on difficulty, then applies answer scoring:
     * - Easy: 5pts activation cost, then +1pt if correct OR -3pts if incorrect
     * - Medium: 8pts activation cost, then +1pt if correct OR -3pts if incorrect
     * - Hard: 12pts activation cost, then +1pt if correct OR -3pts if incorrect
     *
     * @param player The player activating the question cell
     * @param difficulty The current game difficulty (1=Easy, 2=Medium, 3=Hard)
     * @param isCorrect Whether the answer was correct
     */
    public void scoreQuestionCellActivated(PlayerState player, int difficulty, boolean isCorrect) {
        int cost = calculateQuestionActivationCost(difficulty);
        player.addScore(-cost);
        
        // Apply answer scoring
        if (isCorrect) {
            player.addScore(1);
            sysData.addHistoryEntry(String.format("%s activated question cell (-%dpts cost, answered correctly: +1pt)", 
                    player.getPlayerName(), cost));
        } else {
            player.addScore(-3);
            sysData.addHistoryEntry(String.format("%s activated question cell (-%dpts cost, answered incorrectly: -3pts)", 
                    player.getPlayerName(), cost));
        }
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
     * Awards +1 point.
     *
     * @param player The player who revealed the empty cell correctly
     */
    public void scoreEmptyCellRevealedCorrectly(PlayerState player) {
        player.addScore(1);
        sysData.addHistoryEntry(String.format("%s revealed empty cell correctly (+1pt)", player.getPlayerName()));
    }

    /**
     * Handles scoring when an empty cell is flagged incorrectly.
     * Penalizes -3 points.
     *
     * @param player The player who incorrectly flagged an empty cell
     */
    public void scoreEmptyCellFlaggedIncorrectly(PlayerState player) {
        player.addScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged empty cell incorrectly (-3pts)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a question cell is revealed correctly.
     * Awards +1 point.
     * Note: This is separate from answering the question correctly.
     *
     * @param player The player who revealed the question cell correctly
     */
    public void scoreQuestionCellRevealedCorrectly(PlayerState player) {
        player.addScore(1);
        sysData.addHistoryEntry(String.format("%s revealed question cell correctly (+1pt)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a question cell is flagged incorrectly.
     * Penalizes -3 points.
     *
     * @param player The player who incorrectly flagged a question cell
     */
    public void scoreQuestionCellFlaggedIncorrectly(PlayerState player) {
        player.addScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged question cell incorrectly (-3pts)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a surprise cell is revealed correctly.
     * Awards +1 point.
     *
     * @param player The player who revealed the surprise cell correctly
     */
    public void scoreSurpriseCellRevealedCorrectly(PlayerState player) {
        player.addScore(1);
        sysData.addHistoryEntry(String.format("%s revealed surprise cell correctly (+1pt)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a surprise cell is flagged incorrectly.
     * Penalizes -3 points.
     *
     * @param player The player who incorrectly flagged a surprise cell
     */
    public void scoreSurpriseCellFlaggedIncorrectly(PlayerState player) {
        player.addScore(-3);
        sysData.addHistoryEntry(String.format("%s flagged surprise cell incorrectly (-3pts)", player.getPlayerName()));
    }

    /**
     * Handles scoring when a surprise cell is activated.
     * Can only be activated after the cell has been revealed.
     * Cost and rewards vary by difficulty:
     * - Easy: 5pts cost, 50% chance: +1 life +8pts OR -1 life -8pts
     * - Medium: 8pts cost, 50% chance: +1 life +12pts OR -1 life -12pts
     * - Hard: 12pts cost, 50% chance: +1 life +16pts OR -1 life -16pts
     *
     * @param player The player activating the surprise cell
     * @param difficulty The current game difficulty (1=Easy, 2=Medium, 3=Hard)
     */
    public void scoreSurpriseCellActivated(PlayerState player, int difficulty) {
        int cost = calculateSurpriseActivationCost(difficulty);
        player.addScore(-cost);
        
        // 50-50 chance for good or bad surprise
        boolean goodSurprise = random.nextBoolean();
        int[] reward = calculateSurpriseReward(difficulty, goodSurprise);
        
        player.addScore(reward[0]); // Points change (can be positive or negative)
        if (reward[1] > 0) {
            player.addLife();
        } else if (reward[1] < 0) {
            player.loseLife();
        }
        
        String outcome = goodSurprise ? "good" : "bad";
        String lifeChange = reward[1] > 0 ? String.format("+%d life", reward[1]) : 
                           reward[1] < 0 ? String.format("%d life", reward[1]) : "";
        String pointsChange = reward[0] >= 0 ? String.format("+%dpts", reward[0]) : 
                             String.format("%dpts", reward[0]);
        
        sysData.addHistoryEntry(String.format("%s activated surprise cell (%s: -%dpts cost, %s, %s)", 
                player.getPlayerName(), outcome, cost, pointsChange, lifeChange));
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
     * Handles scoring when a question cell is used.
     * Scoring depends on solution type and game difficulty, with both positive and negative outcomes.
     * question types: 1=Easy Question, 2=Medium Question, 3=Hard Question, 4=Expert Question
     * 
     * @param player The player clicked on a question cell
     * @param difficulty The current game difficulty (1=Easy, 2=Medium, 3=Hard)
     * @param QuestionType The type of question (1=Easy Question, 2=Medium Question, 3=Hard Question, 4=Expert Question)
     * @param isCorrect Whether the correct answer was chosen
     */
    public void scoreSolutionUsed(PlayerState player, int difficulty, int QuestionType, boolean isCorrect) {
        int[] scoreChange = calculateSolutionScore(difficulty, QuestionType, isCorrect);
        player.addScore(scoreChange[0]); // Points change
        if (scoreChange[1] != 0) {
            if (scoreChange[1] > 0) {
                for (int i = 0; i < scoreChange[1]; i++) {
                    player.addLife();
                }
            } else {
                for (int i = 0; i < -scoreChange[1]; i++) {
                    player.loseLife();
                }
            }
        }
        String questionTypeName = getQuestionTypeName(QuestionType);
        String result = isCorrect ? "correctly" : "incorrectly";
        String lifeChange = scoreChange[1] != 0 ? String.format(", %s%d life", scoreChange[1] > 0 ? "+" : "", scoreChange[1]) : "";
        sysData.addHistoryEntry(String.format("%s used %s solution %s (%s%dpts%s)", 
                player.getPlayerName(), questionTypeName, result, 
                scoreChange[0] >= 0 ? "+" : "", scoreChange[0], lifeChange));
    }
    
    /**
     * Legacy method for backward compatibility - uses default solution type 1 (Easy Question).
     * @deprecated Use scoreSolutionUsed(player, difficulty, solutionType, isCorrect) instead
     */
    @Deprecated
    public void scoreSolutionUsed(PlayerState player, int difficulty) {
        // Default to Easy Question type with incorrect usage (may have no penalty based on 50% chance)
        scoreSolutionUsed(player, difficulty, 1, false);
    }

    /**
     * Calculates the cost of using a solution based on difficulty.
     *
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return The cost in points
     */
    private int[] calculateSolutionScore(int difficulty, int solutionType, boolean isCorrect) {
        int pointsChange = 0;
        int lifeChange = 0;
        
        if (isCorrect) {
            // Correct usage - positive scoring
            switch (difficulty) {
                case 1: // Easy game
                    switch (solutionType) {
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
                    switch (solutionType) {
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
                    switch (solutionType) {
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
                    switch (solutionType) {
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
                    switch (solutionType) {
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
                    switch (solutionType) {
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
     * Gets the name of a solution type.
     *
     * @param solutionType The solution type (1=Easy Question, 2=Medium Question, 3=Hard Question, 4=Expert Question)
     * @return The name of the solution type
     */
    private String getQuestionTypeName(int solutionType) {
        switch (solutionType) {
            case 1: return "Easy Question";
            case 2: return "Medium Question";
            case 3: return "Hard Question";
            case 4: return "Expert Question";
            default: return "Unknown";
        }
    }

    /**
     * Initializes player states for a new game based on difficulty.
     * According to requirements:
     * Easy: 10 lives
     * Medium: 8 lives
     * Hard: 6 lives
     *
     * @param player1Name Name of player 1
     * @param player2Name Name of player 2
     * @param difficulty The difficulty level (1=Easy, 2=Medium, 3=Hard)
     * @return Array with [player1State, player2State]
     */
    public PlayerState[] initializePlayerStates(String player1Name, String player2Name, int difficulty) {
        int lives;
        
        switch (difficulty) {
            case 1: // Easy
                lives = 10;
                break;
            case 2: // Medium
                lives = 8;
                break;
            case 3: // Hard
                lives = 6;
                break;
            default:
                lives = 10;
        }

        PlayerState player1 = new PlayerState(player1Name, lives);
        PlayerState player2 = new PlayerState(player2Name, lives);

        return new PlayerState[]{player1, player2};
    }
}

