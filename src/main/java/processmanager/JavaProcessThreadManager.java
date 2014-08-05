package main.java.processmanager;


import java.util.Arrays;
import java.util.HashMap;


import main.java.JavaProcess;
import main.java.commands.RunCommands;
import main.java.util.ManageProcessObjects;

public class JavaProcessThreadManager extends Thread {

	private static HashMap<String, JavaProcess> jpsMap = new HashMap<String, JavaProcess>();
	private static HashMap<String, ProcessManagerObject> processManagerObjects = new HashMap<String, ProcessManagerObject>();

	public void run() {
		System.out.println("Started Thread Manager");

		while (true){

			RunCommands.getJps(jpsMap);
			
			ManageProcessObjects.startMonitorProcess(jpsMap, processManagerObjects);
			
			ManageProcessObjects.removeClosedProcesses(jpsMap, processManagerObjects);
			
			for(ProcessManagerObject process: processManagerObjects.values()){
				
				System.out.println(Arrays.toString(process.getProcess().getProcessData()));
				
			}System.out.println();

			try{
				Thread.sleep(4000);
			} catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
