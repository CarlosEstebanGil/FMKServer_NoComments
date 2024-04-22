package configuration;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;


public class SingletonFrameworkConfiguration implements ISingletonConfiguration { // //

		//static sentence to create the instance only once
		private static SingletonFrameworkConfiguration INSTANCE = new SingletonFrameworkConfiguration();
		
		static final Logger logger = Logger.getLogger(SingletonApplicationConfiguration.class);
		
		Properties myPropertiesFile = new Properties(); //when the unique creation happens
		String valorStr;
		String claveStr;

		//private constructor:
		private SingletonFrameworkConfiguration() {
			InputStream inputStream = null;
			try {
				
				myPropertiesFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("FmkConfiguration.properties"));
			
			}catch(Exception e)	{	//se complica relanzar una exception en un singleton static via constructor.. 
				logger.fatal(e.getMessage());	//asi que lo dejo así.
			}
		}
		
		
		public static SingletonFrameworkConfiguration getInstance() {
	        return INSTANCE;
	    }

		public String getPropValue(String strKey){
			return myPropertiesFile.getProperty(strKey);
		}
			
		public Set getKeys() {
			return myPropertiesFile.keySet();
		}
		private void fakeForModif1(){};
	}
