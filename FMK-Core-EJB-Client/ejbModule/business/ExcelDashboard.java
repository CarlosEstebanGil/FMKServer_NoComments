package business;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
*@author CGIL
*
*/

@Stateless(name="ExcelDashboard", mappedName="ExcelDashboard")
@LocalBean
public class ExcelDashboard extends AbstractExcelOutput {

	static final Logger logger = Logger.getLogger(ExcelDashboard.class);

	static final String EXCELFILE = "DASHBOARD.xlsx"; 
	
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

		 } catch (Exception e) { 
			 throw new Exception (e.getMessage());  
		 }	 
    }
    
    /**
  	 * Get query SQL according sheet excel document
  	 * @param sheet
  	 * @return querySQL
  	 */
	public  String getStringSQL(int sheet){
		  String strQrySql = getGeneralSQL();
		  String where="";
		  String group_by ="";
		  String order_by = "";
		  String having = "";  
		  return replaceClausesSQL(strQrySql, where, group_by, order_by, having);
	}	
	
    /**
  	 * Return on common SQL queries
  	 * @return strSql - SQL query
  	 */
	public String getGeneralSQL() {
        String strQrySql = "SELECT " +
        				   "o.`numop` AS numop, " +
        				   "IFNULL(o.`numoc`, '') AS numoc, " +
        				   "IFNULL(o.`fecoc`, '') AS fecoc, " + 
        				   "IFNULL(proveedor.`nombre`, '') AS proveedor, " +
        				   "'VER' AS tipo, " +
        				   "IFNULL(via.`nombre`,'') AS modal, " +
        				   "'VER' AS tipoproveedor, " +
        				   "'VER' AS main_lane, " +
        				   "IFNULL(o.`fecETSU`,'') AS fecETSU, " +
        				   "IFNULL(o.`fecrequeridaenplanta`,'') AS fecrequeridaenplanta, " +
        				   "CURRENT_DATE() AS feclistaparacoletar, " + // TODO VER
        				   "CURRENT_DATE() AS fecembarque, " + //TODO VER IFNULL(o.`fecSHIPPED`, CURRENT_DATE())
        				   "IFNULL(o.`fecarribotransporteazonaprimariaaduanera`,'') AS fecarribo, " +
        				   "CURRENT_DATE() AS fecaplaza, " + // TODO VER IFNULL(o.`fecaplaza`, CURRENT_DATE())
        				   "IFNULL(o.`fecingresosistemaimportador`,'') AS fecingresosistemaimportador, " +
        				   "MONTH(o.`fecingresosistemaimportador`) AS mes_fecingresosistemaimportador, " +
        				   "YEAR(o.`fecingresosistemaimportador`) AS anio_fecingresosistemaimportador, " +
        				   "CURRENT_DATE() AS fecliberacionSDU, " + //TODO VER
        				   "'VER' AS tintas, " +
        				   "'VER' AS statusparareporte, " +
        				   "'VER' AS cumplimientoproveedor, " +  
        				   "1 " + 
        				   "FROM " +
        				   "`openpo` AS o " + 
        				   "LEFT JOIN proveedor " + 
        				   "ON proveedor.`id` = o.`idvendor` " + 
        				   "LEFT JOIN via " +
        				   "ON via.`codvia` = o.`idvia` " + 
        				   "WHERE 1 " +     
        				   "#AND o.`numoc`='3000669847' " + 
        				   "AND o.`fecaplaza` >='2014-01-01'" ;
        return strQrySql;
	}
	
	/**
  	 * Write data to excel row
  	 * @param sheet
  	 * @param row 
  	 * @return cell
	 * @throws Exception 
  	 */
	public int writeDataRow(int sheet, XSSFRow row, int numRow) throws Exception { 
		int numCell = -1;
		switch (sheet){
		   case 0:
			   writeDataCell(row, ++numCell, "numop");
			   writeDataCell(row, ++numCell, "numoc");
			   writeDataCell(row, ++numCell, "fecoc");
			   writeDataCell(row, ++numCell, "proveedor");
			   writeDataCell(row, ++numCell, "tipo");
			   writeDataCell(row, ++numCell, "modal");			   
			   writeDataCell(row, ++numCell, "tipoproveedor");
			   writeDataCell(row, ++numCell, "main_lane");
			   writeDataCell(row, ++numCell, "fecETSU");
			   writeDataCell(row, ++numCell, "fecrequeridaenplanta");
			   writeDataCell(row, ++numCell, "feclistaparacoletar");
			   writeDataCell(row, ++numCell, "fecembarque");
			   writeDataCell(row, ++numCell, "fecarribo");
			   writeDataCell(row, ++numCell, "fecaplaza");
			   writeDataCell(row, ++numCell, "fecingresosistemaimportador");
			   writeDataCell(row, ++numCell, "mes_fecingresosistemaimportador");
			   writeDataCell(row, ++numCell, "anio_fecingresosistemaimportador");
			   writeDataCell(row, ++numCell, "fecliberacionSDU");
			   writeDataCell(row, ++numCell, "tintas");
			   writeDataCell(row, ++numCell, "statusparareporte");
			   writeDataCell(row, ++numCell, "cumplimientoproveedor");
			   writeDataCell(row, ++numCell, "L", "K", numRow);
			   writeDataCell(row, ++numCell, "N", "L", numRow);
			   writeDataCell(row, ++numCell, "R", "N", numRow);			   
			   break;
		}
		return numCell;
    }
	
	/**
  	 * Create excel file tab
  	 * @param numSheet
  	 */
	public void createSheetXLSXFile(int numSheet) {
		int line = 2;
		try {
			if (wb != null) {
				XSSFSheet sheet	= wb.getSheetAt(numSheet);		
				while (rs.next()) {		
					sheet.shiftRows(line,line,1);
					XSSFRow row = sheet.createRow(line-1);
					writeDataRow(numSheet, row, line);
					line++;
				}
				wb.setForceFormulaRecalculation(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	/**
  	 * Write data in cell
  	 * @param row
  	 * @param numCell
  	 * @param columnLabel
  	 * @return cell
	 * @throws Exception 
  	 */
    public XSSFCell writeDataCell(XSSFRow row, int numCell, String firstCell, String secondCell, int numRow) throws Exception {		
		XSSFCell formulaCell = row.createCell(numCell);
    	try {	
    	   FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();	
    	   String strNumRow = String.valueOf(numRow);
    	   String strFormula = firstCell + strNumRow + "-" + secondCell + strNumRow;
    	   formulaCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
    	   formulaCell.setCellFormula(strFormula);
    	   CellValue value = evaluator.evaluate(formulaCell);
    	    if (value != null) {
    	    	formulaCell.setCellValue(formulaCell.getNumericCellValue());
    	    }
		} catch (Exception e) {
			throw new Exception (e.getMessage());  
		}            
		return formulaCell;
    }

	@Override
	int writeDataRow(int sheet, XSSFRow row) {
		return 0;
	}
}
