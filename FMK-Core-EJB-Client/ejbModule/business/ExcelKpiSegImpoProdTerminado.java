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

@Stateless(name="ExcelKpiSegImpoProdTerminado", mappedName="ExcelKpiSegImpoProdTerminado")
@LocalBean
public class ExcelKpiSegImpoProdTerminado extends AbstractExcelOutput {

		static final Logger logger = Logger.getLogger(ExcelKpiSegImpoProdTerminado.class);
		static final String EXCELFILE = "KPI - Seguimiento Importacion PT.xlsx";  
	
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
		  switch(sheet) {
			  case 0: // TRANSITO
				  where = " AND openpo_articulo.`idtipoproducto` = 1"; // Producto Terminado (FERT)
				  group_by = " GROUP BY openpo.numop, openpo_articulo.id";

				  having = "";
				  having += " AND fecSHIPPED <> ''"; // CON Fecha de embarque
				  having += " AND fecefectivaentregaenplanta = ''"; // SIN Fecha efectiva entrega en planta

				  order_by = " ORDER BY openpo.id, openpo_articulo.id";
				  break;
			  case 1: // PLANTA
				  where = " AND openpo_articulo.`idtipoproducto` = 1"; // Producto Terminado (FERT)
				  group_by = " GROUP BY openpo.numop, openpo_articulo.id";

				  having = "";
				  having += " AND fecSHIPPED <> ''"; // CON Fecha de embarque
				  having += " AND fecefectivaentregaenplanta <> ''"; // CON Fecha efectiva entrega en planta
				  
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
			switch (sheet) {
			   case 0: // TRANSITO
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "fecoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "numfactura");
				   writeDataCell(row, ++cell, "transporteinternacional");
				   writeDataCell(row, ++cell, "cantidadcontenedor");
				   writeDataCell(row, ++cell, "tipo_contenedor");
				   writeDataCell(row, ++cell, "cantidadcontenedor");
				   writeDataCell(row, ++cell, "fecSHIPPED");
				   writeDataCell(row, ++cell, "fecturnocarga");
				   writeDataCell(row, ++cell, "fecETW");
				   writeDataCell(row, ++cell, "fecefectivaentregaenplanta");
			       break;
			   case 1: // PLANTA
				   writeDataCell(row, ++cell, "numop4pl");
				   writeDataCell(row, ++cell, "numoc");
				   writeDataCell(row, ++cell, "vendor");
				   writeDataCell(row, ++cell, "origen");
				   writeDataCell(row, ++cell, "numfactura");
				   writeDataCell(row, ++cell, "fecSHIPPED");
				   writeDataCell(row, ++cell, "fecefectivaentregaenplanta");
				   writeDataCell(row, ++cell, "fecingresosistemaimportador");
				   writeDataCell(row, ++cell, "LT");
				   break;
			}
			return cell;
		}
}