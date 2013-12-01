package edu.uwm.rajohnson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MatrixToArff {

	public MatrixToArff() {
		
	}
	
	public void convertMatrixToArff()
	{
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the name of a matrix you would like to convert into an arff: ");
		String fileName = sc.next();
		File f = new File(fileName);
		while(!f.exists() && !f.isDirectory())
		{
			System.out.println("\n" + fileName + " was not a valid file.");
			System.out.print("Enter the name of a matrix you would like to convert into an arff: ");
			fileName = sc.next();
			f = new File(fileName);
		}
		
		System.out.print("Enter the desired name for your output file: ");
		String outFileName = sc.next();
		while(true)
		{
			System.out.print("\n" + outFileName + ".arff will be the name of your output file. Is this acceptable? (y/n): ");
			String response = sc.next();
			if(response.equalsIgnoreCase("y"))
			{
				System.out.print("\n");
				break;
			}
			else if(response.equalsIgnoreCase("n"))
			{
				System.out.print("\nEnter the desired name for your output file: ");
				outFileName = sc.next();
			}
			else
			{
				System.out.print("\n" + response + " is not a valid response.");
			}
		}
		String finalOutfileName = outFileName + ".arff";
		File out = new File(finalOutfileName);
		BufferedReader inFile = null;
		BufferedWriter outFile = null;
		try {
			inFile = new BufferedReader(new FileReader(f));
			outFile = new BufferedWriter(new FileWriter(out));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm z");
			
			outFile.write("% 1. Title: " + finalOutfileName + "\n%\n");
			outFile.write("% 2. Date Generated: " + sdf.format(new Date()) + "\n%\n");
			outFile.write("% 3. Generated using: " + fileName + "\n%\n");
			
			outFile.write("@RELATION matrix\n\n");
			
			String inputLine = inFile.readLine();
			while(inputLine.startsWith("#"))
			{
				inputLine = inFile.readLine();
			}
			StringTokenizer tokenizer = new StringTokenizer(inputLine);
			
			int numColumns = tokenizer.countTokens();
			//Writes out a number of attributes equal the number of columns in the first entry.
			for(int i = 0; i < numColumns;i++)
			{
				outFile.write("@ATTRIBUTE col" +Integer.toString(i+1) + " NUMERIC\n");
			}
			outFile.write("\n");
			outFile.write("@DATA\n");
			
			writeRow(tokenizer, outFile);
			
			// While there is still input to read.
			while(inputLine != null)
			{
				
				tokenizer = new StringTokenizer(inputLine);
				if(tokenizer.countTokens() < 1)
				{
					System.out.println("End of input file reached. File creation finalizing...");
					sc.close();
					return;
				}
				// If the line is not the solitary new line that exists at the end of the file and the right number of columns are not found.
				if(tokenizer.countTokens() != numColumns)
				{
					System.out.println("Staggered matrix detected. Cannot convert to arff.");
					sc.close();
					return;
				}
				writeRow(tokenizer, outFile);
				inputLine = inFile.readLine();
			}
			
			// All rows are written. Cleanup occurs in finally block. 
			
			
		} catch (FileNotFoundException e) {
			System.err.println("Input file as entered was not found.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			if(inFile != null)
			{
				try {
					inFile.close();
				} catch (IOException e) {
					System.err.println("Something strange occured when closing the input file.");
					e.printStackTrace();
				}
			}
			if(outFile != null)
			{
				try {
					outFile.close();
					System.out.println("Output file created successfully!");
				} catch (IOException e) {
					System.err.println("Something strange occured when closing the output file.");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void writeRow(StringTokenizer inputRow, BufferedWriter outFile) throws IOException
	{
		int numColumnsMinusOne = inputRow.countTokens()-1;
		
		// All columns except for the last column are followed by a comma. 
		for(int i = 0; i < numColumnsMinusOne; i++)
		{
			outFile.write(inputRow.nextToken() + ",");
		}
		
		// The last column is instead followed by a newline.
		outFile.write(inputRow.nextToken() + "\n");
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MatrixToArff mta = new MatrixToArff();
		mta.convertMatrixToArff();
	}

}
