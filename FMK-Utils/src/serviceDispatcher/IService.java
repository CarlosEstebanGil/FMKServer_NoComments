package serviceDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import configuration.ISingletonConfiguration;

public interface IService extends IStandardExecutableService  {

	public abstract void setServiceName(String strServiceName);
	public abstract String getServiceName();
	
		
	public abstract void setConfiguration(ISingletonConfiguration svcConf);

	public abstract void setResources(String strSvcName,
			HttpServletRequest req, HttpServletResponse res) throws Exception;

	//implementaciones por defecto
	public abstract void printResponseHeader() throws Exception; //

	public abstract void printResponseFooter() throws Exception;

	public abstract void fillService() throws Exception;

	/*public abstract void execute(String strSvcName, HttpServletRequest req,
			HttpServletResponse res) throws Exception;*/

	public abstract HttpServletRequest getReq();

	public abstract void setReq(HttpServletRequest req);

	public abstract HttpServletResponse getRes();

	public abstract void setRes(HttpServletResponse res);
	
	//new 2014 xa setearle el nombre de una. 
	public abstract void initServiceImpl() throws Exception; // put here all initialization.. 

}