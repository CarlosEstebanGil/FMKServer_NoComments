package Entities;

public class Expense {
	
	private int id;
	
	private int idInvoiceExpense;
	
	private double amountExpense;
	
	private double amountProration;
	
	private double porcentageExpense;
	
	private double porcentageProration;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdInvoiceExpense() {
		return idInvoiceExpense;
	}

	public void setIdInvoiceExpense(int idInvoiceExpense) {
		this.idInvoiceExpense = idInvoiceExpense;
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

	public double getPorcentageExpense() {
		return porcentageExpense;
	}

	public void setPorcentageExpense(double porcentageExpense) {
		this.porcentageExpense = porcentageExpense;
	}

	public double getPorcentageProration() {
		return porcentageProration;
	}

	public void setPorcentageProration(double porcentageProration) {
		this.porcentageProration = porcentageProration;
	}

}
