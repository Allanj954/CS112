package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {

		if (poly1 == null) {
			return poly2;
		}
		if (poly2 == null) {
			return poly1;
		}
		
		Node firstP = poly1;
		Node secondP = poly2;
		Node thirdP = null;
		Node front = null;
		float coe = 0;
		
		while (firstP != null && secondP != null) {
			Node t = new Node (firstP.term.coeff, firstP.term.degree, null);
			if (firstP.term.degree == secondP.term.degree) {
				coe = (float)firstP.term.coeff + secondP.term.coeff;
				t.term.coeff = coe;
				firstP = firstP.next;
				secondP = secondP.next;
			}
			
			else if (firstP.term.degree < secondP.term.degree) {
				coe = firstP.term.coeff;
				firstP = firstP.next;
			}
			
			else if (firstP.term.degree > secondP.term.degree ) {
				t.term.coeff = secondP.term.coeff;
				t.term.degree = secondP.term.degree;
				coe = secondP.term.coeff;
				secondP = secondP.next;
				
			}
			
			if (coe != 0 ) {
				if (front == null) {
					front = t;
					thirdP = front;
				} 
				if (front != null) {
					thirdP.next = t;
					thirdP = thirdP.next;
					}
				}
			}
		
		while (firstP != null) {
			if (front == null) {
				front = new Node (firstP.term.coeff, firstP.term.degree, null);
				thirdP = front;
			} else {
				thirdP.next = new Node (firstP.term.coeff, firstP.term.degree, null);;
				thirdP = thirdP.next;
			} 
			firstP = firstP.next;
			
		}
		
		while (secondP != null) {
			if (front == null) {
				front = new Node (secondP.term.coeff, secondP.term.degree, null);;
				thirdP = front;
			} else {
				thirdP.next = new Node (secondP.term.coeff, secondP.term.degree, null);
				thirdP = thirdP.next;
			} 
			secondP = secondP.next;
		}
		return front;
		
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		
		if (poly1 == null || poly2 == null) {
			return null;
		}
		
		Node firstP = poly1;
		Node secondP = poly2;
		Node finalP = null;
		for (Node i = firstP; i != null; i = i.next) {
			Node front = null;
			Node finalTemp = null;
			for (Node a = secondP; a != null; a = a.next) {
				float coe = i.term.coeff * a.term.coeff;
				int degree = i.term.degree + a.term.degree;
				Node localTemp = new Node(coe, degree, null);
				if (finalTemp == null) {
					front = localTemp;
					finalTemp = front;
				} else {
					finalTemp.next = localTemp;
					finalTemp = finalTemp.next;
					}
				
				}
			finalP = Polynomial.add(finalP, front);
			}
		return finalP;
		}

		

	
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float finalEval = 0;
		for (Node i = poly; i != null; i = i.next) {
			finalEval += i.term.coeff * (Math.pow(x, i.term.degree));
		}
		
		return finalEval;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
