package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	StringTokenizer token = new StringTokenizer(expr, delims);
    	
    	while (token.hasMoreTokens()) {
    		String str = token.nextToken();
    		int indexCheck = str.length() + expr.indexOf(str);
    		
    		if (indexCheck < expr.length()) {
				if (expr.charAt(indexCheck) == '[') {
					Array arr = new Array(str);
					if (!(arrays.contains(arr))) {
						arrays.add(arr);
					}
				} else if (isInteger(str)) {
					continue;
				} else {
					Variable var = new Variable(str);
					if (!(vars.contains(var))) {
						vars.add(var);
					}
				}
			} else if (indexCheck == expr.length()) {
				if (isInteger(str)) {
					continue;
				} else {
					Variable var = new Variable(str);
					if (!(vars.contains(var))) {
						vars.add(var);
					}
				}
			}
    	}
    	
    	System.out.println(vars);
    	System.out.println(arrays);
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation

    	Stack<Float> value = new Stack<>();
    	Stack<String> operator = new Stack<>();
    	StringTokenizer str = new StringTokenizer(expr, delims, true);
    	while(str.hasMoreTokens()) {
    		String current = str.nextToken();
    		if(current.equals(" ") || current.equals("\t")) {
    		} 
    		else if(isInteger(current)) {//adds constant to stack
    			value.push(Float.parseFloat(current));
    			continue;
    		} else if(isVariable(vars, current) != -1) {
    			value.push((float)vars.get(isVariable(vars,current)).value);
    		} else if(current.equals("(")) {
    			operator.push("(");
    		} else if(current.equals(")")){
    			while(operator.peek() != "("){
    				float second = value.pop();
    				float first = value.pop();
    				String oper = operator.pop();
    				value.push(checkSign(second, first, oper));
    			}
    			operator.pop();
    		} else if(current.equals("[")){
    			operator.push("[");
    		} else if(isArray(arrays, current) != -1){
    			operator.push(current);
    		} else if(current.equals("]")) {
    			while(operator.peek() != "["){
    				float second = value.pop();
    				float first = value.pop();
    				String oper = operator.pop();
    				value.push(checkSign(second, first, oper));
    			}
    			operator.pop();
    			String ArrayName = operator.pop();
    			float ArrayIndex = value.pop();
    			value.push(getValue(arrays, ArrayIndex, ArrayName));
    		} else if(current.equals("+") || current.equals("-") || current.equals("*") || current.equals("/")){
    			while(!(operator.isEmpty()) && ifNeedPemda(current, operator.peek())){
    				float second = value.pop();
    				float first = value.pop();
    				String oper = operator.pop();
    				value.push(checkSign(second, first, oper));
			}
    			operator.push(current);
    		}
    	}
    	while(operator.isEmpty() != true) {
    		float second = value.pop();
			float first = value.pop();
			String oper = operator.pop();
			value.push(checkSign(second, first, oper));
			
    	}
    	return value.pop();
    }
    
    private static float checkSign(float second, float first, String oper) {
    	float result = 0;
    	switch(oper){
		case "+": 
			result = first + second;
			break;
		case "-":
			result = first - second;
			break;
		case "*":
			result = first * second;
			break;
		case "/":
			result = first / second;
			break;
    	}
    	return result;
    }
    
    private static boolean isInteger(String number){
        try
        {
           Integer.parseInt(number);
           return true;
        }
        catch(Exception e)
        {
           return false;
        }
     }
     private static boolean ifNeedPemda(String current, String top){
     	if((current.equals("*") || current.equals("/")) && (top.equals("+") || top.equals("-"))) {
     		return false;
     	}if(top.equals("(")) {
     		return false;
     	}if(top.equals("[")){
     		return false;
     	}
     	return true;
     	}
     
     private static int isVariable(ArrayList<Variable> vars, String name){
     	for(int i = 0; i < vars.size(); i++) {
     		if(vars.get(i).name.equals(name)) {
     			return i;
     		}
     	}
     	return -1;
     		
     }
     private static int isArray(ArrayList<Array> arrays, String name){
     	for (int i = 0; i < arrays.size(); i++) {
     		if(arrays.get(i).name.equals(name)) {
     			return i;
     		}
     	}
     	return -1;
     }
     private static float getValue(ArrayList<Array> arrays, float index, String name) {
     	int firstIndex= (int) index;
     	float result = -1;
     	for(int i = 0; i < arrays.size(); i++){
     		if(arrays.get(i).name.equals(name)){
     			Array array1 = arrays.get(i);
     			return (float)array1.values[firstIndex];
     		}
     	}
     	return result;
     }
}
