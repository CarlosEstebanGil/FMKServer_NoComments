package fmk_core_client;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;



import serviceDispatcher.AbstractServiceDispatcher;
//import fmk_core_server.AbstractServiceDispatcher;

/**
 * @author carlos Esteban gil
 * corre la impl de dispatcher del server pero localmente y x eso funciona el reflection
 * corre como session ejb bean , el cual es injectado localmente en el servlet locator del modulo cliente
 * que escucha las peticiones desde los clientes web via http y utiliza esta clase para instanciar , llenar y 
 * ejecutar el servicio indicado como parametro valiendose de la conf en file properties para dicho servicio 
 */

//MainApp y Session bean de prueba no sirven ( borrar ) mepa.. 
//le meto la capacidad de bean (sb, corre manejado en el app server) 

@Stateless  (name="dispatcherSessionBean", mappedName="dispatcherSessionBean") //
@LocalBean
public class ServiceDispatcher extends AbstractServiceDispatcher {
	
	//(*viene) solo x ahora borrar..
	/*@Override
	public void setDs(DataSource ds) {
		// TODO Auto-generated method stub
		
	} */
	//abstractSvcDisp no tiene methodos abstract pero me obliga a extenderla x q la clase es abstract

	//extends (uses) execute from core.
	
	
	private void fakeForModif1(){};
}
