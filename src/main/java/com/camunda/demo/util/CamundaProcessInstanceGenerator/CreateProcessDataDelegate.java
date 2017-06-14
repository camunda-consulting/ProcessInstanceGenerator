package com.camunda.demo.util.CamundaProcessInstanceGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.util.json.JSONArray;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.beans.PropertyAccessException;

public class CreateProcessDataDelegate implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		Map<String, Vector<Object>> procDataOptions = (Map)execution.getVariable("prcoessData");
		Vector<Object> values = new Vector<Object>();
		if(procDataOptions == null)
		{
			procDataOptions = new HashMap<String, Vector<Object>>();
		}
		
		//String theJSONVars = (String) execution.getVariable("JSONVariables");
		String processVarsString = (String) execution.getVariable("JSONVariables");
		System.out.println("-------------------"+processVarsString);
		JSONArray theJSONVars = new JSONArray(processVarsString);
		
		parseProcessData(theJSONVars, procDataOptions);

		
		execution.setVariable("prcoessData", procDataOptions);
		
		removeAllVars(execution);
		
	}
	
	/*
	 * [[{"name":"ff","value":"","type":"Range","$$hashKey":"0DH","valuefrom":"5","valueto":"10"},
	 * {"name":"ddf","value":"dfsfs,def,hgsdj","type":"String","$$hashKey":"0DW"}]
	 * 
	 */
	private void parseProcessData(JSONArray variables, Map<String, Vector<Object>> procDataOptions){
		
		
		 for (int i = 0, size = variables.length(); i < size; i++)
		 {
			Vector<Object> finalValues = new Vector<Object>();

			 JSONObject var = variables.getJSONObject(i);
			 String varName = var.getString("name");
			 String type = var.getString("type");
			 String values = var.getString("value");
			 
			 
			 if(type.equals("Boolean")){
				 finalValues.addElement(true);
				 finalValues.addElement(false);
 
				 
			 }else if(type.equals("String")){
				 	
					List<String> valuesList = Arrays.asList(values.split(","));
					finalValues.addAll(valuesList);

			 }if(type.equals("Integer")){
					
					List<String> valuesList = Arrays.asList(values.split(","));
					List<Integer> valuesListInts = new ArrayList<Integer>();
					
					for(String s : valuesList) valuesListInts.add(Integer.valueOf(s));
					
					finalValues.addAll(valuesListInts);
			}if(type.equals("Range")){
				
				int from = var.getInt("valuefrom");
				int to = var.getInt("valueto");
				finalValues.addElement(from);
				finalValues.addElement(to);
			}if(type.equals("Long")) {
				finalValues.addElement(Long.valueOf(values));
			}
			 
			 procDataOptions.put(varName, finalValues);
			 
			 
		 }
		
		
		
	}

	private void removeAllVars(DelegateExecution execution) {
		execution.removeVariable("variableName");
		execution.removeVariable("varialbeType");
		execution.removeVariable("varialbeValues");
		
	}
	
	private void oldParseVars()
	{
//		String varName = (String)execution.getVariable("variableName");
//		String varType = (String)execution.getVariable("varialbeType");
//		String varValue = (String)execution.getVariable("varialbeValues");
//		
//		if(varType.equals("Boolean")){
//			values.addElement(true);
//			values.addElement(false);
//			
//		}if(varType.equals("String") ){
//			
//			List<String> valuesList = Arrays.asList(varValue.split(","));
//			values.addAll(valuesList);
//		}if(varType.equals("Interger")){
//			
//			List<String> valuesList = Arrays.asList(varValue.split(","));
//			List<Integer> valuesListInts = new ArrayList<Integer>();
//			
//			for(String s : valuesList) valuesListInts.add(Integer.valueOf(s));
//			
//			values.addAll(valuesListInts);
//		}
//		
//		procDataOptions.put(varName, values);
//		
//		execution.setVariable("prcoessData", procDataOptions);
	}


}
