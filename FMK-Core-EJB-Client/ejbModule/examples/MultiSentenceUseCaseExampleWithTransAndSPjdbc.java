package examples;

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

@Stateless(name="ExampleUseCaseTransAndSPDBServiceBean", mappedName="ExampleUseCaseTransAndSPDBServiceBean")
@LocalBean
public class MultiSentenceUseCaseExampleWithTransAndSPjdbc extends AbstractMultiSentenceDBServiceConverter {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;   
	
    public MultiSentenceUseCaseExampleWithTransAndSPjdbc() {
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
	public void writeResponse() throws Exception {
		writeResultXML("bool"); // 							 
	}
	
    @Override 
  	public void executionAlgorithm() throws Exception {
		try {
				
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
    }
}
