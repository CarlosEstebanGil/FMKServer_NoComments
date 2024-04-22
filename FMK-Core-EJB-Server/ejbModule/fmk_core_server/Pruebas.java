package fmk_core_server;

import serviceDispatcher.IStandardExecutableService;

public class Pruebas {

	/**
	 * @param args
	 */
	public static void main(String[] args) { //
		// TODO Auto-generated method stub
		//SessionBeanDePruebaEnClientEJB
		
		//Prueba 2: Fake dispatcher:
		//Class klass = null;
		//try {
			//el string es variable (se levanta desde el propertie
		//	klass = Class. forName(""); //o por jndi ?? x @ejb!?  o.0
			
			/*!!!!!!!!!SessionBeanDePrueba -> esto es especifico y se redefine en el cliente
			8es lo que cambia y q no forma parte del esqueleto abstracto del dispatcher!!!!!!!! */
			
			//claseAinstanciar = klass.newInstance();
			
			//No!, esperá, si se referencia con iExecutable está todo bien !
			Class klass = null;
			try {
				klass = Class.forName("moduloCli.package.claseAinstanciar"); //o por jndi ?? x @ejb!?  o.0
				IStandardExecutableService objRef= (IStandardExecutableService) klass.newInstance();
				
				//claseAinstanciar = klass.newInstance();
			}
			 catch (Exception e){
				e.getStackTrace();
			}
			
	}

}
