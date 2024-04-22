package fmk_core_server;

import java.sql.Connection;

import serviceDispatcher.IService;

public interface IDBService extends IService {

	//Modularizo la carga de la sentencia sql standard (básica) (1 sola):
	public abstract String loadSQLQuery() throws Exception;

	//SobreCargo para parametrizar el "tagName" o clave prop de la sentencia sql, q sea var xa + de 1!
	public abstract String loadSQLQuery(String strSqlKey) throws Exception;

	public abstract Connection getConnection() throws Exception;

	//In Java 7, you should not close them explicitly, 
	//but use automatic resource management to ensure that resources are closed and exceptions are handled appropriately. 
	public abstract void liberarRecursosJDBC() throws Exception; // // // //

}