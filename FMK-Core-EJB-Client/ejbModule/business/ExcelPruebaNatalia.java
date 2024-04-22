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

@Stateless(name="ExcelPruebaNatalia", mappedName="ExcelPruebaNatalia")
@LocalBean
public class ExcelPruebaNatalia extends AbstractExcelOutput {

		static final Logger logger = Logger.getLogger(ExcelPruebaNatalia.class);
	
		static final String EXCELFILE = "Prueba - Natalia.xlsx"; 
		
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
	    		
			      completeDataSheet(sheet);		      
			      
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
	    	return getGeneralSQL();
	    }
    	
    	/**
    	 * Write data to excel row
    	 * @param sheet
    	 * @param row 
    	 * @return cell
    	 */
    	public int writeDataRow(int sheet, XSSFRow row) { 
    	   int cell = -1;
		   writeDataCell(row, ++cell, "numop");
		   writeDataCell(row, ++cell, "idgasto");
		   writeDataCell(row, ++cell, "artic");
		   writeDataCell(row, ++cell, "importe");
		   return cell;
    	}
    	
    	/**
    	 * Write data to excel row
    	 * @param sheet
    	 * @param row 
    	 * @return cell
    	 */
    	public int writeDataRow2(int sheet, XSSFRow row) { 
    	   int cell = -1;
		   writeDataCell(row, ++cell, "numop");
		   writeDataCell(row, ++cell, "idgasto");
		   //writeDataCell(row, ++cell, "artic");
		   writeDataCell(row, ++cell, "importe");
		   return cell;
    	}
    	
    	public String getGeneralSQL() {
	        String strSql="SELECT * FROM 4pl_factura_gasto_articulo";
	        return strSql;
		}
    	
    	public String getGeneralSQL3() {
	        String strSql="SELECT * FROM 4pl_factura_gasto_operacion";
	        return strSql;
		}
}
