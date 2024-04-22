package business;

class Celdas {
	public Object Valor;
	public int Escribeenexcel;
	  
	public Celdas(Object Valor, int Escribeenexcel) {
	    this.Valor = Valor;
	    this.Escribeenexcel = Escribeenexcel;
	    }
	
	public Object getValor() {
		return this.Valor;
	}

	public void setValor(Object Valor) {
		this.Valor = Valor;
	}

	public int getEscribeenexcel() {
		return this.Escribeenexcel;
	}

	public void setEscribeenexcel(int Escribeenexcel) {
		this.Escribeenexcel = Escribeenexcel;
	}

}