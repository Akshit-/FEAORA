package model;

public class OutputParams<T> {

	public T value;
	public String typeString;
	
	public OutputParams(T value, String typeString) {
		this.value = value;
		this.typeString = typeString;
	}
}
