/* Credit: http://www.byteauthor.com/2010/08/sudoku-solver-update/ */
package com.musevisions.android.SudokuSolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class SudokuSolverOptimised {

    // -----
    // INNER TYPES
    // -----
    
    private final static class FileFormatException extends Exception {

        /**
         * 
         */
        private static final long serialVersionUID = -6708704362775269448L;
    }

    // -----
    // CONSTANTS
    // -----
    
    private final static int[] allowedBitFields = new int[] {
            0,
            1,
            1 << 1,
            1 << 2,
            1 << 3,
            1 << 4,
            1 << 5,
            1 << 6,
            1 << 7,
            1 << 8,
    };
    
    private final static int allAllowed = arraySum(allowedBitFields);

    // -----
    // STATIC METHODS
    // -----
    
    /**
     * @param args
     */
    public static final void main(String[] args) {
        // a dummy board solve to force the JIT compiler to precompile everything. (much faster)
        solveBoard(new int[][] { 
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0},
                new int[] {0,0,0,0,0,0,0,0,0}});
        
        // A delay to allow JIT compilation to run.
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        
        if (args.length > 0) {
            try {
                final int[][] board = readBoard(new File(args[0]));
                
                final long timeBefore = System.nanoTime();

                final int placedNumbers = solveBoard(board);
                
                final long timeAfter = System.nanoTime();
                
                if (placedNumbers == 81) {
                    System.out.println("La r�solution a dur� " + ((timeAfter - timeBefore) / 1) + " nanoseconds.");
                }
                else {
                    System.out.println("La r�solution est impossible. L'op�ration a dur� " + ((timeAfter - timeBefore) / 100) + " nanoseconds.");
                }
                
                printBoard(board);
            } 
            catch (final FileFormatException e) {
                System.out.println("Le fichier est d'un format incorrect.");
            } 
            catch (final IOException e) {
                System.out.println("Une erreur s'est produite lors de la lecture du fichier.");
            }
        }
        else {
            System.out.println("Vous devez fournir le chemin du fichier contenant le probl�me.");
        }
    }

    public final static int solveBoard(final int[][] board) {
        final int[][] allowedValues = new int[9][9];
        int placedNumberCount = 0;
        
        for (int[] allowedValuesRow : allowedValues) {
            Arrays.fill(allowedValuesRow, allAllowed);
        }
        
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (board[x][y] > 0) {
                    allowedValues[x][y] = 0;
                    applyAllowedValuesMask(board, allowedValues, x, y);
                    placedNumberCount++;
                }
            }
        }
        

        return solveBoard(board, allowedValues, placedNumberCount);
    }

    private final static int solveBoard(final int[][] board, final int[][] allowedValues, int placedNumberCount) {
      
        int lastPlacedNumbersCount = 0;
      
        while (placedNumberCount - lastPlacedNumbersCount > 3 && placedNumberCount < 68 && placedNumberCount > 10) {
            lastPlacedNumbersCount = placedNumberCount;
            placedNumberCount += moveNothingElseAllowed(board, allowedValues);
            placedNumberCount += moveNoOtherRowOrColumnAllowed(board, allowedValues);
            placedNumberCount += moveNothingElseAllowed(board, allowedValues);
            
            if (placedNumberCount < 35) {
                applyNakedPairs(allowedValues);
                applyLineCandidateConstraints(allowedValues);
            }
        }
      
        if (placedNumberCount < 81) {
            final int[][] bruteForcedBoard = attemptBruteForce(board, allowedValues, placedNumberCount);

            if (bruteForcedBoard != null) {
                placedNumberCount = 0;
                
                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 9; y++) {
                        board[x][y] = bruteForcedBoard[x][y];
                        
                        if (bruteForcedBoard[x][y] > 0) {
                            placedNumberCount++;
                        }
                    }
                }
            }
        }
        
        return placedNumberCount;
    }

    private final static int[][] attemptBruteForce(final int[][] board, final int[][] allowedValues, final int placedNumberCount) {

        for (int x = 0; x < 9; x++) {
            final int[] allowedValuesRow = allowedValues[x];
            final int[] boardRow = board[x];
            
            for (int y = 0; y < 9; y++) {
                if (boardRow[y] == 0) {
                    for (int value = 1; value <= 9; value++) {
                        if ((allowedValuesRow[y] & allowedBitFields[value]) > 0) {
                            final int[][] testBoard = copyGameMatrix(board);
                            final int[][] testAllowedValues = copyGameMatrix(allowedValues);
                            setValue(testBoard, testAllowedValues, value, x, y);
                            
                            final int placedNumbers = solveBoard(testBoard, testAllowedValues, placedNumberCount + 1);
                            
                            if (placedNumbers == 81) {
                                return testBoard;
                            }
                        }
                    }
                    
                    return null;
                }
            }
        }
        
        return null;
    }

    private final static int moveNoOtherRowOrColumnAllowed(final int[][] board, final int[][] allowedValues) {
        
        int moveCount = 0;
        
        for (int value = 1; value <= 9; value++) {
            final int allowedBitField = allowedBitFields[value];
            
            for (int x = 0; x < 9; x++) {
                int allowedY = -1;
                final int[] allowedValuesRow = allowedValues[x];
                
                for (int y = 0; y < 9; y++) {
                    if ((allowedValuesRow[y] & allowedBitField) > 0) {
                        if (allowedY < 0) {
                            allowedY = y;
                        }
                        else {
                            allowedY = -1;
                            break;
                        }
                    }
                }
                
                if (allowedY >= 0) {
                    setValue(board, allowedValues, value, x, allowedY);
                    moveCount++;
                }
            }
            
            for (int y = 0; y < 9; y++) {
                int allowedX = -1;
                
                for (int x = 0; x < 9; x++) {
                    if ((allowedValues[x][y] & allowedBitField) > 0) {
                        if (allowedX < 0) {
                            allowedX = x;
                        }
                        else {
                            allowedX = -1;
                            break;
                        }
                    }
                }
                
                if (allowedX >= 0) {
                    setValue(board, allowedValues, value, allowedX, y);
                    moveCount++;
                }
            }
        }

        return moveCount;
    }

    private final static int moveNothingElseAllowed(final int[][] board,
            final int[][] allowedValues) {
        
        int moveCount = 0;
        
        for (int x = 0; x < 9; x++) {
            final int[] allowedValuesRow = allowedValues[x];
            
            for (int y = 0; y < 9; y++) {
                final int currentAllowedValues = allowedValuesRow[y];
                if (countSetBits(currentAllowedValues) == 1) {
                    setValue(board, allowedValues, getLastSetBitIndex(currentAllowedValues), x, y);
                    moveCount++;
                }
            }
        }

        return moveCount;
    }

    private final static void applyNakedPairs(final int[][] allowedValues) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                final int value = allowedValues[x][y];
                
                if (countSetBits(value) == 2) {
                    for (int scanningY = y + 1; scanningY < 9; scanningY++) {
                        if (allowedValues[x][scanningY] == value) {
                            final int removeMask = ~value;
                            
                            for (int applyY = 0; applyY < 9; applyY++) {
                                if (applyY != y && applyY != scanningY) {
                                    allowedValues[x][applyY] &= removeMask;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                final int value = allowedValues[x][y];
                
                if (value != 0 && countSetBits(value) == 2) {
                    for (int scanningX = x + 1; scanningX < 9; scanningX++) {
                        if (allowedValues[scanningX][y] == value) {
                            final int removeMask = ~value;
                            
                            for (int applyX = 0; applyX < 9; applyX++) {
                                if (applyX != x && applyX != scanningX) {
                                    allowedValues[applyX][y] &= removeMask;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final static void applyLineCandidateConstraints(final int[][] allowedValues) {
        for (int value = 1; value <= 9; value++) {
            final int valueMask = allowedBitFields[value];
            final int valueRemoveMask = ~valueMask;
            final int[] sectionAvailabilityColumn = new int[9];
            
            for (int x = 0; x < 9; x++) {
                final int finalX = x;
                
                for (int y = 0; y < 9; y++) {
                    if ((allowedValues[finalX][y] & valueMask) != 0) {
                        sectionAvailabilityColumn[finalX] |= (1 << (y / 3));
                    }
                }
                
                if (finalX == 2 || finalX == 5 || finalX == 8) {
                    for (int scanningX = finalX - 2; scanningX <= finalX; scanningX++) {
                        final int bitCount = countSetBits(sectionAvailabilityColumn[scanningX]);
                        
                        if (bitCount == 1) {
                            for (int applyX = finalX - 2; applyX <= finalX; applyX++) {
                                if (scanningX != applyX) {
                                    for (int applySectionY = 0; applySectionY < 3; applySectionY++) {
                                        if ((sectionAvailabilityColumn[scanningX] & (1 << applySectionY)) != 0) {
                                            for (int applyY = applySectionY * 3; applyY < (applySectionY + 1) * 3; applyY++) {
                                                allowedValues[applyX][applyY] &= valueRemoveMask;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (bitCount == 2 && scanningX < finalX) {
                            for (int scanningSecondPairX = scanningX + 1; scanningSecondPairX <= finalX; scanningSecondPairX++) {
                                if (sectionAvailabilityColumn[scanningX] == sectionAvailabilityColumn[scanningSecondPairX]) {
                                    final int applyX;
                                    
                                    if (scanningSecondPairX != finalX) {
                                        applyX = finalX;
                                    }
                                    else if (scanningSecondPairX - scanningX > 1) {
                                        applyX = scanningSecondPairX - 1;
                                    }
                                    else {
                                        applyX = scanningX - 1;
                                    }
                                    
                                    for (int applySectionY = 0; applySectionY < 3; applySectionY++) {
                                        if ((sectionAvailabilityColumn[scanningX] & (1 << applySectionY)) != 0) {
                                            for (int applyY = applySectionY * 3; applyY < (applySectionY + 1) * 3; applyY++) {
                                                allowedValues[applyX][applyY] &= valueRemoveMask;
                                            }
                                        }
                                    }
                                    
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            final int[] sectionAvailabilityRow = new int[9];
            
            for (int y = 0; y < 9; y++) {
                final int finalY = y;
                
                for (int x = 0; x < 9; x++) {
                    if ((allowedValues[x][finalY] & valueMask) != 0) {
                        sectionAvailabilityRow[finalY] |= (1 << (x / 3));
                    }
                }
                
                if (finalY == 2 || finalY == 5 || finalY == 8) {
                    for (int scanningY = finalY - 2; scanningY <= finalY; scanningY++) {
                        final int bitCount = countSetBits(sectionAvailabilityRow[scanningY]);
                        
                        if (bitCount == 1) {
                            for (int applyY = finalY - 2; applyY <= finalY; applyY++) {
                                if (scanningY != applyY) {
                                    for (int applySectionX = 0; applySectionX < 3; applySectionX++) {
                                        if ((sectionAvailabilityRow[scanningY] & (1 << applySectionX)) != 0) {
                                            for (int applyX = applySectionX * 3; applyX < (applySectionX + 1) * 3; applyX++) {
                                                allowedValues[applyX][applyY] &= valueRemoveMask;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (bitCount == 2 && scanningY < finalY) {
                            for (int scanningSecondPairY = scanningY + 1; scanningSecondPairY <= finalY; scanningSecondPairY++) {
                                if (sectionAvailabilityRow[scanningY] == sectionAvailabilityRow[scanningSecondPairY]) {
                                    final int applyY;
                                    
                                    if (scanningSecondPairY != finalY) {
                                        applyY = finalY;
                                    }
                                    else if (scanningSecondPairY - scanningY > 1) {
                                        applyY = scanningSecondPairY - 1;
                                    }
                                    else {
                                        applyY = scanningY - 1;
                                    }
                                    
                                    for (int applySectionX = 0; applySectionX < 3; applySectionX++) {
                                        if ((sectionAvailabilityRow[scanningY] & (1 << applySectionX)) != 0) {
                                            for (int applyX = applySectionX * 3; applyX < (applySectionX + 1) * 3; applyX++) {
                                                allowedValues[applyX][applyY] &= valueRemoveMask;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final static void setValue(final int[][] board, final int[][] allowedValues, final int value, final int x, final int y) {
        board[x][y] = value;
        allowedValues[x][y] = 0;
        applyAllowedValuesMask(board, allowedValues, x, y);
    }
    
    private final static int getLastSetBitIndex(int value) {
        int bitIndex = 0;
        
        while (value > 0) {
            bitIndex++;
            value >>= 1;
        }
        
        return bitIndex;
    }

    private final static void applyAllowedValuesMask(final int[][] board,
            final int[][] allowedValues, final int x, final int y) {

        final int mask = ~allowedBitFields[board[x][y]];
        
        for (int maskApplyX = 0; maskApplyX < 9; maskApplyX++) {
            allowedValues[maskApplyX][y] &= mask;
        }
        
        final int[] allowedValuesRow = allowedValues[x];
        
        for (int maskApplyY = 0; maskApplyY < 9; maskApplyY++) {
            allowedValuesRow[maskApplyY] &= mask;
        }
        
        int sectionX1 = 0;
        int sectionX2 = 0;
        
        switch (x) {
            case 0:
                sectionX1 = x + 1;
                sectionX2 = x + 2;
                break;
            case 1:
                sectionX1 = x - 1;
                sectionX2 = x + 1;
                break;
            case 2:
                sectionX1 = x - 2;
                sectionX2 = x - 1;
                break;
            case 3:
                sectionX1 = x + 1;
                sectionX2 = x + 2;
                break;
            case 4:
                sectionX1 = x - 1;
                sectionX2 = x + 1;
                break;
            case 5:
                sectionX1 = x - 2;
                sectionX2 = x - 1;
                break;
            case 6:
                sectionX1 = x + 1;
                sectionX2 = x + 2;
                break;
            case 7:
                sectionX1 = x - 1;
                sectionX2 = x + 1;
                break;
            case 8:
                sectionX1 = x - 2;
                sectionX2 = x - 1;
                break;
                
        }

        int sectionY1 = 0;
        int sectionY2 = 0;
        
        switch (y) {
            case 0:
                sectionY1 = y + 1;
                sectionY2 = y + 2;
                break;
            case 1:
                sectionY1 = y - 1;
                sectionY2 = y + 1;
                break;
            case 2:
                sectionY1 = y - 2;
                sectionY2 = y - 1;
                break;
            case 3:
                sectionY1 = y + 1;
                sectionY2 = y + 2;
                break;
            case 4:
                sectionY1 = y - 1;
                sectionY2 = y + 1;
                break;
            case 5:
                sectionY1 = y - 2;
                sectionY2 = y - 1;
                break;
            case 6:
                sectionY1 = y + 1;
                sectionY2 = y + 2;
                break;
            case 7:
                sectionY1 = y - 1;
                sectionY2 = y + 1;
                break;
            case 8:
                sectionY1 = y - 2;
                sectionY2 = y - 1;
                break;
        }
        
        final int[] allowedValuesRow1 = allowedValues[sectionX1];
        final int[] allowedValuesRow2 = allowedValues[sectionX2];
        
        allowedValuesRow1[sectionY1] &= mask;
        allowedValuesRow1[sectionY2] &= mask;
        allowedValuesRow2[sectionY1] &= mask;
        allowedValuesRow2[sectionY2] &= mask;
    }

    private final static int countSetBits(int value) {
        int count = 0;
        
        while (value > 0) {
            value = value & (value - 1);
            count++;
        }
        
        return count;
    }

    private final static int arraySum(final int[] array) {
        int sum = 0;
        
        for (int value : array) {
            sum += value;
        }
        
        return sum;
    }

    private final static int[][] copyGameMatrix(final int[][] matrix) {
        return new int[][] {
            Arrays.copyOf(matrix[0], 9),
            Arrays.copyOf(matrix[1], 9),
            Arrays.copyOf(matrix[2], 9),
            Arrays.copyOf(matrix[3], 9),
            Arrays.copyOf(matrix[4], 9),
            Arrays.copyOf(matrix[5], 9),
            Arrays.copyOf(matrix[6], 9),
            Arrays.copyOf(matrix[7], 9),
            Arrays.copyOf(matrix[8], 9),
        };
    }

    private final static void printBoard(final int[][] board) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                System.out.print(board[x][y]);
            }
            
            System.out.println();
        }
    }

    private final static int[][] readBoard(final File file) throws FileFormatException, IOException {
        final int[][] board = new int[9][9];
        final BufferedReader input =  new BufferedReader(new FileReader(file));

        try {
            String line = null; //not declared within while loop
            int lineIndex = 0;
            
            while (( line = input.readLine()) != null) {
                if (line.length() != 9 || lineIndex > 8) {
                    throw new FileFormatException();
                }
            
                for (int letterIndex = 0; letterIndex < 9; letterIndex++) {
                    final int value = Character.getNumericValue(line.charAt(letterIndex));
                    
                    if (value < 0 || value > 9) {
                        throw new FileFormatException();
                    }
                    
                    board[letterIndex][lineIndex] = value;
                }
                
                lineIndex++;
            }
            
            if (lineIndex != 9) {
                throw new FileFormatException();
            }
        }
        finally {
            input.close();
        }
        
        return board;
    }
}