/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ndsr.trash;

/**
 *
 * @author lkufel
 */
class VarGreeter {

	public static void printGreeting(String... names) {
		System.out.println("len = " + names.length);
		System.out.println(names[0]);
		for (String n : names) {
			System.out.println("Hello " + n + ". ");
		}
	}

	public static void main(String[] args) {
		printGreeting("Paul", "Sue");
		System.out.println("asdasdasds");
		printGreeting();
	}
}
