package com.camunda.demo.util.CamundaProcessInstanceGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import com.camunda.demo.util.CamundaProcessInstanceGenerator.helper.RandomUtilTool;

public class FindCurrentWaitStateDelegate implements JavaDelegate {
	
	RandomUtilTool rando = new RandomUtilTool();

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String busKey = (String) execution.getVariable("procBusKey");
		String currentState = (String) execution.getVariable("currentState");
		String quickEventExecution = (String) execution.getVariable("quickEventExecution");
		String[] quickEvents = null;
		if (quickEventExecution != null) {
			quickEvents = quickEventExecution.split(":");
		}
		if(currentState != null && currentState.equals("stopHere"))
		{
			execution.setVariable("currentState", "ended");
			return;
		}
		
		List<ProcessInstance> procInstances = execution.getProcessEngineServices().getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceBusinessKey(busKey)
				.list();
		

		for (ProcessInstance processInstance : procInstances) 
		{
				
			HashMap<String, String> executionIDTOElemnetType = getActivityLocation(execution, processInstance);
			
			// Just going to get the types and put them in a List so i can shuffle them to add a little randomness :) 
			Collection<String> typesCol = executionIDTOElemnetType.values();
			List<String> types = new ArrayList<String>(typesCol);
			Collections.shuffle(types);
			
			//System.out.println(executionIDTOElemnetType.toString());
			
			// now i need to look around for boundary events or event sub-processes
			
			
			for (String type : types) 
			{
				
				if(type.contains("userTask")){
					int randomEvent = 15;
					//the below code is responsible for executing events more frequently if a specific variable is set
					if (quickEvents != null) {
						String executionId = execution.getProcessEngineServices().getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(busKey).singleResult().getId();
						if (quickEvents[3].equals(execution.getProcessEngineServices().getRuntimeService().getVariable(executionId, quickEvents[2]))) {
							randomEvent = Integer.parseInt(quickEvents[1]);
						}
					}
					if(takeALookForEvents(execution, busKey) && rando.randomBoolean(rando.randomNumber(randomEvent)))
					{
						execution.setVariable("currentState", "event");
					}else{
						execution.setVariable("currentState", "task");
					}	
					
					return;
				}
				else if(type.contains("Event") || type.contains("eventBasedGateway") || type.contains("receiveTask")){
					
					execution.setVariable("currentState", "event");
					return;
					
					
				}else if(type.contains("service")){
					if(takeALookForEvents(execution, busKey) && rando.randomBoolean(rando.randomNumber(20)))
					{
						execution.setVariable("currentState", "event");
					}else{
						execution.setVariable("currentState", "externalTask");
					}
					return;
				}
			}
		}
		
		execution.setVariable("currentState", "ended");

	}
	
	
	private HashMap<String, String> getActivityLocation(DelegateExecution execution, ProcessInstance procInc)
	{
		
	    HashMap<String, String> activityIDAndActivityType = new HashMap<String, String>();
	    
	    List<String> activeActivityIds =
	    		execution.getProcessEngineServices().getRuntimeService().getActiveActivityIds(procInc.getId());

	    // get bpmn model of the process instance
	    BpmnModelInstance bpmnModelInstance =
	    		execution.getProcessEngineServices().getRepositoryService().getBpmnModelInstance(procInc.getProcessDefinitionId());

	    for (String activeActivityId : activeActivityIds) {
	      // get the speaking name of each activity in the diagram
	      ModelElementInstance modelElementById =
	          bpmnModelInstance.getModelElementById(activeActivityId);
	      
	      String activityType = modelElementById.getElementType().getTypeName();

	      activityIDAndActivityType.put(activeActivityId, activityType);
	    }
	    
	    


	    // map contains now all active activities
	    return activityIDAndActivityType;
	}
	
	private boolean takeALookForEvents(DelegateExecution delExe, String busKey){
		
		List<ProcessInstance> procs = delExe.getProcessEngineServices().getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(busKey).list();
		
		for (ProcessInstance proc : procs) {
			
		
		
		List<Job> jobList = delExe.getProcessEngineServices().getManagementService().createJobQuery().processInstanceId(proc.getId()).list();
		List<EventSubscription> events = delExe.getProcessEngineServices().getRuntimeService().createEventSubscriptionQuery().processInstanceId(proc.getId()).list();
		
			
			if(!jobList.isEmpty() || !events.isEmpty()){			
				return true;
		    }
		}
		
		return false;
	}


}
