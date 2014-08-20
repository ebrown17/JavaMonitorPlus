package main.java.processmanager;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;


public class JavaProcessQueue extends Thread {
	
	private final BlockingQueue<String[]> javaProcessQueue;
	String[] data;
	
	public JavaProcessQueue(BlockingQueue<String[]> queue){
		
		this.javaProcessQueue=queue;
		
	}
	
	public void run(){
		
		while(true){
			
			data = javaProcessQueue.poll();
			
			if(data!=null){
				System.out.println(Arrays.toString(data) + "\n");
				
			}
			else {
				try{
					Thread.sleep(2000);
				} catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
