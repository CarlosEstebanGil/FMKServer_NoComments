package examples;

import java.sql.Connection;


import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import configuration.SingletonApplicationConfiguration;

import fmk_core_server.AbstractMultiSentenceDBService;

/**
 *AUTHOR: CGIL
 *
 */

@Stateless(name="artici_djai_copiar_faltantesDBServiceBean", mappedName="artici_djai_copiar_faltantesDBServiceBean")
@LocalBean
public class artici_djai_copiar_faltantes extends AbstractMultiSentenceDBService {
	@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;   
    
	static final Logger logger = Logger.getLogger(artici_djai_copiar_faltantes.class);

	public artici_djai_copiar_faltantes() {
        super();
    }

    //-----------Standard Session, allways the same (copy Paste from example) ----------
    @Override
  	public Connection getConnection() throws Exception {
  		Connection conn = null;
  		try{
  			conn = this.ds.getConnection();
  		}catch(Exception e){
  			logger.fatal(e.getMessage());
  			throw new Exception(e.getMessage());
  		}
  		return conn;
  	}
    //------------------------Standard Session, allways the same-------------------------
    
    
    //--The variable part!:  THE ALGORITHM !	(Customize ) 			-----------------
    //--------------------	 ------------- -
    
    @Override 
	public void executionAlgorithm() throws Exception {

    	final String tabla  = SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.tabla");
    	 
		StringBuffer valores = new StringBuffer();
		
		result = false;

		final String origen_de_datos_key = SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.origen_de_datos_key");
		final String djai_numop_key = SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.djai_numop_key");
		
		String origen_de_datos=getStringVarValue(origen_de_datos_key);
		int djai_numop=getIntVarValue(djai_numop_key);
		
		if	( 
				(!origen_de_datos.equalsIgnoreCase(SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.origen_de_datos_maria_djai_subitem"))) &&
				(!origen_de_datos.equalsIgnoreCase(SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.origen_de_datos_artici"))) 
			 ) {
			error=true;
        	xmlMsge= SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.err.paramOrigenDatosInvalid"); //"el parametro origen de datos no es válido";
		}else {
			
			try{
		        if(origen_de_datos.equalsIgnoreCase("maria_djai_subitem")) {
		            valores .append ( " SELECT " )
							.append	("    maria_djai_subitem.articulo_nro_item")
							.append	("    ,maria_djai_subitem.nro_subitem" )
							.append	( " 	,maria_djai_subitem.numop" )
							//----------Filtra el prefijo del codigo de articulo---------
							.append	( " 	,IF(SUBSTRING(maria_djai_subitem.articulo,1,LENGTH(clientes.prefijo)) = clientes.prefijo,")
							.append	( " 	    SUBSTRING(maria_djai_subitem.articulo, LENGTH(clientes.prefijo) + 1)")
							.append	(" 	,")
							.append	( " 	    maria_djai_subitem.articulo")
							.append	( " 	) AS indicador_propio")
							//------------------------------------------------------------
							.append(	 " 	,maria_djai_item.pais_origen")
							.append(	 " 	,maria_djai_subitem.cantidad_declarada")
							.append(	 " 	,maria_djai_subitem.precio_unitario_precision")
							.append(	 " FROM maria_djai_subitem")
							.append(	 " JOIN maria_djai_item ON 1")
							.append(	 " 	AND maria_djai_item.nro_item = maria_djai_subitem.articulo_nro_item")
							.append(	 " 	AND maria_djai_item.numop = maria_djai_subitem.numop")
							.append(	 " JOIN operai ON operai.tripli = maria_djai_subitem.numop")
							.append(	 " JOIN clientes ON clientes.idcliente = operai.idcliente")
							//------------ Join para filtrar aquellos articulos que fueron copiados ----------------
							.append(	 " LEFT JOIN artici_djai ON 1 ")
							.append(	 "    AND artici_djai.tripli = maria_djai_subitem.numop    ")
							.append(	 "    AND artici_djai.maria_djai_nro_item = maria_djai_subitem.articulo_nro_item")
							.append(	 "    AND artici_djai.maria_djai_nro_subitem = maria_djai_subitem.nro_subitem")
							.append(	 " WHERE 1")
							.append(	 " 	AND ISNULL(artici_djai.maria_djai_nro_subitem) ")
							//--------------------------------------------------------------------------
						//	.append(	 " 	AND maria_djai_subitem.numop = ? ") //setLong? 20130206 volver a meter esta linea
							.append(	 " 	AND maria_djai_subitem.numop = " + djai_numop ) //setLong?						
							.append(	 " GROUP BY maria_djai_subitem.numop,maria_djai_subitem.articulo_nro_item,maria_djai_subitem.nro_subitem");
						}
		        else if  ( origen_de_datos.equalsIgnoreCase("artici") ) {
		        	valores.append	 (" SELECT")
							.append	 (" (@cont:=@cont+1),0,tripli,artic AS indicador_propio,origen2,canti,fobuni")
							.append	 (" FROM artici a")
							.append	 (" JOIN(SELECT @cont:= 0) u ON 1")
							//.append	 (" WHERE tripli = ? ") //setLong? 20130206 volver a meter esta linea
							.append	 (" WHERE tripli = " + djai_numop )
							.append	 ("    AND (SELECT COUNT(*) cant FROM artici_djai WHERE 1 ")
							.append	 ("        AND tripli = a.tripli ")
							.append	 ("    ) = 0 ")
							.append	 (" GROUP BY tripli,artic,origen2,fobuni");
		        }
	
	 	       //------------ Verifico si existen articulos vacios -------------------
		        boolean tiene_cod_artic_vacios  = false;
		        String QRY = "";
		        QRY += " SELECT IF(COUNT(*)>0,TRUE,FALSE) AS tiene_cod_artic_vacios ";
		        QRY += " FROM (" + valores + ") u "; //aca hace ref a 1 de las 2 anteriores!
		        QRY += " WHERE 0 ";
		        QRY += " 	OR u.indicador_propio = '' ";
		        QRY += " 	OR ISNULL(u.indicador_propio) ";
		        
		        ps = conn.prepareStatement(QRY);
		        rs = ps.executeQuery();
		        
		        while (rs.next()) {
					tiene_cod_artic_vacios=rs.getBoolean("tiene_cod_artic_vacios");
				}
		        
		        //--------------------------------------------------------------------------
		        if (!tiene_cod_artic_vacios) {
		        	
		        	liberarRecursosJDBC(); 
		        	
		            QRY = "INSERT INTO " + tabla + " ( maria_djai_nro_item,maria_djai_nro_subitem,tripli,artic,origen,canti_saldo,fobuni  ) " + valores;
	
					ps = conn.prepareStatement(QRY); //(strSQL);

					int resgistros_insertados;
					resgistros_insertados = ps.executeUpdate(); 

					if (resgistros_insertados > 0 && origen_de_datos.equalsIgnoreCase("artici")  ) { //r=# filas insertadas (actualizadas)	               

						result = artici_djai_validar(djai_numop); //en c/ punto de salida seteo result e info xa al final writeXML!
						xmlMsge= SingletonApplicationConfiguration.getInstance().getPropValue("Application.artici_djai_copiar_faltantes.executionAlgorithm.ok.insertedAndArtici"); //"resgistros_insertados y origen_de_datos = artici";	
					}
		        }
		        else{ //tiene cod artic vacios.. 
			        		
			        		xmlMsge="El despacho del SIM para la DJAI '" + djai_numop + "' tiene diferencias con los artículos" +
			        			" cargados en MYMTEC. Comunicarse con sistemas para evaluar como proceder.";
		        } //end if gral
	
			}catch(Exception e){
				result = false;
				xmlMsge="Exception" + e.getMessage();
				logger.fatal(e.getMessage());
				error = true; //TODO ó writteXml out with error.. ???
				//throw new Exception(e.getMessage()); 
			}finally{
				liberarRecursosJDBC();
			}		
		}		
	}  
    
    private boolean artici_djai_validar(Integer djai_numop ) throws Exception{
    	result = false;
    	StringBuffer sbSQL=null;
    	try{
    		sbSQL = new StringBuffer();
    		sbSQL.append("")
             .append("SELECT ")
             .append("	(")
             .append("		SELECT COUNT(*) ")
             .append("                FROM artici_djai ")
             .append("                WHERE tripli = " + djai_numop + " ")
             .append("	)=( ")
             .append("		SELECT COUNT(*) ")
             .append("                FROM maria_djai_subitem ")
             .append("                WHERE numop = " + djai_numop + " ")
             .append("	) AS valido");
    		
    	    ps = conn.prepareStatement(sbSQL.toString());
 	        rs = ps.executeQuery();
 	       
 	     	while (rs.next()) {
				result = rs.getBoolean("valido");
 	     	}
 	     	
    	}catch(Exception e){
    		logger.fatal(e.getMessage());
    		throw new Exception(e.getMessage());
    	}finally{
    		ps.close();
    		rs.close();
    	}
    	return result;
    }

	@Override
	public void writeResponse() throws Exception {
		writeResultXML("bool"); 
	}
    
    
}
