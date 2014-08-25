package main.java.commands;

import java.util.ArrayList;
import java.util.List;

public class Commands {
	
	private static final Commands INSTANCE = new Commands();
	
	private List<String> free = new ArrayList<String>();
	private List<String> top = new ArrayList<String>();
	private List<String> jps = new ArrayList<String>();
	private List<String> rm = new ArrayList<String>();
	
	private static ProcessBuilder pFree;
	private static ProcessBuilder pTop;
	private static ProcessBuilder pJps;
	private static ProcessBuilder pRm;
	
	private Commands(){	
		
		free.add("/bin/bash");
		free.add("-c");
		free.add("free -m");		
		pFree = new ProcessBuilder(free);
		pFree.redirectErrorStream(true);
		
		top.add("/bin/bash");
       	top.add("-c");
		top.add("top -n1 -b | grep java");		
		pTop = new ProcessBuilder(top);
		pTop.redirectErrorStream(true);
		
		jps.add("/bin/bash");
		jps.add("-c");
		jps.add("jps -v");		
		pJps = new ProcessBuilder(jps);
		pJps.redirectErrorStream(true);
		
		rm.add("/bin/bash");
		rm.add("-c");
		rm.add("rm .attach_pid*");		
		pRm = new ProcessBuilder(rm);
		pRm.redirectErrorStream(true);
	}
	
	public static Commands getInstance(){
		return INSTANCE;
	}
	
	public  ProcessBuilder getTop(){
		return pTop;
	}
	
	public  ProcessBuilder getJps(){
		return pJps;
	}
	
	public  ProcessBuilder getFree(){
		return pFree;
	}
	
	public  ProcessBuilder getRm(){
		return pRm;
	}

}
