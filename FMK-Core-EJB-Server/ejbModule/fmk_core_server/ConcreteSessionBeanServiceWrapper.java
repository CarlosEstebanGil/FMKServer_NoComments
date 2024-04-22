package fmk_core_server;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//NOOOO No puedo injectar un bean con @ejb en unca clase comun que no es ni servlet de jboss managed ni ejb bean !

/*public class ConcreteSessionBeanServiceWrapper  implements IStandardExecutableService {
	@EJB(name="GenericSelectDBServiceBean") //lookup="java:global/EAPProjForPruebaLocalBean/MiPrimerEEEJB/HolaMundo!edu.curso.ee.sb.HolaMundoLocal"	)
	GenericSelectDBService genericSelectDBServiceBean;   

    public ConcreteSessionBeanServiceWrapper() { // //
        super();
        // TODO Auto-generated constructor stub
    }
	@Override
	public void execute(String strSvcName, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		genericSelectDBServiceBean.execute(strSvcName, req, res);
		
	}
	@Override
	public IStandardExecutableService getImplementacion() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    

}
*/