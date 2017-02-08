//==============================================================================
//	
 //	Copyright (c) 2015-
//	Authors:
//	* Simos Gerasimou (University of York)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of EvoChecker.
//	
//==============================================================================

package org.spg.refactoring.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Utility class with helper functions
 * @author sgerasimou
 *
 */
public class Utility {
	
	public static void exportToFile(String fileName, String output, boolean append){
		try {
			FileWriter writer = new FileWriter(fileName, append);
			writer.append(output);
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void createFileAndExport(String inputFileName, String outputFileName, String outputStr){
		FileChannel inputChannel 	= null;
		FileChannel outputChannel	= null;
				
		try {
			File input 	= new File(inputFileName);
			File output 	= new File(outputFileName);
			
			inputChannel 	= new FileInputStream(input).getChannel();
			outputChannel	= new FileOutputStream(output).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

			inputChannel.close();
			outputChannel.close();
			
			exportToFile(outputFileName, outputStr, false);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("resource")
	public static String readFile(String fileName) {
		try {
			File f = new File(fileName);
			if (!f.exists() || f.isDirectory())
				throw new IOException("File does not exist! " + f );
		
			StringBuilder model = new StringBuilder(100);
			BufferedReader bfr = null;

			bfr = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = bfr.readLine()) != null) {
				model.append(line + "\n");
			}
			model.delete(model.length() - 1, model.length());
			return model.toString();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	
	public static void exportToFile(List<String> outputList, String fileName){
		try {
			FileWriter writer = new FileWriter(fileName);
			for (String str : outputList){	
				writer.append(str +"\n");
			}
				writer.flush();
				writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	
}
