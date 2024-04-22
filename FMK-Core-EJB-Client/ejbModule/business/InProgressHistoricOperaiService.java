package business;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import fmk_core_server.AbstractMultiSentenceDBServiceConverter;


	@Stateless(name="InProgressHistoricOperaiService", mappedName="InProgressHistoricOperaiService")
	@LocalBean
	public class InProgressHistoricOperaiService extends AbstractMultiSentenceDBServiceConverter {
		@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;
		
		/**
	  	 * Obtained Connect
	  	 * @return conn
		 * @throws Exception 
	  	 */
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
	    
	    /**
	  	 * Execution algorithm
	  	 * @throws exception
	  	 */
	    @Override 
	  	public void executionAlgorithm() throws Exception {
	    	try {
	    		String whereClause = getWhereClause();
	    		getOperaiSelected(whereClause);
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
	    }     
		
		/**
		  * Get Operai Selected
		  * @return resultSet
		  * @throws exception
		  */ 
		public ResultSet getOperaiSelected(String whereClause) throws Exception {
			try {					
				String strQrySql = "SELECT operai.idcliente AS idcliente, " +
								   "operai.tripli AS tripli, " +
								   "IF(LENGTH( operai.titmer ) > 21, CONCAT('',TRIM(LEFT(operai.titmer,18)),'...'), operai.titmer) AS titmer, " +
								   "DATE_FORMAT(IF(operai.fecsub=0, '', operai.fecsub), '%d/%m/%Y') AS fechaofi, " +
								   "DATE_FORMAT(IF(operai.feccar=0, '', operai.feccar), '%d/%m/%Y') AS fechaplaza, " +
								   "IF(numdesp, CONCAT(RIGHT(operai.numdesp, 6), '-', operai.letdesp),'') AS despacho, " +
								   "IF(LENGTH(aduanas.nombre) > 13, CONCAT('',TRIM(LEFT(aduanas.nombre,11)),'...'), aduanas.nombre) AS codadunom, " +
								   "IF(operaiadd.doccopiasquefalta = '' || ISNULL(operaiadd.doccopiasquefalta), 0, 1) AS hay_doccopiasquefalta, " +
								   "IF(operaiadd.docorigquefalta = '' || ISNULL(operaiadd.docorigquefalta), 0, 1) AS hay_docorigquefalta, " +
								   "IF(operaiadd.obsalcliente = '' || ISNULL(operaiadd.obsalcliente), 0, 1) AS hay_obsalcliente, " +
								   "IF(operai.fecfac > 0, DATE_FORMAT(operai.fecfac,'%d/%m/%y'), 'NO') AS ffacturaRVA, " +
								   "IF(LENGTH(refcli) > 13, CONCAT('',TRIM(LEFT(refcli,11)),'...'), refcli) AS refcli, " +
								   "IF(clientes.idcliente > 0, CONCAT('',clientes.idcliente,''), CONCAT('',operai.idcliente,'')) AS cliente " +
								   "FROM operai LEFT JOIN aduanas ON aduanas.codadu = operai.codadu " +
								   "LEFT JOIN operaiadd ON operaiadd.numop = operai.tripli " +
								   "LEFT JOIN factura_proveedor ON factura_proveedor.tripli = operai.tripli " +
								   "LEFT JOIN clientes  ON operai.idcliente = clientes.idcliente " +
								   "WHERE 1 " +
									whereClause +
								   "AND (operai.fecbaja=0 OR ISNULL(operai.fecbaja)) AND operai.tipdes != 'DJAI' " +
								   "AND (operai.fecfac >= CONCAT(YEAR(CURDATE())-3,'-01-01') OR operai.fecfac=0) " +
								   "AND operai.idcliente = 1521 " +
								   "AND operai.tripli >= 0 AND operai.tripli <= 999999 AND operai.fecini >= '20141001' " +
								   "GROUP BY operai.tripli " +
								   "ORDER BY operai.feccar ASC, tripli ";
				
			   	ps = conn.prepareStatement(strQrySql);  
			   	rs = ps.executeQuery(); 
 	
		   	} catch(Exception e) {
		   		throw new Exception (e.getMessage()); 
		   	}
			return rs;
		}	
		
		public String getWhereClause() throws Exception {
			
			String nopera_desde = getStringVarValue("nopera_desde");
    		String nopera_hasta = getStringVarValue("nopera_hasta");
    		String finicio_desde = getStringVarValue("finicio_desde");
    		String finicio_hasta = getStringVarValue("finicio_hasta");
    		String refcli = getStringVarValue("refcli");
    		String proveedor = getStringVarValue("proveedor");
    		String codaduana = getStringVarValue("codaduana");
    		String tipodespacho = getStringVarValue("tipodespacho"); 
    		String ndespacho = getStringVarValue("ndespacho");
    		String fembarque_desde = getStringVarValue("fembarque_desde");
    		String fembarque_hasta = getStringVarValue("fembarque_hasta");
    		String fentradaestimada_desde = getStringVarValue("fentradaestimada_desde");
    		String fentradaestimada_hasta = getStringVarValue("fentradaestimada_hasta");
    		String fentrada_desde = getStringVarValue("fentrada_desde");
    		String fentrada_hasta = getStringVarValue("fentrada_hasta");
    		String canal = getStringVarValue("canal");
    		String ffacturacion_desde = getStringVarValue("ffacturacion_desde");
    		String ffacturacion_hasta = getStringVarValue("ffacturacion_hasta");
    		String foficializacion_desde = getStringVarValue("foficializacion_desde");
    		String foficializacion_hasta = getStringVarValue("foficializacion_hasta");
    		String faplaza_desde = getStringVarValue("faplaza_desde");
    		String faplaza_hasta = getStringVarValue("faplaza_hasta");
    		String incoterm = getStringVarValue("incoterm");   
    		String facturaproveedor = getStringVarValue("facturaproveedor");   		

			String whereclause = "";
			
			if (nopera_desde != null && nopera_desde != "") 
				whereclause += "AND operai.tripli >= " + nopera_desde + " ";
			
			if (nopera_hasta != null && nopera_hasta != "") 
				whereclause += "AND operai.tripli <= " + nopera_hasta + " ";
			
			if (finicio_desde != null && finicio_desde != "") 
				whereclause += "AND operai.fecini >= '" + finicio_desde + "' ";
			
			if (finicio_hasta != null && finicio_hasta != "") 
				whereclause += "AND operai.fecini <= '" + finicio_hasta + "' ";
			
			if (refcli != null && refcli != "" && !refcli.equals("-1")) 
				whereclause += "AND operai.refcli = '" + refcli + "' ";
			
			if (proveedor != null && proveedor != "" && !proveedor.equals("-1")) 
				whereclause += "AND operai.nomven = '" + proveedor + "' ";
			
			if (codaduana != null && codaduana != "" && !codaduana.equals("-1")) 
				whereclause += "AND operai.codadu = " + codaduana + " ";
			
			if (tipodespacho != null && tipodespacho != "" && !tipodespacho.equals("-1")) 
				whereclause += "AND operai.tipdes = '" + tipodespacho + "' ";
			
			if (ndespacho != null && ndespacho != "" && !ndespacho.equals("-1")) 
				whereclause += "AND operai.numdesp = " + ndespacho + " ";
			
			if (fembarque_desde != null && fembarque_desde != "")
				whereclause += "AND operai.fecemb >= '" + fembarque_desde + "' ";
			
			if (fembarque_hasta != null && fembarque_hasta != "")
				whereclause += "AND operai.fecemb <= '" + fembarque_hasta + "' ";
			
			if (fentradaestimada_desde != null && fentradaestimada_desde != "") 
				whereclause += "AND operai.fecesp >= '" + fentradaestimada_desde + "' ";
			
			if (fentradaestimada_hasta != null && fentradaestimada_hasta != "")
				whereclause += "AND operai.fecesp <= '" + fentradaestimada_hasta + "' ";
			
			if (fentrada_desde != null && fentrada_desde != "")
				whereclause += "AND operai.feceent >= '" + fentrada_desde + "' ";
			
			if (fentrada_hasta != null && fentrada_hasta != "")
				whereclause += "AND operai.feceent <= '" + fentrada_hasta + "' ";
			
			if (canal != null && canal != "" && !canal.equals("-1")) 
				whereclause += "AND operai.canal = '" + canal + "' ";
			
			if (ffacturacion_desde != null && ffacturacion_desde != "")
				whereclause += "AND operai.fecfac >= '" + ffacturacion_desde + "' " ;
				
			if (ffacturacion_hasta != null && ffacturacion_hasta != "")
				whereclause += "AND operai.fecfac <= '" + ffacturacion_hasta + "' " ;
			
			if (foficializacion_desde != null && foficializacion_desde != "")
				whereclause += "AND operai.fecsub >= '" + foficializacion_desde + "' " ;
			
			if (foficializacion_hasta != null && foficializacion_hasta != "")
				whereclause += "AND operai.fecsub <= '" + foficializacion_hasta + "' " ;
			
			if (faplaza_desde != null && faplaza_desde != "") 
				whereclause += "AND operai.fecsub >= '" + faplaza_desde + "' " ;
			
			if (faplaza_hasta != null && faplaza_hasta != "")
				whereclause += "AND operai.fecsub <= '" + faplaza_hasta + "' " ;

			if (incoterm != null && incoterm != "" && !incoterm.equals("-1")) 
				whereclause += "AND operai.convta = '" + incoterm + "' ";
			
			if (facturaproveedor != null && facturaproveedor != "" && !facturaproveedor.equals("-1")) 
				whereclause += "AND factura_proveedor.numfaci = '" + facturaproveedor + "' ";
			
			
			return whereclause;
		}
}