package com.camunda.demo.util.CamundaProcessInstanceGenerator;

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.event.EventType;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;

import com.camunda.demo.util.CamundaProcessInstanceGenerator.helper.RandomUtilTool;

public class CompleteEventDelegate implements JavaDelegate {
	
	RandomUtilTool rando = new RandomUtilTool();

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		//Boolean fireEvent = rando.randomBoolean(50);
		String busKey = (String) execution.getVariable("procBusKey");
		
		List<Execution> executions = execution.getProcessEngineServices().getRuntimeService().createExecutionQuery()
		.processInstanceBusinessKey(busKey)
		.active()
		.list();
		
		Collections.shuffle(executions);
		
//		execution.getProcessEngineServices().getRuntimeService().createEventSubscriptionQuery()
//		.
		
		System.out.println("Event/Job has been found and will be completed... ");
		
		for (Execution processExe : executions) {
			
			//execution.getProcessEngineServices().getRuntimeService().signal(processExe.getId());
			
			
			List<EventSubscription> events = execution.getProcessEngineServices().getRuntimeService()
					.createEventSubscriptionQuery()
					.executionId(processExe.getId())
					.list();
			
			System.out.println("List of events are: " + events.toString());
			
			List<Job> jobList = execution.getProcessEngineServices().getManagementService()
					.createJobQuery()
					.executionId(processExe.getId())
					.list();
			
			System.out.println("List of jobs are: "+ jobList.toString());
			
			
			/*
			 * The Horrible mess below is intended to do the following
			 * If there are both jobs and events - we'll randomly do one of them
			 * if there are just events we'll do one of those
			 * if there are just jobs we'll do one of those...
			 * ALSO..
			 * If for some reason there was an issue firing a job or events a BPMN error is throw. 
			 */
			
			if(!jobList.isEmpty() && !events.isEmpty()){			
				if(rando.randomBoolean(50)){
					if(fireEvent(events, execution))
						return;
				}else{
					if(fireJob(jobList, execution))
						return;
				}
				}else if(!jobList.isEmpty()){
					if(fireJob(jobList, execution))
						return;
				}else if(!events.isEmpty()){
					if(fireEvent(events, execution))
						return;	
				}

		}

		
		throw new BpmnError("WEIRD");

	}
	
	private boolean fireEvent(List<EventSubscription> events, DelegateExecution context){
		System.out.println("Going to tell an event to do some stuff... ");
		
		//Just do a quick shuffle.
		Collections.shuffle(events);
		
		for (EventSubscription event : events) {
			//Execution thisEventExecution = context.getProcessEngineServices().getRuntimeService().createExecutionQuery().executionId(event.getExecutionId()).singleResult();
			

			
			String eventType = event.getEventType();
			
				if(event.getActivityId() == null )
					break;
				
				if(eventType.equals("message")){
					context.getProcessEngineServices().getRuntimeService()
					.messageEventReceived(event.getEventName(), event.getExecutionId());
					
					return true;
				}else if(eventType.equals("signal")){
					context.getProcessEngineServices()
					.getRuntimeService()
					.signalEventReceived(event.getEventName(), event.getExecutionId());
					
					return true;
				}else if(eventType.equals("conditional")){
					// this will have to do for now
					context.getProcessEngineServices().getRuntimeService().signal(event.getExecutionId());
					// this doens't work with an event based gateway. 
					return true;
				}else {
					System.out.println("An event was found but was not caught -- " + event.toString());
				}
				

		}
		
		return false;
	}
	
	private boolean fireJob(List<Job> jobList, DelegateExecution context){
		System.out.println("Going to tell a job to do some stuff... ");
		
		Collections.shuffle(jobList);
		
		for (Job job : jobList) {
			
			
			
			
			System.out.println("Completing " + job.toString());
			//Execution thisEventExecution = context.getProcessEngineServices().getRuntimeService().createExecutionQuery().executionId(job.getExecutionId()).singleResult();
			
			context.getProcessEngineServices().getManagementService().executeJob(job.getId());
			
			// I can't use a signal - because it's possible that the executinon is waiting at an event-based gateway :(
			//context.getProcessEngineServices().getRuntimeService().signal(thisEventExecution.getId());
			return true;
			
		}
		
		return false;
	}

}
