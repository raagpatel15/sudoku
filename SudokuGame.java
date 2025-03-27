package main;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class SudokuGame {
	//Neelay Ranjan
    private static int[][] board = new int[9][9];
    private static int hintCounter;

    public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
       System.out.println("Welcome to Sudoku!");


       // Prompt user to choose difficulty
       System.out.println("Choose difficulty: Easy, Medium, Hard");
       String difficulty = scanner.next().toLowerCase();
       int cellsToRemove;

       switch (difficulty) {
           case "easy":
               cellsToRemove = 20; // easier puzzle
				hintCounter = 6;
               break;
           case "medium":
               cellsToRemove = 40; // medium difficulty
				hintCounter = 4;
               break;
           case "hard":
               cellsToRemove = 60; // harder puzzle
				hintCounter = 2;
               break;
           default:
               System.out.println("Invalid difficulty! Defaulting to Medium.");
				hintCounter = 4;
               cellsToRemove = 40;
       }


       // Generate a randomized Sudoku board
       generateBoard();
       removeNumbers(cellsToRemove);


       while (!isBoardComplete()) {
           displayBoard();
           System.out.println("Enter your move (format: row, column, number)\nAlternatively, if you want a hint, enter (row, column, -1), or if you want pencilmarking, enter (row, column, 0)");
           System.out.println("Hints remaining: " + hintCounter);         
           // Inside ReplaceAll method is REGEX - chatgpt helped on the regex part to match brackets and whitespace
           String input = scanner.nextLine().replaceAll("[(){}\s]", "").trim(); // Remove parentheses and extra spaces
           String[] parts = input.split(",");
           if (parts.length != 3) {
               System.out.println("Invalid input format! Use row,column,number.");
               continue;
           }


           try {
               int row = Integer.parseInt(parts[0].trim()) - 1;
               int col = Integer.parseInt(parts[1].trim()) - 1;
               int num = Integer.parseInt(parts[2].trim());

               if (row >= 0 && row <= 8 && col >= 0 && col <= 8 && num == -1) {
            	   System.out.println("Computing hints!");
            	   fillHints(row, col);
            	   hintCounter--;
            	   

                   continue;
               }
               
               if (row >= 0 && row <= 8 && col >= 0 && col <= 8 && num == 0) {
            	// handling a filled cell
                   if (board[row][col] != 0) {
                       System.out.println("This cell is already filled with " + board[row][col] + ".");
                       continue;
                   }
                   
                   List<Integer> candidates = getPossibleValues(row, col);
                   System.out.println("Possible values for cell (" + (row+1) + "," + (col+1) + "): " + candidates);
                   continue;
               }
               
               if (row < 0 || row > 8 || col < 0 || col > 8 || num < 1 || num > 9) {
                   System.out.println("Invalid input! Numbers must be between 1 and 9.");
                   continue;
               }


               if (board[row][col] != 0) {
                   System.out.println("This cell is already filled! Try another one.");
                   continue;
               }


               if (isValidMove(row, col, num)) {
                   board[row][col] = num;
                   System.out.println("Move accepted!");
               } else {
                   System.out.println("Invalid move! This number violates Sudoku rules.");
               }
           } catch (NumberFormatException e) {
               System.out.println("Invalid input! Use row,column,number format with integers.");
           }
       }


       System.out.println("Congratulations! You've completed the Sudoku puzzle.");
       displayBoard();
       scanner.close();
   }

   // Nicholas
   // Display the Sudoku Board
   public static void displayBoard() {
       System.out.println("\n\n\nCurrent Sudoku Board:");
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               // Print an underscore if cell value is zero, else print the cell
               if (board[row][col] == 0) {
                   System.out.print("_" + " ");
               } else {
                   System.out.print(board[row][col] + " ");
               }
               // if the column is the end of a 3x3 block, print a "|"
               if ((col + 1) % 3 == 0 && col != 8) {
                   System.out.print("| ");
               }
           }
           // if at the bottom of a 3x3 block, newline and print a wall of "-"
           System.out.println();
           if ((row + 1) % 3 == 0 && row != 8) {
               System.out.println("---------------------");
           }
       }
   }

   // Raag Patel
   // for loop to fill the board
   public static void generateBoard() {
       Random random = new Random();
       int fail_count = 0;
       // Go one cell at a time, and test if a random number can fill the spot
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               boolean placed = false;
               int[] numbers = random.ints(1, 10).distinct().limit(9).toArray(); // Random numbers 1-9
               // Not every number randomly picked will lead to a valid board, so raise a flag to alert if failed
               for (int num : numbers) {
                   if (isValidMove(row, col, num)) {
                       board[row][col] = num;
                       placed = true;
                       break;
                   }
               }
               // If flag is triggered, reset row and try again
               if (!placed) {
                   // Reset the row and try again
                   for (int resetCol = 0; resetCol <= col; resetCol++) {
                       board[row][resetCol] = 0;
                   }
                   col = -1; // Restart this row
                   fail_count++;
               }
               // If we have failed too many times, this is an error that cannot be easily dealt with, error program for now
               if (fail_count >= 1000){
                   throw new IllegalStateException("Failed to generate a valid Sudoku board");
               }
           }
       }
   }

   // Neelay Ranjan
   // Remove numbers to create the puzzle
   public static void removeNumbers(int count) {
       Random random = new Random();
       for (int i = 0; i < count; i++) {
           int row, col;
           do {
               row = random.nextInt(9);
               col = random.nextInt(9);
           } while (board[row][col] == 0);
           board[row][col] = 0; // Remove the number
       }
   }

   // Liam
   // Check if a move is valid
   public static boolean isValidMove(int row, int col, int num) {
       // Check if the number already exists in the row
       for (int i = 0; i < 9; i++) {
           if (board[row][i] == num) {
        	   return false;
           }
       }
       // Check if the number already exists in the column
       for (int i = 0; i < 9; i++) {
           if (board[i][col] == num) {
        	   return false;
           }
       }
       // Check if the number already exists in the 3x3 grid
       int startRow = row - row % 3, startCol = col - col % 3;
       for (int i = 0; i < 3; i++) {
           for (int j = 0; j < 3; j++) {
               if (board[startRow + i][startCol + j] == num) {
            	   return false;
               }
           }
       }
       return true;
   }

   // Liam
   // Check if the board is complete
   public static boolean isBoardComplete() {
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               if (board[row][col] == 0) {
            	   return false;
               }
           }
       }
       return true;
   }
   
   //Neelay Ranjan
   // Check if the board is complete
   public static void fillHints(int row, int col) {
       Random random = new Random();
       System.out.print("Filled in cell(s): ");
       // Brute force numbers to fill in that cell
       int[] numbers = random.ints(1, 10).distinct().limit(9).toArray(); // Random numbers 1-9
       
       // Not every number randomly picked will lead to a valid board, so raise a flag to alert if failed
       for (int num : numbers) {
           if (isValidMove(row, col, num)) {
               board[row][col] = num;
               System.out.print("Filled in cell " + "(" + (row+1) + "," + (col+1) + "," + (board[row][col]) + ") ");
               return;
           }
       }
       throw new IllegalStateException("Failed to fill cell, this board has NO valid solution, exiting.");
   }
   
   // Nicholas
   public static List<Integer> getPossibleValues(int row, int col) {
       List<Integer> candidates = new ArrayList<>();
       if (board[row][col] != 0) {
    	   // Cell already filled, returning empty ARlist
           return candidates;
       }
       for (int num = 1; num <= 9; num++) {
           if (isValidMove(row, col, num)) {
               candidates.add(num);
           }
       }
       return candidates;
   }
}


