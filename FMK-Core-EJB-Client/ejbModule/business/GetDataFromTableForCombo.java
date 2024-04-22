package business;

import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


import configuration.SingletonFrameworkConfiguration;
import configuration.SingletonServicesConfiguration;
import fmk_core_server.AbstractMultiSentenceDBServiceConverter;

/**
 *AUTHOR: CGIL
 *
 */

@Stateless(name="GetDataFromTableForCombo", mappedName="GetDataFromTableForCombo") //copy paste
@LocalBean // copy paste
public class GetDataFromTableForCombo extends AbstractMultiSentenceDBServiceConverter {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;  //copy paste

static final Logger logger = Logger.getLogger(GetDataFromTableForCombo.class);

		@Override //copy paste 
	  	public Connection getConnection() throws Exception { //copy paste
	  		Connection conn = null;
	  		try{
	  			conn = this.ds.getConnection();
	  		}catch(Exception e){
	  		//	logger.fatal(e.getMessage());
	  			throw new Exception(e.getMessage());
	  		}
	  		return conn;
	  	}

	    @Override //copy paste 
	  	public void executionAlgorithm() throws Exception { //copy paste
	    try {
	    	final String CMB_ID_PROP_NAME  
    		= SingletonServicesConfiguration
    							.getInstance()
    							.getPropValue
    							("getDataFromTableForCombo.cte.1.name.ID");

	    	final String SVC_NAME="getDataFromTableForCombo";  
	    			
	    	final String STR_GRAL_CTE_KEY = SingletonFrameworkConfiguration.
										getInstance().
										getPropValue("Framework.global.services.CONSTANT_GRAL_CTE_KEY_NAME");
	    	
	    	
	    	final String STR_GRAL_CTE_SUBKEY_NAME = SingletonFrameworkConfiguration.
	    										getInstance().
	    										getPropValue("Framework.global.services.CONSTANT_SUBKEY_CTE_NAME");

	    	final String ORDER= "2"; 
	    	
	    	final String CTE_NAME = "VALUE";
	
	    	final String CMB_VALUE_PROP_NAME 
    		= SingletonServicesConfiguration
    						.getInstance()
    						.getPropValue(SVC_NAME+"."+STR_GRAL_CTE_KEY+"."+ORDER+"."+STR_GRAL_CTE_SUBKEY_NAME+"."+CTE_NAME);
    		
	    	String tableName= getStringVarValue("tableName");           
	    	String idFieldName=getStringVarValue("idFieldName");
	    	String valueFieldName=getStringVarValue("valueFieldName");
	    	String orderFieldName=getStringVarValue("orderFieldName"); 
	    	String orderType=getStringVarValue("orderType"); 
	    	String whereCondition=getStringVarValue("whereCondition"); 
	    	
	    	String strError = null;
	    	
	    	if ( ( tableName == null ) || (tableName.length()==0 ) ) {
	    		strError = "argumento invalido";
	    		logger.error(strError);
				throw new Exception( strError); 
	    	}
	    	
	    	if ( ( idFieldName == null ) || (idFieldName.length()==0 ) ) {
	    		strError = "argumento invalido";
	    		logger.error(strError);
				throw new Exception( strError);
	    	}
	    	
	     	if ( ( valueFieldName == null ) || (valueFieldName.length()==0 ) ) {
	    		strError = "argumento invalido";
	    		logger.error(strError);
				throw new Exception( strError); 
	    	}
	    	
	     	if ( ( orderFieldName == null ) || (orderFieldName.length()==0 ) ) {
	     		orderFieldName=valueFieldName; 
	     	}
	     	
	      	if ( ( orderType == null ) || (orderType.length()==0 ) ) {
	     		orderFieldName="ASC"; 
	     	}
	     	
	      	String strWhereSentence="";
	      	if (whereCondition!=null) {
	      		strWhereSentence=" WHERE " + whereCondition + " ";
	      	}
	    	StringBuffer sbSQL = new StringBuffer();
	    		sbSQL	.append("SELECT ")
	    				.append("`"+idFieldName+"`")
	    				.append(" AS " )
	    				.append( "`"+ CMB_ID_PROP_NAME+"`")
	    				.append(" , ")
	    				.append("`"+valueFieldName+"`")
	    				.append(" AS " )
	    				.append( "`"+CMB_VALUE_PROP_NAME+"`")
	    				.append(" FROM ")
	    				.append("`"+tableName+"`")
	    				.append( " " )  
	    				.append(strWhereSentence)
	    				.append(" ORDER BY " )
	    				.append("`"+orderFieldName+"`")
	    				.append(" ")
	    				.append( orderType); 
	    		
	    	ps = conn.prepareStatement(sbSQL.toString());
	    		
	    	rs = ps.executeQuery();
	    	
		 } catch (Exception e) {
			 result = false;
			 xmlMsge="Exception" + e.getMessage();
			 logger.fatal(e.getMessage());
			 error = true; 
		 }	
	    	 
	    }	
}
