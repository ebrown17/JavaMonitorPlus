package main.java.processmanager;

import java.util.LinkedList;
import java.util.Queue;

import main.java.JavaProcess;

public class ProcessManagerObject {
	
	private  JavaProcess javaProcess;
	private  JavaProcessThread processThread;
	private  Queue<String> processQueue = new LinkedList<String>();
	
	public ProcessManagerObject(JavaProcess process){
		this.javaProcess=process;
		//this.processThread=jpThread;		
	}
	
	public void startThread(){
		
		processThread = new JavaProcessThread(javaProcess.getPID(), processQueue);
		processThread.start();
	}
	
	public boolean threadRunning(){
		
		
		
		return processThread.isAlive();
		
	}
	
	public void threadStop(){	
		
		processQueue.add("stop");
	}
	
	public  String getPID(){
		return javaProcess.getPID();
	}
	
	public  String getName(){
		return javaProcess.getName();
	}
}