package business;
// TODO HACER el tratarReg, el validAction, [el invalidAction], y PROBAR EL MECANISMO COMPLETO!!!! 
//		 Luego ajustar este 1er svc, Y probarlo escheduleado (hacer una clase mng q lanze scheduleado los execute 
//		de los n programas (el manager es el unico q necesita sear bean (stateless) xa scheduleado los demas mepa q no..
//		me parece q de los demas hace un new y un .execute ( crea él las inst de esas clases comunes y no el container ni jndi)
import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.sql.DataSource;
//Estas 2 y la de @schedule van,xero xa otra prueba com oestan incompletas comento xa q jboss no las levante como bean y no jodan
//@Stateless(name="FileGen_IMPO_EnvioInfoLineaAzul", mappedName="FileGen_IMPO_EnvioInfoLineaAzul") //copy paste
//@LocalBean   
public class FileGen_IMPO_EnvioInfoLineaAzul extends AbstractHDFileGenerator {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds; 

//@Schedule(dayOfWeek = "*", hour = "*", minute = "*", second = "*/5", persistent = false)
public void backgroundProcessing() throws Exception { 
	try {
		execute(); //OJO: -> .. VER si NO tengo que hacer una clase MANAGER q sea la scheduleada e invoque 
				   //				los execute() de cada generador de archivos!. ( los usé a todos )!!!!!
	} catch (Exception e) {
		throw new Exception(e.getMessage());
	}
}

	@Override
	void customInitialization() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	String loadSQLQuery() throws Exception {
		//TODO cargarlo desde el properties (application, x q no es un servicio q se inovca x web desde/via servlet)
		
		//Obs: Como es siempre un query sql estático (sin variables x lo tanto sin necesidad de concatenar nada) podria
		//meter todo el sql en un único String , pero uso SB xa q quede + legible en n-renglones de código y x si  desp 
		//cambia y hay que hacerlo concatenando algo:
		
		StringBuffer sb = new StringBuffer(); //idea: el formateo de fechas podria ser configurable xa el querie..
		
		/* SQL SEMI-POSTA: 
		sb 	.append("SELECT '                         ' 								AS blancos 		, 	")
			.append("		DATE_FORMAT(F.`FeiDtDi` , '%Y%m%d') 						AS fechaDI 		,	")
			.append("		'BR02      ' 												AS BR02			,	")
			.append("		CAST( E.`ProCodi` AS CHAR(30) )								AS producto		,	")
			.append("		CAST( E.`leiQtde` AS DECIMAL(10,5) )						AS cantidad 	,	")
			.append("		'E' 														AS E 			,	")
			.append("		'R' 														AS R 			,	")
			.append("		CAST( F.`FeiDi` AS CHAR(15) )								AS DI15			,	")
			.append("		CAST( E.`IdeDiAdic` AS CHAR(3) )							AS AdicionDI	,	") 
			.append("		CAST( E.`TecCodi`  	AS CHAR(8) )							AS NCM			,	")	
			.append("		DATE_FORMAT( F.`FeiDtDi`, '%Y%m%d' )						AS FechaDI2		,	")  
			.append("		CASE XXX.`modalidadDespacho` WHEN '3' THEN 'DS' ELSE 'DI' 	AS Tipo 		,	") //??
			.append("		CAST( F.`FeiDi` AS CHAR(13) )								AS DI13			,	")
			.append("		CAST( E.`PdiCodi` AS CHAR(5) )								AS pedido		,	")
			.append("		'CFOP' 														AS CFOP			,	")
			.append("		CAST( E.`ItiLin` AS CHAR(5) )								AS lineaPedido	,	")
			.append("		CAST( E.`EmiCodi` AS CHAR(15) )								AS embarque			")
			.append("FROM ")
			.append("		`EmbIteImp` E ") 
			.append("INNER JOIN	 ")
			.append(" 		`FolEmbImp`	F ")
			.append(" 	ON E.FeiDi = F.FeiDi") // ??
			.append("WHERE 1=1 AND ")
			.append(" 		")
			.append("ORDER BY F.FeiDi ")
			.append("	desc ")
			.append(" "); 
			*/
		
		//Obs:  esta consulta sql trata ya de aplicar cierta logica de negocio (case, joins, where, etc) y ademas!:
		//		FORMATEAR! cada campo / castearlo etc xa q venga en string(char)de tamaño fijo correcto x def logica
		//		xa el arch plano de salida xa este svc/c.uso impl en particular.
		
		// q pasa sicasteo int, decimal, long, etc yfallan? (salta exception y la consulta no muere el proceso x q 
		//	NO obtengo el rs!
		// ent: de ultima, se castea solo lo q se sabe q no va a fallar (lo basico y q no falla seguro (formato fechas 
		//	o long solicitada en sql q sabemos q en la db nunca va a ser > xq el campo está def de <= a esta long pedida
		//	etc!.. Ent: dejo los casteo locos o de long o de ctrl de logica adicional + compleja de neg xa el campo q
		//	tmb puede hacer q no cumpla ( ctrls añadidos x code source java ) a los campos mas locos.
 		
		//ent: ya hice la funcion en DB. pero x ahora como no tengo las tablas devuelvo kaka del formato correcto:
		
		
		// FAKE SQL: Se parece igual mas a la realidad.
		//	Ver:
		//			-1  Ver si se rellenan las cosas a izq o der.
		//			-2	Ver si los numeros se rrellenan c/ espacios o con 0s sin valor (a la izq o der en parte fraccionaria ?)
		sb 	.append("SELECT '                         ' 									AS blancos 		, 	")
			.append("		DATE_FORMAT(F.`FeiDtDi` , '%Y%m%d') 							AS fechaDI 		,	")
			.append("		'BR02      ' 													AS BR02			,	")
			.append("		fixedValueLength(  E.`ProCodi`  ,30, ' ' , 'R', FALSE )			AS producto		,	")
			.append("		fixedValueLength(  E.`leiQtde`  ,16, ' ' , 'R', FALSE )			AS cantidad		,	")
			.append("		'E' 															AS E 			,	")
			.append("		'R' 															AS R 			,	")
			.append("		fixedValueLength( F.`FeiDi`  	,15, ' ' , 'R', FALSE )			AS DI15			,	")
			.append("		fixedValueLength( E.`IdeDiAdic`	,3, ' '  , 'R', FALSE )			AS AdicionDI	,	") 
			.append("		fixedValueLength( E.`TecCodi` 	,8, ' '  , 'R', FALSE )			AS NCM			,	")	
			.append("		DATE_FORMAT( F.`FeiDtDi`, '%Y%m%d' )							AS FechaDI2		,	")  
			.append("		CASE F.`modalidadDespacho` WHEN '3' THEN 'DS' ELSE 'DI' END		AS Tipo 		,	") //??
			.append("		fixedValueLength(  F.`FeiDi`	,13, ' ' , 'R', FALSE )			AS DI13			,	")
			.append("		fixedValueLength(  E.`PdiCodi` 	,5, ' '  , 'R', FALSE )			AS pedido		,	")
			.append("		'CFOP' 															AS CFOP			,	")
			.append("		fixedValueLength(  E.`ItiLin` 	,5, ' '  , 'R', FALSE )			AS lineaPedido	,	")
			.append("		fixedValueLength(E.`EmiCodi` 	,15, ' ' , 'R', FALSE )			AS embarque			")
			.append(" FROM `EmbIteImp` E ")
			.append("		INNER JOIN	`FolEmbImp`	F ")
			.append(" 		ON E.FeiDi = F.FeiDi") // ??
			.append(" WHERE 1=1   ")
			.append(" ORDER BY F.FeiDi DESC ");
			
		return sb.toString(); // <- Levantardesde Application.properties..
		
	}

	@Override
	public Object tratarReg() throws Exception { //deberia devolver null al 1er getter intelig de campo  q no cumpla del and
												// o el string concatenado de TODO EL REG (DE TODOS LOS CAMPOS) ,xq todo campo 
												//cumplia (ctrl y formateo) 
		// TODO  Hacer entonces: 1 and entre los getters de cada campo. Hacer los getters de cada campo y x c/u hacer 
		//																2 func mas! (ctrl y formateo).
		return null;
	}

	@Override
	public Object validRegAction() throws Exception {
		// TODO deberia grabar en el file en disco (osea en el outpusStream os.write o algo asi..ver ej grab arch schedule) ..
		return null;
	}

	@Override
	public Object invalidRegAction() throws Exception {
		// TODO definir si graba un txt de regs invalidos o un log en db.. ver si lleva la cuenta (contador de invalidos)
		return null;
	}

	@Override
	Connection getConnection() throws Exception {
		Connection conn = null;
  		try{
  			conn = this.ds.getConnection();
  		}catch(Exception e){
  		//	logger.fatal(e.getMessage());
  			throw new Exception(e.getMessage());
  		}
  		return conn;
	}
	
}
