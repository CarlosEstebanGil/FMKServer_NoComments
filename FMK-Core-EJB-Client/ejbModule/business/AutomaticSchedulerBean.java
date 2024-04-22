package business;

import javax.ejb.Stateless;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.ejb.Schedule;


//@Stateless(name="AutomaticSchedulerBean")
public class AutomaticSchedulerBean {
	 
	//@Schedule(dayOfWeek = "*", hour = "*", minute = "*", second = "*/5", persistent = false)
     public void backgroundProcessing() throws Exception {
		//System.out.println("\n\n\t AutomaticSchedulerBean's backgroundProcessing() called....at: "+new Date());
		 InputStream is=null;
		 int bytesRead= -1 ;
		 byte[] buffer = new byte[8 * 1024];
		 OutputStream out = null; 
			  
			  try {
				 /* 
				is = Thread.currentThread().getContextClassLoader().getResourceAsStream("JBOSS_HOME"); //("TxtInputDeprueba.txt");
				
				out = new FileOutputStream("C:\\temp\\test.tgz");
	
				while ((bytesRead = is.read(buffer)) != -1) {
			    	out.write(buffer, 0, bytesRead);
			    }
			    */
				/*  
				  out = new FileOutputStream("C:\\temp\\test.txt");
				  String strJbossPath = System.getProperty("jboss.home.dir"); //System.getProperty("JBOSS_HOME");
				  strJbossPath+="\\standalone\\configuration\\standalone.xml";
				  
				  out.write(strJbossPath.getBytes());
				  */
				  
				  out = new FileOutputStream("C:\\temp\\test.txt");
				  
				  //Obtengo el path al standAlone.xml
				  String strJbossPath = System.getProperty("jboss.home.dir"); //y si usan el ha? !!
				  strJbossPath+="\\standalone\\configuration\\standalone.xml";
				  
				  strJbossPath="C:\\temp\\standalone.xml";
				  
				  //abro el file
				  is = new FileInputStream(new File(strJbossPath));
				  
				  //grabo la data del file xml leido a disco en ruta y filename def xa out
				  while ((bytesRead = is.read(buffer)) != -1) {
				    	out.write(buffer, 0, bytesRead);
				    }
				  
			  } 
			  catch (Exception e) {
				  throw new Exception(e.getMessage());
			  }
			  finally {
			    out.close();
			    is.close();
			  }
    }

}


