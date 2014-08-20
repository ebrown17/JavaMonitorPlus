package main.java.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import main.java.JavaProcess;
import main.java.processmanager.ProcessManagerObject;

public class ManageProcessObjects {
	
	private static ProcessManagerObject pMObject;
	private static List<String> removeObjects = new ArrayList<String>();
	
	public static void startMonitorProcess(HashMap<String, JavaProcess> jpsMap, HashMap<String, ProcessManagerObject> processManagerObjects,BlockingQueue<String[]> queue){
		
		for (JavaProcess proc : jpsMap.values()){

			if (!processManagerObjects.containsKey(proc.getPID())){
				pMObject = new ProcessManagerObject(proc);
				pMObject.startThread(queue);
				processManagerObjects.put(proc.getPID(),pMObject);
			}
		}
		
	}
	
	public static void removeClosedProcesses(HashMap<String, JavaProcess> jpsMap, HashMap<String, ProcessManagerObject> processManagerObjects){
		
		removeObjects.clear();
		
		if (jpsMap.size() != processManagerObjects.size()){

			// System.out.println("removing threads");
			for (ProcessManagerObject pmo : processManagerObjects.values()){

				if (!jpsMap.containsKey(pmo.getPID())){
					removeObjects.add(pmo.getPID());

				}

			}

			for (String removeObject : removeObjects){

				// System.out.println(removeObject
				// +" thread is now shutting down");
				processManagerObjects.get(removeObject).clearObject();
				processManagerObjects.remove(removeObject);
				
			}
		}
		
		
	}

}
