package fmk_core_client;

import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;






//import fmk_core_server.AbstractMultiSentenceDBService;
import fmk_core_server.AbstractMultiSentenceDBServiceConverter;
//import fmk_core_server.AbstractMultiSentenceDBServiceWithTrans;

/**
 *AUTHOR: CGIL
 *
 */

@Stateless(name="GetServicesDefinitionBean", mappedName="GetServicesDefinitionBean")
@LocalBean
public class GetServicesDefinition extends AbstractMultiSentenceDBServiceConverter {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;   


    public GetServicesDefinition() {
    }
    
    @Override
  	public Connection getConnection() throws Exception {
  		Connection conn = null;
  		try{
  			conn = this.ds.getConnection();
  		}catch(Exception e){
  		//	logger.fatal(e.getMessage());
  			throw new Exception(e.getMessage());
  		}
  		return conn;
  	}

    @Override 
  	public void executionAlgorithm() throws Exception {
    	try {
    		
    		//20140901 : saco la sig linea y los levanto a mano desde el req.getParameter..
    		//				( esto se hace en la func interna buildURL... ) 
    		//fillLogicVars (); //deja hlogicalParamVars cargado con strKey varParamName y oValue 
    		
    		
    		strSQL="SELECT 	`svc_fantasy_name`, `svc_real_name`, `appname`, `description`, `local` FROM `services` ";
    		
    		ps = conn.prepareStatement(strSQL);
   		 	
			rs = ps.executeQuery();  
			
		
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }
    
	@Override
	public void writeResponse() throws Exception { //
		//op1) standardResult writeResultXML("bool"); //
		//op2) con Converter: converter.set(rs).. etc
		//op3) custom xml output!:
		int i;
		 
		try {
			i=0; 
			while (rs.next()) {
				i++;
				if (i==1) {
					o.print('<'); o.print("root");o.print('>');
				}
				
				//http://localhost:8080/FMK-DynamicWEB/ServiceLocator?svc=exampleDelService 
				//ó
				//data/conf/gastos.xml 			        --> 					(local=true)
				
				
				o.print('<');  o.print( rs.getString("svc_fantasy_name") ); o.print('>');
				o.print(escapeURL(buildURL()));
				o.print("</");  o.print( rs.getString("svc_fantasy_name") ); o.print('>');
				
			}
			if (i>0) {
				o.print("</"); o.print("root");o.print('>');
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			
		}
	}

    protected String buildURL() throws Exception {
    	String r=null;
    	String server= null;
    	String port=null;
    	try {
	    		if (rs.getInt("local")==0) {
	    		//las 2 sig lineas las cambio x levantar a mano los param asi no 
	    		//tengo que definir el servicio en el properties.
	    		//server= getStringVarValue("server");     
	    		//port= getStringVarValue("port");     
	    		
		    		server= req.getParameter("server");     
		    		port= req.getParameter("port");     
		    		
	    		r= "http://" + server + ":" + port  + "/" + rs.getString("appname") + "?svc=" + rs.getString("svc_real_name");
    		}
    		else {
    			r=rs.getString("appname") + rs.getString("svc_real_name");
    		}
    		return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }
    
    protected String escapeURL(String strURL) throws Exception {
    	try {
    		// x ahora no le efectúo ningun proceso:
    		return strURL;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }
    private void fakeForModif1(){};
}
