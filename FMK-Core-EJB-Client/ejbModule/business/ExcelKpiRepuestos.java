package business;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
*@author CGIL
*
*/

@Stateless(name="ExcelKpiRepuestos", mappedName="ExcelKpiRepuestos")
@LocalBean
public class ExcelKpiRepuestos extends AbstractExcelOutput {

	static final Logger logger = Logger.getLogger(ExcelKpiRepuestos.class);

	static final String EXCELFILE = "KPI - Seguimiento Rep.xlsx"; 
	
	/**
  	 * Gets filename excel
  	 * @return excelFile
  	 * @throws exception
  	 */
	@Override
  	public String getFileName() throws Exception {
  	    return EXCELFILE;
  	}

	/**
  	 * Execution algorithm
  	 * @throws exception
  	 */
    @Override
  	public void executionAlgorithm() throws Exception {
    	try {
    		  int sheet=0;
    		  wb = new XSSFWorkbook(is);
    		  
  		      XSSFCellStyle style = getDefaultCellStyle(XSSFCellStyle.ALIGN_LEFT);
  		      super.setStyle(style);
  		    
  		      XSSFCellStyle styleNumeric = getDefaultCellStyle(XSSFCellStyle.ALIGN_RIGHT);
  		      super.setNumericStyle(styleNumeric);
    		
		      completeDataSheet(sheet);
		      /*2015011
		       ps.close();
			   rs.close();		      
		       */
		      completeDataSheet(++sheet);
		      /*2015011
		       ps.close();
			   rs.close();		      
		       */
		      completeDataSheet(++sheet);
		      /*2015011
		       ps.close();
			   rs.close();		      
		       */
		    // completeDataSheet(++sheet);
		      
		 } catch (Exception e) { 
			 throw new Exception (e.getMessage());  
		 }	 
    }
    
    /**
  	 * Get query SQL according sheet excel document
  	 * @param sheet
  	 * @return querySQL
  	 */
    public  String getStringSQL(int sheet) {
		  String strSQL2 = getGeneralSQL();
		  String where="";
		  String group_by ="";
		  String order_by = "";
		  String having = "";
		  
		  switch(sheet) {
		     case 0: // PENDIENTES
		    	 // # Repuestos (ERSA)
		    	 where = "";
		    	 where += " AND openpo_articulo.idtipoproducto = 9 ";
		    	 
		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";
		    	 
		    	 having = "";
		    	 // # CON Fecha confirmada pick up
		    	 having += " AND fecCSD = ''"; // having += " AND fecCSD <> ''";
		    	 // CON fecha Shipped
		    	 having += " AND fecSHIPPED = ''"; //having += " AND fecSHIPPED <> ''";
		    	 
		    	 order_by = " ORDER BY openpo.id, openpo_articulo.id";
		    	 break;
		     case 1: // TRANSITO
		    	 // # Repuestos (ERSA)
		    	 where = "";
		    	 where += " AND openpo_articulo.idtipoproducto = 9 ";
		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";

		    	 having = "";
		    	 // (CON Fecha confirmada pick up OR Shipped)
				// OR 
				// (CON Arribo MT a zona primaria aduanera AND SIN Fecha de despacho a plaza (carga efectiva))
				having += " AND (";
				having += "(fecCSD <> '' OR fecSHIPPED <> '')";
				having += " OR ";
				having += "(fecarribotransporteazonaprimariaaduanera <> '' AND fecaplaza = '')";
				having += ")";

				order_by = " ORDER BY openpo.id, openpo_articulo.id";
				break;
		     case 2: // PLANTA
		    	 // # Repuestos (ERSA)
		    	 where = "";
		    	 where += " AND openpo_articulo.idtipoproducto = 9 ";

		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";

		    	 having = "";
		    	 // CON Fecha efectiva entrega en planta
		    	 having += " AND fecefectivaentregaenplanta <> ''";
				  
				order_by = " ORDER BY openpo.id, openpo_articulo.id";
				break;
		  }
		  return replaceClausesSQL(strSQL2, where, group_by, order_by, having);
    	}
    	
    	/**
    	 * Write data to excel row
    	 * @param sheet
    	 * @param row 
    	 * @return cell
    	 */
    	public int writeDataRow(int sheet, XSSFRow row) { 
    		int cell = -1;
			switch (sheet) {
			   case 0: // PENDIENTES
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "via");
				   writeDataCell(row, ++cell, "cantidad");
				   writeDataCell(row, ++cell, "articulo");
				   writeDataCell(row, ++cell, "fecETSU");
				   writeDataCell(row, ++cell, "fecrequeridaenplanta");
				   writeDataCell(row, ++cell, "fecRDD");
				   writeDataCell(row, ++cell, "numdespachodjai");
				   writeDataCell(row, ++cell, "despachante");
				   break;
			   case 1: // TRANSITO
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "via");
				   writeDataCell(row, ++cell, "cantidad");
				   writeDataCell(row, ++cell, "articulo");
				   writeDataCell(row, ++cell, "fecETSU");
				   writeDataCell(row, ++cell, "fecrequeridaenplanta");
				   writeDataCell(row, ++cell, "fecRDD");
				   writeDataCell(row, ++cell, "fecETD");
				   writeDataCell(row, ++cell, "fecETA");
				   writeDataCell(row, ++cell, "fecarribotransporteazonaprimariaaduanera");
				   writeDataCell(row, ++cell, "numfactura");
				   writeDataCell(row, ++cell, "numdocumentotransporte");
				   writeDataCell(row, ++cell, "numdespachodjai");
				   writeDataCell(row, ++cell, "despachante");
				   break;
			   case 2: // PLANTA
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "fecefectivaentregaenplanta");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "via");
				   writeDataCell(row, ++cell, "cantidad");
				   writeDataCell(row, ++cell, "articulo");
				   writeDataCell(row, ++cell, "fecETSU");
				   writeDataCell(row, ++cell, "fecrequeridaenplanta");
				   writeDataCell(row, ++cell, "fecRDD");
				   writeDataCell(row, ++cell, "fecETD");
				   writeDataCell(row, ++cell, "fecETA");
				   writeDataCell(row, ++cell, "fecarribotransporteazonaprimariaaduanera");
				   writeDataCell(row, ++cell, "numfactura");
				   writeDataCell(row, ++cell, "numdocumentotransporte");
				   writeDataCell(row, ++cell, "numdespachodjai");
				   writeDataCell(row, ++cell, "despachante");
			       break;
			}
			return cell;
    	}
}
