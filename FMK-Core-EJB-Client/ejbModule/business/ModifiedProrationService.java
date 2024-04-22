package business;

import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import fmk_core_server.AbstractMultiSentenceDBServiceConverter;

@Stateless(name="ModifiedProrationService", mappedName="ModifiedProrationService")
@LocalBean
public class ModifiedProrationService extends AbstractMultiSentenceDBServiceConverter {
	@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;
	 
    @Override
  	public Connection getConnection() throws Exception {
  		Connection conn = null;
  		try {
  			conn = this.ds.getConnection();
  		} catch(Exception e) {
  			throw new Exception(e.getMessage());
  		}
  		return conn;
  	}
    
    @Override 
  	public void executionAlgorithm() throws Exception {
    	try { 
    		modifiedProration();
    		getProrationOfOperations(); 
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }
    
    public void modifiedProration() throws Exception {    	
    	try {
			String strIdfactura = getStringVarValue("idfactura");
			String strIdgastos = getStringVarValue("idgastos");
			String strOperaciones = getStringVarValue("operaciones");
			String strImportes = getStringVarValue("importes");
			
			String[] idgastos = strIdgastos.split(",");
			String[] operations = strOperaciones.split(",");
			String[] amounts = strImportes.split(",");
			
			if(idgastos.length == operations.length) {				
				for(int i=0; i<idgastos.length; i++) {
					updateProration(amounts[i], operations[i], idgastos[i], strIdfactura);
				}
			}			
    	} catch (Exception e) {
    		throw new Exception(e.getMessage());
		}    
    }
    
    public void updateProration(String amount, String numop, String idgasto, String idfactura) throws Exception {
	   	try {		   	
		   	final String QRY_UP_PRORRATEO = "QRY_UP_PRORRATEO";
	    	String strSQL2 = loadSQLQuery(QRY_UP_PRORRATEO);
	    	ps = conn.prepareStatement(strSQL2);
	    	fillService(QRY_UP_PRORRATEO);
			ps.setDouble(1, Double.parseDouble(amount));
			ps.setInt(2, Integer.parseInt(numop));
			ps.setInt(3, Integer.parseInt(idgasto));
			ps.setInt(4, Integer.parseInt(idfactura));
			ps.setInt(5, Integer.parseInt(idgasto));
	    	ps.executeUpdate(); 	
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }
    
    public void getProrationOfOperations() throws Exception {
		try {
			String ids = getStringVarValue("idgastos");
			String numops = getStringVarValue("operaciones");
			String strSQL2 = "SELECT fgo.numop AS numop, fgo.idgasto AS idgasto, g.nombre AS nombre, fgo.importe AS importe " +
					 "FROM 4pl_factura_gasto_operacion fgo " +
					 "LEFT JOIN 4pl_gasto g " + 
					 "ON fgo.idgasto = g.id " +
					 "WHERE idgasto IN (" + ids + ") " +
					 "AND fgo.numop IN (" + numops + ")"; 			
		   	ps = conn.prepareStatement(strSQL2);  
		   	rs = ps.executeQuery(); 		   	
	   	}catch(Exception e){
	   		throw new Exception(e.getMessage());
	   	}
	}
    
}