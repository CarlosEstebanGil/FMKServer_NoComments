package examples;

import java.sql.Connection;
import java.sql.PreparedStatement;

import fmk_core_server.AbstractDBService;
import fmk_core_server.AbstractMultiSentenceDBService;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;
import javax.sql.DataSource;

//import org.apache.log4j.Logger;






import org.jboss.ejb3.annotation.Clustered;

import configuration.SingletonServicesConfiguration;

/**
 *AUTHOR: CGIL
 *
 */

@Stateless(name="stockAvailable", mappedName="stockAvailable")
// @TransactionManagement (TransactionManagementType.CONTAINER)  //cgil: 2014 
@LocalBean
@Clustered
@Named
public class stockAvailable extends AbstractMultiSentenceDBService {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;   

//20141015 prueba dejar 1 solo ds x caida conn: @Resource(mappedName="java:jboss/datasources/MyDS94") DataSource ds94; //2014

//20141015 prueba dejar 1 solo ds x caida conn: Connection conn94;

	//static final Logger logger = Logger.getLogger(stockAvailable.class);
    /**
     * @see AbstractDBService#AbstractDBService()
     */
    public stockAvailable() {
        super();
        // TODO Auto-generated constructor stub
    }

    //-----------Standard Session, allways the same (copy Paste) ----------
    @Override
  	public Connection getConnection() throws Exception {
  		Connection conn = null;
  		try{
  			conn = this.ds.getConnection();
  		//20141015 prueba dejar 1 solo ds x caida conn: conn94=ds94.getConnection();
  		}catch(Exception e){
  		//	logger.fatal(e.getMessage());
  			throw new Exception(e.getMessage());
  		}
  		return conn;
  	}
    //------------------------Standard Session, allways the same-------------------------
    
    
    //--The variable part!:  THE ALGORITHM !	(Customize) 	-----------------
    //--------------------	 ------------- -
    
    @Override 
	public void executionAlgorithm() throws Exception {
  				
		final String QRY_SEL_PERS="QRY_SEL_PERS";
		final String QRY_INS_PERS="QRY_INS_PERS";
		final String QRY_UPD_HIST="QRY_UPD_HIST";
	 
		final int MAYORIA_EDAD = 18;

		boolean mayorDeEdad=false;
		
		try{
		 
			strSQL= loadSQLQuery(QRY_SEL_PERS);
			ps = conn.prepareStatement(strSQL);
		 
			fillService(QRY_SEL_PERS);
		
			rs = ps.executeQuery();  
			 
			int edad = 0;
		 
			while (rs.next()) {
				
				edad = rs.getInt("edad");
				
				if (edad >= MAYORIA_EDAD) {	 
					mayorDeEdad = true;  
					error = false;		 
					break;			
				}				
			}
			
		 
			if (mayorDeEdad) {
				 
				strSQL= loadSQLQuery(QRY_INS_PERS);  
				ps = conn.prepareStatement(strSQL);
				fillService(QRY_INS_PERS); 
				int r;
				r = ps.executeUpdate();  
			 
				
				if (r > 0) {  
			 
					strSQL= loadSQLQuery(QRY_UPD_HIST); 
					ps = conn.prepareStatement(strSQL);
					fillService(QRY_UPD_HIST);

					String strSql94 = "INSERT INTO prutransdistribtmp ( `desc`) VALUES ('hola')  ";
					//20141015 prueba dejar 1 solo ds x caida conn: PreparedStatement ps94= conn94.prepareStatement(strSql94);
					//20141015 prueba dejar 1 solo ds x caida conn: int r94 = ps94.executeUpdate();
					
					boolean pruebaTmp = true;
					if (pruebaTmp) 
						throw new Exception("Prueba Transaccionabilidad"); 
					
					r = ps.executeUpdate(); 
					if ( r> 0) { 
						result = true;  
					}
				}else {  
					error = true;
				}
				
			}  
			
		 
			
		}catch(Exception e){
			error = true;  
			//		logger.fatal(e.getMessage());
			throw new Exception(e.getMessage()); 
		}		
	}  

	@Override
	public void writeResponse() throws Exception {
		writeResultXML("bool"); // 
									 
	}
	
  
}
