package com.camunda.demo.util.CamundaProcessInstanceGenerator;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.camunda.demo.util.CamundaProcessInstanceGenerator.helper.RandomUtilTool;

public class StartProcessInstanceDelegate implements JavaDelegate {
	
	RandomUtilTool rando = new RandomUtilTool();

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String processInstanceKey = (String)execution.getVariable("ProcessKey");
		Map<String, Object> vars = (Map<String, Object>)execution.getVariable("procVars");
		String busKey = rando.randomUUID();
		
		execution.setVariable("procBusKey", busKey);
		
		execution.getProcessEngineServices().getRuntimeService()
		.startProcessInstanceByKey(processInstanceKey, busKey, vars);
		
		
		
		
				//String busKey = (String) execution.getVariable("procBusKey");

	}

}
