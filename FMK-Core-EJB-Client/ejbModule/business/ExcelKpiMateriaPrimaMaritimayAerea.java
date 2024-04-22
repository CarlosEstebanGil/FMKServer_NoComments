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

@Stateless(name="ExcelKpiMateriaPrimaMaritimayAerea", mappedName="ExcelKpiMateriaPrimaMaritimayAerea")
@LocalBean
public class ExcelKpiMateriaPrimaMaritimayAerea extends AbstractExcelOutput {

	static final Logger logger = Logger.getLogger(ExcelKpiMateriaPrimaMaritimayAerea.class);

	static final String EXCELFILE = "KPI - Seguimiento MP MyA.xlsx"; 
	
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
	public  String getStringSQL(int sheet){
		  String strSQL2 = getGeneralSQL();
		  String where="";
		  String group_by ="";
		  String order_by = "";
		  String having = "";
	  
		  switch(sheet){
		  
		  
		     case 0: // Entregadas
		    	 // 5 # Materia Prima (ROH)
		    	 // 4 # AEREO Y MARITIMO
		    	 
		    	 where = "";
		    	 where += " AND openpo_articulo.`idtipoproducto` = 5 AND openpo.`idvia` <> 4";

		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";
		    	 
		    	 having = "";
		    	 // CON Fecha efectiva entrega en planta
		    	 having += " AND fecefectivaentregaenplanta <> ''"; 
		    	 
		    	 order_by = " ORDER BY openpo.id, openpo_articulo.id";
		    	 break;
		    	 
		    	 
		     case 1: // Arribados x Entregar
		    	 // 5 # Materia Prima (ROH)
		    	 // 4 # AEREO Y MARITIMO
		    	 where = "";
		    	 where += " AND fecarribotransporteazonaprimariaaduanera <> '' AND fecaplaza = '' ";//" AND openpo_articulo.`idtipoproducto` = 5 AND openpo.`idvia` <> 4 ";

		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";
		    	 
		    	 having = "";
		    	 // # CON Arribo MT a zona primaria aduanera
		    	 having += " AND fecarribotransporteazonaprimariaaduanera <> ''";
		    	 // # SIN Fecha de despacho a plaza (carga efectiva)
		    	 having += " AND fecaplaza = ''"; 
		    	 
		    	 order_by = " ORDER BY openpo.id, openpo_articulo.id";
		    	 break;

		    	 
		     case 2: // Colectados o Embarcados
		    	 // 5 # Materia Prima (ROH)
		    	 // 4 # AEREO Y MARITIMO

		    	 where = "";
		    	 where += " AND openpo_articulo.`idtipoproducto` = 5 AND openpo.`idvia` <> 4 ";

		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";
		    	 
		    	 having = "";
		    	 // # CON Fecha confirmada pick up OR CON Shipped		 
		    	 having += " AND (fecCSD <> '' OR fecSHIPPED <> '')"; 

		    	 order_by = " ORDER BY openpo.id, openpo_articulo.id";
		    	 break;

		    	 
		     case 3: // Pendientes de Colecta
		    	 // 5 # Materia Prima (ROH)
		    	 // 4 # AEREO Y MARITIMO
		    	 where = "";
		    	 where += " AND openpo_articulo.`idtipoproducto` = 5 AND openpo.`idvia` <> 4 ";

		    	 group_by = " GROUP BY openpo.numop, openpo_articulo.id";
		    	 
		    	 having = "";
		    	 // # SIN Fecha confirmada pick up
		    	 having += " AND fecCSD = ''";
		    	 // SIN Shipped
		    	 having += " AND fecSHIPPED = ''"; 
		    	 
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
		switch (sheet){
		   case 0:
			   writeDataCell(row, ++cell, "numop4pl");
			   writeDataCell(row, ++cell, "numoc");
			   writeDataCell(row, ++cell, "vendor");
			   writeDataCell(row, ++cell, "cantidad");
			   writeDataCell(row, ++cell, "articulo");
			   writeDataCell(row, ++cell, "fecrequeridaenplanta");
			   writeDataCell(row, ++cell, "fecETS");
			   writeDataCell(row, ++cell, "fecllegadapuertoorigen");
			   writeDataCell(row, ++cell, "numfactura");
			   writeDataCell(row, ++cell, "transporteinternacional");
			   writeDataCell(row, ++cell, "fecETD");
			   writeDataCell(row, ++cell, "numdocumentotransporte");
			   writeDataCell(row, ++cell, "fecETA");
			   writeDataCell(row, ++cell, "fecarribotransporteazonaprimariaaduanera");
			   writeDataCell(row, ++cell, "terminal");
			   writeDataCell(row, ++cell, "numcontenedor");
			   writeDataCell(row, ++cell, "cantidadcontenedor");
			   writeDataCell(row, ++cell, "numdespachodjai");
			   writeDataCell(row, ++cell, "despachante");
		       break;
		   case 1:
			   writeDataCell(row, ++cell, "numop4pl");
			   writeDataCell(row, ++cell, "numoc");
			   writeDataCell(row, ++cell, "vendor");
			   writeDataCell(row, ++cell, "cantidad");
			   writeDataCell(row, ++cell, "articulo");
			   writeDataCell(row, ++cell, "fecrequeridaenplanta");
			   writeDataCell(row, ++cell, "fecETS");
			   writeDataCell(row, ++cell, "fecllegadapuertoorigen");
			   writeDataCell(row, ++cell, "numfactura");
			   writeDataCell(row, ++cell, "transporteinternacional");
			   writeDataCell(row, ++cell, "fecETD");
			   writeDataCell(row, ++cell, "numdocumentotransporte");
			   writeDataCell(row, ++cell, "fecETA");
			   writeDataCell(row, ++cell, "fecarribotransporteazonaprimariaaduanera");
			   writeDataCell(row, ++cell, "terminal");
			   writeDataCell(row, ++cell, "numcontenedor");
			   writeDataCell(row, ++cell, "cantidadcontenedor");
			   writeDataCell(row, ++cell, "numdespachodjai");
			   writeDataCell(row, ++cell, "despachante");
			   break;
		   case 2:
			   writeDataCell(row, ++cell, "numop4pl");
			   writeDataCell(row, ++cell, "numoc");
			   writeDataCell(row, ++cell, "vendor");
			   writeDataCell(row, ++cell, "cantidad");
			   writeDataCell(row, ++cell, "articulo");
			   writeDataCell(row, ++cell, "fecrequeridaenplanta");
			   writeDataCell(row, ++cell, "fecETS");
			   writeDataCell(row, ++cell, "fecllegadapuertoorigen");
			   writeDataCell(row, ++cell, "numfactura");
			   writeDataCell(row, ++cell, "transporteinternacional");
			   writeDataCell(row, ++cell, "fecETD");
			   writeDataCell(row, ++cell, "numdocumentotransporte");
			   writeDataCell(row, ++cell, "fecETA");
			   writeDataCell(row, ++cell, "numdespachodjai");
			   writeDataCell(row, ++cell, "despachante");
		       break;
		   case 3:
			   writeDataCell(row, ++cell, "numop4pl");
			   writeDataCell(row, ++cell, "numoc");
			   writeDataCell(row, ++cell, "vendor");
			   writeDataCell(row, ++cell, "cantidad");
			   writeDataCell(row, ++cell, "articulo");
			   writeDataCell(row, ++cell, "fecrequeridaenplanta");
			   writeDataCell(row, ++cell, "fecETS");
			   writeDataCell(row, ++cell, "fecarribotransporteazonaprimariaaduanera");
			   writeDataCell(row, ++cell, "fecETS");
			   writeDataCell(row, ++cell, "numdespachodjai");
			   writeDataCell(row, ++cell, "despachante");
			   break;
		}
		return cell;
    }
}
