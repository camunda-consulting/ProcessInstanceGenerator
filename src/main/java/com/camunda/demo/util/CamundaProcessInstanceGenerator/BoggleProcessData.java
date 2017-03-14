package com.camunda.demo.util.CamundaProcessInstanceGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import com.camunda.demo.util.CamundaProcessInstanceGenerator.helper.RandomUtilTool;

public class BoggleProcessData implements ExecutionListener {
	
	RandomUtilTool rando = new RandomUtilTool();

	@Override
	public void notify(DelegateExecution execution) throws Exception {

		Map<String, Vector<Object>> procDataOptions = (Map<String, Vector<Object>>)execution.getVariable("prcoessData");
		Map<String, Object> procVars = new HashMap<String, Object>();

		for (Entry<String, Vector<Object>> entry : procDataOptions.entrySet())
		{
			procVars.put(entry.getKey(), entry.getValue()
					.elementAt(rando.randomNumber(entry.getValue().size())));
			
		}
		
		execution.setVariable("procVars", procVars);

	}

}
