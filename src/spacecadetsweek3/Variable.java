package spacecadetsweek3;

public class Variable {
	String name;
	int value;
	
	public Variable(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public void increment() {
		value += 1;
	}
	
	public void decrement() {
		value -= 1;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
}
