package com.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

public class Grid {
	
	private final int maxColumns;
	private final int maxRows;
	private final String name;
	private Cell[][] grid;
	private boolean solved;
	
	/**
	 * Grid constructor
	 * @param grid
	 * @param maxColumns
	 * @param maxRows
	 */
	Grid(Cell[][] grid, int maxRows, int maxColumns, String name) {
		this.grid = grid;
		this.maxColumns = maxColumns;
		this.maxRows = maxRows;
		this.name = name;
		this.solved = false;
	}
	
	/**
	 * @return List<Cell> column at the given column number
	 */
	public List<Cell> getColumn(int columnNumber) {
		List<Cell> column = new ArrayList<>();
		for (int i = 0; i < maxRows; i++) {
			column.add(grid[i][columnNumber]);
		}
		return column;
	}
	
	/**
	 * @return List<Cell> row at the given row number
	 */
	public List<Cell> getRow(int rowNumber) {
		List<Cell> row = new ArrayList<>();		
		for (int i = 0; i < maxColumns; i++) {
			row.add(grid[rowNumber][i]);
		}
		return row;
	}
	
	/**
	 * Gets the contents of the box a the given coordiates and returns it in list form
	 * @param int startX the starting x coordinate for the box
	 * @param int startY the starting y coordinate for the box
	 * @return List<Cell> all the cells in the box
	 * @throws Exception
	 */
	public List<Cell> getBoxList(int startX, int startY) throws Exception {
		if (startX + 3 > maxRows && startY + 3 > maxColumns) {
			return null;
		}
		
		List<Cell> box = new ArrayList<>();
		for (int i = startX; i < startX + 3; i++) {
			for (int j = startY; j < startY + 3; j++) {
				box.add(grid[i][j]);
			}
		}
		return box;
	}
	
	/**
	 * Gets the column of the box a the given coordiates.
	 * @param int startX the starting x coordinate for the box
	 * @param int startY the starting y coordinate for the box
	 * @param int column to return
	 * @return List<Cell>
	 */
	public List<Cell> getBoxColumn(int startX, int startY, int column) {
		if (startX + 3 > maxRows && startY + 3 > maxColumns) {
			return null;
		}
		
		List<Cell> box = new ArrayList<>();
		for (int row = startX; row < startX + 3; row++) {
			box.add(grid[row][startY+column]);
		}
		return box;
	}
	
	/**
	 * Gets the row of the box a the given coordiates.
	 * @param int startX the starting x coordinate for the box
	 * @param int startY the starting y coordinate for the box
	 * @param int row to return
	 * @return List<Cell>
	 */
	public List<Cell> getBoxRow(int startX, int startY, int row) {
		if (startX + 3 > maxRows && startY + 3 > maxColumns) {
			return null;
		}
		
		List<Cell> box = new ArrayList<>();
		for (int column = startY; column < startY + 3; column++) {
			box.add(grid[startX+row][column]);
		}
		return box;
	}
	
	/**
	 * Gets the cell at the given coordinates
	 * @param int x coordinate
	 * @param int y coordinate
	 * @return Cell
	 */
	public Cell getCell(int x, int y) {
		return grid[x][y];
	}
	
	/**
	 * Sets the cell at the given coordinates
	 * @param Cell value to be set
	 * @param int x coordinate
	 * @param int y coordinate
	 */
	public void setCell(Cell value, int x, int y) {
		grid[x][y] = value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < maxRows; i++) {
			for (int j = 0; j < maxColumns; j++) {
				sb.append(grid[i][j].getContents());
			}
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	/**
	 * @return the name of the puzzle
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the solved
	 */
	public boolean isSolved() {
		return solved;
	}

	/**
	 * @param solved the solved to set
	 */
	public void setSolved(boolean solved) {
		this.solved = solved;
	}
	
}
