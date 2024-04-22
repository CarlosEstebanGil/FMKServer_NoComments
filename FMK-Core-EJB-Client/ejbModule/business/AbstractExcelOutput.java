package business;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import fmk_core_server.AbstractMultiSentenceDBServiceHTTPExcelOutput;

public abstract class AbstractExcelOutput extends AbstractMultiSentenceDBServiceHTTPExcelOutput{
		@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;

		static final String DATE_FORMAT = "dd/MM/yyyy";	
		
		private XSSFCellStyle style;
		
		private XSSFCellStyle numericStyle;
		
		/**
	  	 * Obtained Connect
	  	 * @return conn
		 * @throws Exception 
	  	 */
		@Override
	  	public Connection getConnection() throws Exception {
	  		Connection conn = null;
	  		try{
	  			conn = this.ds.getConnection();
	  		}catch(Exception e){
	  			throw new Exception(e.getMessage());
	  		}
	  		return conn;
	  	}
		
	  	/**
	  	 * Create excel file tab
	  	 * @param numSheet
	  	 * @throws Exception 
	  	 */
		public void createSheetXLSXFile(int numSheet) {
			int numCell = 0;
			int line = 2;
			try {
				if (wb != null) {
					XSSFSheet sheet	= wb.getSheetAt(numSheet);
					while (rs.next()) {		
						sheet.shiftRows(line,line,1);
						XSSFRow row = sheet.createRow(line-1);
						numCell = writeDataRow(numSheet, row);
						line++;
					}
					autoAdjustColumns(sheet, numCell);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	  	
		/**
	  	 * Complete data excel sheet
	  	 * @param numSheet
		 * @throws Exception 
	  	 */
	  	public void completeDataSheet(int numSheet) {	
	  		String strSQL2 = null;
	  		try {
				strSQL2 = getStringSQL(numSheet);
				ps = conn.prepareStatement(strSQL2);
				rs = ps.executeQuery();	       
				createSheetXLSXFile(numSheet);
			} catch (SQLException e) {
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
	    public XSSFCell writeDataCell(XSSFRow row, int numCell, String columnLabel) {
			XSSFCell cell = row.createCell(numCell);
			
	    	try {			
				String strCell = rs.getString(columnLabel);
				cell.setCellValue(strCell);
				cell.setCellStyle(this.getStyle());
				
				if(isDouble(strCell)) {
					cell.setCellValue(Double.valueOf(strCell));
					cell.setCellStyle(this.getNumericStyle());
				} else if(isNumeric(strCell)) {
					cell.setCellStyle(this.getNumericStyle());
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}            
			return cell;
	    }
	    
	    /**
	  	 * Gets formatted date
	  	 * @param date
	  	 * @return formattedDate
	  	 */
	    public Date getFormattedDate(String strDate) {
			DateFormat df = new SimpleDateFormat(DATE_FORMAT);
			try {
				return df.parse(strDate);
			} catch (ParseException e) {
				return null;
			}	    	
	    }
	    
	    /**
	  	 * Validates double format
	  	 * @param strNum
	  	 * @return true if the format is correct
	  	 */	    
	    public  boolean isDouble(String str) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
	    
	    /**
	  	 * Validates other numeric formats
	  	 * @param strNum
	  	 * @return true if the format is correct
	  	 */
	    public boolean isNumeric(String strNum) {
			return  strNum.matches("(.*(.|-|/|\')+[0-9]+)+.*");
	    }  
	
	  	/**
	  	 * Returns default cell style
	  	 * @param align
	  	 * @return defaultStyle
	  	 */
		public XSSFCellStyle getDefaultCellStyle(short align) {
		    XSSFCellStyle defaultStyle = wb.createCellStyle();		    
			defaultStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			defaultStyle.setAlignment(align);
	    	defaultStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		    defaultStyle.setFont(wb.createFont());
			defaultStyle.setBorderLeft(CellStyle.BORDER_THIN);
			defaultStyle.setBorderRight(CellStyle.BORDER_THIN);
			defaultStyle.setBorderTop(CellStyle.BORDER_THIN);
			defaultStyle.setBorderBottom(CellStyle.BORDER_THIN);
			return defaultStyle;
		}	
		
	  	/**
	  	 * Return on common SQL queries
	  	 * @return strSql - SQL query
	  	 */
		public String getGeneralSQL() {
	        String strSql2="  SELECT " +
	      		  "'' AS status_embarque, " +
	      		  "IFNULL(IF(openpo.idvendor = 0,'',openpo.idvendor),'') AS idvendor, " +
	      		  "IFNULL(proveedor.nombre,'') AS vendor, " +
	      		  "IFNULL(IF(openpo.numop = 0,'',openpo.numop),'') AS numop4pl, " +
	      		  "IFNULL(pedidoi.refcli,'') AS nro_referencia, " +
	      		  "IFNULL(IF(openpo.numopbroker = 0,'',openpo.numopbroker),'') AS numopbroker, " +
	      		  "IFNULL(openpo.numdespachodjai,'') AS numdespachodjai, " +
	      		  "IFNULL(paises.pais,'') AS origen, " +
	      		  "IFNULL(openpo.depositointermedio,'') AS depositointermedio, " +
	      		  "IFNULL(openpo.numoc,'') AS numoc, " +
	      		  "IFNULL(IF(openpo.fecoc = 0,'',DATE_FORMAT(openpo.fecoc, '%d/%m/%y')),'') AS fecoc, " +
	      		  "IFNULL(openpo.incoterm,'') AS incoterm, " +
	      		  "IFNULL(tipo_producto.abrev,'') AS tipoproducto, " +
	      		  "IFNULL(via.nombre,'') AS via, " +
	      		  "IFNULL(openpo_articulo.articulo,'') AS articulo, " +
	      		  "IFNULL(openpo_articulo.descripcion,'') AS descripcion, " +
	      		  "IFNULL(openpo_articulo.cantidad,'') AS cantidad, " +
	      		  "IFNULL(unidades.lummuntmsr,'') AS uom, " +
	      		  "IFNULL(ROUND(openpo_articulo.fobuni,2),'') AS fobuni, " +
	      		  "IFNULL(monedas.ISO4217code,'') AS idmoneda, " +
	      		  "IFNULL(openpo.planner,'') AS planner, " +
	      		  "IFNULL(IF(openpo.fecenvioocalvendor = 0,'',DATE_FORMAT(openpo.fecenvioocalvendor, '%d/%m/%y')),'') AS fecenvioocalvendor, " +
	      		  "IFNULL(IF(openpo.fecRDD = 0,'',DATE_FORMAT(openpo.fecRDD, '%d/%m/%y')),'') AS fecRDD, " +
	      		  "IFNULL(IF(openpo.fecETS = 0,'',DATE_FORMAT(openpo.fecETS, '%d/%m/%y')),'') AS fecETS, " +
	      		  "IFNULL(IF(openpo.fecETSU = 0,'',DATE_FORMAT(openpo.fecETSU, '%d/%m/%y')),'') AS fecETSU, " +
	      		  "IFNULL(IF(openpo.fecrecepcionfactura = 0,'',DATE_FORMAT(openpo.fecrecepcionfactura, '%d/%m/%y')),'') AS fecrecepcionfactura, " +
	      		  "IFNULL(openpo.numfactura,'') AS numfactura, " +
	      		  "IFNULL(IF(openpo.fecCSD = 0,'',DATE_FORMAT(openpo.fecCSD, '%d/%m/%y')),'') AS fecCSD, " +
	      		  "IFNULL(IF(openpo.fecentradadepositointermedio = 0,'',DATE_FORMAT(openpo.fecentradadepositointermedio, '%d/%m/%y')),'') AS fecentradadepositointermedio, " +
	      		  "IFNULL(IF(openpo.okembarque = 1,'SI','NO'),'') AS okembarque, " +
	      		  "IFNULL(IF(openpo.fecsalidadepositointermedio = 0,'',DATE_FORMAT(openpo.fecsalidadepositointermedio, '%d/%m/%y')),'') AS fecsalidadepositointermedio, " +
	      		  "IFNULL(IF(openpo.fecllegadapuertoorigen = 0,'',DATE_FORMAT(openpo.fecllegadapuertoorigen, '%d/%m/%y')),'') AS fecllegadapuertoorigen, " +
	      		  "IFNULL(IF(openpo.fecETD = 0,'',DATE_FORMAT(openpo.fecETD, '%d/%m/%y')),'') AS fecETD, " +
	      		  "IFNULL(IF(openpo.fecSHIPPED = 0,'',DATE_FORMAT(openpo.fecSHIPPED, '%d/%m/%y')),'') AS fecSHIPPED, " +
	      		  "IFNULL(IF(openpo.fecdocumentotransporte = 0,'',DATE_FORMAT(openpo.fecdocumentotransporte, '%d/%m/%y')),'') AS fecdocumentotransporte, " +
	      		  "IFNULL(openpo.numdocumentotransporte,'') AS numdocumentotransporte, " +
	      		  "IFNULL(transporte.nombre,'') AS transporteinternacional, " +
	      		  "IFNULL(openpo.numcontenedor,'') AS numcontenedor, " +
	      		  "IFNULL(GROUP_CONCAT(c1.detalle SEPARATOR ' / '),'') AS cantidadcontenedor, " +
	      		  "IFNULL(IF(openpo.feccrucefrontera = 0,'',DATE_FORMAT(openpo.feccrucefrontera, '%d/%m/%y')),'') AS feccrucefrontera, " +
	      		  "IFNULL(IF(openpo.fecarribotransporteazonaprimariaaduanera = 0,'',DATE_FORMAT(openpo.fecarribotransporteazonaprimariaaduanera, '%d/%m/%y')),'') AS fecarribotransporteazonaprimariaaduanera, " +
	      		  "IFNULL(aduanas.nombre,'') AS aduana, " +
	      		  "IFNULL(IF(openpo.fecenviodocsabroker = 0,'',DATE_FORMAT(openpo.fecenviodocsabroker, '%d/%m/%y')),'') AS fecenviodocsabroker, " +
	      		  "IFNULL('','') AS fecenvioanexoabroker, " +
	      		  "IFNULL(IF(openpo.fecenviossppadtparafirmar = 0,'',DATE_FORMAT(openpo.fecenviossppadtparafirmar, '%d/%m/%y')),'') AS fecenviossppadtparafirmar, " +
	      		  "IFNULL(IF(openpo.fecenviossppfirmadoabroker = 0,'',DATE_FORMAT(openpo.fecenviossppfirmadoabroker, '%d/%m/%y')),'') AS fecenviossppfirmadoabroker, " +
	      		  "IFNULL(IF(openpo.fecrecepciondocsssppenbroker = 0,'',DATE_FORMAT(openpo.fecrecepciondocsssppenbroker, '%d/%m/%y')),'') AS fecrecepciondocsssppenbroker, " +
	      		  "IFNULL(IF(openpo.fecpresentacionprimeranmat = 0,'',DATE_FORMAT(openpo.fecpresentacionprimeranmat, '%d/%m/%y')),'') AS fecpresentacionprimeranmat, " +
	      		  "IFNULL(IF(openpo.fecpresentacionprimeriname = 0,'',DATE_FORMAT(openpo.fecpresentacionprimeriname, '%d/%m/%y')),'') AS fecpresentacionprimeriname, " +
	      		  "IFNULL(IF(openpo.fecaprobacionultimoanmat = 0,'',DATE_FORMAT(openpo.fecaprobacionultimoanmat, '%d/%m/%y')),'') AS fecaprobacionultimoanmat, " +
	      		  "IFNULL(IF(openpo.fecaprobacionultimoiname = 0,'',DATE_FORMAT(openpo.fecaprobacionultimoiname, '%d/%m/%y')),'') AS fecaprobacionultimoiname, " +
	      		  "IFNULL(IF(openpo.fecpresentacioniascav = 0,'',DATE_FORMAT(openpo.fecpresentacioniascav, '%d/%m/%y')),'') AS fecpresentacioniascav, " +
	      		  "IFNULL(IF(openpo.fecaprobacioniascav = 0,'',DATE_FORMAT(openpo.fecaprobacioniascav, '%d/%m/%y')),'') AS fecaprobacioniascav, " +
	      		  "IFNULL(IF(openpo.feccierredeposito = 0,'',DATE_FORMAT(openpo.feccierredeposito, '%d/%m/%y')),'') AS feccierredeposito, " +
	      		  "IFNULL(IF(openpo.fecoficializacion = 0,'',DATE_FORMAT(openpo.fecoficializacion, '%d/%m/%y')),'') AS fecoficializacion, " +
	      		  "IFNULL(IF(openpo.fecaprobaciondespacho = 0,'',DATE_FORMAT(openpo.fecaprobaciondespacho, '%d/%m/%y')),'') AS fecaprobaciondespacho, " +
	      		  "IFNULL(IF(openpo.fecturnocarga = 0,'',DATE_FORMAT(openpo.fecturnocarga, '%d/%m/%y')),'') AS fecturnocarga, " +
	      		  "IFNULL(IF(openpo.fecaplaza = 0,'',DATE_FORMAT(openpo.fecaplaza, '%d/%m/%y')),'') AS fecaplaza, " +
	      		  "IFNULL(IF(openpo.fecETW = 0,'',DATE_FORMAT(openpo.fecETW, '%d/%m/%y')),'') AS fecETW, " +
	      		  "IFNULL(IF(openpo.fecefectivaentregaenplanta = 0,'',DATE_FORMAT(openpo.fecefectivaentregaenplanta, '%d/%m/%y')),'') AS fecefectivaentregaenplanta, " +
	      		  "IFNULL(IF(openpo.fecrequeridaenplanta = 0,'',DATE_FORMAT(openpo.fecrequeridaenplanta, '%d/%m/%y')),'') AS fecrequeridaenplanta, " +
	      		  "IFNULL(IF(openpo.fecdescargaenplanta = 0,'',DATE_FORMAT(openpo.fecdescargaenplanta, '%d/%m/%y')),'') AS fecdescargaenplanta, " +
	      		  "IFNULL(IF(openpo.fecingresosistemaimportador = 0,'',DATE_FORMAT(openpo.fecingresosistemaimportador, '%d/%m/%y')),'') AS fecingresosistemaimportador, " +
	      		  "IFNULL(IF(openpo.fecpasadoafacturar = 0,'',DATE_FORMAT(openpo.fecpasadoafacturar, '%d/%m/%y')),'') AS fecpasadoafacturar, " +
	      		  "IFNULL(IF(openpo.fecfacturado = 0,'',DATE_FORMAT(openpo.fecfacturado, '%d/%m/%y')),'') AS fecfacturado, " +
	      		  "IFNULL(IF(openpo.feccierrelegajo = 0,'',DATE_FORMAT(openpo.feccierrelegajo, '%d/%m/%y')),'') AS feccierrelegajo, " +
	      		  "IFNULL(IF(operai.fecesp = 0,'',DATE_FORMAT(operai.fecesp, '%d/%m/%y')),'') AS fecETA, " +
	      		  "IFNULL(IF(openpo.fecokpovendor = 0,'',DATE_FORMAT(openpo.fecokpovendor, '%d/%m/%y')),'') AS fecokpovendor, " +
	      		  "IFNULL(terminal.nombre,'') AS terminal, " +
	      		  "IFNULL(operai.nomdes,'') AS despachante, " +
	      		  "IFNULL(operaiadd.cant_bultos,'') AS cant_bultos, " +
	      		  "IF( " +
	      		  "openpo.fecSHIPPED=0,'', " +
	    		  "DATE_FORMAT(DATE_ADD(openpo.fecSHIPPED, INTERVAL 4 DAY), '%d/%m/%y')" +
	      		  ") AS Arrival_Date_Estimate_Frontera_BR, " +
	      		  "IFNULL(GROUP_CONCAT(c1.tipo_contenedor SEPARATOR ' / '),'') AS tipo_contenedor, " +
	      		  "IF(openpo.fecingresosistemaimportador>0 AND openpo.fecefectivaentregaenplanta>0, " +
	      		  "(openpo.fecingresosistemaimportador-openpo.fecefectivaentregaenplanta) " +
	      		  ",'') AS LT, " +
	      		  "IFNULL(operaiadd.aviso_carga,'') AS aviso_carga, " +
	      		  "IFNULL(IF(operaiadd.fecturno_carga_efec = 0,'',DATE_FORMAT(operaiadd.fecturno_carga_efec, '%d/%m/%y')),'') AS fecturno_carga_efec, " +
	      		  " " +
	      		  "IF(openpo.fecefectivaentregaenplanta=0,0,IF(DATE_SUB(openpo.fecefectivaentregaenplanta, INTERVAL 2 DAY)>CURDATE(),1,0)) AS plantamas48hs," +
	      		  "1 " +
	      		  " " +
	      		  "FROM openpo " +
	      		  "JOIN operai ON operai.tripli = openpo.numop  " +
	      		  "LEFT JOIN operaiadd ON operaiadd.`numop` = openpo.numop  " +
	      		  "LEFT JOIN openpo_articulo ON openpo_articulo.idopenpo = openpo.id AND openpo_articulo.anulado = 0 " +
	      		  "LEFT JOIN proveedor ON proveedor.id = openpo.idvendor " +
	      		  "LEFT JOIN tipo_producto ON tipo_producto.id = openpo_articulo.idtipoproducto " +
	      		  "LEFT JOIN unidades ON CAST(unidades.kunfac AS CHAR) = CAST(openpo_articulo.idmedida AS CHAR) " +
	      		  "LEFT JOIN transportista AS transporte ON transporte.id = openpo.idtransporteinternacional " +
	      		  "LEFT JOIN aduanas ON CAST(aduanas.codadu AS CHAR) = CAST(openpo.idaduana AS CHAR) " +
	      		  "LEFT JOIN via ON CAST(via.codvia AS CHAR) = CAST(openpo.idvia AS CHAR) " +
	      		  "LEFT JOIN monedas ON CAST(monedas.cdev AS CHAR) = CAST(openpo.idmoneda AS CHAR) " +
	      		  "LEFT JOIN paises ON CAST(paises.idpais AS CHAR) = CAST(openpo_articulo.idorigen AS CHAR) " +
	      		  "LEFT JOIN pedidoi ON pedidoi.id = openpo.idpreembarque " +
	      		  "LEFT JOIN ( " +
	      		  "SELECT " +
	      		  "contenedor.numop, " +
	      		  "contenedor.tipo_contenedor, " +
	      		  "CONCAT(COUNT(contenedor.tipo_contenedor),' x ',contenedor.tipo_contenedor) AS detalle " +
	      		  "FROM contenedor " +
	      		  "WHERE 1 " +
	      		  "GROUP BY contenedor.numop, contenedor.tipo_contenedor " +
	      		  ") AS c1 ON c1.numop = openpo.numop  " +
	      		  "LEFT JOIN terminal ON terminal.`id` = operaiadd.`id_terminal`  " +
	      		  "WHERE 1 " +
	      		  "AND openpo.anulado = 0 " +
	      		  "AND operai.fecbaja = 0 " +
	      		  "#WHERECLAUSE# " +
	      		  "#GROUPBY# " +
	      		  "HAVING 1 " +
	      		  "#HAVING# " +
	      		  "#ORDERBY# ";

	        return strSql2;
		}
		
	  	/**
	  	 * Replaces SQL query with data
	  	 * @param strSql
	  	 * @param where
	  	 * @param group
	  	 * @param order
	  	 * @param having
	  	 * @return strSql
	  	 */
		public String replaceClausesSQL(String strSql2, String where, String group, String order, String having) {
			strSql2 = strSql2.replaceAll("#WHERECLAUSE#", where);
			strSql2 = strSql2.replaceAll("#GROUPBY#", group);
			strSql2 = strSql2.replaceAll("#HAVING#", having);
			strSql2 = strSql2.replaceAll("#ORDERBY#", order);
			return strSql2;
		}			
		
		/**
	  	 * Auto size columns 
	  	 * @param sheet
	  	 * @param maxCell
	  	 */
		public void autoAdjustColumns(XSSFSheet sheet, int maxCell) {
			for(int k = 0; k < maxCell; k++){
				sheet.autoSizeColumn(k);
			}
		}
		
		/**
	  	 * Get default style 
	  	 * @return style
	  	 */
		public XSSFCellStyle getStyle() {
			return style;
		}

		/**
	  	 * Set default style 
	  	 * @return style
	  	 */
		public void setStyle(XSSFCellStyle style) {
			this.style = style;
		}

		/**
	  	 * Get default numeric style 
	  	 * @return style
	  	 */
		public XSSFCellStyle getNumericStyle() {
			return numericStyle;
		}

		/**
	  	 * Set default numeric style 
	  	 * @return style
	  	 */
		public void setNumericStyle(XSSFCellStyle numericStyle) {
			this.numericStyle = numericStyle;
		}
		
		abstract String getStringSQL(int sheet);
		
		abstract int writeDataRow(int sheet, XSSFRow row);
		
	}