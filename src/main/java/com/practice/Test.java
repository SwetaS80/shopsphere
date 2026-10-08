package com.practice;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {
//	void show(int x) {
//		System.out.println(x); //op 65 
//		System.out.println("int"); // op int
//	}
//
//	void show(double x) {
//		System.out.println("double");
//	}
//
//	public static void main(String[] args) {
//		Test t = new Test();
//		t.show('A');
//	}
	
	
	/*
	 * Find the frequency of each character in a string and print the character
	 * along with its frequency. using java 8 stream.
	 */
	
	
	public static void main(String[] args) {
		
		String str = "Swetamayee Sahoo";
		
		str.chars().mapToObj(c -> (char) c).filter(c -> !Character.isWhitespace(c))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.forEach((l, v) -> System.out.println(l + " " + v));
		
		
	}
	
	
	

}
