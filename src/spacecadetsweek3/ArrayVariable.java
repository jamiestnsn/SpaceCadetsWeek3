package spacecadetsweek3;

public class ArrayVariable {
	String name;
	int length;
	int[] elements;
	
	public ArrayVariable(String name, int length) {
		this.name = name;
		this.length = length;
		elements = new int[length];
		
		for(int i = 0; i < length; i++) {
			elements[i] = 0;
		}
	}
	
	public void increment(int i) {
		elements[i] += 1;
	}
	
	public void decrement(int i) {
		elements[i] -= 1;
	}
	
	public int getValue(int i) {
		return elements[i];
	}
	
	public void setValue(int i, int value) {
		elements[i] = value;
	}
	
	public String getName() {
		return name;
	}
}
