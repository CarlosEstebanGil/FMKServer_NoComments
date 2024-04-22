package fmk_core_server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import output.Service;
import serviceDispatcher.IService;
import configuration.ISingletonConfiguration;
import configuration.SingletonFrameworkConfiguration;
import configuration.SingletonServicesConfiguration;

/*
 * 20110311 Nueva Clase ppal en la jerarquia de servicios, servirá como clase base para todo lo común entre
 * DBServices y WSServices. Implementará IService
 */
////20p15p01p14 added to generate dependency over fmkutils ctrl 
public abstract class AbstractService extends Service implements IService { //
	static final Logger logger = Logger.getLogger(AbstractService.class);
	
	protected HttpServletRequest req;
	protected HttpServletResponse res;
	
	protected PrintWriter o =  null;
	
	protected String strServiceName = null; //xa que lo vean las clases q la extienden !
	
	static protected String CONF_FILENAME   = "services.properties";
	protected static ISingletonConfiguration svcConfs = null;  //si no era protected ent la q la heredaba
																//no podia accederla / referenciarla!!!!saber!
	protected static String datlim = "2015-04-01";
	
	IService wrappedService=null;
	
	@Override 
	public void initServiceImpl() throws Exception {};

	public IService getWrappedService() {
		return wrappedService;
	}


	public void setWrappedService(IService wrappedService) {
		this.wrappedService = wrappedService;
	}


	/* (non-Javadoc)
	 * @see fmk_core_server.IService#setServiceName(java.lang.String)
	 */
	@Override
	public void setServiceName(String strServiceName) {
		// TODO Auto-generated method stub
		this.strServiceName = strServiceName;
	}

	@Override
	public String getServiceName()  {
		// TODO Auto-generated method stub
		return this.strServiceName;
	}
	
	/* (non-Javadoc)
	 * @see fmk_core_server.IService#setConfiguration(configuration.ISingletonConfiguration)
	 */
	@Override
	public void setConfiguration(ISingletonConfiguration svcConf) {
		// TODO Auto-generated method stub
		this.svcConfs = svcConf;
	}
	
	/* (non-Javadoc)
	 * @see fmk_core_server.IService#setResources(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void setResources(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		if (	strSvcName == null || strSvcName.length()==0 ||
				req == null || res == null ) {
			String errMsge = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.setResources.msges.throw.gralPurposeFail");
			logger.fatal(errMsge);
			throw new Exception(errMsge);
		}else {
			setConfiguration( SingletonServicesConfiguration.getInstance() );
			setServiceName( strSvcName);
			setReq( req);
			setRes( res);
			
			//2014 o=res.getWriter();
			setWriter(res);
		}
	} 


	//public abstract void printResponseHeader() throws Exception;

	//implementaciones por defecto
	/* (non-Javadoc)
	 * @see fmk_core_server.IService#printResponseHeader()
	 */

	@Override
	public void printResponseHeader() throws Exception {
		//TODO obtener el tag "root" from conf file
		
		//o.print("<root>"); //sacar hardcoded tag "root" a arh conf app gral . properties..
		
		//20130319  (ambas lineas van? ) 
		String strRootTagName= SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.global.xmlRootTag.name") ;
		o.print("<" + strRootTagName + " >");
	}
	
	
	/* (non-Javadoc)
	 * @see fmk_core_server.IService#printResponseFooter()
	 */
	
	@Override
	public void printResponseFooter() throws Exception {
		// o.print("</root>");  //20130315
		
		//20130319  (ambas lineas van? ) 
		String strRootTagName= SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.global.xmlRootTag.name") ;
		o.print("</" + strRootTagName + " >");

	}
	
	/* (non-Javadoc)
	 * @see fmk_core_server.IService#fillService()
	 */
	@Override
	public void fillService() throws Exception{};
	
	/* (non-Javadoc)
	 * @see fmk_core_server.IService#execute(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception{};

	/* (non-Javadoc)
	 * @see fmk_core_server.IService#getReq()
	 */
	@Override
	public HttpServletRequest getReq() {
		return req;
	}

	/* (non-Javadoc)
	 * @see fmk_core_server.IService#setReq(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void setReq(HttpServletRequest req) {
		this.req = req;
	}

	/* (non-Javadoc)
	 * @see fmk_core_server.IService#getRes()
	 */
	@Override
	public HttpServletResponse getRes() {
		return res;
	}

	/* (non-Javadoc)
	 * @see fmk_core_server.IService#setRes(javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void setRes(HttpServletResponse res) {
		this.res = res;
	}
	
	public void setWriter(HttpServletResponse res) throws Exception {
		this.o= res.getWriter();
	}
	private void fakeForModif1(){};
}
