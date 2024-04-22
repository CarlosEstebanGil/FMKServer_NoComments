package serviceDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


public interface IStandardExecutableService {
	void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception;
	//void execute();
	public IStandardExecutableService getImplementacion();
	
	//solo x ahora: borrar luego cuando sepa como injectar dinamicamente un bean local ejb session en
	//el dispatcher en lugar del classforname actual q me instancia clases normales ya q necesito  //
	//q las clases de los servicios sean ejb beans! 
	
	//public void setDs(DataSource ds); 
	
}

