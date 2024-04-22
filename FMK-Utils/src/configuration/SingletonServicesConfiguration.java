package configuration;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
//import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class SingletonServicesConfiguration implements ISingletonConfiguration{ //
		//static sentence to create the instance only once
		private static SingletonServicesConfiguration INSTANCE = new SingletonServicesConfiguration();
		
		static final Logger logger = Logger.getLogger(SingletonServicesConfiguration.class);

		
		Properties myPropertiesFile = new Properties(); //when the unique creation happens
		String valorStr;
		String claveStr;

		//private constructor:
		private SingletonServicesConfiguration() {
			InputStream inputStream = null;
			try {
				//load a properties file
				//String path = System.getProperty("jboss.server.home.dir")+"/conf/GLOBAL_FMK_CONF/services.properties";
				//String path = System.getProperty("services.properties");
				
				//myPropertiesFile.load(new FileInputStream(path)); //"services.properties"));
				
				 //load a properties file
				//myPropertiesFile.load(new FileInputStream("services.properties"));
				
				/*
				inputStream = this.getClass().getClassLoader()  
			                .getResourceAsStream("services.properties");  
				 
				myPropertiesFile.load(inputStream); */
				
				//--prueba
				//URL ulrGlobalResource = Thread.currentThread().getContextClassLoader().getResource("services.properties");
				//--fin prueba
				
				//20130125 prueba
				myPropertiesFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("services.properties"));
				
				//myPropertiesFile.put("default.conversorClass", "GenericSelectDBServiceBeanForJndiPrueba");
				
				
				//myPropertiesFile.lo
			}catch(Exception e)	{	//se complica relanzar una exception en un singleton static via constructor.. 
				logger.fatal(e.getMessage());	//asi que lo dejo así.
			}
		}
		
		// ***************************************************************************************************
		//public STATIC method for getting the instance of the configuration file.
		public static SingletonServicesConfiguration getInstance() {
	        return INSTANCE;
	    }
		//***************************************U*S*E*R**-**A*P*I*********************************************
		//** Get a property by especific unique key identificator
		
		public String getPropValue(String strKey){
			return myPropertiesFile.getProperty(strKey);
		}
		//***************************************U*S*E*R**-**A*P*I*********************************************
		
		public Set getKeys() {
			return myPropertiesFile.keySet();
		}

		/*
		public HashMap<String, String> getConfSet(String strMainKey){
			try {
				type = ClassConfiguration.myString(nombre+".param."+i+".type");
			} catch (ClassConfigurationException e) {
				type = "S";
			}
			return null;
		}*/
		private void fakeForModif1(){};
}
