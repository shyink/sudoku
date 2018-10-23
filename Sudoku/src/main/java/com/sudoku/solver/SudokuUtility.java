package com.sudoku.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SudokuUtility {
	
	private static final Logger LOG = LogManager.getLogger(SudokuUtility.class);
	
	private static final int MAX_ROWS = 9;
	private static final int MAX_COLUMNS = 9;
	
	/**
	 * Sets the markup for all the cells in the sudoku puzzle
	 * @param Grid the sudoku puzzle
	 */
	public void setMarkup(Grid puzzle) {
		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLUMNS; column++) {
				computeMarkup(puzzle, row, column);
			}
		}
	}
	
	/**
	 * Computes the markup for the cell at the given x/y coordinate
	 * @param Grid the sudoku puzzle
	 * @param int the row the cell is on
	 * @param int the column the cell is on
	 */
	private void computeMarkup(Grid puzzle, int row, int column) {
		if (puzzle.getCell(row, column).getContents() != 'X') {
			return;
		}
		
		List<Character> columnList = new ArrayList<>();
		List<Character> rowList = new ArrayList<>();
		List<Character> boxList = new ArrayList<>();
		
		try {
			columnList = convertCellListToCharList(puzzle.getColumn(column));
			rowList = convertCellListToCharList(puzzle.getRow(row));
			boxList = convertCellListToCharList(puzzle.getBoxList((row/3)*3, (column/3)*3));
		} catch (Exception e) {
			LOG.error("Failed to get row, column, and box for the % at % / %", puzzle.getName(), row, column, e);
		}
		
		List<Character> markup = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			Character num = Character.forDigit(i, 10);
			if (!rowList.contains(num)
					&& !columnList.contains(num)
					&& !boxList.contains(num)) {
				markup.add(num);
			}
		}
		puzzle.getCell(row, column).setMarkup(markup);
	}
	
	/**
	 * Converts a char[] to a cell[]. Creates a new Cell for all incoming numbers read from the sudoku puzzle files.
	 * @param char[] charArray to convert
	 * @return Cell[]
	 */
	public Cell[] convertCharArrayToCellArray(char[] charArray) {
		Cell[] cellArray = new Cell[charArray.length];
		int i = 0;
		for (char content : charArray) {
			cellArray[i++] = new Cell(content, new ArrayList<Character>());
		}
		
		return cellArray;
	}
	
	/**
	 * Converts the list of Cells into a list of just the Cells Characters
	 * @param List<Cell> cellList to convert
	 * @return List<Character>
	 */
	private List<Character> convertCellListToCharList(List<Cell> cellList) {
		List<Character> charList = new ArrayList<>();
		for (Cell cell : cellList) {
			charList.add(cell.getContents());
		}
		
		return charList;
	}
	
	/**
	 * Checks for cells with only one number in their markup. If found, it will set that number to the cell;
	 * @param Grid puzzle to check
	 */
	public void nakedSinglesCheck(Grid puzzle) {
		boolean puzzleUpdated = false;
		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLUMNS; column++) {
				if (puzzle.getCell(row, column).getContents() != 'X') {
					continue;
				}
				if (puzzle.getCell(row, column).getMarkup().size() == 1) {
					saveCell(puzzle, row, column, puzzle.getCell(row, column).getMarkup().get(0));
					puzzleUpdated = true;
				}
			}
		}
		
		if (puzzleUpdated) {
			nakedSinglesCheck(puzzle);
		}
	}
	
	/**
	 * Checks for hidden singles in the sudoku puzzle. if found it will set the number to the cell.
	 * @param Grid puzzle to check
	 */
	public void hiddenCheck(Grid puzzle) {
		List<Character> markup = new ArrayList<>();
		List<Cell> columnList = new ArrayList<>();
		List<Cell> rowList = new ArrayList<>();
		List<Cell> boxList = new ArrayList<>();
		
		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLUMNS; column++) {
				if (puzzle.getCell(row, column).getContents() != 'X') {
					continue;
				}
				markup = puzzle.getCell(row, column).getMarkup();
				try {
					columnList = puzzle.getColumn(column);
					rowList = puzzle.getRow(row);
					boxList = puzzle.getBoxList((row/3)*3, (column/3)*3);
				} catch (Exception e) {
					LOG.error("Failed to get row, column, and box for the % at % / %", puzzle.getName(), row, column, e);
				}
				if (checkMarkup(puzzle, row, column, markup, columnList)) {
					continue;
				} else if (checkMarkup(puzzle, row, column, markup, rowList)) {
					continue;
				} else if (checkMarkup(puzzle, row, column, markup, boxList)) {
					continue;
				}
			}
		}
	}
	
	/**
	 * Checks the markup for hidden pairs
	 * @param Grid puzzle to check
	 * @param int row the cell is on
	 * @param int column the cell is on
	 * @param List<Character> markup of the cell
	 * @param List<Cell> the row, column, or box thats being checked
	 * @return boolean if a cell has been updated
	 */
	private boolean checkMarkup(Grid puzzle, int row, int column, List<Character> markup, List<Cell> cellList) {
		for (Character content : markup) {
			List<Character> markups = new ArrayList<Character>();
			cellList.stream().forEach(temp -> markups.addAll(temp.getMarkup()));
			if (Collections.frequency(markups, content) == 1) {
				saveCell(puzzle, row, column, content);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks the puzzle for a pointed pair.
	 * @param Grid puzzle to be checked
	 */
	public void pointingPairCheck(Grid puzzle) {
		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLUMNS; column++) {
				if (puzzle.getCell(row, column).getContents() != 'X') {
					continue;
				}
				checkCellForPointedPair(puzzle, row, column);
			}
		}
	}
	
	/**
	 * Checks if the given cell is part of a pointed pair. If it is, it will update the markups of surrounding cells
	 * @param Grid puzzle that's being checked
	 * @param int row of the cell
	 * @param int column of the cell.
	 */
	private void checkCellForPointedPair(Grid puzzle, int row, int column) {
		int boxStartX = (row/3)*3;
		int boxStartY = (row/3)*3;
		List<Cell> box = new ArrayList<>();
		List<Cell> boxRow = new ArrayList<>();
		List<Cell> boxColumn = new ArrayList<>();
		List<Character> boxMarkup = new ArrayList<>();
		List<Character> boxRowMarkup = new ArrayList<>();
		List<Character> boxColumnMarkup = new ArrayList<>();
		try {
			box = puzzle.getBoxList((row/3)*3, (column/3)*3);
			boxRow = puzzle.getBoxRow(boxStartX, boxStartY, row%3);
			boxColumn = puzzle.getBoxColumn(boxStartX, boxStartY, column%3);
		} catch (Exception e) {
			LOG.error("Failed to to generate the list of elements contained within the box", e);
		}
		
		box.stream().forEach(cell -> boxMarkup.addAll(cell.getMarkup()));
		boxRow.stream().forEach(cell -> boxRowMarkup.addAll(cell.getMarkup()));
		boxColumn.stream().forEach(cell -> boxColumnMarkup.addAll(cell.getMarkup()));
		
		for (Character character : puzzle.getCell(row, column).getMarkup()) {
			int rowFrequency = Collections.frequency(boxRowMarkup, character);
			int columnFrequency = Collections.frequency(boxColumnMarkup, character);
			if (rowFrequency > 1 && Collections.frequency(boxMarkup, character) == rowFrequency) {
				List<Cell> rowList = puzzle.getRow(row);
				for (int i = boxStartX+2; i >= boxStartX; i--) {
					rowList.remove(i);
				}
				rowList.stream().forEach(cell -> cell.getMarkup().remove(character));
				return;
			} else if (columnFrequency > 1 && Collections.frequency(boxMarkup, character) == columnFrequency) {
				List<Cell> columnList = puzzle.getColumn(row);
				for (int i = boxStartY+2; i >= boxStartY; i--) {
					columnList.remove(i);
				}
				columnList.stream().forEach(cell -> cell.getMarkup().remove(character));
				return;
			}
		}
	}
	
	/**
	 * Checks if puzzle has been solved;
	 * @param Grid puzzle to check
	 * @return boolean if puzzle has been solved
	 */
	public boolean isSolved(Grid puzzle) {
		if (puzzle.isSolved()) {
			return puzzle.isSolved();
		}
		
		List<Character> solvedList = new ArrayList<>();
		solvedList.add('1');
		solvedList.add('2');
		solvedList.add('3');
		solvedList.add('4');
		solvedList.add('5');
		solvedList.add('6');
		solvedList.add('7');
		solvedList.add('8');
		solvedList.add('9');
		
		
		
		for (int row = 0; row < MAX_ROWS; row++) {
			if (!convertCellListToCharList(puzzle.getRow(row)).containsAll(solvedList)) {
				return false;
			}
		}
		
		for (int column = 0; column < MAX_COLUMNS; column++) {
			if (!convertCellListToCharList(puzzle.getColumn(column)).containsAll(solvedList)) {
				return false;
			}
		}
		
		int row = 0;
		int column = 0;
		for (int i = 0; i < 9; i++) {
			try {
				if (!convertCellListToCharList(puzzle.getBoxList(row, column)).containsAll(solvedList)) {
					return false;
				}
			} catch (Exception e) {
				LOG.error("Failed to to generate the list of elements contained within the box", e);
			}
		}
		
		puzzle.setSolved(true);
		return puzzle.isSolved();
	}
	
	/**
	 * Saves a cell and removes the number from all adjacent markups
	 * @param Grid puzzle to update
	 * @param int row of the cell
	 * @param int column of the cell
	 * @param char content to save to the cell
	 */
	private void saveCell(Grid puzzle, int row, int column, char content) {
		puzzle.getCell(row, column).setContents(content);
		puzzle.getCell(row, column).getMarkup().clear();
		
		try {
			removeFromMarkup(content, puzzle.getRow(row), puzzle.getColumn(column), puzzle.getBoxList((row/3)*3, (column/3)*3));
		} catch (Exception e) {
			LOG.error("Failed to get row, column, and box for the % at % / %", puzzle.getName(), row, column, e);
		}
	}
	
	/**
	 * Removes number from markups
	 * @param char content to be removed
	 * @param List<Cell> row that will have the content removed from their markups
	 * @param List<Cell> column that will have the content removed from their markups
	 * @param List<Cell> box that will have the content removed from their markups
	 */
	private void removeFromMarkup(char content, List<Cell> rowList, List<Cell> columnList, List<Cell> boxList) {
		Predicate<Character> charFilter = new Predicate<Character>() {
			@Override
			public boolean test(Character character) {
				return character.equals(content);
			}
		};
		rowList.forEach(cell -> cell.getMarkup().removeIf(charFilter));
		columnList.forEach(cell -> cell.getMarkup().removeIf(charFilter));
		boxList.forEach(cell -> cell.getMarkup().removeIf(charFilter));
	}
}
