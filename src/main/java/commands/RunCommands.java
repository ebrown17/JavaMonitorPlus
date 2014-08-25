package main.java.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.java.JavaProcess;

public class RunCommands {
	
	private static Commands command = Commands.getInstance();
	private static List<String> processList = new ArrayList<String>();
	private static Process process;
	private static Scanner scanner;
	private static String [] jps;
	private static String line,name,pid;
	
	
	public static HashMap<String, JavaProcess> getJps(HashMap<String, JavaProcess> jpsMap) {

		try{
			process = command.getJps().start();
		} catch (IOException e){

			System.out.println("Error running jps command: " + e.getMessage());
		}
		scanner = new Scanner(process.getInputStream());

		while (scanner.hasNext()){
			line = scanner.nextLine();
			// System.out.println(line);
			if (line.contains("Jps"))
				continue;
			jps = line.trim().split("\\s+");
			pid = jps[0];
			name = jps[1];
			processList.add(pid);
			
			if(!jpsMap.containsKey(pid)){
				jpsMap.put(pid,new JavaProcess(name, pid));
			}
			
			if (processList.size() != jpsMap.size()){

				// removed elements from hashmap this way because it helps avoid
				// ConcurrentModificationExceptions that the for each causes.

				Iterator<Map.Entry<String, JavaProcess>> iterator = jpsMap.entrySet().iterator();
				while (iterator.hasNext()){
					Map.Entry<String, JavaProcess> values = (Map.Entry<String, JavaProcess>) iterator.next();
					if (!processList.contains(values.getKey())){
						iterator.remove();
					}
				}
			}
			
		}
		processList.clear();	
		return jpsMap;
	}
	
	public static void runRM(){
		try{
			process = command.getRm().start();
		} catch (IOException e){
			System.out.println("Error running rm command: " + e.getMessage());;
		}
		
		
	}
	
	

}
