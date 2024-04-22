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

@Stateless(name="ExcelKpiSegImpoMateriaPrimaTerrestre", mappedName="ExcelKpiSegImpoMateriaPrimaTerrestre")
@LocalBean
public class ExcelKpiSegImpoMateriaPrimaTerrestre extends AbstractExcelOutput {

	static final Logger logger = Logger.getLogger(ExcelKpiSegImpoMateriaPrimaTerrestre.class);
	static final String EXCELFILE = "KPI - Seguimiento MP T.xlsx"; 
	
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
			int sheet = 0;
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
    	
    	  String strSqlGeneral = getGeneralSQL();
		  String where="";
		  String group_by ="";
		  String order_by = "";
		  String having = "";	
		  
		  switch(sheet){
		  
		  
		     case 0: // PENDIENTES
	              where = "";
	              // Materia Prima (ROH)
	              where += " AND openpo_articulo.`idtipoproducto` = 5"; 
	              // TERRESTRE
	              where += " AND openpo.`idvia` = 4"; 

	              group_by = " GROUP BY openpo.numop, openpo_articulo.id";

	              having = "";
	              // SIN Fecha entrada depósito intermedio OR SIN Fecha de embarque
		          having +=" AND (	fecentradadepositointermedio = '' OR fecSHIPPED = '')"; //" AND (fecentradadepositointermedio != '' OR  fecSHIPPED != '')";
	              
		          order_by = " ORDER BY openpo.id, openpo_articulo.id";
		          break;
		          
		          
		     case 1: // DEPOSITO INTER. ORIGEN
	              where = "";
	              // Materia Prima (ROH)
	              where += " AND openpo_articulo.`idtipoproducto` = 5"; 
	              // TERRESTRE
	              where += " AND openpo.`idvia` = 4"; 

	              group_by = " GROUP BY openpo.numop, openpo_articulo.id";

	              having = "";
	              // # CON Fecha entrada depósito intermedio
                  having += " AND fecentradadepositointermedio != ''";
			      // # SIN Fecha salida depósito intermedio
                  having += " AND fecsalidadepositointermedio = ''";

                  order_by = " ORDER BY openpo.id, openpo_articulo.id";
			      break;
			      
			      
		     case 2: // TRANSITO
	              where = "";
	              // Materia Prima (ROH)
	              where += " AND openpo_articulo.`idtipoproducto` = 5"; 
	              // TERRESTRE
	              where += " AND openpo.`idvia` = 4"; 

	              group_by = " GROUP BY openpo.numop, openpo_articulo.id";
		          
	              having = "";
	              // CON Fecha de embarque
		          having += " AND fecSHIPPED <> ''";
		          // CON Fecha efectiva entrega en planta
		          having += "  AND fecefectivaentregaenplanta = '' ";//" AND fecefectivaentregaenplanta <> ''";
	              
		          order_by = " ORDER BY openpo.id, openpo_articulo.id";
		          break;
		          
		          
		  }
		  
		  return replaceClausesSQL(strSqlGeneral, where, group_by, order_by, having);
    }
    
    /**
  	 * Write data to excel row
  	 * @param sheet
  	 * @param row 
  	 * @return cell
  	 */
    public int writeDataRow(int sheet, XSSFRow row) { 
    		int cell = -1;
			switch (sheet)	{
			   case 0:
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "via");
				   writeDataCell(row, ++cell, "incoterm");
				   writeDataCell(row, ++cell, "cantidad");
				   writeDataCell(row, ++cell, "articulo");
				   writeDataCell(row, ++cell, "fecoc");
				   writeDataCell(row, ++cell, "fecenvioocalvendor");
				   writeDataCell(row, ++cell, "fecokpovendor");
				   writeDataCell(row, ++cell, "fecETS");
				   writeDataCell(row, ++cell, "fecrequeridaenplanta");
			       break;
			   case 1:
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "via");
				   writeDataCell(row, ++cell, "incoterm");
				   writeDataCell(row, ++cell, "cantidad");
				   writeDataCell(row, ++cell, "articulo");
				   writeDataCell(row, ++cell, "fecoc");
				   writeDataCell(row, ++cell, "cant_bultos");
				   writeDataCell(row, ++cell, "numfactura");
				   break;
			   case 2:
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "via");
				   writeDataCell(row, ++cell, "incoterm");
				   writeDataCell(row, ++cell, "cantidad");
				   writeDataCell(row, ++cell, "articulo");
				   writeDataCell(row, ++cell, "fecoc");
				   writeDataCell(row, ++cell, "fecETSU");
				   writeDataCell(row, ++cell, "cant_bultos");
				   writeDataCell(row, ++cell, "numfactura");
				   writeDataCell(row, ++cell, "cantidadcontenedor");
				   writeDataCell(row, ++cell, "fecSHIPPED");
				   writeDataCell(row, ++cell, "numdocumentotransporte");
				   writeDataCell(row, ++cell, "Arrival_Date_Estimate_Frontera_BR");
				   writeDataCell(row, ++cell, "fecarribotransporteazonaprimariaaduanera");
				   writeDataCell(row, ++cell, "feccrucefrontera");
				   writeDataCell(row, ++cell, "fecETW");					   
			       break;
			}
			return cell;
    	}
}
