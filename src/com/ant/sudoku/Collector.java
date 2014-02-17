package com.ant.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class Collector {
	static final String FILENAME_ALL = "playing_grids";
	//static final String FILENAME_PLAYING = "playing_grids";
	
	private static Context context; 
	
	public static ArrayList<String> data; //store all strings in FILENAME_ALL
	
	public static void initialize(Context context) {
		Collector.context = context;
		
		//check if the file exists
		boolean fileExisting = true;
		try {
			FileOutputStream file = context.openFileOutput(FILENAME_ALL, Context.MODE_APPEND);
			file.close();
		} catch (FileNotFoundException e) {
			fileExisting = false;
		} catch (IOException e) {
			//do nothing
		}
		
		while (true) {
			if (/*true*/!fileExisting) {
				try {
					//not exist -> create
					FileOutputStream fos = context.openFileOutput(FILENAME_ALL, Context.MODE_PRIVATE);
					InputStream is = context.getResources().openRawResource(R.raw.initial_sudoku_grids);
					String str = "";
					int c;
					while ((c=is.read()) != -1) {
						if (Character.isDigit(c) || c==',')
							str += ((char) c);
						else if (c=='\n') {
							//adjust one line str, then write it into file
							int order = parse2GetOrder(str);
							int level = parse2GetLevel(str);
							String str2 = parse2GetGrid(str);
							str = Integer.toString(order) + ',' + Integer.toString(level) + ",0,";
							for (int i=0; i<9; i++)
								for (int j=0; j<9; j++) {
									int value = str2.charAt(i*9+j) - '0';
									if (value == 0)
										for (int k=0; k<10; k++)
											str += '0';
									else 
										for (int k=0; k<10; k++)
											if (k==0) str += '-';
											else if (k==value) str += '1';
											else str += '0';
								}
							str += '\n';
							for (int i=0; i<str.length(); i++)
								fos.write(str.charAt(i));
							str = "";
						}
					}
					fos.close();
					is.close();
				} catch (FileNotFoundException e) {
					Log.i("Collector", "FILENAME_ALL not found for reading.");
				} catch (IOException e) {
					Log.i("Collector", "IOException.");
				}
			}
			//read data to string ArrayList data
			try {
				FileInputStream fis = context.openFileInput(FILENAME_ALL);
				data = new ArrayList<String>();
				String str = "";
				int c = fis.read();
				while (true) {
					while ((c<='9' && c>='0') || c==',' || c=='p' || c=='-') {
						str += ((char) c);
						c = fis.read();
					}
					data.add(str);
					str = "";
					while (c!=-1 && c!=',' && c!='p' && c!='-' && (c<'0' || c>'9'))
						c = fis.read();
					if (c==-1)
						break;
				}
				fis.close();
			} catch (IOException e) {
				Log.i("Collector", "IOException.");
			}
			//check the data array
			if (data.size() < 2) 
				fileExisting = false;
			else 
				break;
		}
	}
	
	//write the data back to file
	public static void dismiss() {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME_ALL, Context.MODE_PRIVATE);
			while (!data.isEmpty()) {
				String str = data.remove(0);
				for (int i=0; i<str.length(); i++)
					fos.write(str.charAt(i));
				fos.write('\n');
			}
			fos.close();
		} catch (FileNotFoundException e) {
			Log.i("Collector", "FILENAME_ALL not found for rewriting.");
		} catch (IOException e) {
			Log.i("Collector", "IOException.");
		}
	}
	
	//check if the first character is 'p' 
	/*public static boolean isPlaying(String str) {
		str = str.trim();
		char c = str.charAt(0);
		if (c == 'p') return true;
		else return false;
	}*/
	
	public static boolean isLatestPlaying(String str) {
		str = str.trim();
		char c = str.charAt(0);
		if (c == 'p') return true;
		else return false;
	}
	
	//get the first number in str
	public static int parse2GetOrder(String str) {
		str = str.trim();
		int i=0;
		while (!Character.isDigit(str.charAt(i)))
			i++;
		int j=i;
		while (Character.isDigit(str.charAt(j)))
			j++;
		String s = str.substring(i, j);
		int n = Integer.parseInt(s);
		return n;
	}
	
	//get the second number in str
	public static int parse2GetLevel(String str) {
		str = str.trim();
		int i=0;
		while (!Character.isDigit(str.charAt(i)))
			i++;
		while (Character.isDigit(str.charAt(i)))
			i++;
		while (!Character.isDigit(str.charAt(i)))
			i++;
		int j=i;
		while (Character.isDigit(str.charAt(j)))
			j++;
		return Integer.parseInt(str.substring(i,j));
	}
	
	//get the last digit string (including '-' character which symbolizes -1) in str
	public static String parse2GetGrid(String str) {
		str = str.trim();
		int n = str.length();
		int i = n-1;
		while (!Character.isDigit(str.charAt(i)))
			i--;
		int j = i;
		while (Character.isDigit(str.charAt(j)) || str.charAt(j)=='-')
			j--;
		return str.substring(j+1, i+1);
	}
	
	//check if the first character is 'p' 
	/*public static boolean isPlaying(int index) {
		return isPlaying(data.get(index));
	}*/
	
	public static boolean isLatestPlaying(int index) {
		return isLatestPlaying(data.get(index));
	}
	
	//get the first number in data[index]
	public static int parse2GetOrder(int index) {
		return parse2GetOrder(data.get(index));
	}
	
	//get the second number in data[index]
	public static int parse2GetLevel(int index) {
		return parse2GetLevel(data.get(index));
	}
	
	//get the last digit string in data[index]
	public static String parse2GetGrid(int index) {
		return parse2GetGrid(data.get(index));
	}
	
	//get the third number in data[index]
	public static int parse2GetTime(int index) {
		/*if (!Collector.isPlaying(index))
			return 0;*/
		String str = data.get(index);
		str = str.trim();
		int i=0;
		//find first number
		while (!Character.isDigit(str.charAt(i)))
			i++;
		while (Character.isDigit(str.charAt(i)))
			i++;
		//find second number
		while (!Character.isDigit(str.charAt(i)))
			i++;
		while (Character.isDigit(str.charAt(i)))
			i++;
		//find third number
		while (!Character.isDigit(str.charAt(i)))
			i++;
		int j=i;
		while (j<str.length() && Character.isDigit(str.charAt(j)))
			j++;
		if (j-i>5)
			return 0;
		else
			return Integer.parseInt(str.substring(i,j));
	}
	
	public static int size() {
		return data.size();
	}
	
	//replace the string at line index
	public static void replace(int index, int order, int level, int time, String grid) {
		data.remove(index);
		data.add(index, Integer.toString(order) + ',' + Integer.toString(level) + ',' + Integer.toString(time) + ',' + grid);
	}
	
	//return index of the grid which was latest played
	public static int getLatestPlayingIndex() {
		//look for string with "p" prefix
		for (int i=0; i<size(); i++)
			if (Collector.isLatestPlaying(i))
				return i;
		//take the first string
		//String str = "pp," + data.remove(0);
		//data.add(0, str);
		return 0;
	}
	
	/*public static String getGrid() {
		return "000700428714800050200940010890000001002060700600000049020084005050002174469007000";
	}*/
	
	public static void updateData(int index, String str) {
		data.remove(index);
		data.add(index, str);
	}
	
	public static void removeLatestPlayingMark() {
		for (int i=0; i<data.size(); i++)
			if (isLatestPlaying(i)) {
				int order = parse2GetOrder(i);
				int level = parse2GetLevel(i);
				int time = parse2GetTime(i);
				String grid = parse2GetGrid(i);
				replace(i, order, level, time, grid);
				break;
			}
	}
}
