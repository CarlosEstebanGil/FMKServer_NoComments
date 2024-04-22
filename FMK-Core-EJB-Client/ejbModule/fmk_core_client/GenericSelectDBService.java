package fmk_core_client; //2014: si todas tuvieran sus interfases bien definidas, viendo las <<i>> ya daria una idea
						 //clara de cuando usar cada una. por ej multiSentence es + q nada "free code" xa el user
						// osea q tmb se usa xa queries armados dyn (todo a mano ) mas allá q sea una sola sentencia
						// osea q su nombre no está tan bien puesto, mas q nada es una "Free code" o algo asi.. 

import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.Clustered;

import fmk_core_server.AbstractDBServiceConverter;



//import xml.IXMLConverter;
//new: LO MARCO COMO EJB BEAN (Session Bean stateless) xa ver si asi corre como ejb y toma el ds con @resource!
//recordar que lo estoy instanciando desde el dispatcher con class.forName asi que hay q ver si cargando 
//la clase asi, x + q esté marcada como ejb bean de session, funciona como tal y dentro del container jboss managed!
//parece q si la marco como ejb bean y en un codigo x ahi le hago class.forName no funca
//o tal vez no funca x q un session bean no puede extender una clase ? o.0 .. no se pero si habilito esto no funca
@Stateless(name="GenericSelectDBServiceBean", mappedName="GenericSelectDBServiceBean") //
@Clustered
@LocalBean  
public class GenericSelectDBService extends AbstractDBServiceConverter { //AbstractDBService { // AbstractServiceDispatcher {//20130125 prueba AbstractDBService { 	
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds; 

	static final Logger logger = Logger.getLogger(GenericSelectDBService.class);

	//Constructor:							//20130125
	public GenericSelectDBService() { //q no tenga constructor no era el problema sino q extiende una clase
		super();						//q invocaba al static conf class y se ejecutaba auto x q esto es un SB!! 
	}

	@Override
	public Connection getConnection() throws Exception {
		Connection conn = null;
		try{
			conn = this.ds.getConnection();
		}catch(Exception e){
			logger.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		}
		return conn;
	}

	/*@Override 
	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception {
	}*/	
	 
	/*	No se redefine el execute, ya q el execute del abstracto es un esqueleto genérico
	 	que sirve para cualquier query a DB. Lo que se redefine para cada tipo de query 
	 	es el algoritmo de ejecución (q es lo único que varía) */
	
	/* Algoritmo para SELECT (query sql de tipo consulta)
	 * Obs: Todos los servicios que ejecuten un select y esperen un resultset de datos
	 * ejecutarán este algoritmo dentro del esqueleto genérico de una consulta gral abstracta
	 * Mediante esta clase puede ejecutarse cualquier consulta del tipo Select, toda consulta
	 * sql de tipo select debe referenciar por jndi a esta clase y utilizará una instancia de
	 * esta para ejecutar el select que corresponda.   
	 * */
	@Override 
	public void executionAlgorithm() throws Exception {
		try{
			rs = ps.executeQuery();
		/* 20130311	
			converter.setRecordSet(rs);
			converter.setConfKey( strServiceName);
			//converter.convert(itemTag, o); //el converter xml se encaga ahora de todo el xml de rta sino asumiria
			// un header ej root xml de rta xa todo converter.
			
			converter.convert(o,strServiceName,DEFAULT_SERVICE_CONF,SingletonServicesConfiguration.getInstance());
			*/
		}catch(Exception e){
			logger.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		}
		
		
	}
	
	/* esto ya lo hereda de una nueva clase especializada q lo añade (abstractDBConverter..)
	//New 20140623 : Como esto lo van a usar todos los q usen converters ent lo meto en una clase abstracta
	// 				 nueva extendida q añada justamente esto ( q tmb sirve x ej xa el file converter ).. 
	
	//El servicio genérico de la ejecucion de 1 solo select y que devuelve su RS en xml usa el converter.
	//a dif de los update / ins / bool o multiservice, se sobrescribe xa q no invoque el standardActionXmlResult,
	//básicamente este usa el conversor dentro del cual imprime incluso el header root y todo..
	@Override
	public void writeResponse() throws Exception {
		
		converter.setRecordSet(rs); //(*) esto no iria xa los fileOutputConverter .. !
		
		converter.setConfKey( strServiceName); // -> Esto si va siempre.. 
		
		//converter.convert(itemTag, o); //el converter xml se encaga ahora de todo el xml de rta sino asumiria
		// un header ej root xml de rta xa todo converter y estaría mal xa converters json etc.
		
		converter.convert(o,strServiceName,DEFAULT_SERVICE_CONF,SingletonServicesConfiguration.getInstance(),result,error,xmlMsge);
		//quiero q esta invocacion sea = xa fileConverter y q el mismo tnga en estos params ( o agregar) todo lo
		//necesario xa poder implementarlo a su manera. Que necesita fileConverter to convert? 
		//necesita : (el workbook (ya trabajado) + el outPut stream o el obj response o el printWriter del mismo. 
		// wb.write(sOutStream);
		// x lo tanto el o (writer del response) lo tiene, pero necesita el wb, en lugar de setearsele el rs (*)
		
		//ent: hay cosas en comun y cosas <>s e/los <>s converters, ent:
		//1) defino una <i> q reciba un object y c/u lo castee a lo suyo,	
		//ó
		//2) añado un parametro mas ej el wb y ent xa el otro lo paso en null ? ..pero y (*) (setRecordSet(rs)..) ??
		
		//deberia sacar setRecordSet y pasarlo como parametro y como un object, ent en lo casteo a lo q quiero en
		//cada impl ( ya q seria el objeto "Data_a_escribir_x_el_OUTPUt" (ya sea procesada en el converter o q ya
		//venia procesada en la impl de c/bean (ej los de file output xa fileConverter) ) .. 
		
		//ó    ------------>    ESTAAAAAAAA! : 
		//3) !!! No uso un converter !!! x q es básica la salida, ent hago de una wb.write(sOutStream); en el code 
		// de writeResponse y listo ( seria un writeResponse nuevo, siempre igual, xa los de fileOutput y este
		// writeresponse asi como estaba aca seria para los de xml output (x default de ultima).
		
		//LO QUE SI!: -> tengo q sacar este writeResponse q usa converter a algo generico Abstracto! , q no sea 
		//esta clase GenericSelectDBService, seria una nueva clase abstracta en la jerarquia que sea SvcWithConverter
		//x ej, y viene como hermana de las q su writeResponse lo implementan con el writeResult osea con el xml
		//de encabeza, cuerpo summary standard y footer xml!! (osea las multisentence q ya heredan eso de abstDBsvc)!
		
		//ENT! ->  MultiSentenceDBService X Default es c/writeResult y saca un xml summary
		//     ->  MultiSentenceDBServiceConverter redefine writeResponse con este code de aca
		//	   ->  MultiSentenceDBServiceFileOutput extiende a la anterior y redefine tmb el writeResponse
		//	   ->   	 	( poniendo el code basico xa dado un istream de bytes loopearlo e ir escrib en 
		//						la salida, asi esto seria xa agarrar un is de cualquier tipo de file y escribirlo,
		//						osea bien generico !! ) Y LUEGO, PARA EXCEL (podria ya funcar c/eso generico) pero
		//				( hago una nueva clase MultiSentenceDBServiceEXCELOutput q herede de la anterior de file
		//				  generica y  q sobreEscriba tmb el writeResponse x wb.write(sOutStream) etc !!!!!!!!!!!!!!
		//				( esta clase es todo lo de las anteriores xero la uso xa sacar excels ya q tiene la impl
		//					de outpur especifica xa los mismos .. !!!!!!!!!!!! )
		
		
	}
	
	*/
	private void fakeForModif1(){};
}
