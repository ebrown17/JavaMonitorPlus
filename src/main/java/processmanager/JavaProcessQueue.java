package main.java.processmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.concurrent.BlockingQueue;




public class JavaProcessQueue extends Thread {
	
	private final BlockingQueue<String[]> javaProcessQueue;
	private static List<String[]> processDataList = new ArrayList<String[]>();
	boolean duplicate = false;
	String[] data;
	
	public JavaProcessQueue(BlockingQueue<String[]> queue){
		
		this.javaProcessQueue=queue;
		
	}
	
	public void run(){
		
		while(true){
			
			data = javaProcessQueue.poll();
			
			while(data!=null){
				
				for(String[] pid: processDataList){
					
					if(pid[1].equals(data[1])){
						duplicate = true;
						break;
					}
					else {
						duplicate = false;
					}
					
				}
				
				if(!duplicate)processDataList.add(data);
				
				//System.out.println(Arrays.toString(data) + "\n");
				data = javaProcessQueue.poll();
			}
			
			 Collections.sort(processDataList, new Comparator<String []>() {
		            @Override
		            public int compare(final String [] object1, final String [] object2) {
		            	//if(object1[1].equals(object2[1]))
		                return object1[0].compareTo(object2[0]);
		            }
		           } );
			 
			
			 
			 for(String[] sortedList : processDataList){
				 
				 System.out.println(Arrays.toString(sortedList) + "\n");
			 }
			
			 processDataList.clear();
			//else {
				try{
					Thread.sleep(2000);
				} catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
		}
		
	}

}
