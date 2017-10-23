package spacecadetsweek3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BareBonesInterpreter {
	public static void main(String[] args) throws IOException{
		BareBonesInterpreter interpreter = new BareBonesInterpreter();
		interpreter.interpret(interpreter.readFile());
	}
	
	public ArrayList<String> readFile() throws IOException{
		String fileName;
		String line = null;
		ArrayList<String> lines = new ArrayList<String>(0);
		
		BufferedReader consoleBr = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter the file name.");
		fileName = consoleBr.readLine();
		
		try {
			BufferedReader fileBr = new BufferedReader(new FileReader(fileName));
			
			while((line = fileBr.readLine()) != null) {
				lines.add(line);
			}
			
			fileBr.close();
		}catch(FileNotFoundException ex) {
			System.out.println("File not found.");
		}
		
		return lines;
	}
	
	void interpret(ArrayList<String> lines) {
		String line;
		String variableName;
		Variable variable = null;
		ArrayList<Variable> variables = new ArrayList<Variable>(0);
		ArrayList<int[]> loopLines = new ArrayList<int[]>(0);
		int nestingLevel = 0, skipLevel = 0;
		int ifLevel = 0, ifSkipLevel = 0;
		boolean skip = false;
		boolean ifSkip = false;
		boolean error = false;
		char operation = '+';
		int arraySize;
		int index;
		int value2 = 0, value3 = 0;
		ArrayVariable arrayVariable;
		ArrayList<ArrayVariable> arrayVariables = new ArrayList<ArrayVariable>(0);
		Variable variable1 = null;
		ArrayVariable arrayVariable1 = null;
		int index1 = 0;
		
		for(int i = 0; i < lines.size(); i++) {
			line = lines.get(i);
			
			if(line.startsWith("clear") && !skip) {
				if(line.contains("[")) {
					variableName = line.substring(6, line.indexOf("["));
					
					if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
						System.out.println("Error: Cannot clear an already declared array");
					}else {
						arraySize = Integer.parseInt(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
						arrayVariable = new ArrayVariable(variableName, arraySize);
						arrayVariables.add(arrayVariable);
					}
				}else {
					variableName = line.substring(6, line.indexOf(";"));
					
					if((variable = findVariable(variableName, variables)) != null) {
						variable.setValue(0);
					}else {
						variable = new Variable(variableName, 0);
						variables.add(variable);
					}
				}
			}else if(line.startsWith("incr") && !skip) {
				if(line.contains("[")) {
					variableName = line.substring(5, line.indexOf("["));
					
					if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
						index = getIndex(line, arrayVariable, variables);
						arrayVariable.increment(index);
					}else {
						System.out.println("Error: Unknown variable at line " + i);
					}
				}else {
					variableName = line.substring(5, line.indexOf(";"));
					
					if((variable = findVariable(variableName, variables)) != null) {
						variable.increment();
					}else {
						System.out.println("Error: Unknown variable at line " + i);
					}
				}
			}else if(line.startsWith("decr") && !skip) {
				if(line.contains("[")) {
					variableName = line.substring(5, line.indexOf("["));
					
					if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
						index = getIndex(line, arrayVariable, variables);
						arrayVariable.decrement(index);
					}else {
						System.out.println("Error: Unknown variable at line " + i);
					}
				}else {
					variableName = line.substring(5, line.indexOf(";"));
					
					if((variable = findVariable(variableName, variables)) != null) {
						variable.decrement();
					}else {
						System.out.println("Error: Unknown variable at line " + i);
					}
				}
			}else if(line.startsWith("while")) {
				if(line.contains("[")) {
					if(!skip) {
						variableName = line.substring(6, line.indexOf("[") - 6);
						
						if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
							index = getIndex(line, arrayVariable, variables);
							if(arrayVariable.getValue(index) == 0) {
								skip = true;
								skipLevel = nestingLevel;
							}else {
								int[] loopLine = {nestingLevel, i};
								loopLines.add(loopLine);
							}
						}else {
							System.out.println("Error: Unknown variable at line " + i);
						}
					}
					
					nestingLevel++;
				}else {
					if(!skip) {
						variableName = line.substring(6, line.indexOf(";") - 6);
						if((variable = findVariable(variableName, variables)) != null) {
							if(variable.getValue() == 0) {
								skip = true;
								skipLevel = nestingLevel;
							}else {
								int[] loopLine = {nestingLevel, i};
								loopLines.add(loopLine);
							}
						}else {
							System.out.println("Error: Unknown variable at line " + i);
						}
					}
					
					nestingLevel++;
				}
			}else if(line.startsWith("endwhile")) {
				nestingLevel--;
				
				if(!skip) {
					for(int k = loopLines.size() - 1; k >= 0; k--) {
						if(loopLines.get(k)[0] == nestingLevel) {
							i = loopLines.get(k)[1] - 1;
							k = -1;
						}
					}
				}
				else if(nestingLevel == skipLevel) {
					skip = false;
				}
			}else if(line.startsWith("if")) {
				if(line.contains("[")) {
					if(!skip) {
						variableName = line.substring(3, line.indexOf("["));
						
						if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
							index = getIndex(line, arrayVariable, variables);
							if(arrayVariable.getValue(index) == 0) {
								skip = true;
								ifSkip = true;
								ifSkipLevel = ifLevel;
							}
						}else {
							System.out.println("Error: Unknown variable at line " + i);
						}
					}
					
					ifLevel++;
				}else {
					if(!skip) {
						variableName = line.substring(3, line.indexOf(";") - 6);
						if((variable = findVariable(variableName, variables)) != null) {
							if(variable.getValue() == 0) {
								skip = true;
								ifSkip = true;
								ifSkipLevel = ifLevel;
							}
						}else {
							System.out.println("Error: Unknown variable at line " + i);
						}
					}
					
					ifLevel++;
				}
			}else if(line.startsWith("else")) {
				ifLevel--;
				
				if(ifSkip && ifLevel == ifSkipLevel) {
					skip = false;
					ifSkip = false;
				}else if(!skip) {
					skip = true;
					ifSkipLevel = ifLevel;
				}
				
				ifLevel++;
			}else if(line.startsWith("endif")) {
				ifLevel--;
				
				if(skip && ifLevel == ifSkipLevel) {
					skip = false;
					ifSkip = false;
				}
			}else if(line.startsWith("print") && !skip) {
				if(line.contains("\"")){
					System.out.println(line.substring(7, line.indexOf(";") - 1));
				}else {
					if(line.contains("[")) {
						variableName = line.substring(6, line.indexOf("["));
						
						if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
							index = getIndex(line, arrayVariable, variables);
							System.out.println(arrayVariable.getValue(index));
						}else {
							System.out.println("Error: Unknown variable at line " + i);
						}
					}else {
						variableName = line.substring(6, line.indexOf(";"));
						
						if((variable = findVariable(variableName, variables)) != null) {
							System.out.println(variable.getValue());
						}else {
							System.out.println("Error: Unknown variable at line " + i);
						}
					}
				}
			}else if(!line.startsWith("#") && !skip) {
				variableName = line.substring(0, line.indexOf(" "));
				
				if(variableName.contains("[")) {
					variableName = variableName.substring(0, variableName.indexOf("["));
					
					if((arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
						index = getIndex(line, arrayVariable, variables);
						arrayVariable1 = arrayVariable;
						index1 = index;
						variable1 = null;
					}else {
						System.out.println("Error: Unknown variable at line " + i);
						error = true;
					}
				}else {
					if((variable = findVariable(variableName, variables)) != null) {
						variable1 = variable;
						arrayVariable1 = null;
					}else {
						System.out.println("Error: Unknown variable at line " + i);
						error = true;
					}
				}
				
				if(!error && !line.substring(line.indexOf(" ") + 1).startsWith("=")) {
					System.out.println("Error: Missing equals sign at line " + i);
					error = true;
				}else {
					variableName = line.substring(line.indexOf("=") + 2, line.indexOf(" ", line.indexOf("=") + 2));
				}
				
				if(variableName.contains("[")) {
					variableName = variableName.substring(0, variableName.indexOf("["));
					
					if(!error && (arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
						index = getIndex(line, arrayVariable, variables);
						value2 = arrayVariable.getValue(index);
					}else {
						System.out.println("Error: Unknown variable at line " + i);
						error = true;
					}
				}else {
					if(!error && (variable = findVariable(variableName, variables)) != null) {
						value2 = variable.getValue();
					}else {
						System.out.println("Error: Unknown variable at line " + i);
						error = true;
					}
				}
				
				if(!error && line.contains("+")) {
					operation = '+';
				}else if(!error && line.contains("-")) {
					operation = '-';
				}else if(!error && line.contains("*")) {
					operation = '*';
				}else if(!error && line.contains("/")) {
					operation = '/';
				}else if(!error && line.contains("%")) {
					operation = '%';
				}else if(!error){
					System.out.println("Error: Missing operation at line " + i);
					error = true;
				}
				
				if(!error) {
					variableName = line.substring(line.indexOf(operation) + 2, line.indexOf(";"));
				}
				
				if(variableName.contains("[")) {
					variableName = variableName.substring(0, variableName.indexOf("["));
					
					if(!error && (arrayVariable = findArrayVariable(variableName, arrayVariables)) != null) {
						index = getIndex(line, arrayVariable, variables);
						value3 = arrayVariable.getValue(index);
					}else {
						System.out.println("Error: Unknown variable at line " + i);
						error = true;
					}
				}else {
					if(!error && (variable = findVariable(variableName, variables)) != null) {
						value3 = variable.getValue();
					}else {
						System.out.println("Error: Unknown variable at line " + i);
						error = true;
					}
				}
				
				if(variable == null) {
					switch(operation) {
					case '+':
						arrayVariable1.setValue(index1, value2 + value3);
						break;
					case '-':
						arrayVariable1.setValue(index1, value2 - value3);
						break;
					case '*':
						arrayVariable1.setValue(index1, value2 * value3);
						break;
					case '/':
						arrayVariable1.setValue(index1, value2 / value3);
						break;	
					case '%':
						arrayVariable1.setValue(index1, value2 % value3);
						break;	
					}
				}else {
					switch(operation) {
					case '+':
						variable1.setValue(value2 + value3);
						break;
					case '-':
						variable1.setValue(value2 - value3);
						break;
					case '*':
						variable1.setValue(value2 * value3);
						break;
					case '/':
						variable1.setValue(value2 / value3);
						break;	
					case '%':
						variable1.setValue(value2 % value3);
						break;	
					}
				}
				
				error = false;
			}
			
			/*if(!skip) {
				if(!line.startsWith("while") && !line.startsWith("end")) {
					for(int j = 0; j < variables.size(); j++) {
						System.out.print(variables.get(j).getName());
						System.out.print(" = ");
						System.out.println(variables.get(j).getValue());
					}
					
					System.out.println();
				}
			}*/
		}
	}
	
	Variable findVariable(String name, ArrayList<Variable> variables) {
		Variable variable = null;
		
		for(int i = 0; i < variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				variable = variables.get(i);
				i = variables.size();
			}
		}
		
		return variable;
	}
	
	ArrayVariable findArrayVariable(String name, ArrayList<ArrayVariable> arrayVariables) {
		ArrayVariable arrayVariable = null;
		
		for(int i = 0; i < arrayVariables.size(); i++) {
			if(arrayVariables.get(i).getName().equals(name)) {
				arrayVariable = arrayVariables.get(i);
				i = arrayVariables.size();
			}
		}
		
		return arrayVariable;
	}
	
	int getIndex(String line, ArrayVariable arrayVariable, ArrayList<Variable> variables) {
		int index;
		String variableName;
		Variable variable = null;
		
		try {
			index = Integer.parseInt(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
		}catch(NumberFormatException ex){
			variableName = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
			
			for(int i = 0; i < variables.size(); i++) {
				if(variables.get(i).getName().equals(variableName)) {
					variable = variables.get(i);
					i = variables.size();
				}
			}
			
			index = variable.getValue();
		}
		
		return index;
	}
}
