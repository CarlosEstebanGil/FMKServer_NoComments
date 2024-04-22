package Entities;

import java.util.ArrayList;

public class Operation {
	
	private int id;
	
	private int numop;
	
	private double totfob;
	
	private double totalOperations;
	
	private double totalArticles;
	
	private double amountExpense;
	
	private double amountProration;

	private ArrayList<Expense> expenses;
	
	private ArrayList<Article> articles;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumop() {
		return numop;
	}

	public void setNumop(int numop) {
		this.numop = numop;
	}

	public double getTotfob() {
		return totfob;
	}

	public void setTotfob(double totfob) {
		this.totfob = totfob;
	}

	public double getTotalOperations() {
		return totalOperations;
	}

	public void setTotalOperations(double totalOperations) {
		this.totalOperations = totalOperations;
	}

	public double getTotalArticles() {
		return totalArticles;
	}

	public void setTotalArticles(double totalArticles) {
		this.totalArticles = totalArticles;
	}

	public double getAmountExpense() {
		return amountExpense;
	}

	public void setAmountExpense(double amountExpense) {
		this.amountExpense = amountExpense;
	}

	public double getAmountProration() {
		return amountProration;
	}

	public void setAmountProration(double amountProration) {
		this.amountProration = amountProration;
	}

	public ArrayList<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(ArrayList<Expense> expenses) {
		this.expenses = expenses;
	}

	public ArrayList<Article> getArticles() {
		return articles;
	}

	public void setArticles(ArrayList<Article> articles) {
		this.articles = articles;
	}

}
