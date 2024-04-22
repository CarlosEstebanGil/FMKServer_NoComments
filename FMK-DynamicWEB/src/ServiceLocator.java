 
import java.io.IOException;
 


import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
 

import org.apache.log4j.Logger;

 

import exceptions.ApplicationException;
import fmk_core_client.ServiceDispatcher;
 


 
@WebServlet("/ServiceLocator")
public class ServiceLocator extends HttpServlet {
	@Resource UserTransaction tx;
	static final Logger log = Logger.getLogger(ServiceLocator.class);
	
	private static final long serialVersionUID = 1L;
	
	 
	@EJB(name="dispatcherSessionBean") 
	ServiceDispatcher serviceDispatcherBean; 
       
   
    public ServiceLocator() {
        super();
        // TODO Auto-generated constructor stub
    }
   
    
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		super.service(req, res);
		
		res.setContentType("text/xml;charset=ISO-8859-1");
		 
		
		
		String serviceKey = req.getParameter("svc");
		 
		if (serviceKey == null)
		{
			String strError = "Error interno: No se recibió el parámetro svc";
			//log.error(strError);
			throw new ServletException(strError); 
		}
		
		try{
			 
			tx.begin(); 
			
			
			serviceDispatcherBean.execute( serviceKey, req, res); 		
			
			tx.commit();
		}catch(Exception e){
			try {
				tx.rollback();
			}catch(Exception e2) {
				 
				logger(e2); //
				throw new ServletException(e2.getMessage());
			}
			 
			logger(e); 
			
		 
			
			throw new ServletException(e.getMessage()); //
		} finally {
			
		}
		 
	}

	/**
	 *  
	 *
	 
	 *  
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	private void logger(Exception e) {
		try {
		if ( ApplicationException.hastackTrace(e))
			log.fatal(e.getMessage());	 
	} catch (Exception e2) {
	}	 		
	}
	 
	
	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	private void fakeForModif1(){};
}
