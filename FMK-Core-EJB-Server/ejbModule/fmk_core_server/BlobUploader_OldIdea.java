package fmk_core_server;

import java.io.FileOutputStream; //

import java.io.InputStream;
import java.sql.ResultSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Fuck: necesito aplicar el insert a 1 reg en particular, x lo q debe llegar un id xa el where! , además, 
 * 		 pueden querer meter más cosas en el where asi que puede concatenarse el id a la var where con un and
 * 		 (inicializando esta en 1=1 ) xa q no joda en lugar de "" y listo. PERO, ENTONCES TENGO QUE RESOLVER EL
 * 		 TEMA DE Q DEBO MANDAR EL ID JUNTO CON EL FILE!. 
 * 		 Conclusión, quedaría esta clase q hace lo básico pero todo auto (x conf) y arma sql dinámico , x lo q
 * 					 no necesitaría def de sql en el properties ni nada. Incluso si ctes de conf estuvieran en
 * 					 el properties, ent con  una unica clase usuaria en el modulo cli como uploaderExample
 * 					 alcanzaría!. ( simil getDataTableForCombo y TODO simil downloader) -> COSA QUE NO TENGAN QUE
 * 					 CREAR CLASES (1 clase nueva x c/ svc del mismo tipo solo xq varian las ctes!) sino solo 
 * 					 cuando quieran un execution algorithm diferente. 
 * 		 Igual tmb voy a modif el fillService de abstractDBService así todas ya contemplan .type=blob y ahí
 * 		 podrían hacer inserts con la genérica de insert y def en properties, y tmb todo lo demás ej update etc.
 *
 *		NEW: TODO SACAR TODAS LAS KTES AL PROPERTIES Y Q LA ABSTRACTA LAS LEVANTE 1 A 1 !!! ASI NO ESTOY OBLIGADO
 *			 A CREAR 1 CLASE X CADA IMPL SIMILAR solo x las ktes !!!!
 *
 *		( yl directamente modificar el fillservice de siempre y q se haga todo como un insert o update normal,
 *		  aunq si los hago usar esta clase es mas mágico y mete mas ruido xq terminó siendo medio como una clase 
 *		  al pedo, xq con lo de modificar el fillservice de abstractDBService alcanzaba xa manejarse basicamente
 *		  igual q siempre .. pero .. bue.. de ultima se los digo mas adelante. Igual tienen la opcion de redef
 *		  el execution algorithm de esta clase O de hacerlo con la modalidad normal cuando se los comente.
 */

public abstract class BlobUploader_OldIdea extends AbstractMultiSentenceDBServiceConverter{
	private ServletOutputStream sOutStream=null;
	private InputStream in=null;
	
	//OBS: Este es 100% idem multiservice, acá si se puede es devolver xml xq justamente no anulé el output writer 
	//	   ya q no extiendo de HttpFileOutput sino del comun multiSentenceConverter (xml output) en este obvio lo 
	//	   q NO se puede es responder files ,enviar archivos,loCual estáOk (yaQsi usas uno NO se puede usar elOtro)
	
	//	!! ASÍ QUE EN RESÚMEN, es IDEM multiservice pero no puede sacar xml sino un blob como rtado al cli !! 
	
	//Seteos Estaticos  (conocidos de antemano en la impl concreta del servicio)
	//					(se setean en el initServiceImpl).
	

	public static final int UPDATE_MODE =1;
	public static final int INSERT_MODE =2;
	
	private int mode=UPDATE_MODE;
	
	private boolean useEntityIdWhereFilter=true;
	
	private String entityIdFieldName="";
	
	
	private boolean useFileNameColumn=false;
	private boolean useFileExtensionColumn=false;
	private String tableName="";
	private String whereClause=" 1 = 1 "; //si no lo cambian se hace el and del id=reqGetHttpParam y de esto 
										 // si lo cambian ent del id = param & lo q pongan.
	
	private String blobColumnName=null; // <- lo usa el esqueleto abstracto en base a lo seteado x las ktes y 
										//		los getters de olas mismas.
	private String blobFileNameColumnName=null; 
	private String blobFileExtensionColumnName=null; 
	private String blobFileNameValue=null; 
	private String blobFileExtensionValue=null; 
	
	public abstract int getMode() throws Exception; 
	
	public abstract String getBlobColumnName() throws Exception; // LO CONOCEN de anteMano
	public abstract String getBlobFileNameColumnName() throws Exception; //ESTE SI LO CONOCEN de anteMano
	public abstract String getBlobFileExtensionColumnName() throws Exception; //ESTE SI LO CONOCEN de anteMano
	
	public abstract String getBlobFileNameValue() throws Exception; //ESTE SI LO CONOCEN de anteMano
	public abstract String getBlobFileExtensionValue() throws Exception; //ESTE SI LO CONOCEN de anteMano

	public abstract boolean getUseFileNameColumn() throws Exception; 
	public abstract boolean getUseFileExtensionColumn() throws Exception; 
	public abstract String getTableName() throws Exception; 
	public abstract String getWhereClause() throws Exception; 
	
	public abstract boolean getUseEntityIdWhereFilter() throws Exception; 
	
	public abstract String getEntityIdFieldName() throws Exception; 
	
	
	//EN este metodo se setean todas las ktes del usuario a variables privadas xa el uso esqueleto de la clase
	 @Override
		public void initServiceImpl() throws Exception { 
	    	try {
				blobColumnName= getBlobColumnName(); // strFileName = getFileName(); //EXCELFILE; //"file.xlsx"; 
				
				blobFileNameColumnName=getBlobFileNameColumnName();	//el init las setea de las kts y el 
				blobFileExtensionColumnName=getBlobFileExtensionColumnName(); // execute las usa xa armar un
				blobFileNameValue=getBlobFileNameValue();	// string sql dinamico xa el ps y ejecutarlo.
				blobFileExtensionValue=getBlobFileExtensionValue(); // se devuelve xml out x ok o exception x err.
	    	
				useFileNameColumn=getUseFileNameColumn();
				useFileExtensionColumn=getUseFileExtensionColumn();
				tableName=getTableName();
				whereClause=getWhereClause();		 
				   
				mode=getMode();
	    	}
	    	catch(Exception e){
	    		throw new Exception(e.getMessage());
	    	}
		}
	 
	@Override 
	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception  {
		try{
			setResources(strSvcName, req, res); //idem orig extended (ioe)
			initServiceImpl(); 
			conn = getConnection(); //queda, idem orig extended
			
			//Hago la op1 PERO luego hago la op 3 q seria la posta si pudiera viajar todo (data y file de una)
			//ent 	xsobrecargo y redefino loadSQL xa q enBase a todos los preseteos,arme un sql DINAMICO de blob con
			//ellos! -> Obs: -> !! el uso de los campos fileName y fileExtension deben ser opcionales.
			//Si necesitan un sql distinto pueden sobrecargar de nuevo el loadSQL y poner el de ellos 
			String strSQL2 = loadSQLQuery(); // <- PUEDO: op1) q definan en properties pero q type? ENT: MEJOR:
											 // OPCION 2!: -> x ahora, redefino el loadSQL xa q en base a los
											 // 			  valores ya seteados de nombre, extension, y el file
											 // 			  yLos nombresTmb deDichos campos (x si son <>s c/vez!)
											 // esto xq solo puedo mandar un file con el uploader y no una url?
											 // OP3: + adelante, si sin usar el uploader de flex pudiese manejarme
											// 		o usandolo pero sin usar .upload method, sino enviarlo mediante
											//		un request standard x post y attacharle el stream del file 
											//		como objeto adjunto x post pero tmb en la url enviar como 
											//		siempre todo lo demás! ent si ambas cosas llegan, ent en
											//		el properties definen todo y en el type del campo blob ponen
											//		x ej, .type=blob luego el fillService si lee type blob ent
											//		toma el file del req obteniendo un inputStream yHace ps.setBlob
											//		de ese stream q es el file, y no q lo toma del req url (x get)
											//		osea q en ese caso no tiene q usar el param del req sino usar
											//		el file del req (inputStream) OBS!-> Para MANTENER EL ORDEN,
											// 		deberia enviarse null en esa posicion (totalfillservice usa
											//		el is y no el val del url get, pero mantendria las posiciones
											//		enviar un null en ese lugar x ej, O SINO q si es blob ent 
											//		tiene qSeguir leyendo losParams get desde el hash enOrden PERO
											// 		TIEWNE QUE SETEARLOS a partir de ahi en i+1 !! OJO !!!! 
											//		( no modificar el i xq desp tiene q leer el sig inmediato del
											//		hash PERO SI grabar ya en ps.set(campoXpostBlob,i+1) ent ese
											//		i q representa la pos del ps debe ser una var q siempre aarran
											//		que siendo = a i y se vaya siempre igualando a i pero si se
											//		encuentra un blob ent debe inc en 1 ese val x yL tmb en 1 mas
											//		x el code comun a todo tipo de dato.. (x lo q en esaa vuelta
											//		se incrementaria en 2 el x, y el i seguiria igual (en 1 menos)
											//		ent no se saltea ningun param del url get req hash PERO ya los
											//		empieza a grabar en i+1 digamos (tomando en cuenta el lugar
											//		q ocupó el blob q no está en el hash sino q vino del req is)!
											//		ESTA ULTIMA OP ES MEJOR XQ NO TIENEN Q MANDAR null ni nada
			ps = conn.prepareStatement(strSQL2); //xq strSQL me queda en "" ????!!! 0____________0 20141218
			
			//------- solveSvcParameters();
			
			//Lo que puedo hacer acá es sobreEscribir el fillService xa q setee el blob y opcionalmente los 
			//otro 2 campos.
			fillService(); //-> esta no la puedo usar xq no contempla blob. ademas no pueden viajar blob y datos?
			
			// IMPORTANTE! -> SI NECECITAN GRABAR UN REG tmb con un blob, ent 2 servicios, si alguno falla ent
			//				  rollback a mano (se contralaria desde flex). Otra Opcion seria ver si se pueden
			//				  enviar datos en la url y un file tmb attachado en el post.. (ver..!). <- TODO !!!!!!
			
			fillLogicVars (); //tmb se usa/queda idem (deja hlogicalParamVars cargado con strKey varParamName y oValue )

			executionAlgorithm(); 
			writeResponse();  //saca el xml boolean default (q significa todo ok, sino seria exception a flex)
		}catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	//Arma el strSQL standard. contempla fileName y fileEXtension (optional fields) 
	//Pone 1ero el blob, segundo el fileName y 3ero el file extension (siempre asi ,x convención nada mas. )
	
	@Override
	public String loadSQLQuery() throws Exception{
		//return loadSQLQuery(".sql");
		String strTmp=null;
		try {
			 
			//MAL, ES UN UPDATE EN REALIDAD. PERO Y SI LLEAN A necesitar update ??!!
			if (mode==UPDATE_MODE) { 
				strTmp = getUpdateSQL();
			}else {
				strTmp=getInsertSQL();
			}
			
			String strCompleteWhere=" WHERE ";
			
			if (useEntityIdWhereFilter) {
				strCompleteWhere = entityIdFieldName + " = " +  new String(this.req.getParameter("id")) + " AND ";
			}
			
			strCompleteWhere += whereClause; //x defecto tiene 1 = 1
			
			strTmp = strCompleteWhere;
			
			return strTmp;
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public String getUpdateSQL() throws Exception{ 
		String strTmp=null;
		try {
			strTmp="UPDATE " + tableName + " SET "  + blobColumnName + " =  ? ";
			
			if ( useFileNameColumn ) { // x defecto no se usa (xero si el user lo pone explicitamente en true si). 
				strTmp+= " , " + blobFileNameColumnName  + " =  ? ";
			}
			
			if ( useFileExtensionColumn ) { // idem.
				strTmp+= " , " + blobFileExtensionColumnName  + " =  ? ";
			}
			
		//	strTmp += whereClause; //x defecto tiene 1 = 1
			
			return strTmp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public String getInsertSQL() throws Exception{ 
		String strTmp=null;
		try {
			strTmp="INSERT INTO " + tableName + " ( "  + blobColumnName + " ";
			
			if ( useFileNameColumn ) { // x defecto no se usa (xero si el user lo pone explicitamente en true si). 
				strTmp+= " , " + blobFileNameColumnName + " " ; 
			}
			
			if ( useFileExtensionColumn ) { // idem.
				strTmp+= " , " + blobFileExtensionColumnName + " " ; 
			}
			
			strTmp += " ) ";
			
			strTmp += " VALUES ( " + " ? " ;
			
			if ( useFileNameColumn ) {
				strTmp+= " , " + " ? " ; 
			}
			
			if ( useFileExtensionColumn ) {
				strTmp += " , " + " ? " ; 
			}
			
			strTmp += " ) ";

		//	strTmp += whereClause; //x defecto no tiene nada. 
			
			return strTmp;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	@Override
	public void fillService() throws Exception{   // se SobreEscribe fillService ajustado a este essquema basico
		InputStream blobFile=null;
		try{
			//fillService("");
			//1) call obtenerFile ( podria ser un metodo public x si lo necesitan en impls + personalizadas)!!
			//		SACARLO DEL EJEMPLO DEL SERVICE LOCATOR!
			blobFile=getFile();
			int iPos=1;
			//2) SETEA EL BLOB usando ese inputStream obtenido en (1)
			 	ps.setBlob(iPos, blobFile);
			//3) 
			 	if ( useFileNameColumn ) { // x defecto no se usa (xero si el user lo pone explicitamente en true si).
			 		ps.setString(++iPos, getBlobFileNameValue()); 
			 	}
			//4) 
			 	if ( useFileExtensionColumn ) { // idem.	
			 		ps.setString(++iPos, getBlobFileExtensionValue()); 
			 	}
			//y listo.
			
			// TODO : hacer esto, + probar. luego añadir lo q falta al ecommand_file en flex. y LISTO X ahora,
			//	Luego , TODO : Probar enviar file como attachment stream en el ecommand y ver en el servlet
			//	del fmkria web app si le llegan ambas cosas (url get completa + params y x otro lado el file
			//	como attachment por post y q lo pueda obtener x separado como un inputStream, SI SE PUEDE Q 
			//	LLEGAN AMBAS COSAS, ENTONCES, implementar opcion 3 q seria modificar el fillService original
			//  ( ppio de clases heredadas ) xa q contemple como detallé anteriormente en esta clase (execute)
			//	y q tome x sep los params y l si .type = blob ent el file y posPs ++ (ent qda 1 arriba de pos
			//	params hash q se mantiene igual (solo inc en 1) y qda 1 abajo y todo se mantiene y tndria q
			//	funcar, el fillservice añadiria al case o ifs anidados q si es un blob obtiene el file 
			//	con la func ya expuesta aca ( la probada en el service locator web q funciona y dev el is)
			//	y ent usa ps.setBlob en pos i y luego inc x en 1 ( x va a ser una nueva var q viene todo como
			//	i h' q encontré un .type = blob, ent, ahi ademas del setBlob pasa a inc en 1 adicional al i,
			// luego fuera del case, previa a"la vuelta al bucle" (code q se ejecuta xa todo type) hace :
			// i+++, x++ ( ent x ya qdó 1 arriba, i sigue trabajando ok con el hash sin saltearse ninguno pero
			// el ps (valor,x) setea en i +1 (osea en x) :)
			
		}catch(Exception e){
			logger.fatal(e.getMessage());
			throw new Exception(e.getMessage()); 
		}finally {
			try {
				blobFile.close();
				//out.close();
			} 
			catch (Exception e2) {
				//System.out.println(e2.getMessage());
				throw new Exception(e2.getMessage());
			}  
		}
	}
	
	public InputStream getFile() throws Exception {
		 
		/* FileOutputStream out=null; */
		 
		 InputStream input = null; 
		  
		try {
		
			input = req.getInputStream();
			
			/* byte[] buffer = new byte[4096];
			int n = - 1;
		    out =new FileOutputStream("c:\\morsaReloaded.jpg");
			while ( (n = input.read(buffer)) != -1)
			{
			    if (n > 0)
			    {
			        out.write(buffer, 0, n);
			    }
			} */
			
			//output.close();
			return input;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
			//System.out.println(e.getMessage());
		}finally {
		/*	try {
				 input.close();
			   out.close();
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			} */
			
		}
	}
	
	@Override 
	public void executionAlgorithm() throws Exception {
		int r=-1;
		try{
			//rs = ps.executeQuery(); //siempre es un executeQuery (xq es xa Bajar blob..).. simil GenericSelectDBService.. 
			r = ps.executeUpdate(); 
			
			/*
			 	if (rs!=null) {
				rs.next();
					
				//New!: 
				//	-> Lo nuevo: POST exec select y prev a writeResponse: !!!
	
				//a) toma el blob :
				java.sql.Blob blob = rs.getBlob(getBlobColumnName());
				
				//b) obtiene el in xa usar en el out 
				in = blob.getBinaryStream();
				
				//c) arma el fileName y lo setea a la var
				setFileName(getFileName()); //el getFileName lo redef 'ellos' yme lo guardo en la var
				
				// ..  y luego obvio el writeResponse lo escribe..  
				*/
			
				/* 20130311	
				converter.setRecordSet(rs);
				converter.setConfKey( strServiceName);
				//converter.convert(itemTag, o); //el converter xml se encaga ahora de todo el xml de rta sino asumiria
				// un header ej root xml de rta xa todo converter.
				
				converter.convert(o,strServiceName,DEFAULT_SERVICE_CONF,SingletonServicesConfiguration.getInstance());
				 
			} */
		}catch(Exception e){
			logger.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}
	
	//20141216 prueba fileUpload:
		public void setBlob(HttpServletRequest req) // throws Exception 
		{
			 //DataOutputStream 
			 FileOutputStream out=null;
			 //DataInputStream is=null;
			 InputStream input = null; 
			  
			try {
			
				input = req.getInputStream();
				byte[] buffer = new byte[4096];
				int n = - 1;
			    out =new FileOutputStream("c:\\morsaReloaded.jpg");
				while ( (n = input.read(buffer)) != -1)
				{
				    if (n > 0)
				    {
				        out.write(buffer, 0, n);
				    }
				}
				//output.close();
				
			} catch (Exception e) {
				//throw new Exception(e.getMessage());
				System.out.println(e.getMessage());
			}finally {
				try {
					input.close();
				    out.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
				
			}
		}
	/*Para grabar un blob en DB no haria falta esto: 
	
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
	*/
	
	/*solamente voy a hacer q devuelva booleando
		@Override
		public void writeResponse() throws Exception {
			try {
				
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}finally {
				sOutStream.close();			//fileOut.close();
			}
			
		}
	*/
			
	/*COMENTO LA ANULACION DEL WRITTER,asiSigue usandoEl writter normal xaSacar xml xEl output y no outStream	
	@Override
	public void setWriter(HttpServletResponse res) throws Exception {
		//skip (no abre/pide al res el writter, asi puede usar el getOutPutStream();
	} 
	*/
		
	//Como no conocen el fileName de antemano , al fileName lo tienen q setear a una var
	// y el sistema abstracto usa los getters en el codigo. 

	//-- com fileName (seteo dinamico en code (t. de ejecucion) ):	
	
		// ESTO NO IRIA XA UPLOAD private String outFileName=null;
	
	//Este no lo conocen de antemano, tienen q setear una var en el codigo dinámico y el esquema
	//abstracto usa dicha var.
	
	//public abstract String getFileName() throws Exception; //que retorne x ej myImg.jpg / myDoc.txt etc etc..
	
	/* Tienen que sobreEscribir el metodo (el esqueleto generico del executionService
	 * lo invoca luego de ejecutar el rs 
	public  void setFileName(String outputFileNameParam) throws Exception {
		this.outFileName=outputFileNameParam;
	}*/
	//public abstract void setFileName(String outputFileNameParam) throws Exception;
	/*no haria falta, todo se conoce de antemano, este codigo era vigente pero del download )
	public void setFileName(String outputFileNameParam) throws Exception {
		this.outFileName=outputFileNameParam;
	} */
	
	
	
	/*@Override NO IBA ASI, SE DEJA A REDEFINIR, AHI VA EL ARMADO CUSTOM DEL FILENAME.
	public String getFileName() throws Exception {
		return this.outFileName; //user app final usa el setter xa dyn en code setear la var
	}	*/						 // y este prog usa la var ( o el setter q ya lo heredaba y listo)
// -- fin fileName 
	
		private void fakeForModif1(){};
	 
}
