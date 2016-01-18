package pcgui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.OutputParams;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.charts.*;
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;

public class ModelSaver {
		
	static ArrayList<HashMap<String, OutputParams>> modelResult;
	static String excelFileName;

	public static void saveInputModel(ArrayList<ArrayList<Object>> model, String modelName){
		
		modelResult = new ArrayList<HashMap<String, OutputParams>>();
		
		excelFileName = modelName;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Input");
		 				
		ArrayList<Object> header = new ArrayList<>();
		header.add("ParamName");
		header.add("ParamDataType");
		header.add("ParamType");
		header.add("ParamValue");
		header.add("ImportFromFile");
		header.add("ImportFromRange");
		header.add("RandomizationFunction");
		header.add("OutputTracking");
		model.add(0, header);
		
		
		XSSFFont font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		
		int rownum = 0;
		
		for(ArrayList<Object> rowData : model){
		    Row row = sheet.createRow(rownum++);
		    int cellnum = 0;
		    		    
		    for (Object obj : rowData) {
		        Cell cell = row.createCell(cellnum++);
		        		        
		        //TODO put cases for DataTypes, currently all are treated Strings
		        cell.setCellValue((String)obj.toString());
		        
		        if(cellnum==0){
		        	style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		        	cell.setCellStyle(style);
		        }
		        
		    }
		    
		}
		 
		try {
		    FileOutputStream out = 
		            new FileOutputStream(new File(modelName));
		    workbook.write(out);
		    out.close();
		    workbook.close();//closing workbook
		    System.out.println("Excel written successfully..");
		    
		    JOptionPane.showMessageDialog(new JFrame("Success"), 
					"Success: Input Model saved. You can now run the model");
		     
		} catch(Exception exc){
			CustomErrorDialog.showDialog("Exception while saving Input Model to Excel", exc.toString());
			exc.printStackTrace();
		}
		
		
	}

	//store each iteration result
	public static void saveIterationResult(HashMap<String, OutputParams> resultList){
		
		modelResult.add(resultList);
	}
	
	//This is called upon completion of iterations from GUI
	public static void saveModelResult(){
		
		try {
		    FileInputStream file = new FileInputStream(new File(excelFileName));
		    
		    XSSFWorkbook workbook = new XSSFWorkbook(file);
		    
		    XSSFSheet sheet = workbook.getSheet("Output");
		    
		    if(sheet==null)
		    	sheet = workbook.createSheet("Output");

			XSSFFont font = workbook.createFont();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFont(font);
			
			//Add row for Header with names of all symbols
			Row headerRow = sheet.createRow(0);
			 
			int cellNumHeader = 0;
			HashMap<String, OutputParams> top = modelResult.get(0);
			
			System.out.println("modelResult size="+modelResult.size());
			
		    //keys for top element are taken as header rows
			Set<String> headerKeys = top.keySet();
			
			System.out.println("Row 1:");
			
			for (String key : headerKeys) {
				
				Cell cell = headerRow.createCell(cellNumHeader++);
				cell.setCellValue(key);
				
				//styling header row
				style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
				cell.setCellStyle(style);
				System.out.println(key+":"+top.get(key).value+":"+top.get(key).typeString);
				
			}
					
			System.out.println("HeaderKeys="+Arrays.toString(headerKeys.toArray()));
			System.out.println("Max word length in a Excel cell:"
									+SpreadsheetVersion.EXCEL2007.getMaxTextLength());
			
			int rownum = 1;		
			for (int a=0; a<modelResult.size();a++){
				Row row = sheet.createRow(rownum++);
			    int cellnum = 0;
			    
				for (String key : headerKeys) {
					
					Cell cell = row.createCell(cellnum++);
					
					OutputParams obj = modelResult.get(a).get(key);
					System.out.println("{MODELRESULT}Name:"+key
							+"Value:"+obj.value
							+"type:"+obj.typeString);
					
			        if(obj.typeString!=null){
			        	
			        	String val = obj.value.toString().trim();
			        	String type = obj.typeString.trim();
			        	
				        if(type.equals("integer") || 
				        		type.trim().equals("constant integer"))
				        	cell.setCellValue((int)Integer.parseInt(val));
				        
				        else if(type.equals("real")||
				        		type.equals("constant real"))
				        	cell.setCellValue((double)Double.parseDouble(val));
				        
				        else if (type.equals("boolean")||
				        		type.equals("constant boolean"))
				        	cell.setCellValue((boolean)Boolean.parseBoolean(val));
				        
				        else{
				        	if (val.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
							    System.out.println("max excel:"+SpreadsheetVersion.EXCEL2007.getMaxTextLength());
							    val = val.substring(0, 500);
							    val = val + "..." + val.substring(val.length()-10, val.length()-1);
							    
							    cell.setCellValue(val);
				        	}else{
				        		cell.setCellValue((String)obj.value.toString());
				        	}
				        	
				        }
			        }else{
			        	cell.setCellValue((String)obj.value.toString());
			        }
				}
			}
			
			FileOutputStream out = new FileOutputStream(new File(excelFileName));
		    
		    workbook.write(out);
		    out.close();
		    workbook.close();//closing workbook
		    file.close();
		    
		    System.out.println("Output to Excel written successfully..");		    
		    
		} catch (FileNotFoundException e) {
			CustomErrorDialog.showDialog("Error while writing to Excel", e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
			CustomErrorDialog.showDialog("Error while writing to Excel", e.toString());
			 e.printStackTrace();
		} catch(Exception e){
			CustomErrorDialog.showDialog("Exception while writing to Excel", e.toString());
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void drawLineChart(String Xname, String Yname)throws FileNotFoundException,
																IOException, Exception{
		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();
		
		String sheetName = "LineChart_";
		System.out.println("Xname="+Xname+", Yname="+Yname);
		
		if(Xname==null){
			
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Yname);
				Y.add(obj1.value.toString());
			
				X.add(String.valueOf(iter+1));
				
			}
			sheetName+= Yname+"-vs-"+"Iterations";
			Xname = "Iterations";
		}else{
		
			//get values of vars X and Y
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Xname);
				X.add(obj1.value.toString());
	
				OutputParams obj2 = obj.get(Yname);
				Y.add(obj2.value.toString());
				
			}
			sheetName+= Yname+"-vs-"+Xname;
		}
		
		System.out.println("Xsize="+X.size()+", Ysize="+Y.size());
		
		
		
			FileInputStream file = new FileInputStream(new File(excelFileName));
			
			/* Create a Workbook object that will hold the final chart */
			XSSFWorkbook my_workbook = new XSSFWorkbook(file);
						
			XSSFSheet my_worksheet = my_workbook.getSheet(sheetName);

			if(my_worksheet==null)
				my_worksheet = my_workbook.createSheet(sheetName);
						
			int xRowNum = 0; //start row
			
			//currently making every cell type as Double only
			
			XSSFRow X_row = my_worksheet.createRow(xRowNum);
			XSSFCell cellX = X_row.createCell((short) 0);
			cellX.setCellValue(Xname);
			
			XSSFCell cellY = X_row.createCell((short) 1);
			cellY.setCellValue(Yname);
			
			for (int rowIndex = 0; rowIndex < X.size(); rowIndex++) {
				
				XSSFRow rowX = my_worksheet.createRow(++xRowNum);
				
				XSSFCell cellXX = rowX.createCell((short) 0);
				cellXX.setCellValue(Double.parseDouble(X.get(rowIndex)));
				
				XSSFCell cellYY = rowX.createCell((short) 1);
				cellYY.setCellValue(Double.parseDouble(Y.get(rowIndex)));
				
			}
			
			
			/* At the end of this step, we have a worksheet with test data, that we want to write into a chart */
			/* Create a drawing canvas on the worksheet */
			XSSFDrawing xlsx_drawing = my_worksheet.createDrawingPatriarch();
			
			/* Define anchor points in the worksheet to position the chart */
			XSSFClientAnchor anchor = xlsx_drawing.createAnchor(0, 0, 0, 0, 5, 0, 15, 10);
			
			/* Create the chart object based on the anchor point */
			XSSFChart my_line_chart = xlsx_drawing.createChart(anchor);
			
			/* Define legends for the line chart and set the position of the legend */
			XSSFChartLegend legend = my_line_chart.getOrCreateLegend();
			legend.setPosition(LegendPosition.BOTTOM);
			
			/* Create data for the chart */
			LineChartData data = my_line_chart.getChartDataFactory().createLineChartData();
			
			/* Define chart AXIS */
			ChartAxis bottomAxis = my_line_chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
			ValueAxis leftAxis = my_line_chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
			leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

			/* Define Data sources for the chart */
			/* Set the right cell range that contain values for the chart */
			/* Pass the worksheet and cell range address as inputs */
			/* Cell Range Address is defined as First row, last row, first column, last column */
			ChartDataSource < Number > xs = DataSources.
								fromNumericCellRange(my_worksheet, new CellRangeAddress(1, xRowNum, 0, 0));
			
			ChartDataSource < Number > ys = DataSources.
								fromNumericCellRange(my_worksheet, new CellRangeAddress(1, xRowNum, 1, 1));
			
			/* Add chart data sources as data to the chart */
			data.addSeries(xs, ys);
			//data.addSeries(xs, ys2);

			/* Plot the chart with the inputs from data and chart axis */
			my_line_chart.plot(data, new ChartAxis[] {
					bottomAxis, leftAxis
			});
			
			/* Finally define FileOutputStream and write chart information */
			FileOutputStream fileOut = new FileOutputStream(excelFileName);
			my_workbook.write(fileOut);
			fileOut.close();
			my_workbook.close();
			file.close();

	}
	
	public static void drawScatterPlot(String Xname, String Yname) throws FileNotFoundException,
																IOException, Exception{

		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();
		
		String sheetName = "ScatterChart_";
		System.out.println("Xname="+Xname+", Yname="+Yname);
		
		if(Xname==null){
			
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Yname);
				Y.add(obj1.value.toString());
			
				X.add(String.valueOf(iter+1));
				
			}
			sheetName+= Yname+"-vs-"+"Iterations";
			Xname = "Iterations";
		}else{
		
			//get values of vars X and Y
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Xname);
				X.add(obj1.value.toString());
	
				OutputParams obj2 = obj.get(Yname);
				Y.add(obj2.value.toString());
				
			}
			sheetName+= Yname+"-vs-"+Xname;
		}
		
		System.out.println("Xsize="+X.size()+", Ysize="+Y.size());
		
		
		
			FileInputStream file = new FileInputStream(new File(excelFileName));
			
			/* Create a Workbook object that will hold the final chart */
			XSSFWorkbook my_workbook = new XSSFWorkbook(file);
						
			XSSFSheet my_worksheet = my_workbook.getSheet(sheetName);

			if(my_worksheet==null)
				my_worksheet = my_workbook.createSheet(sheetName);
						
			int xRowNum = 0; //start row
			
			//currently making every cell type as Double only
			
			XSSFRow X_row = my_worksheet.createRow(xRowNum);
			XSSFCell cellX = X_row.createCell((short) 0);
			cellX.setCellValue(Xname);
			
			XSSFCell cellY = X_row.createCell((short) 1);
			cellY.setCellValue(Yname);
			
			for (int rowIndex = 0; rowIndex < X.size(); rowIndex++) {
				
				XSSFRow rowX = my_worksheet.createRow(++xRowNum);
				
				XSSFCell cellXX = rowX.createCell((short) 0);
				cellXX.setCellValue(Double.parseDouble(X.get(rowIndex)));
				
				XSSFCell cellYY = rowX.createCell((short) 1);
				cellYY.setCellValue(Double.parseDouble(Y.get(rowIndex)));
				
			}
      
	        Drawing drawing = my_worksheet.createDrawingPatriarch();
	        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 0, 15, 10);
	
	        Chart chart = drawing.createChart(anchor);
	       	        
	        ChartLegend legend = chart.getOrCreateLegend();
	        legend.setPosition(LegendPosition.TOP_RIGHT);
	
	        ScatterChartData data = chart.getChartDataFactory().createScatterChartData();
	
	        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
	        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
	        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
	
	        ChartDataSource<Number> xs = DataSources.fromNumericCellRange(my_worksheet, new CellRangeAddress(1, xRowNum, 0, 0));
	        ChartDataSource<Number> ys = DataSources.fromNumericCellRange(my_worksheet, new CellRangeAddress(1, xRowNum, 1, 1));
	
	        data.addSerie(xs, ys);
	
	        chart.plot(data, bottomAxis, leftAxis);
	
			/* Finally define FileOutputStream and write chart information */
			FileOutputStream fileOut = new FileOutputStream(excelFileName);
			my_workbook.write(fileOut);
			fileOut.close();
			my_workbook.close();
			file.close();
			
	}

	public static void drawBarChart(String Xname, String Yname)throws FileNotFoundException,
													IOException, Exception{
        
		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();
		
		String sheetName = "BarChart_";
		System.out.println("Xname="+Xname+", Yname="+Yname);
		
		if(Xname==null){
			
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Yname);
				Y.add(obj1.value.toString());
				//Y.add(String.valueOf(2*iter+1));
			
				X.add(String.valueOf(iter+1));
				
			}
			sheetName+= Yname+"-vs-"+"Iterations";
			Xname = "Iterations";
		}else{
		
			//get values of vars X and Y
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Xname);
				X.add(obj1.value.toString());
				//X.add(String.valueOf(iter+1));
				
				OutputParams obj2 = obj.get(Yname);
				Y.add(obj2.value.toString());
				//Y.add(String.valueOf(2*iter+1));
				
			}
			sheetName+= Yname+"-vs-"+Xname;
		}
		
		System.out.println("Xsize="+X.size()+", Ysize="+Y.size());
		
		
		
			FileInputStream file = new FileInputStream(new File(excelFileName));
			
			/* Create a Workbook object that will hold the final chart */
			XSSFWorkbook my_workbook = new XSSFWorkbook(file);
						
			XSSFSheet my_worksheet = my_workbook.getSheet(sheetName);

			if(my_worksheet==null)
				my_worksheet = my_workbook.createSheet(sheetName);
						
			int xRowNum = 0; //start row
			
			//currently making every cell type as Double only
			
			XSSFRow X_row = my_worksheet.createRow(xRowNum);
			XSSFCell cellX = X_row.createCell(0);
			cellX.setCellValue(Xname);
			
			
			XSSFCell cellY = X_row.createCell(1);
			cellY.setCellValue(Yname);
			
			for (int rowIndex = 0; rowIndex < X.size(); rowIndex++) {
				
				XSSFRow rowX = my_worksheet.createRow(++xRowNum);
				
				XSSFCell cellXX = rowX.createCell( 0);
				cellXX.setCellValue(Double.parseDouble(X.get(rowIndex)));
				
				XSSFCell cellYY = rowX.createCell(1);
				cellYY.setCellValue(Double.parseDouble(Y.get(rowIndex)));
				
			}
			
			DefaultCategoryDataset my_bar_chart_dataset = new DefaultCategoryDataset();

			Iterator<Row> rowIterator = my_worksheet.iterator(); 
			/* Loop through worksheet data and populate bar chart dataset */
			String chart_label="";
			Number chart_data=0;         
			
			//skip header row
			rowIterator.next();
			
			while(rowIterator.hasNext()) {

				//Read Rows from Excel document
				Row row = rowIterator.next();  
				//Read cells in Rows and get chart data
				Iterator<Cell> cellIterator = row.cellIterator();
				
				Cell cell1 = cellIterator.next(); 
				chart_label = String.valueOf(cell1.getNumericCellValue());
				
				Cell cell2 = cellIterator.next(); 
				chart_data = cell2.getNumericCellValue();
					
				/* Add data to the data set */          
				/* We don't have grouping in the bar chart, so we put them in fixed group */            
				my_bar_chart_dataset.addValue(chart_data.doubleValue(),Yname,chart_label);
			
			}               
            
	        /* Create a logical chart object with the chart data collected */
	        JFreeChart BarChartObject=ChartFactory.createBarChart(sheetName, Xname, Yname, my_bar_chart_dataset,PlotOrientation.VERTICAL,true,true,false);  
	        /* Dimensions of the bar chart */               
	        int width=640; /* Width of the chart */
	        int height=480; /* Height of the chart */               
	        /* We don't want to create an intermediate file. So, we create a byte array output stream 
	        and byte array input stream
	        And we pass the chart data directly to input stream through this */             
	        /* Write chart as PNG to Output Stream */
	        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
	        ChartUtilities.writeChartAsPNG(chart_out,BarChartObject,width,height);
	        /* We can now read the byte data from output stream and stamp the chart to Excel worksheet */
	        int my_picture_id = my_workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
	        /* we close the output stream as we don't need this anymore */
	        chart_out.close();
	        /* Create the drawing container */
	        XSSFDrawing drawing = my_worksheet.createDrawingPatriarch();
	        /* Create an anchor point */
	        ClientAnchor my_anchor = new XSSFClientAnchor();
	        /* Define top left corner, and we can resize picture suitable from there */
	        my_anchor.setCol1(4);
	        my_anchor.setRow1(5);
	        /* Invoke createPicture and pass the anchor point and ID */
	        XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
	        /* Call resize method, which resizes the image */
	        my_picture.resize();
	        /* Close the FileInputStream */
	        file.close();               
	        /* Write changes to the workbook */
	        FileOutputStream out = new FileOutputStream(new File(excelFileName));
	        my_workbook.write(out);
	        out.close();            

	}
	
	
	public static void drawPieChart(String Xname, String Yname) throws FileNotFoundException,
													IOException, Exception{                
		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();
		
		String sheetName = "PieChart_";
		System.out.println("Xname="+Xname+", Yname="+Yname);
		
		if(Xname==null){
			
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Yname);
				Y.add(obj1.value.toString());
				//Y.add(String.valueOf(2*iter+1));
			
				X.add(String.valueOf(iter+1));
				
			}
			sheetName+= Yname+"-vs-"+"Iterations";
			Xname = "Iterations";
		}else{
		
			//get values of vars X and Y
			for(int iter=0;iter<modelResult.size();iter++){
				HashMap<String,OutputParams> obj = modelResult.get(iter);
				
				OutputParams obj1 = obj.get(Xname);
				X.add(obj1.value.toString());
				//X.add(String.valueOf(iter+1));
				
				OutputParams obj2 = obj.get(Yname);
				Y.add(obj2.value.toString());
				//Y.add(String.valueOf(2*iter+1));
				
			}
			sheetName+= Yname+"-vs-"+Xname;
		}
		
		System.out.println("Xsize="+X.size()+", Ysize="+Y.size());
		
		
		
			FileInputStream file = new FileInputStream(new File(excelFileName));
			
			/* Create a Workbook object that will hold the final chart */
			XSSFWorkbook my_workbook = new XSSFWorkbook(file);
						
			XSSFSheet my_worksheet = my_workbook.getSheet(sheetName);

			if(my_worksheet==null)
				my_worksheet = my_workbook.createSheet(sheetName);
						
			int xRowNum = 0; //start row
			
			//currently making every cell type as Double only
			
			XSSFRow X_row = my_worksheet.createRow(xRowNum);
			XSSFCell cellX = X_row.createCell(0);
			cellX.setCellValue(Xname);
			
			
			XSSFCell cellY = X_row.createCell(1);
			cellY.setCellValue(Yname);
			
			for (int rowIndex = 0; rowIndex < X.size(); rowIndex++) {
				
				XSSFRow rowX = my_worksheet.createRow(++xRowNum);
				
				XSSFCell cellXX = rowX.createCell( 0);
				cellXX.setCellValue(Double.parseDouble(X.get(rowIndex)));
				
				XSSFCell cellYY = rowX.createCell(1);
				cellYY.setCellValue(Double.parseDouble(Y.get(rowIndex)));
				
			}
        /* Create JFreeChart object that will hold the Pie Chart Data */
        DefaultPieDataset my_pie_chart_data = new DefaultPieDataset();
        
		Iterator<Row> rowIterator = my_worksheet.iterator(); 
		/* Loop through worksheet data and populate pie chart dataset */
		String chart_label="";
		Number chart_data=0;         
		
		//skip header row
		rowIterator.next();
		
		while(rowIterator.hasNext()) {

			//Read Rows from Excel document
			Row row = rowIterator.next();  
			//Read cells in Rows and get chart data
			Iterator<Cell> cellIterator = row.cellIterator();
			
			Cell cell1 = cellIterator.next(); 
			chart_label = String.valueOf(cell1.getNumericCellValue());
			
			Cell cell2 = cellIterator.next(); 
			chart_data = cell2.getNumericCellValue();
				
			/* Add data to the data set */          
			/* We don't have grouping in the pie chart, so we put them in fixed group */            
			my_pie_chart_data.setValue(chart_label, chart_data);
		
		}               
        
        
        /* Create a logical chart object with the chart data collected */
        JFreeChart myPieChart=ChartFactory.createPieChart(sheetName,my_pie_chart_data,true,true,false);        
        /* Dimensions of the chart */               
        int width=640; /* Width of the chart */
        int height=480; /* Height of the chart */               
        /* We don't want to create an intermediate file. So, we create a byte array output stream 
        and byte array input stream
        And we pass the chart data directly to input stream through this */             
        /* Write chart as PNG to Output Stream */
        ByteArrayOutputStream chart_out = new ByteArrayOutputStream();          
        ChartUtilities.writeChartAsPNG(chart_out,myPieChart,width,height);
        /* We can now read the byte data from output stream and stamp the chart to Excel worksheet */
        int my_picture_id = my_workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
        /* we close the output stream as we don't need this anymore */
        chart_out.close();
        /* Create the drawing container */
        XSSFDrawing drawing = my_worksheet.createDrawingPatriarch();
        /* Create an anchor point */
        ClientAnchor my_anchor = new XSSFClientAnchor();
        /* Define top left corner, and we can resize picture suitable from there */
        my_anchor.setCol1(4);
        my_anchor.setRow1(5);
        /* Invoke createPicture and pass the anchor point and ID */
        XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
        /* Call resize method, which resizes the image */
        my_picture.resize();
        /* Close the FileInputStream */
        file.close();               
        /* Write changes to the workbook */
        FileOutputStream out = new FileOutputStream(new File(excelFileName));
        my_workbook.write(out);
        out.close();
		
		
}

	
	
	public static List<List<String>> loadInputConfigFromExcel(String fileName){
				
		List<List<String>> inputList = new ArrayList<List<String>>();
		
		try {
			
			FileInputStream file = new FileInputStream(new File(fileName));
			
			//Get the workbook instance for XLS file 
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			//Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			
			
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				List<String> rowList = new ArrayList<String>();
				
				//For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()) {
					
					Cell cell = cellIterator.next();
					
					rowList.add(cell.getStringCellValue());
				}
				
				inputList.add(rowList);
				
			}
			
			file.close();
			workbook.close();
			
		} catch (FileNotFoundException e) {
			CustomErrorDialog.showDialog("Error while reading Input from Excel", e.toString());
		    e.printStackTrace();
		} catch (IOException e) {
			CustomErrorDialog.showDialog("Error while reading Input from Excel", e.toString());
			 e.printStackTrace();
		} catch(Exception e){
			CustomErrorDialog.showDialog("Exception while reading Input from Excel", e.toString());
			e.printStackTrace();
		}
		
		return inputList;
	}
	
}
