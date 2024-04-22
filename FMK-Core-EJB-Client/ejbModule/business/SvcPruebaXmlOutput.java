package business;

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

@Stateless(name="SvcPruebaXmlOutput", mappedName="SvcPruebaXmlOutput")
@LocalBean
public class SvcPruebaXmlOutput extends AbstractMultiSentenceDBServiceConverter {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;   

public static final String KTE_XXX="Soy Una Constante";

    public SvcPruebaXmlOutput() {
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
    		strSQL="SELECT * FROM clientes limit 5";
    		
    		ps = conn.prepareStatement(strSQL);
   		 	
			rs = ps.executeQuery();  
			
			//recorrerResultSet();
			int cantRegs=11;
			if (rs!=null) {
				//cantRegs=DameCantRegsDevueltosEnRS()
				if (cantRegs > 10) { 
					strSQL="INSERT INTO log(usuario) values ('carlos')";
					ps = conn.prepareStatement(strSQL);
					 
					int r;
					r = ps.executeUpdate();  
					
					String mensajeResultado= getStringVarValue("msgeRtado");   
					
					if (r > 0) { 
						System.out.println(mensajeResultado);
					}
				}
			}
		 
		
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }
    
    public void recorrerResultSet() throws Exception {
   	 	int id=-1;
   	 	String nombre="";
    	try {
	   		while (rs.next()) {
				
	   			id= rs.getInt("idcliente");
				nombre=rs.getString("cliente");
				
				if (id == 0) {	 
					nombre="Id 0";	
				}
				imprimirResultado(id + "-" + nombre);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
   }
    
    public void imprimirResultado(String str) throws Exception {
    	 System.out.println(str);
    }
}
