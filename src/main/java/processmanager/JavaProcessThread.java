package main.java.processmanager;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import main.java.JavaProcess;


import sun.tools.jconsole.LocalVirtualMachine;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class JavaProcessThread extends Thread {

	private String pid, queueData, name;
	private String[] names;
	private long heapUsed, maxHeap;
	private double percentMem;
	private BlockingQueue<String[]> processQueue;
	private boolean running = true;
	private String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private ThreadMXBean remoteThreading;
	private MemoryMXBean memoryBean;
	private static DecimalFormat df = new DecimalFormat("##.##");
	private JavaProcess javaProcess;
	private VirtualMachine vm;
	private String connectorAddress = null;
	private String agent;
	private JMXConnector connector;
	private MBeanServerConnection remote;
	private LocalVirtualMachine lvm;

	public JavaProcessThread(String pid, BlockingQueue<String[]> queue, JavaProcess javaProcess) {
		this.javaProcess = javaProcess;
		this.pid = pid;
		this.processQueue = queue;
	}

	public void run() {

		System.out.println("Java Processing Thread started for " + pid + "\n");

		lvm = LocalVirtualMachine.getLocalVirtualMachine(Integer.parseInt(pid));
		
		// if process closes while trying to connect, skip it before trying to
		// assign values
		try{
			vm = VirtualMachine.attach(pid);
			agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
			vm.loadAgent(agent);
			connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
			connector = JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
			remote = connector.getMBeanServerConnection();

			remoteThreading = ManagementFactory.newPlatformMXBeanProxy(remote,ManagementFactory.THREAD_MXBEAN_NAME,ThreadMXBean.class);

			memoryBean = ManagementFactory.newPlatformMXBeanProxy(remote,ManagementFactory.MEMORY_MXBEAN_NAME,MemoryMXBean.class);
			
			names = lvm.displayName().split("\\.");
			name = names[names.length - 1].toString();
			
			javaProcess.updateName(name);
			maxHeap = memoryBean.getHeapMemoryUsage().getMax();

		} catch (AttachNotSupportedException e){
			try {
				connector.close();
			} catch (IOException e1) {
				/*// TODO Auto-generated catch block
				e1.printStackTrace();*/
			}
			running = false;
			e.printStackTrace();
		} catch (IOException e){
			try {
				connector.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			running = false;
			e.printStackTrace();
		} catch (AgentLoadException e){
			try {
				connector.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			running = false;
			e.printStackTrace();
		} catch (AgentInitializationException e){
			try {
				connector.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			running = false;
			e.printStackTrace();
		}

		

		while (running){

			/*queueData = processQueue.poll();

			if (queueData != null){
				if (queueData.equals("stop")){
					running = false;
					continue;
				}
			}*/

			try{
				heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
				percentMem = ((double) heapUsed / (double) maxHeap) * 100;

				//System.out.print(name + ", ");
				javaProcess.setLiveThreads(String.valueOf(remoteThreading.getThreadCount()));
				javaProcess.setPeakThreads(String.valueOf(remoteThreading.getPeakThreadCount()));
				javaProcess.setDaemonThreads(String.valueOf(remoteThreading.getDaemonThreadCount()));
				javaProcess.setStartedThreads(String.valueOf(remoteThreading.getTotalStartedThreadCount()));
				javaProcess.setHeapUsed(String.valueOf(df.format(percentMem)));
				
				
				processQueue.add(javaProcess.getProcessData());
				/*System.out.print(String.valueOf(remoteThreading.getThreadCount()) + ", ");
				System.out.print(String.valueOf(remoteThreading.getPeakThreadCount()) + ", ");
				System.out.print(String.valueOf(remoteThreading.getDaemonThreadCount()) + ", ");
				System.out.print(String.valueOf(remoteThreading.getTotalStartedThreadCount()) + ", ");
				System.out.print(String.valueOf(df.format(percentMem)) + " \n");*/

			} catch (Exception e){
				try {
					connector.close();
				} catch (IOException e1) {
					/*// TODO Auto-generated catch block
					e1.printStackTrace();*/
				}
				running = false;
			}

		
			try{
				Thread.sleep(3000);
			} catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println(javaProcess.getName() + " thread stopped \n");

	}

}
