package serviceDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IServiceDispatcher {

	public abstract void execute(String strSvcName, HttpServletRequest req,
			HttpServletResponse res) throws Exception;

	// 20130311 public abstract void execute();

	//2013011 public abstract IStandardExecutableService getImplementacion();
	 public abstract IStandardExecutableService getImplementacion(); //
}