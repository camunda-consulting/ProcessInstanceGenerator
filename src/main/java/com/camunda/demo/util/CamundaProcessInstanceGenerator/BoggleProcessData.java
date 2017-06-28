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
			if (!entry.getKey().contains("_weight")) {
				if (procDataOptions.containsKey(entry.getKey()+"_weight")) {
					procVars.put(entry.getKey(),rando.randomBoolean(Integer.parseInt(procDataOptions.get(entry.getKey()+"_weight").get(0).toString())));
				} else {
					procVars.put(entry.getKey(), entry.getValue()
							.elementAt(rando.randomNumber(entry.getValue().size())));
				}
			}
		}
		for (Entry<String, Vector<Object>> entry : procDataOptions.entrySet())
		{
			// using this special weight we can now define weight depending on process variable values in the format
			// {\"name\": \"Task_automatically_assigned_weight_special\",\"value\": \"JobExperience:Beginner:100\",\"type\": \"String\"}
			if (procDataOptions.containsKey(entry.getKey()+"_weight_special")) {
				String specialWeight = (String) procDataOptions.get(entry.getKey()+"_weight_special").get(0).toString();
				String[] specialWeightSplit = specialWeight.split(":");
				if (procVars.get(specialWeightSplit[0]).equals(specialWeightSplit[1])) {
					procVars.put(entry.getKey(),rando.randomBoolean(Integer.parseInt(specialWeightSplit[2])));
				}
			}
			// using this special wdurationeight we can now define duration depending on process variable values in the format
			// {\"name\": \"Task_ScreenApplication_duration_special\",\"value\": \"Department:IT:2000000\",\"type\": \"String\"}			
			if (procDataOptions.containsKey(entry.getKey()+"_duration_special")) {
				String specialDuration = (String) procDataOptions.get(entry.getKey()+"_duration_special").get(0).toString();
				String[] specialDurationSplit = specialDuration.split(":");
				if (procVars.get(specialDurationSplit[0]).equals(specialDurationSplit[1])) {
					procVars.put(entry.getKey(),Long.parseLong(specialDurationSplit[2]));
				}
			}
		}
		
		execution.setVariable("procVars", procVars);

	}

}
