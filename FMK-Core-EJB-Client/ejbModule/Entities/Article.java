package Entities;

import java.util.ArrayList;

public class Article {
	
	private int tripli;
	
	//private int artic;
	private String artic;
	
	private double fobuni;
	
	private ArrayList<Expense> expenses;

	public int getTripli() {
		return tripli;
	}

	public void setTripli(int tripli) {
		this.tripli = tripli;
	}

	/*public int getArtic() {
		return artic;
	}

	public void setArtic(int artic) {
		this.artic = artic;
	}*/
	public String getArtic() { //ceg 20141201
		return artic;
	}

	public void setArtic(String artic) {
		this.artic = artic;
	}


	public double getFobuni() {
		return fobuni;
	}

	public void setFobuni(double fobuni) {
		this.fobuni = fobuni;
	}

	public ArrayList<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(ArrayList<Expense> expenses) {
		this.expenses = expenses;
	}


}
