package main.java.processmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import main.java.JavaProcess;
import main.java.commands.RunCommands;

public class JavaProcessThreadManager extends Thread {

	private static HashMap<String, JavaProcess> jpsMap = new HashMap<String, JavaProcess>();
	private static List<String> removeObjects = new ArrayList<String>();
	private static ProcessManagerObject pMObject;
	private static HashMap<String, ProcessManagerObject> processManagerObjects = new HashMap<String, ProcessManagerObject>();

	public void run() {
		System.out.println("Started Thread Manager");

		while (true){

			RunCommands.getJps(jpsMap);

			for (JavaProcess proc : jpsMap.values()){

				if (!processManagerObjects.containsKey(proc.getPID())){
					pMObject = new ProcessManagerObject(proc);
					pMObject.startThread();
					processManagerObjects.put(proc.getPID(),pMObject);
				}
			}

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
					processManagerObjects.remove(removeObject);
				}
			}

			try{
				Thread.sleep(4000);
			} catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
