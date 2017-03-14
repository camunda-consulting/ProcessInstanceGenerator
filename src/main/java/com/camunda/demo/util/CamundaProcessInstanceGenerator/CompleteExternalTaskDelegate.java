package com.camunda.demo.util.CamundaProcessInstanceGenerator;


import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.externaltask.ExternalTask;

import org.camunda.bpm.engine.runtime.Execution;

public class CompleteExternalTaskDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String busKey = (String) execution.getVariable("procBusKey");
		
		
		List<Execution> executions = execution.getProcessEngineServices().getRuntimeService().createExecutionQuery()
		.processInstanceBusinessKey(busKey)
		.active()
		.list();
		
		Collections.shuffle(executions);
		
		for (Execution thisExecution : executions) {
			
			List<ExternalTask> extTasks = execution.getProcessEngineServices()
					.getExternalTaskService()
					.createExternalTaskQuery()
					.executionId(thisExecution.getId())
					.list();
			
			if(!extTasks.isEmpty()){
				execution.getProcessEngineServices().getRuntimeService().signal(thisExecution.getId());
				return;
			}

		}
		
		//It's likely that it's just async service task so... lets just continue without an error
		
		System.out.println("Probably a service task with async... probably... otherwise we're in an infinate loop");
	//	throw new BpmnError("WEIRD");
		


	}

}
