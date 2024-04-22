package business;
/* 20150104 esta no se usaria a menos q necesiten redefinir el execution algorithm a mano q basicamente
 * esta pre-progrmado en el server xq q invoque 2 sentencias sql de modif a db (ins o upd) 1 xa la data de 
 * la entidad q contiene un file y otra xa ins o upd en la tabla corresp de files (blobs) y q qde todo en 1 sola
 * trans y ent con eso no hace falta ninguna clase de estas casi nunca ya q ya tienen 1 clase multisentence q les 
 * ejecuta ambas sentencias sql y c/u de estas sentencias sql las pueden def en el properties como quieran! solo
 * q pueden meterle .type =B y eso ya esta contemplado. con un ecommand file desde flex envian toda la data de
 * lña entidad y tmb el file (solo pueden de a 1 sile ) si necesitan mas ent de a 1 con sentencias comunes 
 * y clase select generica (sin clase java concreta x programdor) pero usando .type B y listo. lo q tiene la 
 * multiservice es q t permite ins o upd data de una entdad y el file todo de una en un solo llamado (1 solo servicio)
 * !!! 
 */
/*
import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import fmk_core_server.BlobDownloader;
import fmk_core_server.BlobUploader;

@Stateless(name="UploadBlobExample", mappedName="UploadBlobExample")
@LocalBean
public class UploadBlobExample extends BlobUploader {
	@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;
	
	static final boolean USE_FILENAME_COLUMN =false;
	static final boolean USE_ENTITY_ID_WHERE_FILTER =true;
	
	static final boolean USE_FILEEXTENSION_COLUMN =false;
	static final String TABLENAME ="test";
	static final String WHERECLAUSE =""; // <- CAGUÉ .. EL id me tiene que venir como parametro :S
	static final String BLOB_COLUMNNAME ="MyBlobColumn";
	static final String BLOB_FILENAME_COLUMN_NAME ="";
	static final String BLOB_FILEEXTENSION_COLUMN_NAME ="";
	static final String BLOB_FILENAME_VALUE ="";
	static final String BLOB_FILEEXTENSION_VALUE ="";
	static final String ENTITY_ID_FIELD_NAME ="id";
 
	// copy & past code:
	
	@Override
	public String getBlobFileNameColumnName() throws Exception {
		return BLOB_FILENAME_COLUMN_NAME;
	}

	@Override
	public int getMode() throws Exception {
		return BlobUploader.UPDATE_MODE;
	}

	@Override
	public boolean getUseEntityIdWhereFilter() throws Exception {
		return USE_ENTITY_ID_WHERE_FILTER;
	}

	@Override
	public String getEntityIdFieldName() throws Exception {
		return ENTITY_ID_FIELD_NAME;
	}

	@Override
	public String getBlobFileExtensionColumnName() throws Exception {
		 return BLOB_FILEEXTENSION_COLUMN_NAME;
	}

	@Override
	public String getBlobFileNameValue() throws Exception {
		return BLOB_FILENAME_VALUE;
	}

	@Override
	public String getBlobFileExtensionValue() throws Exception {
		 return BLOB_FILEEXTENSION_VALUE;
	}

	@Override
	public boolean getUseFileNameColumn() throws Exception {
		return USE_FILENAME_COLUMN;
	}

	@Override
	public boolean getUseFileExtensionColumn() throws Exception {
		return USE_FILEEXTENSION_COLUMN;
	}

	@Override
	public String getTableName() throws Exception {	 
		return TABLENAME;
	}

	@Override
	public String getWhereClause() throws Exception {
		return WHERECLAUSE;
	}

	@Override
	public String getBlobColumnName() throws Exception {
		return BLOB_COLUMNNAME;
	}

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
	
	
} */
