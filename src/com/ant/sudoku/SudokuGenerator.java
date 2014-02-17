package com.ant.sudoku;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Point;
import android.util.Log;

/*
 *  produce a Sudoku grid 
 *  @author: Paul-benoit Larochelle
 */
public class SudokuGenerator {

	// the grid with the numbers
	private int[][] grid;
	// random and array list
	private Random ran;
	private ArrayList<Integer> al;
	// number of row and column in a region
	private int size;
	private int regionSize;

	// size of the Sudoku grid is received as parameter
	// actually not the size of the grid but the size of the region
	SudokuGenerator(int size) {
		regionSize = size;
		this.size = size * size;
		// random number generator
		ran = new Random();
		// arraylist that will contain the possible values for every case in the grid
		al = new ArrayList<Integer>();

	}
	
	// call to generate a new grid of sudoku solution (not problem)
    public void generate(boolean traceOn) {
    	// start by row 0
    	int currentRow = 0;
    	// to count the startOver
    	int[] trials = new int[size];
		// this this the grid that we will fill
		grid = new int[size][size];
		// now let's fill the grid row by row
		while(currentRow < size) {
			trials[currentRow]++;
			// try to generate the row if it works pass to next row
			if(genRow(currentRow)) {
				if(traceOn) {
					System.out.print("Row " + (currentRow+1) + " generated after " + trials[currentRow] + " trial");
					if(trials[currentRow] > 1)
						System.out.print("s");
					System.out.println(".");
				}
				currentRow++;
				continue;
			}
			// so it didn't work check our count
			if(trials[currentRow] < regionSize * regionSize * regionSize * 2) {
				continue;
			}
			// so despite all our effort it does not fit we will have to restart for the whole
			// row regions
			if(traceOn) 
				System.out.print("Quitting for row: " + (currentRow+1));
			while(currentRow % regionSize != 0) {
				trials[currentRow--] = 0;
			}
			trials[currentRow] = 0;
			if(traceOn)
				System.out.println(". Starting over with row: "  + (currentRow+1) + ".");
		}
		// ok our grid is filled with 0-size but sudoku grids do not have 0
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				grid[i][j]++;
			}
		}
    }

    // try to generate an return true if it worked
    // (it might be impossible to fill the row for example if the only element left in the row
    //  is 5 so the last column must be 5 but I may already have a 5 in the column or region)
    private boolean genRow(int row) {
    	// for every column in the row
    	for(int col = 0; col < size; col++) {
    		// fill the arrayList of available value if no value abort
    		if(fillArrayList(row, col) == 0) {
    			return false;
    		}
    		// ok I can retrieve a random value from the arrayList
    		grid[row][col] = al.remove(ran.nextInt(al.size()));
    	}
    	return true;
    }
    
	// fill the ArrayList with all available number for that row,col
	// returns the number of elements in the arraylist
	private int fillArrayList(int row, int col) {
		boolean[] available = new boolean[size];
		// flag all the slot as available
		for(int i = 0; i < size; i++)
			available[i] = true;
		
		// remove the number already used in row
		for(int i = 0; i < row; i++)
			available[grid[i][col]] = false;
		// remove the number already used in col
		for(int i = 0; i < col; i++)
			available[grid[row][i]] = false;
		// now the region. I just have to take care of the row over me in
		// the region the columns to the left of my position have already been checked as unavailable
		Point rowRange = getRegionRowsOrCols(row);
		Point colRange = getRegionRowsOrCols(col);
		for(int i = rowRange.x; i < row; i++) {
			for(int j = colRange.x; j <= colRange.y; j++) {
				available[grid[i][j]] = false;
			}
		}
		
		// empty the arrayList
		al.clear();
		// fill it with all still available numbers
		for(int i = 0; i < size; i++) {
			if(available[i])
				al.add(i);
		}
		return al.size();
	}
	
    // return the first and last row/column of the region into which is located the (row or col)
    private Point getRegionRowsOrCols(int rowOrCol) {
    	int x = (rowOrCol / regionSize) * regionSize;
    	int y = x + regionSize - 1;
    	Point point = new Point(x,y);
    	return point;
    }
	
	
	// to retrieve the grid
	public int[][] getGrid() {
		return grid;
	}
	
	// to print the grid 
	public String toString() {
        // line to separate the region we build it in a StringBuffer
		StringBuffer buffer = new StringBuffer(size * size * size);
		buffer.append('+');
		for(int i = 0; i < size * 2 + size - 2; i++) {
			buffer.append('-');
		}
		// if I use 2 digits to represent the number
		if(size >= 16) {
			for(int i = 0; i < regionSize * 2 + 4; i++)
				buffer.append('-');
		}
		buffer.append('+');
		// saved in a String
		String dash = new String(buffer);

		// and we continue with the numbers
		buffer.append("\n|");
		for(int i = 0; i < size; i++) {        // for every row
			for(int j = 0; j < size; j++) {    // and column
				// depending of the size of the display
				// we may have to pad with spaces
				if(size >= 16)
				{
					if(grid[i][j] < 16)
						buffer.append(' ');
				}
				buffer.append(' ');
				buffer.append(Integer.toHexString((grid[i][j])).toUpperCase()); // add value to String

				// add a | to separate the regions
				if(j % regionSize == regionSize - 1)
					buffer.append(" | ");
			}
			// add a serie of dash every region
			if(i % regionSize == regionSize -1) {
				buffer.append('\n').append(dash);
			}
			buffer.append('\n');
			// add a | but if it is the last one
			if(i < size -1)
				buffer.append('|');
		}
		return new String(buffer);
	}
    
	/*public static void main(String[] arg) {
		SudokuGenerator s = new SudokuGenerator(3);
		s.generate(true);
		System.out.print(s);
	}*/
	
	public static int MAX_FIXED_CELL = 36;
	public static int MIN_FIXED_CELL = 28;
	private ArrayList<Point> cellList;
	private ArrayList<Point> cellListDel;
	private int[][] gridp;
	
	//create sudoku problem from the created sudoku solution grid
	public boolean createProblem() {
		//Log.i("SudokuGenerator", "createProblem() begining");
		//initialize
		cellList = new ArrayList<Point>();
		cellListDel = new ArrayList<Point>();
		gridp = new int[size][size];
		
		//deflate grid[][] to cellList
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				cellList.add(new Point(i*size + j, grid[i][j]));
		
		//randomly delete some cells until satisfying conditions
		int ncell = size*size;
		int maxRan = size*size;
		int trials = 0;
		while (ncell > MIN_FIXED_CELL) {
			Log.i("SudokuGenerator", "createProblem() ncell="+ncell);
			int t = ran.nextInt(ncell);
			trials++;
			
			//check if the cell t is deletable
			boolean deletable = true;
			int backup = cellList.get(t).y;
			for (int j=1; j<=9; j++)
				if (j!=backup) {
					//replace the value to j
					Point p = cellList.get(t);
					cellList.remove(t);
					p.y = j;
					cellList.add(t,p);
					
					inflateGrid(cellList, gridp);
					if (solveGrid(gridp)) {
						deletable = false;
						p = cellList.remove(t);
						p.y = backup;
						cellList.add(t,p);
						break;
					}
				}
			//return the value backup to cell
			Point p = cellList.remove(t);
			p.y = backup;
			cellList.add(t,p);
			
			if (deletable) {
				cellListDel.add(cellList.remove(t));
				ncell--;
				maxRan--;
				trials = 0;
				if (maxRan==0) {
					if (ncell < MAX_FIXED_CELL) {
						//already acceptable
						inflateGrid(cellList, gridp);
						return true;
					} else 
						return false;
				}
			} else if (trials > maxRan) {
				if (ncell < MAX_FIXED_CELL) {
					//already acceptable
					inflateGrid(cellList, gridp);
					return true;
				}
				if (cellListDel.size() == 0) {
					Log.i("SudokuGenerator", "createProblem() can't create from current grid");
					return false;
				}
				//back tracking
				trials = 0;
				cellList.add(cellListDel.remove(cellListDel.size()-1));
				ncell++;
			}
		}
		//finish creating
		inflateGrid(cellList, gridp);
		return true;
	}
	
	//create grid from passed-in ArrayList
	private void inflateGrid(ArrayList<Point> al, int[][] grid) {
		//Log.i("SudokuGenerator", "inflateGrid() begining");
		if (grid == null)
			grid = new int[size][size];
		
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				grid[i][j] = 0;
		
		for (int i=0; i<al.size(); i++) {
			int x = al.get(i).x / size;
			int y = al.get(i).x - x*size;
			grid[x][y] = al.get(i).y + 1;
		}
	}
	
	ArrayList<Point> stack;
	int[][][] flag;
	//solve passed-in sudoku grid
	private boolean solveGrid(int[][] grid) {
		//Log.i("SudokuGenerator", "solveGrid() begining");
		stack = new ArrayList<Point>();
		flag = new int[size][size][size+1];
		
		//check if the grid is right
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				if (grid[i][j] > 0) {
					int backup = grid[i][j];
					grid[i][j] = 0;
					if (!checkPut(grid, i, j, backup))
						return false;
					grid[i][j] = backup;
				}
		
		//init flag[][][]
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++) {
				flag[i][j][0] = (grid[i][j]==0)?0:1; //0->need to be filled, 1->fixed
				for (int k=1; k<=size; k++) 
					flag[i][j][k] = 0; //0->not try yet, 1->tried already
			}
		
		//backtracking
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				if (flag[i][j][0]==0) {
					stack.add(new Point(i,j));
					flag[i][j][0] = 1;
					break;
				}
		
		while (stack.size() > 0) {
			Point p = stack.get(stack.size()-1);
			int i, j, k;
			for (k=1; k<=size; k++)
				if (flag[p.x][p.y][k]==0) break;
			if (k>size) {
				//there is no more possible number
				//Log.i("SudokuGenerator", "solveGrid() stack remove");
				Point p2 = stack.remove(stack.size()-1);
				flag[p2.x][p2.y][0] = 0;
				for (k=1; k<=size; k++)
					flag[p2.x][p2.y][k] = 0;
				continue;
			}
			if (checkPut(grid, p.x, p.y, k)) {
				grid[p.x][p.y] = k;
				flag[p.x][p.y][k] = 1;
				i = p.x;
				j = p.y;
				while (flag[i][j][0] == 1) {
					j++;
					if (j>=size) {
						i++;
						j = 0;
						if (i>=size)
							break;
					}
				}
				if (i<size) {
					//Log.i("SudokuGenerator", "solveGrid() stack add ("+i+","+j+")");
					stack.add(new Point(i,j));
					flag[i][j][0] = 1;
				}
			} else {
				flag[p.x][p.y][k] = 1;
			}
		}
		return true;
	}
	
	private boolean checkPut(int[][] grid, int x, int y, int t) {
		//Log.i("SudokuGenerator", "checkPut() ("+x+","+y+") "+t);
		int i,j;
		i = x;
		for (j=0; j<size; j++)
			if (grid[i][j] == t) return false;
		j = y;
		for (i=0; i<size; i++)
			if (grid[i][j] == t) return false;
		x = (x / regionSize) * regionSize;
		y = (y / regionSize) * regionSize;
		for (i=x; i<=x+2; i++)
			for (j=y; j<=y+2; j++)
				if (grid[i][j] == t) return false;
		
		return true;
	}
	
	public void pourProblem() {
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				grid[i][j] = gridp[i][j];
	}
}

