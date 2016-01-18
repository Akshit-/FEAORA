package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseMoselFile {
	public static List<String> getParameters(File file){
		List<String> paramList = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String currentLine;
			 
			while ((currentLine = br.readLine() )!= null) {
				//check for declarations
				if(currentLine.startsWith("declarations")){
					while((currentLine = br.readLine() )!= null){
						if(!currentLine.startsWith("end-declarations")){
							if(currentLine.contains(":")){
								
							}
							
						} else {
							break;
						}
					}
					
				}
				System.out.println(currentLine);
			}
			br.close();
			return paramList;
		} catch(IOException ex){
			System.out.println("Exception: "+ex.getMessage());
			return null;
		}
	}
}
