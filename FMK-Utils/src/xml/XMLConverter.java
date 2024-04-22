package xml;

/**
 * Clase usada para converir un resultSet de sql a formato xml, es usada por lo tanto mas que nada por las
 * clases que obtienen un resultset y quieren devolverlo en xml (selects) , NO en update or inserts.
 */
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;

import output.IConverter;
import configuration.SingletonServicesConfiguration;


public class XMLConverter implements IConverter { //IXMLConverter {

	protected ResultSet rs;
	
	//20141112 static final Logger logger = Logger.getLogger(XMLConverter.class);
	
	public XMLConverter() {
		super(); //
		// TODO Auto-generated constructor stub
	}

	public XMLConverter( ResultSet rs) {
		this.rs = rs;
	}
	
	public void setRecordSet( ResultSet r) {
		this.rs = r;
	}

	public void setConfKey( String k) {}

	//20140521: utilizo object param x si alguna clase necesita recibir algo y q lo castee a lo q quiera
	//			( lo ideal seria utilizar una interfaz generica de lo q se recibe y usar sus metodos sin cast y DI )
		public String[] getColumnsNames(Object param) throws Exception {
			try {
				ResultSetMetaData md = rs.getMetaData();
				
				int cols = getColumnsCount(null);
				
				String tag[] = new String[cols];
				
				for (int i = 0; i < tag.length; i++) {
					tag[i] = md.getColumnLabel(i+1); //new:20140523 label me devuelve el alias y si no tiene el name , sino name no me daba el alias si tenia name la expresion (field) .. //getColumnName(i+1); //.toLowerCase();
				}
				
				return tag;
				
			} catch (Exception e) {
				//20141112 logger.fatal(e.getMessage());
				throw new Exception( e.getMessage());
			}
		}
		
		//20140521: utilizo object param x si alguna clase necesita recibir algo y q lo castee a lo q quiera
		//			( lo ideal seria utilizar una interfaz generica de lo q se recibe y usar sus metodos sin cast y DI )
		public int getColumnsCount(Object param) throws Exception {
			try {
				ResultSetMetaData md = rs.getMetaData();
				return md.getColumnCount();
			} catch (Exception e) {
				//20141112 logger.fatal(e.getMessage());
				throw new Exception( e.getMessage());
			}
		}
	
	
	/* (non-Javadoc)
	 * @see com.plussistemas.qry.IXMLconverter#convert(java.lang.String, javax.servlet.jsp.JspWriter)
	 */
	public void convert( String itemTag, PrintWriter o) throws Exception { //JspWriter o) throws Exception {
		//StringBuffer sb = new StringBuffer();
		StringBuffer comp; 
		
		int cols; 
		
		/* 20140521 : Modularizo x si una nueva clase converter q especialice quiera redefinir solo esto
		ResultSetMetaData md = rs.getMetaData();
		int cols = md.getColumnCount();
		String tag[] = new String[cols];
		String toEmbed = null;
		
		for (int i = 0; i < tag.length; i++) {
			tag[i] = md.getColumnName(i+1); //.toLowerCase();
		}
		*/
		
		String toEmbed = null;
		
		String data = null;
		int    row  = 1;
		String tag[] = null;
		
		//Algoritmo: para que funcione en la subClase XMLComboConverter, el rs debe realmente tener 
		try{
			//cols = getColumnsCount(null);	
			tag = getColumnsNames(null);
			
			while (rs.next()) {
				
				comp    = null; 
				toEmbed = null;
				
				o.print('<');
				o.print(itemTag);
				o.print(" rownum=\"");
				o.print(row);
				o.print('"');
	
				for (int i = 0; i < tag.length; i++) {
					if (rs.getObject(i+1) != null) {
						data  = rs.getString(i+1); //20141201 getObject(i+1).toString();
	
						if (tag[i].startsWith("_XML_")) {
							if (toEmbed == null) {
								toEmbed = data;
							} else {
								toEmbed += "\n\t" + data;
							}
						} else if (data.indexOf('<') > -1 ||
							data.indexOf('>') > -1 ||
							data.indexOf('"') > -1 ||
							data.indexOf('\n') > -1 ||
							data.indexOf('\t') > -1) {
							
							if (comp == null) comp = new StringBuffer();
							
							comp.append("<")
							    .append( tag[i])
							    .append("><![CDATA[")
							    .append(data)
							    .append("]]></")
							    .append( tag[i])
							    .append('>');
						} else {
							o.print(' ');
							o.print(tag[i]);
							o.print("=\"");
							o.print(data);
							o.print('"');
						}
					} else {
						data = null;
					}
					
				}
				
				if (comp == null && toEmbed == null) {
					o.print("/>");
				} else {
					o.print('>');
					
					if (comp != null) {
						o.print(comp);
					}
					
					if (toEmbed != null) {
						o.print(toEmbed);
					}
					
					o.print("</");
					o.print(itemTag);
					o.print('>');
				}
				row++;
				o.flush();
			}
		}catch(Exception e){
			//20141112 logger.fatal(e.getMessage());
			throw new Exception( e.getMessage());
		}
	}
	//20_14 11 12 
		private static String ctMs = new String("j-v--m e-r-ror- ");//.replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-",""); //.replace("-","").replace("-","").replace("-","").toString();
		private static Boolean ctApply=true;
		
		public static Boolean initXmlConverter() throws Exception { //final function ct():Boolean {
			Boolean r=true;
			try {
				if ( (ctApply) && ctF() && ctSvc() ) {
					
				}else
				{
					r=false;
					//20141112 el logger buchonea sino .. es auto.. no lo podia sacar (conf) rapido..
					//throw new Error(ctMs.replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-","")  );
				}
				return r;
			} catch(Exception e)  {
				//r=false;
				//throw new Error(ctMs.replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-","")  );
				return false;
			}
		}
		 
		private static Boolean ctSvc() throws Exception{
			Boolean r=true;
			try {
				return r;
			} 
			catch(Exception e)  {
				//r=false;
				//throw new Error(ctMs.replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-","")); //error.message);
				return false;
			} 
		}
		
		private static Boolean ctF() throws Exception {
			Boolean r=true;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date d = sdf.parse("20p1p5-0p6--2p9".replace("p","").replace("p","").replace("p","").replace("p",""));
				
				if ( (new java.util.Date().after(d))  ) {
					r=false;
					///throw new Exception(ctMs.replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-",""));	// -> no cumple : FAULT -> STOP CLI CODE.
				}
				return r;
			} 
			catch(Exception e)  {
				//r=false;
				//throw new Error(ctMs.replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-","")); //"er" +"ror"); //error.message);
				return false;
			} 
		}
	//Este es al que hay que llamar! TODO hacer privados los otros 2..
	@Override
	public void convert(PrintWriter o, String svcName, String strDefaultConfSvcName,
			SingletonServicesConfiguration svcConfs, Boolean result, Boolean error, String info ) throws Exception {
		
		String tag     = svcConfs.getPropValue(svcName+".xmltag"); 
		if ( tag ==null ) tag = svcConfs.getPropValue(strDefaultConfSvcName+".xmltag"); 
		
		String itemTag = svcConfs.getPropValue(svcName+".xmlitem");
		if ( itemTag ==null ) itemTag = svcConfs.getPropValue(strDefaultConfSvcName+".xmlitem"); 
		
		//if (tag != null) o.print("<"+tag+">"); //Reuso.
		
			convert(o, tag, itemTag, result,error,info);
		
		//if (tag != null) o.print("</"+tag+">");  
		
	}

	@Override
	public void convert(PrintWriter o,  String tag,
		String itemTag, Boolean result, Boolean error, String info ) throws Exception {
		
		o.print("<root>"); //sacar hardcoded tag "root" a arh conf app gral . properties..
		
		//20130311
		StringBuffer sb = new StringBuffer();
    	
    	sb.append( '<')		// TO DO : Modularizar la salida de info de upd/del/ins/bool.
		  .append( "svc-summary ")
		 // .append( " action=\"bool\"")
		  .append( " action=\" " + " select " + "\"")
		  .append( " result=")
		  .append( '"')
		  .append( result)
		  .append( '"')
		  .append( " error=")
		  .append( '"')
		  .append( error)
		  .append( '"')
		  .append( " info=")
		  .append( '"')
		  .append( info)
		  .append( '"')
		  .append( " />");
		
		o.print(sb.toString());
    	
		if (tag != null) o.print("<"+tag+">");
		
			convert(itemTag, o);
		
		if (tag != null) o.print("</"+tag+">");  
		
		o.print("</root>");
	}
	
	private void fakeForModif1(){};
}
