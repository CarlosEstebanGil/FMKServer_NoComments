package Entities;

import java.util.ArrayList;

public class Invoice {
	
	private int id;
	
	private int typeProration;
	
	private double totalAmountExpense;
	
	private int quantityExpenses;
	
	private double totalOperations;	
	
	private double amountProration;
	
	private ArrayList<Operation> operations;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTotalAmountExpense() {
		return totalAmountExpense;
	}

	public void setTotalAmountExpense(double totalAmountExpense) {
		this.totalAmountExpense = totalAmountExpense;
	}

	public int getQuantityExpenses() {
		return quantityExpenses;
	}

	public void setQuantityExpenses(int quantityExpenses) {
		this.quantityExpenses = quantityExpenses;
	}

	public double getTotalOperations() {
		return totalOperations;
	}

	public void setTotalOperations(double totalOperations) {
		this.totalOperations = totalOperations;
	}

	public int getTypeProration() {
		return typeProration;
	}

	public void setTypeProration(int typeProration) {
		this.typeProration = typeProration;
	}

	public double getAmountProration() {
		return amountProration;
	}

	public void setAmountProration(double amountProration) {
		this.amountProration = amountProration;
	}

	public ArrayList<Operation> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<Operation> operations) {
		this.operations = operations;
	}

}
