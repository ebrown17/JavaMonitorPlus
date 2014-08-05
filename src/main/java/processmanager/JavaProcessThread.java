package main.java.processmanager;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.Queue;

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
	private Queue<String> processQueue;
	private boolean running = true;
	private String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private ThreadMXBean remoteThreading;
	private MemoryMXBean memoryBean;
	private static DecimalFormat df = new DecimalFormat("##.##");

	public JavaProcessThread(String pid, Queue<String> processQueue) {
		this.pid = pid;
		this.processQueue = processQueue;
	}

	public void run() {

		System.out.println("Java Processing Thread started for " + pid);

		LocalVirtualMachine lvm = LocalVirtualMachine.getLocalVirtualMachine(Integer.parseInt(pid));

		VirtualMachine vm;
		String connectorAddress = null;
		String agent;
		JMXConnector connector;
		MBeanServerConnection remote;

		// if process closes while connected, skip it before trying to
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

		} catch (AttachNotSupportedException e){

			e.printStackTrace();
		} catch (IOException e){

			e.printStackTrace();
		} catch (AgentLoadException e){

			e.printStackTrace();
		} catch (AgentInitializationException e){

			e.printStackTrace();
		}

		names = lvm.displayName().split("\\.");
		name = names[names.length - 1].toString();
		maxHeap = memoryBean.getHeapMemoryUsage().getMax();

		while (running){

			queueData = processQueue.poll();

			if (queueData != null){
				if (queueData.equals("stop")){
					running = false;
					continue;
				}
			}

			try{
				heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
				percentMem = ((double) heapUsed / (double) maxHeap) * 100;

				System.out.print(name + " ");
				System.out.print(String.valueOf(remoteThreading.getThreadCount()) + " ");
				System.out.print(String.valueOf(remoteThreading.getPeakThreadCount()) + " ");
				System.out.print(String.valueOf(remoteThreading.getDaemonThreadCount()) + " ");
				System.out.print(String.valueOf(remoteThreading.getTotalStartedThreadCount()) + " ");
				System.out.print(String.valueOf(df.format(percentMem)) + " \n");

			} catch (Exception e){
				running = false;
			}

		
			try{
				Thread.sleep(2000);
			} catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println(name + " thread stopped");

	}

}
