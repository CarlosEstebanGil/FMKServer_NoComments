package fmk_core_client;

import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import configuration.SingletonServicesConfiguration;
import fmk_core_server.BlobDownloader;

@Stateless(name="DownloadBlobServiceBean", mappedName="DownloadBlobServiceBean")
@LocalBean
public class DownloadBlobService extends BlobDownloader {
	@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;
	
	static final String BLOB_FIELD_NAME = "mediumblob"; //
	
	/**
  	 * Obtained Connect
  	 * @return conn
	 * @throws Exception 
  	 */
	@Override
  	public Connection getConnection() throws Exception {
  		Connection conn = null;
  		try{
  			conn = this.ds.getConnection();
  		}catch(Exception e){
  			throw new Exception(e.getMessage());
  		}
  		return conn;
  	}

	@Override
	public String getBlobColumnName() throws Exception {
		//20150116 return BLOB_FIELD_NAME;
		String r=null;
		try {
			r = SingletonServicesConfiguration
					.getInstance()
					.getPropValue(strServiceName+".blobFieldName");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String getFileName() throws Exception {
		String r=null;
		try {
			//20150116 hago q tome del properties sino es cualquiera xq expuesto y n cclases, en cambio ahora va al api!.
				//r= rs.getString("nombreArchBlob") +"."+rs.getString("extensionArchBlob"); //or return "myFile.txt";
			r= rs.getString(getNombreArchBlobColName()) +"."+rs.getString(getExtensionArchBlobColName()); //or return "myFile.txt";
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
		 
		public String getNombreArchBlobColName() throws Exception {
			String r=null;
			try {
				//20150116 hago q tome del properties sino es cualquiera xq expuesto y n cclases, en cambio ahora va al api!.
					//r= rs.getString("nombreArchBlob") +"."+rs.getString("extensionArchBlob"); //or return "myFile.txt";
			//	r= getNombre() +"."+rs.getString("extensionArchBlob"); //or return "myFile.txt";
				r = SingletonServicesConfiguration
	    						.getInstance()
	    						.getPropValue(strServiceName+".nameFieldName");
				return r;
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		}
		
			public String getExtensionArchBlobColName() throws Exception {
				String r=null;
				try {
					//20150116 hago q tome del properties sino es cualquiera xq expuesto y n cclases, en cambio ahora va al api!.
						//r= rs.getString("nombreArchBlob") +"."+rs.getString("extensionArchBlob"); //or return "myFile.txt";
					r = SingletonServicesConfiguration
    						.getInstance()
    						.getPropValue(strServiceName+".extFieldName");
					return r;
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
			}
			private void fakeForModif1(){};
	}



