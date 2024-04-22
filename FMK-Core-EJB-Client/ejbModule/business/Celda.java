package business;

public class Celda {		
	private Object valor;
	private int escribeEnExcel;
	
	public Celda(Object valorCelda, int enExcel){
		this.setValor(valorCelda);
		this.setEscribeEnExcel(enExcel);
	}
	
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}
	public int getEscribeEnExcel() {
		return escribeEnExcel;
	}
	public void setEscribeEnExcel(int escribeEnExcel) {
		this.escribeEnExcel = escribeEnExcel;
	}
}