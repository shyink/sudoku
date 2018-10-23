package com.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

public class Cell {

	private char contents;
	private List<Character> markup;
	
	public Cell() {
		contents = 'X';
		markup = new ArrayList<Character>();
	}

	Cell(char number, List<Character> markup) {
		this.contents = number;
		this.markup = markup;
	}

	/**
	 * @return the contents
	 */
	public char getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(char contents) {
		this.contents = contents;
	}

	/**
	 * @return the markup
	 */
	public List<Character> getMarkup() {
		return markup;
	}

	/**
	 * @param markup the markup to set
	 */
	public void setMarkup(List<Character> markup) {
		this.markup = markup;
	}
	
}
