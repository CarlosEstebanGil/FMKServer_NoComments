package fmk_core_server;
// NO SE USA.
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import serviceDispatcher.IService;
import configuration.ISingletonConfiguration;


//Esta interfase solo agrega cosas de utilidad. NO SE USA POR AHORA!.

public interface IStandardServiceProvider extends IService {
	//void setRequest (HttpServletRequest req);
	
	//20130311 void setServiceName(String strServiceName);
	//Map<String, String []> getParamsFromReq();
	void obtainParamsFromReq();
	 //new: Map<String, String[]> getRequestParams() ;
	
	//getArgsToFillForServiceFromPropertiesConfForThatService()
	//Macthean los paramNames from httpRequest o contienen valores defifidos en el server 
	//Map<String, String []> getArgsToFill(String strServiceName)
	
	//void getArgsToFill(String strServiceName);
	void obtainParamsFromSvcDef(); //(String strServiceName);
	 //new: Map<String, String[]> getServiceParams(String strServiceName); //
//	
	//void fillService (); 
	//20130311 void fillService () throws Exception; 
	//Extended void execute (req, res) method: 
	//execute(...) (implementa executable ponele?)

	
	//resultToXml 	//writteOutput ( ambos pasos en 1 de ultima ) 

	//20130311void writeResult(); //usa la coll u obj simple o rs , mientas lo recorre va armando (y escribiendo)
						//el xml de resultado, en el responseHttp obj (para esto usa el iConverter  xa Xml

	//20130311 void setConfiguration(ISingletonConfiguration svcConf);
	

}
