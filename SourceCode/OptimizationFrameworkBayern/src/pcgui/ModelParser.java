package pcgui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang3.math.NumberUtils;

import model.OutputParams;

import com.dashoptimization.*;

public class ModelParser {
	XPRMModel mod;
	File fileMosel;
	String filePath;
	private ArrayList<Symbol> initialSymbolList;
	
	
	public ArrayList<Symbol> modelImport(File file) throws
		 XPRMLicenseError, IOException {
		
		XPRM mosel;
		ArrayList<Symbol> symbolList = new ArrayList<Symbol>();
		mosel = new XPRM();

		String path = file.getAbsolutePath();
		this.filePath = path;
		this.fileMosel = file;
		
		XPRM.unbindAll();
		
		MyOut cbmsg = new MyOut();
		
		XPRM.bind("mycb", cbmsg);    // Associate Java object with a name in Mosel
        // Set default output stream to cbmsg
		//mosel.setDefaultStream(XPRM.F_ERROR|XPRM.F_LINBUF, "java:mycb");
		
		mosel.setDefaultStream(XPRM.F_ERROR, "java:mycb");
		
		try {
			mosel.compile(path);
			
		} catch (XPRMCompileException e1) {
			System.out.println("cbmsg.errorStr="+cbmsg.errorStr);

			String error = "<html>"+cbmsg.errorStr+"</html";
			
			CustomErrorDialog.showDialog("<html>XPRMCompileException occured</html>",
					error);
			
			XPRM.unbindAll();
			resetMosel();
			mosel.finalize();
			mosel=null;
			
			return null;
			
		}
		
		//First parsing 	
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(fileMosel));
			String line;
			ArrayList<Symbol> totalSymbFound = new ArrayList<>();
			
			while ((line = br.readLine()) != null) {
				
				if (line.contains("parameters")) {
					System.out.println("START OF PARAMETERS BLOCk!!+"+line);
					
					while (!(line = br.readLine()).contains("end-parameters")) {						
						
						line = line.trim();
						
						if(line.isEmpty() || line.equals("") || line.equals("\n"))
							continue;
						
						//skipping comment line
						if(line.charAt(0)=='!'){
							System.out.println("Skipping comment line="+line);
							continue;
						
						}
						System.out.println("current line:"+line);
						
						//removing comments part from the line beforehand
						line = line.split("!")[0].trim();
						
						// for symbols defined as: symName = myvalue i.e. with "="
						Symbol symConstant = tokenizeSymbols(line);
						symbolList.add(symConstant);
						
					}
					
					System.out.println("END OF PARAMETERS BLOCk!!");
					
				}else if(line.contains("declarations")){
					System.out.println("START OF DECLARATIONS BLOCk!!+"+line);
					
					while (!(line = br.readLine()).contains("end-declarations")) {
												
						line = line.trim();
						
						if(line.isEmpty() || line.equals("") || line.equals("\n"))
							continue;
						
						//skipping comment line
						if(line.charAt(0)=='!'){
							System.out.println("Skipping comment line="+line);
							continue;
						
						}
						
						System.out.println("current line:"+line);
						
						//remove comments before hand only
						line = line.split("!")[0].trim();
						
						if(line.contains("=")){
							System.out.println("Symbol Constant case:");
							// for symbols defined as: symName = myvalue i.e. with "="
							Symbol symConstant = tokenizeSymbols(line);
							symbolList.add(symConstant);
						
						}else if(line.contains(":")){
							System.out.println("Symbol Variable case:");
							//now logic for variables with ':'
							String linetokensForVars[]= line.split(":");
						
							String name = linetokensForVars[0].trim();
							
							Symbol symVariable = new Symbol<>();
						
							symVariable.name = name;
							
							String symValString = linetokensForVars[1].trim();
							
							//remove comments if any after the symbolval
							symValString = symValString.split("!")[0].trim();
							
							symVariable.typeString = symValString;
							symVariable.set(""); //set value to empty string
							
							symbolList.add(symVariable);
							System.out.println("Symbol added(variable), Name:"+symVariable.name+", type="+symVariable.typeString+", val="+symVariable.get());
						
						}
						
					}
					
					System.out.println("END OF DECLARATIONS BLOCk!!");
					
				}else if(line.contains("initializations from")){
										
					String externalSourceFileName = line.split("from")[1].trim();
					
					if(externalSourceFileName.charAt(0)!=34 || externalSourceFileName.charAt(0)!=39){
						//eg: init from input_link, here input_link="blahblah.txt"
						Symbol extSymbol = new Symbol<>();
						extSymbol.name = externalSourceFileName;
						
						int index = symbolList.indexOf(extSymbol);
						if(index!=-1){
							
							externalSourceFileName = symbolList.get(index).get().toString();
							
						}
					
					}
					
					externalSourceFileName = externalSourceFileName.replace("'", "");
					externalSourceFileName = externalSourceFileName.replace("\"", "");
					
					System.out.println("init block: externalsource="+externalSourceFileName);
					
					while (!(line = br.readLine()).contains("end-initializations")) {
						
						line = line.trim();
								
						if(line.isEmpty() || line.equals("") || line.equals("\n"))
							continue;
						
						
						//skipping comment line
						if(line.trim().charAt(0)=='!'){
							System.out.println("Skipping comment line="+line.trim());
							continue;
						
						}
						if(externalSourceFileName.contains(".xls")||externalSourceFileName.contains(".xlsx")){
							String varLine[] = line.split("as");
							String name = varLine[0].trim();
							Symbol s = new Symbol<>();
							s.name = name;
							
							int index = symbolList.indexOf(s);
							if(index!=-1){
								
								Symbol sym = symbolList.get(index);
								sym.externalFile =  externalSourceFileName;
								
								String excelFR = varLine[1].trim();
								excelFR = excelFR.replace("'", "");
								excelFR = excelFR.replace("\"", "");
								
								sym.excelFileRange = excelFR;

								
							}
							
							
						}else if(line.contains("as")){
								String name = line.split("as")[0].trim();
								Symbol s = new Symbol<>();
								s.name = name;
								
								int index = symbolList.indexOf(s);
								if(index!=-1){
									
									Symbol sym = symbolList.get(index);
									sym.externalFile =  externalSourceFileName;
									
								}

								
						}else{
							
							//tokenizing from space is for case, where all vars are written in same line eg: var1 var2
							StringTokenizer linetokens = new StringTokenizer(line, " ");
								
							while(linetokens.hasMoreTokens()){
								String token = linetokens.nextToken().trim();
	
								Symbol s = new Symbol<>();
								s.name = token;
								
								int index = symbolList.indexOf(s);
								if(index!=-1){
									
									Symbol sym = symbolList.get(index);
									sym.externalFile =  externalSourceFileName;
									
								}

								System.out.println("Symbol added from 'init block':"+token);
									
							}
							
						}
						
						
					}
						
				}
				
				
			}
		}catch(Exception e){
			// return;
			System.err.println("Exception in parsing File with e=" + e);
			CustomErrorDialog.showDialog("<html>Exception in parsing File</html>",
					e.toString());
			e.printStackTrace();
			
			XPRM.unbindAll();
			resetMosel();
			mosel.finalize();
			mosel=null;
			
			return null;
			
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				//
				e.printStackTrace();
			}
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				//
				e.printStackTrace();
			}	
		}
		

		System.out.println("InitialSymbolList::");
		for (int i = 0; i < symbolList.size(); i++) {
			Symbol s = (Symbol) symbolList.get(i);
			System.out.println(s.name + ": " + s.typeString + ": " + s.get());
		}
		
		this.initialSymbolList = symbolList;
					
		XPRM.unbindAll();
		mosel.finalize();
		mosel=null;
		
		return symbolList;

	}

	private Symbol tokenizeSymbols(String line) {
		
		line = line.split("!")[0].trim();
		StringTokenizer linetokens= new StringTokenizer(line, "=");

		Symbol s = new Symbol<>();
		
		System.out.println("Line being checked by tokenizer:"+line);
		
		while (linetokens.hasMoreTokens()) {
	
			String symName = linetokens.nextToken().trim();
			
			s.name = symName;
			
			System.out.println("name="+s.name);
			String symValString = linetokens.nextToken().trim();
			System.out.println("valstr1="+symValString);
//			symValString = symValString.split("!")[0].trim();
//			System.out.println("valstr2="+symValString);

			//Check for String cases, ""=34 and ''=39
			if(symValString.charAt(0)==34 || symValString.charAt(0)==39){
				
				s.typeString = "String";
				s.set(symValString);
				
			}else if(symValString.equals("true")||symValString.equals("false")){
				
				s.typeString = "Boolean";
				s.set(Boolean.parseBoolean(symValString));
				
			}else if( symValString.charAt(0)=='{'){
				
				s.typeString = "Set";
				s.set(symValString);
				
			}else if(symValString.contains(".")){
				
				if(symValString.contains("..")){
					s.typeString = "Set";
					s.set(symValString);
				}else{
					s.typeString = "Real";
					s.set(Double.parseDouble(symValString));
				}

			}else if(symValString.charAt(0)=='['){
				
				s.typeString = "Array/List";
				s.set(symValString);
				
			}else{
				try{
					int z = Integer.parseInt(symValString);
					s.set(z);
					s.typeString = "Integer";
				}catch(Exception e){
					s.set((String)symValString);
					s.typeString = "String";
				}
			}
			
		}
		
		System.out.println("Symbol added, Name:"+s.name+", type="+s.typeString+", val="+s.get());

		return s;
		
	}

	public HashMap<String,OutputParams> changeModel(ArrayList<Symbol> listOfChangedVars, 
												ArrayList<Symbol> outputVars, long iterationNum)
			throws XPRMLicenseError, IOException {

		String replaceName = fileMosel.getName();

		System.out.println("replacename=" + replaceName);
		String tmpFileName = filePath.replace(replaceName, "temp_"
				+ replaceName);
		System.out.println("tmpFileNAme=" + tmpFileName);

		File newFile = new File(tmpFileName);

		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(fileMosel));
			bw = new BufferedWriter(new FileWriter(newFile));
			String line;
			ArrayList<Symbol> totalSymbFound = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				
				if (line.contains("parameters")) {
					bw.write(line + "\n");
					
					//Parameters parsing
					while (!(line = br.readLine()).contains("end-parameters")) {
						
						//check parameters
						Symbol symbolParameter = checkForConstantOccurence(line,listOfChangedVars);
						
						if (symbolParameter != null){
							System.out.println("Found a Parameter:"+symbolParameter.name+":"+symbolParameter.get().toString());
							line = "  "+symbolParameter.name +"="+symbolParameter.get().toString();
							
						}
						//now writing the line for Parameter
						bw.write(line + "\n");
						
					}
					
					bw.write(line + "\n"); //end-parameters
					

				} else if (line.contains("declarations")) {
					bw.write(line + "\n"); //declarations
					
					ArrayList<Symbol> symbolVarList = new ArrayList<>();
					while (!(line = br.readLine()).contains("end-declarations")) {
						
						//check for Constant first
						Symbol symbolConstant = checkForConstantOccurence(line,listOfChangedVars);
						
						if (symbolConstant != null){
							System.out.println("Found a Constant:"+symbolConstant.name+":"+symbolConstant.get().toString());
							line = "  "+symbolConstant.name +"="+symbolConstant.get().toString();
							
						}else{
							//check for Symbol variables
							Symbol symbolVar = checkForSymbolOccurence(line,listOfChangedVars);
						
							if (symbolVar != null)
								symbolVarList.add(symbolVar);
							
						}
						//now writing the line for symbol/constant
						bw.write(line + "\n");
						
					}
					
					bw.write(line + "\n"); //end-declarations
					
					//putting them all in one list for further parsing
					totalSymbFound.addAll(symbolVarList);
					
					//Grouping symbols having same external source
					Map<String, List<Symbol>> groupedSymbols = new HashMap<String, List<Symbol>>();

					//Doing grouping logic now, using hashmap with symbolvalue as key
					int len = symbolVarList.size();
					for (int i = 0; i < len; i++) {
						Symbol symbolVar = symbolVarList.get(i);
						String symbolValue = symbolVar.get().toString();
						System.out.println("symbolval="+symbolValue);
						
						symbolValue = symbolValue.split("::")[0];
												
						putObjectsForGrouping(groupedSymbols, symbolValue, symbolVar);
						
					}

					System.out.println("START groupedSymbols List---");
					
				    for (Map.Entry<String, List<Symbol>> entry : groupedSymbols.entrySet()) {
				        String key = entry.getKey();
				        List<Symbol> listSymbols = entry.getValue();
				        System.out.println("Key = "+key);
				        //System.out.print(key+":"+"[");
				        
				        if(key.contains(".dat") || key.contains(".txt")){
				        	
				        	//DAT case, Value should have something like "Models/trans.dat"
				        	String strForDat="";
				        	strForDat+="initializations from '"+key+"'\n";
							for (int i = 0; i < listSymbols.size(); i++) {
								Symbol datSymbol = listSymbols.get(i);
								strForDat+=datSymbol.name+"\n";
								
								System.out.print(datSymbol.name+",");
								
								//Important to remove external source symbols from symbolVarList
								symbolVarList.remove(datSymbol);
							}
							
							strForDat+="end-initializations"+"\n";
							
							bw.write(strForDat+"\n");

				        	
				        }else if(key.contains(".xls")||key.contains(".xlsx")){
				        	
				        	//EXCEL case
							//TODO: add "uses mmodbc" if not present
				        	
				        	String strForExcel="";
				        	//VALUE FORMAT: NameOfExcel::[Input$B43:C63]
							String typeStringSplit[] = key.split("::");
							
							
							
							// "mmodbc.excel:MALBP.xlsx"
//							strForExcel+="initializations from 'mmodbc.excel:"+typeStringSplit[0]+"'\n";
							
							if(typeStringSplit[0].contains("mmodbc.excel"))
								strForExcel+="initializations from '"+typeStringSplit[0]+"'\n";
							else
								strForExcel+="initializations from 'mmodbc.excel:"+typeStringSplit[0]+"'\n";


							for (int i = 0; i < listSymbols.size(); i++) {
								Symbol excelSymbol = listSymbols.get(i);
								System.out.println("excel="+excelSymbol.name+",");
								System.out.println("excelFileRange="+excelSymbol.excelFileRange+",");
								
								
								//TODO: check for set/list?
								if(excelSymbol.typeString.contains("array"))
									strForExcel+=excelSymbol.name+" as 'noindex;"+excelSymbol.excelFileRange+"'\n";
								else
									strForExcel+=excelSymbol.name+" as '"+excelSymbol.excelFileRange+"'\n";
							
								
								symbolVarList.remove(excelSymbol);
							}
							
							strForExcel+="end-initializations"+"\n";
							System.out.println("strForExcel being written="+strForExcel);
							bw.write(strForExcel+"\n");

				        }else if(key.contains(".csv")){
				        	//CSV case
							//TODO : uses “mmetc"
				        	
				        	String strForCSV="";
				        	String nameOfCSV = key;
							
							strForCSV+="initializations from 'mmetc.diskdata:'\n";
							for (int i = 0; i < listSymbols.size(); i++) {
								Symbol csvSymbol = listSymbols.get(i);
								strForCSV+=csvSymbol.name+" as '"+nameOfCSV+"'\n";
								
								System.out.print(csvSymbol.name+",");
								symbolVarList.remove(csvSymbol);
							}
							
							strForCSV+="end-initializations"+"\n";
							bw.write(strForCSV+"\n");
				        }
				        
				        System.out.println("]");
				    }
				    System.out.println("END groupedSymbols List---");
						
					//Now, writing inits for all symbol collected till this declaration block
					String strForVars="";
					
					System.out.println("Now list of symbols left for normal initialization---");
					for (int i = 0; i < symbolVarList.size(); i++) {
						Symbol symbolVar = symbolVarList.get(i);
						System.out.println(symbolVar.name+":"+symbolVar.typeString);
						//Array init is different from Set/List, also list can go both ways
						//KK:=["1","2"] !list := || :: 
						//JJ:={"a","b"} !set
						//EE:: [11, 12, 13, 21, 22, 23, 11, 11] !array
						if(symbolVar.typeString==null)
							throw new Exception("TypeString of Symbol found null");
													
						if(symbolVar.typeString.contains("array"))
							strForVars+= symbolVar.name +"::"+symbolVar.get().toString()+"\n";
						else
							strForVars+= symbolVar.name +":="+symbolVar.get().toString()+"\n";
						
					}
					
					//Now adding strings for all variables token we want to change value of
					bw.write(strForVars + "\n");
					
					
				}else if(line.contains("initializations from")){
					System.out.println("Total Symb found:");
					for (int i = 0; i < totalSymbFound.size(); i++) {
						Symbol symbolVar = totalSymbFound.get(i);
						System.out.println(symbolVar.name);
						//strForVars+= "  "+symbolVar.name +":="+symbolVar.get().toString();
					}
					
					//NOTE: In case of ":jraw" and "trans.dat", it should be:
					// this1 as that1 OR VALUE
					// this2 as that2 OR WEIGHT
					// i.e. they should be separated by line and not written in same line
					
					String allStr = line+"\n"; //initializations from
					
					String strInsideInit="";
					while (!(line = br.readLine()).contains("end-initializations")) {
						//bw.write(line + "\n");
						StringTokenizer linetokens = new StringTokenizer(line, " ");
						
						line = line.trim();
						
						if(line.isEmpty() || line.equals("") || line.equals("\n"))
							continue;
						
						//skipping comment line
						if(line.trim().charAt(0)=='!'){
							System.out.println("Skipping comment line="+line.trim());
							continue;
						
						}
						
						//linetokens in case the params are all in same line, space separated
						while(linetokens.hasMoreTokens()){
							String token = linetokens.nextToken().trim();
							//TODO: No need to create symbol for checking equality here
							Symbol s = new Symbol<>();
							s.name = token;
							System.out.println("Symbol in consideration:"+token);
							if(totalSymbFound.contains(s)){
								line = line.replace(token, "");
								System.out.println("Symbol Conflict inside Init="+token);
								
								if(line.contains("as"))
									line = "";
								
							}
							
						}
						strInsideInit+=line+"\n";
						
						
					}
					strInsideInit = strInsideInit.trim();
					System.out.println("strInsideInit--"+strInsideInit.trim()+"--END");
					
					if(!strInsideInit.isEmpty()){
						allStr+=strInsideInit + "\n";
					
						allStr+=line+"\n"; //end-initializations
						System.out.println("(START)After token parsing inside init part");
						System.out.println(allStr);
						System.out.println("(END)After token parsing inside init part");
						bw.write(allStr);
					}
					
						
				}else if( (line.contains(":=")) || (line.contains("::")) ){
					
					String assignmentLineForVar1[] = line.split(":="); 
					String assignmentLineForVar2[] = line.split("::"); //arrays case
					
					
					//CASE: ":=" others
					if(assignmentLineForVar1.length>1){
						Symbol sym = new Symbol<>();
						sym.name = assignmentLineForVar1[0].trim();
							
						//skipping line where a symbol from our list is assigned
						if(!totalSymbFound.contains(sym)){
							bw.write(line + "\n");
						}
					}
					//CASE: "::" arrays
					if(assignmentLineForVar2.length>1){
						Symbol sym = new Symbol<>();
						sym.name = assignmentLineForVar2[0].trim();
							
						//skipping line where a symbol from our list is assigned
						if(!totalSymbFound.contains(sym)){
							bw.write(line + "\n");
						}
					}
					
				}else {
					bw.write(line + "\n");
				}

			}
		} catch (Exception e) {
			// return;
			System.err.println("Exception in parsing File with e=" + e);
			CustomErrorDialog.showDialog("<html>Exception in parsing File</html>",
					e.toString());
			
			e.printStackTrace();
			
			return null;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				//
				e.printStackTrace();
			}
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				//
				e.printStackTrace();
			}
		}
		
		
		
		// Once everything is complete, delete old file..
		// File oldFile = new File(oldFileName);
		// oldFile.delete();

		// And rename tmp file's name to old file name

		// newFile.renameTo(oldFile);
		
		XPRM mosel;
		mosel = new XPRM();

		XPRM.unbindAll();
		
		MyOut cbmsg = new MyOut();
		//mosel.setDefaultStream(XPRM.F_ERROR, "java:mycb");
		cbmsg = new MyOut();
		
		mosel.bind("mycb", cbmsg);    // Associate Java object with a name in Mosel
        // Set default output stream to cbmsg
		//mosel.setDefaultStream(XPRM.F_ERROR|XPRM.F_LINBUF, "java:mycb");
		mosel.setDefaultStream(XPRM.F_ERROR, "java:mycb");
		
		String newfilePath = newFile.getAbsolutePath();

		System.out.println("newfilePath=" + newfilePath);
		
		try {
			mosel.compile(newfilePath);
			
		} catch (XPRMCompileException e1) {
			
			String error = "<html>"+cbmsg.errorStr+"</html";
			System.out.println("cbmsg.errorStr="+error);
			CustomErrorDialog.showDialog("<html>XPRMCompileException occured</html>",
					error);
						
			
			XPRM.unbindAll();
			resetMosel();
		  	mosel.finalize();                   // Finalize Mosel
		  	mosel = null;
			
			return null;
			
		}
		
		// Load a BIM file
		mod = mosel.loadModel(newfilePath.replace(".mos", ".bim"));

		mod.run();
		
		int result = mod.getResult();
		int execStatus =mod.getExecStatus();
		int probStatus = mod.getProblemStatus();
		
		
		if(probStatus!=XPRMModel.PB_OPTIMAL){
			System.err.println("Cannot find optimal result. Problem Status:"+probStatus);
			CustomErrorDialog.showDialog("<html>"+"Cannot find optimal result.</html>",
					 "Problem Status:"+probStatus);
		}
		
		//Clearing the old list
		listOfChangedVars.clear();
		
		HashMap<String, OutputParams> resultList = new HashMap<>();
							
		//adding objective value to the symbol list
		OutputParams objOutput = new OutputParams(mod.getObjectiveValue(), "String");
		resultList.put("Objective Value", objOutput);
		
		OutputParams iterationNumber = new OutputParams(iterationNum, "long");
		resultList.put("Iteration Number", iterationNumber);
		//System.out.println("SymbolsNow:"); 
		// List of symbols
		for(Symbol sym: outputVars){
			
			String name = sym.name;
			XPRMIdentifier id=null;
			try{
				id =  mod.findIdentifier(name);
			}catch(NoSuchElementException e){
				e.printStackTrace();
				System.out.println("Exception occured while trying to find indentifier for symbol"+name);
				CustomErrorDialog.showDialog("<html>"+"Exception occured while trying to find indentifier for symbol : "+name+"</html>",
						e.getLocalizedMessage());
				return null;
			}
			String str = dispsymb(id, false);
			String arr[] = str.split(":");
			
			//arr[0]=NAME  && arr[1]=typeString
			
			String type = arr[1];//id.getTypeName();
			String structName = id.getStructName();
			
			//System.out.println("Name:"+name+", type:"+type+", val:"+id.toString());

			
			String val = id.toString();
			
			System.out.println("Name:"+name
								+", val:"+val
								+", type:"+type);

			
			//for excel output
			OutputParams idVar = new OutputParams(val, type);
			resultList.put(name, idVar);
			
		}
		
//		for (XPRMIdentifiers ids = mod.identifiers(); ids.hasNext();) {
//
//			XPRMIdentifier id = (XPRMIdentifier) ids.next();
//			
//			if (id.getStructCode() != XPRMTyped.STR_PROC) {
//				
//				String str = dispsymb(id, false);
//				String arr[] = str.split(":");
//				//System.out.println("Name:"+arr[0]+", type:"+arr[1]);
//				//arr[0]=NAME  && arr[1]=typeString
//				
//				String name = id.getName();
//				
//				String type = arr[1];//id.getTypeName();
//				String structName = id.getStructName();
//				
//				String val = id.toString();
//				
//				System.out.println("Name:"+name
//									+", val:"+val
//									+", type:"+type);
//
//				
//				//for excel output
//				OutputParams idVar = new OutputParams(val, type);
//				resultList.put(name, idVar);
//				
//			}
//	
//		}
	
		//iteration result saved to list in ModelSaver but not yet added into excel 
		ModelSaver.saveIterationResult(resultList);
			
		XPRM.unbindAll();
		resetMosel();
		mosel.finalize();
		mosel=null;
		
		return resultList;

	}
	
	
	public void resetMosel(){
		if(mod!=null){
			mod.finalize();	// Finalize a model
			mod = null;
		}
	}
	
	public class MyOut extends OutputStream
	 {
		
		String errorStr = "";
		
	  public void flush()
	  { 
		  System.out.flush(); 
		  
	  }
	  public void write(byte[] b)
	  { 
		  String str = new String(b, StandardCharsets.UTF_8);
		 
		 errorStr+=str+"<br>";
		 
		 //<html>Error Message Description.<br> You can use html here</html>
		 
	//	   System.out.print("Mosel: "); 
	//	   System.out.write(b,0,b.length);
		 
	  }
	  // These methods are not used by Mosel:
	  public void write(byte[] b,int off,int len) {}
	  public void write(int b) {}
	  public void close() {
		 this.close();
		 
	  }
	 }
	
	Symbol checkForSymbolOccurence(String line, ArrayList<Symbol> symbolList) {
		int i = 1;
		String splitStr[] = line.split(":");
		
		//found no occurence of symbol variable
		if(splitStr.length<=1)
			return null;
		
		String symbol = splitStr[0].trim();
		
		
//		System.out.println("symbol name being checked="+symbol);
		for (Symbol sym : symbolList) {
			if (symbol.equals(sym.name)) {
				sym.indexInList = i;
				return sym;
			}
			i++;
		}
		return null;
	}

	Symbol checkForConstantOccurence(String line, ArrayList<Symbol> symbolList) {
		int i = 1;
		
		String splitLine[] = line.split("=");
		
		//found no occurence of constant
		if(splitLine.length<=1)
			return null;
		
		String symbol = splitLine[0].trim();		
		
//		System.out.println("symbol name being checked="+symbol);
		for (Symbol sym : symbolList) {
			if (symbol.equals(sym.name)) {
				sym.indexInList = i;
				return sym;
			}
			i++;
		}
		return null;
	}
	
	//Grouping logic for External source initialization symbols
	private void putObjectsForGrouping (Map<String, List<Symbol>> outdoorElements, String key, Symbol value) {
		List<Symbol> myClassList = outdoorElements.get(key);
	    if(myClassList == null) {
	        myClassList = new ArrayList<Symbol>();
	        outdoorElements.put(key, myClassList);
	    }
	    myClassList.add(value);
	    
	}
	
	/**************************************/
	/* Display information about a symbol */
	/**************************************/
	String dispsymb(XPRMIdentifier symb, boolean isRequirement) {
		XPRMProcedure proc;
		String typeWithName = "";

		Symbol<Object> s = new Symbol<>();

		switch (symb.getStructCode()) {
		case XPRMTyped.STR_CONST:
			// System.out.println(" " + symb.getName() + "= " + symb);
			typeWithName += symb.getName() + ": constant " + symb.getTypeName();
			// typeWithName+=dispfulltype(symb, "");
			// System.out.println("const:"+symb.getTypeName());
			// System.out.println("here="+symb.getTypeCode());
			break;
		case XPRMTyped.STR_PROC:
			proc = (XPRMProcedure) symb;
			if (isRequirement)
				dispprocfct(proc); // Display the prototype
			else
				do // Look for all overloading proc/func
				{
					dispprocfct(proc); // Display the prototype
				} while ((proc = proc.next()) != null);
			break;
		case XPRMTyped.STR_UTYP: {
			XPRMTyped expn;
			XPRMIdentifier alias;

			// System.out.print(" " + symb.getName() + "= ");
			typeWithName += symb.getName() + ": ";
			expn = ((XPRMUserType) symb).expand();
			if (expn == symb) {
				alias = (XPRMIdentifier) (mod.expandType(symb.getTypeCode()));
				if (alias.getName().equals(symb.getName())) {
					if (((XPRMUserType) symb).isProblem())
						dispproblem((XPRMUserType) symb);
					else
						disprecord((XPRMUserType) symb);
				} else
					System.out.println(alias.getName());
			} else {
				typeWithName += dispfulltype(expn, "");
				// System.out.println();
			}
			break;
		}
		case XPRMTyped.STR_NTYP:
			break; // ignore native types
		default:
			 //System.out.println(" here=" + symb.getName() + ": ");
			typeWithName += symb.getName() + ": ";
			// s.name = symb.getName();

			typeWithName += dispfulltype(symb, "");
			// System.out.println();
		}

		return typeWithName;
	}

	/*****************************************/
	/* Decode and display a type information */
	/*****************************************/
	String dispfulltype(XPRMTyped type, String typeStr) {
		XPRMTyped expn;
		switch (type.getStructCode()) {
		case XPRMTyped.STR_CONST:
			typeStr += type.getTypeName();
			break;
		case XPRMTyped.STR_REF:
			if (type.getTypeCode() <= XPRMTyped.TYP_LINCTR) {
				// System.out.print(type.getTypeName());
				typeStr += type.getTypeName();
			} else {
				typeStr = dispfulltype(mod.expandType(type.getTypeCode()),
						typeStr);
			}

			break;
		case XPRMTyped.STR_ARRAY:
			// System.out.print("array (" + ((XPRMArray)type).getSignature() +
			// ") of ");
			typeStr += "array(" + ((XPRMArray) type).getSignature() + ") of ";
			XPRMArray arr = (XPRMArray) type;
			//System.out.println("dimension of array="+typeStr+". is="+arr.getDimension());
			// System.out.println("here="+arr.get);
			
			typeStr = dispfulltype(mod.expandType(type.getTypeCode()), typeStr);

			break;
		case XPRMTyped.STR_SET:
			// System.out.print("set of ");
			typeStr += "set of ";		
			typeStr = dispfulltype(mod.expandType(type.getTypeCode()), typeStr);
			break;
		case XPRMTyped.STR_LIST:
			// System.out.print("list of ");
			//System.out.println("mod="+mod+", typestr="+typeStr);
			typeStr += "list of ";
			typeStr = dispfulltype(mod.expandType(type.getTypeCode()), typeStr);

			break;
		case XPRMTyped.STR_UTYP:
			expn = ((XPRMUserType) type).expand();
			if (expn != type)
				typeStr = dispfulltype(expn, typeStr);
			else {
				// System.out.print(type.getTypeName());
				typeStr += type.getTypeName();
			}
			break;
		default:
			typeStr += type.getTypeName();
			// System.out.print(type.getTypeName());

		}
		return typeStr;

	}

	/****************************************************/
	/* Diplay all the published fields of a record type */
	/****************************************************/
	void disprecord(XPRMUserType rec) {
		XPRMRecordFields fields;

		fields = rec.fields();
		if (fields.hasNext()) {
			System.out.println("record publishing:");
			for (; fields.hasNext();) {
				System.out.print("     ");
				dispsymb((XPRMIdentifier) fields.next(), false);
			}
		} else
			System.out.println("record (no public field)");
	}

	/*******************************************************/
	/* Diplay all the problem components of a problem type */
	/*******************************************************/
	static void dispproblem(XPRMUserType prob) {
		XPRMProblemComps comps;

		comps = prob.components();
		if (comps.hasNext()) {
			for (; comps.hasNext();)
				System.out.print(((XPRMNativeType) comps.next()).getTypeName()
						+ " ");
			// System.out.println();
		} else
			System.out.println("problem");
	}

	/***************************************/
	/* Diplay a prototype from a signature */
	/***************************************/
	void dispprocfct(XPRMProcedure proc) {
		char[] parms;
		int i;

		if (proc.getTypeCode() != XPRMTyped.TYP_NOT)
			System.out.print("function " + proc.getName());
		else
			System.out.print(" procedure " + proc.getName());

		if (proc.getNbParameters() > 0) {
			System.out.print("(");
			parms = proc.getParameterTypes().toCharArray();
			i = 0;
			while (i < parms.length) {
				if (i > 0)
					System.out.print(",");
				i = disptyp(i, parms) + 1;
			}
			System.out.print(")");
		}

		if (proc.getTypeCode() != proc.TYP_NOT) {
			System.out.print(":");
			dispfulltype(mod.expandType(proc.getTypeCode()), "");
		}
		// System.out.println();
	}

	/****************************************/
	/* Display a type name from a signature */
	/****************************************/
	int disptyp(int i, char[] parms) {
		int j;

		switch (parms[i]) {
		case 'i':
			System.out.print("integer");
			break;
		case 'r':
			System.out.print("real");
			break;
		case 's':
			System.out.print("string");
			break;
		case 'b':
			System.out.print("boolean");
			break;
		case 'v':
			System.out.print("mpvar");
			break;
		case 'c':
			System.out.print("linctr");
			break;
		case 'I':
			System.out.print("range");
			break;
		case 'a':
			System.out.print("array");
			break;
		case 'e':
			System.out.print("set");
			break;
		case 'l':
			System.out.print("list");
			break;
		case '%': {
			String nstr = new String(parms, i + 1, 3);

			i += 3;
			dispfulltype(mod.expandType(Integer.parseInt(nstr, 16)), "");
			break;
		}
		case '|':
			i++;
			do {
				System.out.print(parms[i++]);
			} while (parms[i] != '|');
			break;
		case '!':
			i++;
			do {
				System.out.print(parms[i++]);
			} while (parms[i] != '!');
			break;
		case 'A':
			System.out.print("Harray (");
			j = ++i;
			while (parms[i] != '.') {
				if (j != i)
					System.out.print(",");
				i = disptyp(i, parms) + 1;
			}
			System.out.print(") of ");
			i = disptyp(++i, parms);
			break;
		case 'E':
			System.out.print("set of ");
			i = disptyp(++i, parms);
			break;
		case 'L':
			System.out.print("list of ");
			i = disptyp(++i, parms);
			break;
		case '*':
			System.out.print("...");
			break;
		default:
			System.out.print("?");
		}
		return i;
	}

}