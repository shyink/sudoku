package com.sudoku.solver;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SudokuSolver implements CommandLineRunner{
	
	private static final Logger LOG = LogManager.getLogger(SudokuSolver.class);
	
	@Value("${sudoku.puzzles.path}")
	private String puzzlesPath;
	
	@Value("${sudoku.solutions.path}")
	private String solutionsPath;
	
	@Value("${sudoku.solutions.maxRows}")
	private int maxRows;
	
	@Value("${sudoku.solutions.maxColumns}")
	private int maxColumns;
	
	private SudokuUtility utility = new SudokuUtility();
	
	@Override
	public void run(String... args) throws Exception {		
    	try (Stream<Path> paths = Files.walk(Paths.get(puzzlesPath)).filter(file -> !file.getFileName().toString().contains("sln"))) {
    	    paths
    	        .filter(Files::isRegularFile)
    	        .forEach(path -> importPuzzle(path));
    	}
	}
	
	/**
	 * Imports the puzzle at the given path and attempts to solve it
	 * @param Path to the puzzle
	 */
	private void importPuzzle(Path path) {
		LOG.info("Reading Sudoku puzzle " + path.getFileName());
		Cell[][] sudokuGrid = new Cell[maxRows][maxColumns];
		int row = 0;
		
		try {
			List<String> list = Files.lines(Paths.get(path.toString())).collect(Collectors.toList());
			for (String line : list) {
				sudokuGrid[row++] = utility.convertCharArrayToCellArray(line.toCharArray());
			}
		} catch (IOException e) {
			LOG.error("Failed to import the puzzle. Make sure % is in the correct format", path.getFileName(), e);
			return;
		}
		
		LOG.info(path.getFileName() + " successfully imported");
		Grid puzzle = new Grid(sudokuGrid, maxRows, maxColumns, path.getFileName().toString().replaceAll(".txt", ""));
		utility.setMarkup(puzzle);
		try {
			solvePuzzle(puzzle);
		} catch (Exception e) {
			LOG.error("Failed to solve % ", puzzle.getName(), e);
		}
		
		try (PrintWriter out = new PrintWriter(solutionsPath + "/" + puzzle.getName() + ".sln.txt")) {
			out.println(puzzle.toString());
			LOG.info("Solution to % saved successfully", puzzle.getName());
		} catch (Exception e) {
			LOG.error("Failed to save solution for puzzle %", puzzle.getName(), e);
		}
	}
	
	/**
	 * Goes through the steps to solve the sudoku puzzle. If the puzzle hasn't been solve, then it calls again to solve the updated puzzle
	 * @param Grid puzzle to be solved
	 */
	private void solvePuzzle(Grid puzzle) {
		utility.nakedSinglesCheck(puzzle);
		utility.pointingPairCheck(puzzle);
		utility.hiddenCheck(puzzle);
		
		if (!utility.isSolved(puzzle)) {
			solvePuzzle(puzzle);
		}		
	}

}
