package com.camunda.demo.util.CamundaProcessInstanceGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.task.Task;

import com.camunda.demo.util.CamundaProcessInstanceGenerator.helper.RandomUtilTool;

public class CompleteTaskDelegate implements JavaDelegate {

	RandomUtilTool rambo = new RandomUtilTool();
		
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String busKey = (String) execution.getVariable("procBusKey");
		
		  List<Task> currentTask = execution.getProcessEngineServices().getTaskService().createTaskQuery()
				  .processInstanceBusinessKey(busKey)
				  .list();
		  
		  
		  
		  for (Task task : currentTask) {
			  
			  // We want to have some processes that are not complete  so we have a slight chance that this will end
			  Boolean completeAll = (Boolean) execution.getVariable("completeAll");
			  if (completeAll == null || completeAll.equals(false)) {
				  if(rambo.randomBoolean(5))
				  {
					  //System.out.println("Now we Stop....... ");
					  execution.setVariable("currentState", "stopHere");
					  
					  return;
				  }
			  }
			  //System.out.println("Complete task: "+ task.getName());
			  
/**
 * I don't want to do the whole -  send message - thing here... 			  
 */
//				  
//			  boolean shouldWeSendAMessage = checkMessageStuff(task);
//			  if(shouldWeSendAMessage){
//				  
//				  
//				  Map<String, Object> variables = new HashMap<String, Object>();
//				  variables.put("percentOfHours", rambo.randomNumber(150));
//				  execution.getProcessEngineServices().getRuntimeService().correlateMessage("HoursRegistered", busKey, variables);
//				  
//			  }
//			 
			  // The Idea ia that we get all the variables that are part of loops
			  // then generate them again. 
//			  Map<String, Object> variables = shuffleLoopVariables(task);
			  
			  execution.getProcessEngineServices().getTaskService().complete(task.getId());		  
			  return;
			  
		  }
		  
		  //throw new BpmnError("WEIRD");
		  
		//  execution.getProcessEngineServices().getTaskService().complete(currentTask.getId());

	}

	private boolean checkMessageStuff(Task task) {
		
		if(task.getProcessDefinitionId().contains("developerProcess"))
		{
			System.out.println("IT'S MESSAGE TIME!");
			return rambo.randomBoolean(40);
		}

		return false;
		
	}
	



}
