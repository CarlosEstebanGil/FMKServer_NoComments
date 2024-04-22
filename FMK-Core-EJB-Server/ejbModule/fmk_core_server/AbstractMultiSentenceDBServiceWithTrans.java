package fmk_core_server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import exceptions.ApplicationException;
import exceptions.SystemException;

/* 2014 !!! -> OJOOOO -> La trans la abro y rollbackeo o comiteo en el servlet serviceLocator con JTA!!!!!!
 * Asi que esta clase no correria Mas!!!!!!!!! comentarla toda o borrarla!!!!
 * 
 * Wrappea la ejecución del algoritmo dentro de un try un begin trasn un commit un catch y rollback
 * las clases finales de impl de casos de uso multiSentence q necesiten transaction la extenderán
 * y deberán SIEMPRE relanzar las exceptions en su implementacion de algorithm method para que el
 * wrapper execute template de la abstracta quien define maneja lo transaccional y abstrae de los aspectos
 * tecnicos de esto, se ejecute su catch y por tanto el rollback q este contiene!. 
 */
public class AbstractMultiSentenceDBServiceWithTrans extends //
		AbstractMultiSentenceDBService {

	/* Inheriteted method execute:
	execute() //wrapper setResources, getConn, FillLogicVars,executionAlgorithm, writeResponse.	
	Se redefine para wrappear todo lo referente a lo transaccional */
	
	@Override
	public void execute(String strSvcName, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		 
		try{
			setResources(strSvcName, req, res); 
			//doesn't load/use any converter class
			
			conn = getConnection(); 
			
			//new: 20130205: adds call to fillLogicVars xa cargar el hash c/ las vars (args funcs a impl) from req.
			
			fillLogicVars (); //deja hlogicalParamVars cargado con strKey varParamName y oValue 

			
			//don't get any .sql default sentence from property for this kind of bool services!
			
			//doesn't prepare any statement object ps for .sql default string
			
			//doen't fill a/that default ps
			
			//---------------------
			//This time, inside it, u will be responsible for jdbc excecution of ps 
			//for selects, updates, inserts, deletes, sp calls , etc.Same as MultiSentence but this time
			// programmers that extends and imprement the algorithm method must rethrow the exception ALWAYS!
			
			//new: this is the only added diference beetween this class and the multiSentence without trans.
			
			conn.setAutoCommit(false); 				// BEGIN TRANS
			
				executionAlgorithm(); //JDB CODE
			
			conn.commit();							// COMMIT TRANS
			
			writeResponse();
    	}catch(ApplicationException ae) { //TODO catchear las exceptions especificas y especializadas q se puedan dar en el code por si hay lostConn o ServerFail de esas q uno se puede recuperar aunque sea reintentando (seria appExceps) solo las irrecuperables seran wrapeadas a runtime system exception 
    		error=true;
    		result=false;
    		conn.rollback();						// ROLLBACK TRANS 		( x app logic error )
    		writeResponse();
    	}
		catch(Exception e ) { // Catch Gral Exception at the end, only for runtime Exceptions and Exceptions not planned or unrecoverable 
    		logger.fatal(e.getMessage());
    		conn.rollback();						// ROLLBACK TRANS 		( x system fatal error )
			throw new SystemException( e.getMessage(), e); //TODO hacer el testo del fmk asi, usar esas 2 exceptions y rewrapear las posta ahi.
		}finally { 
			liberarRecursosJDBC();
		}
	}
	private void fakeForModif1(){};
}
