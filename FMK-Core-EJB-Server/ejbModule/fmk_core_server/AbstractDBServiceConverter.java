package fmk_core_server;

import java.sql.Connection;

import configuration.SingletonServicesConfiguration;

public class AbstractDBServiceConverter extends AbstractDBService {
	//hereda todo lo de la multiService pero redefine el writeResponse 
	//q le venia c/ el default de abstDBSvc q era el xml operation summary.
		
		//New 20140623 : Como esto lo van a usar todos los q usen converters ent lo meto en una clase abstracta
		// 				 nueva extendida q añada justamente esto ( q tmb sirve x ej xa el file converter ).. 
		
		//El servicio genérico de la ejecucion de 1 solo select y que devuelve su RS en xml usa el converter.
		//a dif de los update / ins / bool o multiservice, se sobrescribe xa q no invoque el standardActionXmlResult,
		//básicamente este usa el conversor dentro del cual imprime incluso el header root y todo..
		@Override
		public void writeResponse() throws Exception { //
			
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
			//	   ->  MultiSentenceDBServiceFileOutput extiende a la anterior (bah, al multiSentence, es hermana de la anterior) 
			// 			y redefine tmb el writeResponse
			//	   ->   	 	( poniendo el code basico xa dado un istream de bytes loopearlo e ir escrib en 
			//						la salida, asi esto seria xa agarrar un is de cualquier tipo de file y escribirlo,
			//						osea bien generico !! ) Y LUEGO, PARA EXCEL (podria ya funcar c/eso generico) pero
			//				( hago una nueva clase MultiSentenceDBServiceEXCELOutput q herede de la anterior de file
			//				  generica y  q sobreEscriba tmb el writeResponse x wb.write(sOutStream) etc !!!!!!!!!!!!!!
			//				( esta clase es todo lo de las anteriores xero la uso xa sacar excels ya q tiene la impl
			//					de outpur especifica xa los mismos .. !!!!!!!!!!!! )
			
}

		@Override
		public void executionAlgorithm() throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Connection getConnection() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		private void fakeForModif1(){};
}