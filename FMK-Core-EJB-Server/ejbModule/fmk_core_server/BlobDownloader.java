package fmk_core_server;

import java.io.InputStream;
import java.sql.ResultSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BlobDownloader extends AbstractMultiSentenceDBServiceHTTPFileOutput {
	private ServletOutputStream sOutStream=null;
	private InputStream in=null;
	
	//OBS: en el mismo svc se pueden hacer otras operaciones sql etc (idem multiservice), lo que no se
	// 	   puede es devolver xml xq justamente anulé el output writer del response xa obtener un 
	//	   output stream del mismo xa poder enviar archivos ( si usas uno no se puede usar el otro ya )
	
	//	!! ASÍ QUE EN RESÚMEN, es IDEM multiservice pero no puede sacar xml sino un blob como rtado al cli !! 
	
	@Override 
	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception  {
		try{
			setResources(strSvcName, req, res); //idem orig extended (ioe)
			initServiceImpl(); 
			conn = getConnection(); //queda, idem orig extended
			
			String strSQL2 = loadSQLQuery();			
			ps = conn.prepareStatement(strSQL2); //xq strSQL me queda en "" ????!!! 0____________0 20141218
			
			//------- solveSvcParameters();
			fillService();
			fillLogicVars (); //tmb se usa/queda idem (deja hlogicalParamVars cargado con strKey varParamName y oValue )

			executionAlgorithm(); 
			writeResponse(); //queda, aunq es sobreEscrito, xa que escriba en la salida
		}catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	@Override 
	public void executionAlgorithm() throws Exception {
		try{
			rs = ps.executeQuery(); //siempre es un executeQuery (xq es xa Bajar blob..).. simil GenericSelectDBService.. 
		
			if (rs!=null) {
				rs.next();
					
				//New!: 
				//	-> Lo nuevo: POST exec select y prev a writeResponse: !!!
	
				//a) toma el blob :
				java.sql.Blob blob = rs.getBlob(getBlobColumnName()); //
				
				//b) obtiene el in xa usar en el out 
				in = blob.getBinaryStream();
				
				//c) arma el fileName y lo setea a la var
				setFileName(getFileName()); //el getFileName lo redef 'ellos' yme lo guardo en la var
				
				// ..  y luego obvio el writeResponse lo escribe..  
				
				/* 20130311	
				converter.setRecordSet(rs);
				converter.setConfKey( strServiceName);
				//converter.convert(itemTag, o); //el converter xml se encaga ahora de todo el xml de rta sino asumiria
				// un header ej root xml de rta xa todo converter.
				
				converter.convert(o,strServiceName,DEFAULT_SERVICE_CONF,SingletonServicesConfiguration.getInstance());
				*/
			}
		}catch(Exception e){
			logger.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}
	
	//esta x si necesitan reescribir el executionAlgorithm entonces wrappeo el code necesario q si o si deben igual
	//tener : DEBEN INVOCARLO AL FINAL de SU! executionAlgorithm
	public void writeBlob(ResultSet res, String blobFieldName) throws Exception {
		try {
			//a) toma el blob :
			java.sql.Blob blob = res.getBlob(blobFieldName); //si usan esta ent explicitamente obliga a 
															 //pasar el blobFieldName..
			//b) obtiene el in xa usar en el out 
			in = blob.getBinaryStream();
			
			//c) arma el fileName y lo setea a la var
			setFileName(getFileName()); //el getFileName lo redef 'ellos' yme lo guardo en la var
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	//El servicio genérico de la ejecucion de 1 solo select y que devuelve su RS en xml usa el converter.
		//a dif de los update / ins / bool o multiservice, se sobrescribe xa q no invoque el standardActionXmlResult,
		//básicamente este usa el conversor dentro del cual imprime incluso el header root y todo..
		@Override
		public void writeResponse() throws Exception {
			try {
				String mimetype = "application/octet-stream";	 // The Internet media type for an arbitrary byte stream is application/octet-stream.
				 // sino tmb x ej:  resp.setContentType("application/pdf"); .. etc .. !!
				res.setContentType(mimetype);
				
				res.setHeader("Content-Disposition", "attachment; filename=\"" +  outFileName + "\""); // getFileName() + "\""); 	// -> sets HTTP header .. 
								 
				//20140627 sOutStream  = res.getOutputStream();
				
				
				//FileOutputStream fileOut = new FileOutputStream("d:/tmp/Test.xlsx");
				//pru 20140626
				//20140627 wb.write(sOutStream); // OJOOOO -> Ver que funque, le estoy pasando q escriba en otro tipo de outPutStream aunq tmb extiende 
				//	o.close();
				sOutStream = res.getOutputStream();
				//en excel era asi -> wb.write(sOutStream); 
				//´sin excel asi no es -> sOutStream.write(in);
				//es asi:
				byte[] buffer = new byte[10240];
				for (int length = 0; (length = in.read(buffer)) > 0;) {
					sOutStream.write(buffer, 0, length);
			    }
				
				//					 outPutStream q es lo q en realidad espera el metodo write de la clase poi workbook osea q deberia funcar..
				
				sOutStream.flush();			//fileOut.flush();
				//lo muevo al finally! sOutStream.close();			//fileOut.close();			
				
				
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}finally {
				sOutStream.close();			//fileOut.close();
			}
			
		}
	
			
		
	@Override
	public void setWriter(HttpServletResponse res) throws Exception {
		//skip (no abre/pide al res el writter, asi puede usar el getOutPutStream();
	}
	//Como no conocen el fileName de antemano , al fileName lo tienen q setear a una var
	// y el sistema abstracto usa los getters en el codigo. 

//-- com fileName (seteo dinamico en code (t. de ejecucion) ):	
	private String outFileName=null;
	
	//Este no lo conocen de antemano, tienen q setear una var en el codigo dinámico y el esquema
	//abstracto usa dicha var.
	
	//public abstract String getFileName() throws Exception; //que retorne x ej myImg.jpg / myDoc.txt etc etc..
	
	/* Tienen que sobreEscribir el metodo (el esqueleto generico del executionService
	 * lo invoca luego de ejecutar el rs 
	public  void setFileName(String outputFileNameParam) throws Exception {
		this.outFileName=outputFileNameParam;
	}*/
	//public abstract void setFileName(String outputFileNameParam) throws Exception;
	public void setFileName(String outputFileNameParam) throws Exception {
		this.outFileName=outputFileNameParam;
	}
	
	
	
	/*@Override NO IBA ASI, SE DEJA A REDEFINIR, AHI VA EL ARMADO CUSTOM DEL FILENAME.
	public String getFileName() throws Exception {
		return this.outFileName; //user app final usa el setter xa dyn en code setear la var
	}	*/						 // y este prog usa la var ( o el setter q ya lo heredaba y listo)
// -- fin fileName 
	
	//Seteos Estaticos  (conocidos de antemano en la impl concreta del servicio)
	//					(se setean en el initServiceImpl).
	//blobColumnName
	private String blobColumnName=null;

	public abstract String getBlobColumnName() throws Exception; //ESTE SI LO CONOCEN de anteMano
	
	 @Override
		public void initServiceImpl() throws Exception { 
	    	try {
				blobColumnName= getBlobColumnName(); // strFileName = getFileName(); //EXCELFILE; //"file.xlsx"; 
	    	}
	    	catch(Exception e){
	    		throw new Exception(e.getMessage());
	    	}
		}
	 private void fakeForModif1(){};
}
