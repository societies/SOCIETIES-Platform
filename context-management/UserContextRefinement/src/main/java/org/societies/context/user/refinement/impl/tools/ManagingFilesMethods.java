package org.societies.context.user.refinement.impl.tools;
/*
 * Implementation of several methods to manage files
 * (Saving to a file, dividing, extracting columns)
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.NodeBL;

public class ManagingFilesMethods {
	
	/**
	 * This method is used to save the values of a window into a file.
	 * @param windowInput Array with the window values to be stored.
	 * @param runtime An array containing the runtime of every window value.
	 * @param inputFile The name of the file where the values are stored.
	 * @throws IOException
	 */
	public static void saveWindowToFile(double[] windowInput,double[] runtime,String inputFile) throws IOException{
		File fichero = new File(inputFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
		int i=0;
		while (i<windowInput.length){
			bw.write(Double.toString(runtime[i])+'\t');
			bw.write(Double.toString(windowInput[i]));
			bw.newLine();
			
			i++;
		}
		bw.close();
	
	}
	
	public static void saveWindowsToFile(double[] w1,double[] w2, double[] w3, double[] w4,double[] runtime,String inputFile) throws IOException{
		File fichero = new File(inputFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
		int i=0;
		while (i<runtime.length){
			bw.write(Double.toString(runtime[i])+'\t');
			bw.write(Double.toString(w1[i])+'\t');
			bw.write(Double.toString(w2[i])+'\t');
			bw.write(Double.toString(w3[i])+'\t');
			bw.write(Double.toString(w4[i])+'\t');
			bw.newLine();
			
			i++;
		}
		bw.close();
	
	}
	
	/**
	 * This method is used to extract the runtime (first columen) from a file.
	 * @param numberEntries Number of entries of the file.
	 * @param fileName 
	 * @return
	 * @throws FileNotFoundException
	 */
	public static double[] extractRunTimeFromFile(int numberEntries,String fileName) throws FileNotFoundException{
		
		double[] runtime=new double[numberEntries];
		File input = new File (fileName); 
		Scanner sc=new Scanner(input);
		//sc.nextLine();
		int i=0;
		while (sc.hasNext()){
			
			String[] tempString=sc.nextLine().split("\t");
			runtime[i]=Double.valueOf(tempString[0]);
			i++;
		}
		
		return runtime;
		
	}
	
	/**
	 * This method extracts some columns from a file a puts them into an array, following a certain ordering criteria.
	 * @param columns Number of columns of the file
	 * @param numberEntries Number of entries of the file
	 * @param wanted Specific order in which we want to extract the columns from the file
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[][] extractColumnsFromFile (int columns,int numberEntries,int[] wanted,String fileName) throws FileNotFoundException{
		
				
		String[][] output=new String[numberEntries][wanted.length];
		File input = new File (fileName); 
		Scanner sc=new Scanner(input);
		int i=0;
		//sc.nextLine();
		//String prueba = sc.nextLine();
		//prueba = sc.nextLine();
		
		//String[] tempArray=new String[columns];
								
		while (sc.hasNext()){
			
			String tempString=sc.nextLine();
			String[] parts=tempString.split("\t");
			
			for (int j=0;j<wanted.length;j++){
				output[i][j]=parts[wanted[j]];
			}
			
			i++;
		
		}
		
		return output;
	}

	
	/**
	 * This method extracts the probability tables of a bayesian network, and stores it in a file.
	 * @param dag 
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveCPTtoFile(DAG dag,String fileName) throws IOException{
		File fichero = new File(fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
		int cont=0;
		NodeBL[] tempNodes=(NodeBL[]) dag.getNodes();
		for (int i=0;i<tempNodes.length;i++){
			bw.write(((NodeBL) tempNodes[i]).getName());
			bw.newLine();
			
			Probability[] tempCPT=tempNodes[i].getProbTable().getProbabilities();
			
			for (int j=0;j<tempCPT.length;j++){
				cont++;
				if(cont==4){
					cont=0;
					bw.newLine();
				}
				bw.write(tempCPT[j].getProbability()+",");
				
			
			
			}
			bw.newLine();
			bw.write("--------------------------------------------");
			bw.write("--------------------------------------------");
		}
		
		bw.close();	
	
	}
	
	/**
	 * This method divides a file into two parts
	 * 
	 * @param medium This is the runtime value that will be the border between the two parts in which the input file is going to be divided into.
	 * @param fileInput
	 * @param output1 File containing the first part of the input file (beginning-medium)
	 * @param output2 File containing the second part of the output file (medium-end)
	 * @throws IOException
	 */
	
	public static void divideFile(int medium,String fileInput,String output1,String output2) throws IOException{
		File input = new File (fileInput); 
		Scanner sc=new Scanner(input);
		String prueba = sc.nextLine();
		
		File fichero = new File(output1);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
	
		File fichero2 = new File(output2);
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(fichero2));
	
		while (sc.hasNextLine()){
			String temp=sc.nextLine();
			String[] parts=temp.split("\t");
			if (Double.valueOf(parts[0])<medium){
				bw.write(temp);
				bw.newLine();
			}else{
				bw2.write(temp);
				bw2.newLine();
			}
			
		}
		
		bw.close();
		bw2.close();
		
	}
	public static void divideFile2(int divisionLine,String fileInput,String output1,String output2) throws IOException{
		File input = new File (fileInput); 
		Scanner sc=new Scanner(input);
		
		String prueba = sc.nextLine();
		int cont=0;
		File fichero = new File(output1);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
	
		File fichero2 = new File(output2);
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(fichero2));
	
		while (sc.hasNextLine()){
			String temp=sc.nextLine();
			cont++;
			String[] parts=temp.split("\t");
			if (cont<divisionLine){
				bw.write(temp);
				bw.newLine();
			}else{
				bw2.write(temp);
				bw2.newLine();
			}
			
		}
		
		bw.close();
		bw2.close();
		
	}
	
	/**
	 * This method it is used to process the information about V_brake, setting it to zero or twenty, depending
	 * on whether V car was braking, or not.
	 * 
	 * @param inputfile
	 * @throws IOException
	 */
	public static void processVbrakeInFile(String inputfile) throws IOException{
		
		File input = new File (inputfile); 
		Scanner sc=new Scanner(input).useDelimiter(",");
		
		int i=0;
		String prueba = sc.nextLine();
		double[] E_brake=new double[19751];
		double[] E_LatPos=new double[19751];
		double[] E_Velocity=new double[19751];
		double[] runtime=new double[19751];
		double[] V_Bremsverhalten=new double[19751];
		double[] V_LatPos=new double[19751];
		double[] V_Velocity=new double[19751];
		
		double[] distance=new double[19751];
		
		File fichero = new File("./data/VP687_V_OK.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
		
				
		int indexLine=0;
		while (sc.hasNextLine()){
		
			String str=sc.nextLine();
			String[] parts=str.split(",");
			
			runtime[i]=Double.valueOf(parts[0]).doubleValue();
			E_Velocity[i]=Double.valueOf(parts[1]).doubleValue();
			V_Velocity[i]=Double.valueOf(parts[2]).doubleValue();
			E_LatPos[i]=Double.valueOf(parts[3]).doubleValue();
			V_LatPos[i]=Double.valueOf(parts[4]).doubleValue();
			E_brake[i]=Double.valueOf(parts[5]).doubleValue();
			distance[i]=Double.valueOf(parts[6]).doubleValue();
			V_Bremsverhalten[i]=Double.valueOf(parts[7]).doubleValue();
			double tempV=V_Bremsverhalten[i];
		
			if (tempV!=0){tempV=20;}
			
			bw.write(parts[0]+",");
			bw.write(parts[1]+",");
			bw.write(parts[2]+",");
			bw.write(parts[3]+",");
			bw.write(parts[4]+",");
			bw.write(parts[5]+",");
			bw.write(parts[6]+",");
			bw.write(Double.toString(tempV));
			bw.newLine();
			
		i++;
		
		}
		
		bw.close();	
		System.out.println("Fertig");
		
		
		
	
	
	}

	/**
	 * This method saves the structure of a DAG into a file.
	 * @param prueba
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveStructureToFile(DAG prueba, String fileName) throws IOException {
		File fichero = new File(fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
		
		bw.write(prueba.toString());
		bw.close();	
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method is used to generate a file suitable to be used as input to the learning process implemented by Patrick Robertson.
	 * Therefore, the information about the runtime is not included in the output file.
	 * @param fileName
	 * @param outputFile
	 * @param numberOfEntries
	 * @throws IOException
	 */
	public static void generateStructureLearningFile(String fileName,String outputFile,int numberOfEntries) throws IOException{
		
		File input = new File (fileName); 
		Scanner sc=new Scanner(input);
		
		File fichero = new File(outputFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
		
		String E_Brake;
		String E_Speed;
		String V_Brake;
		String V_Speed;
		String Distance;
		
		//double[] distance=new double[numberOfEntries];
		String prueba=sc.nextLine();
		System.out.println(prueba);
		
		
		
		bw.write(prueba);
		bw.newLine();
		while (sc.hasNextLine()){
			String str=sc.nextLine();
			String[] parts=str.split("\t");
			//parts[0] is the runtime,which is not needed for the structure learning
			bw.write(parts[1]+"\t");
			bw.write(parts[2]+"\t");
			bw.write(parts[3]+"\t");
			bw.write(parts[4]+"\t");
			bw.write(parts[5]+"\t");
			bw.newLine();
		}
		
		bw.close();	
	}

	/**
	 * This method counts the number of entries of a file.
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static int extractLinesNumber(String fileName) throws FileNotFoundException {
		
		File input = new File (fileName); 
		
		Scanner sc=new Scanner(input);			
		
		int toReturn=0;
		
		while (sc.hasNextLine()){
			toReturn++;
			sc.nextLine();
		}
		
		sc.close();
		
		return toReturn;
		
		
	}
}
