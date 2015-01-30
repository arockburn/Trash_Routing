/***
 * Loads a text file of shortest paths and returns it as an array of strings. I thought I would need 
 * more methods in this class so I made a separate class, but I didn't need any more :D so now there is
 * just one little lonely loner down there all cold and sad in the java wilderness. Hang in there little
 * guy. I hope you don't read this and think I am some sort of neurotic! 
 * 
 * @author Sean M Brown
 * Version 1.0
 * Revision Date: March 2013
 */

import java.io.*;

public class TextFileLoader 
{
	/**
	 * Retrieves all data at an index from a given text file.
	 * 
	 * @param fileName
	 * @param index
	 * @return distances
	 */
	public String[] getDataFromFileAtIndex(String fileName, int index)
	{
		try {
			BufferedReader textReader = new BufferedReader(new FileReader(fileName));
			String tfAsString = "";
			
			while(textReader.ready()) 
			{
				tfAsString += textReader.readLine() + "\n";
			}
			
			String[] tfArray = tfAsString.split("\n");
			String[] distances = new String[tfArray.length];
			for(int i = 0; i < tfArray.length; i++) 
			{
				String[] delimitedValues = tfArray[i].split("@");
				distances[i] = delimitedValues[index];
			}
			textReader.close();
			return distances;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
