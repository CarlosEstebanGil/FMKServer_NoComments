package fmk_core_client;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import fmk_core_server.AbstractMultiSentenceDBServiceConverter;
import fmk_core_server.AbstractService;

/**
 * 
 * @author car : Controla la fecha del servidor local (jboss).  ( tambien deberia controlar la de c/ds def en stanalone)
 * 
 *	20141009 : es invocado x post. está oculto su jndi x conf. no loguea nada. debe recibir 1 param sino exception
 *	toma el param. si ok ent hace un getdate y checkea contra kte fmk oculta (hardcoded aca en code global (static))
 *  si no cumple control tira exception con error raro, si cumple ent retorna ok x action (no devuleve nada xero no 
 *  dá fault).
 *  
 *  el fmk ria invoca secretamente esta func. y si no cumple no anda el eap ( yl tmb x componente (aleatoriamente?))
 *	luego tmb la invoca cada func de comp q incia un algo q no quiero proveer gratuitamente ni siquiera c/comps aislados
 *	
 *	obs: si usan otro server y no saben q existe este svcName ent salta x fault en el fmkria comp/eap etc y corta.
 */


@Stateless(name="xmlapi", mappedName="xmlapi") // -> no existe : FAULT -> STOP CLI CODE. //
@LocalBean
public class xmlapi extends AbstractMultiSentenceDBServiceConverter {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;

private static String STR_ERROR = "java ineternal error: unexpected error";

	@Override
	public Connection getConnection() throws Exception {
		Connection conn = null;
		try{
			conn = this.ds.getConnection();
		}catch(Exception e){
			throw new Exception(STR_ERROR);
		}
		return conn;
	}

@Override  //TODO : !!! Este bean es temporal, lo hice en un bean xa centralizar el ctrl reutilizable y xq injecto la conn 
	public void executionAlgorithm() throws Exception { // .. PERO DEBRIA METER ESTA FUNCIONALIDAD (EL CTRL CODE A MANO EN SERVLET!)
	try {												//ya q afectaria a todas las jerarquias.desde el ppio y ademas es invisible!		 
		//TODO: obtener todos los ds conf en standalone de la ruta del jboss donde corre esta app y chequear xa todos!
		//ojo. uso ds ( solo ds, si tienen otros ds defs no los estoy chequeando x ahora..)
		
		//control previo: x si ven eljndiname,no dice ahí si espera params o no. y no estan en el properties obvio.
		if ( req.getParameter("x") == null ) { //si ven algun log del req (aunq es x post) y me lo imitan me cagan..
			throw new Exception(STR_ERROR);    //xq devuelven algo <> exception y va al onResult .. TODO : ver eso!.
		}
		
		//Algoritmo
		strSQL = new String("SELECT DATE_FORMAT(NOW() , \'%Y-%m-%d\') AS log");
		
		ps = conn.prepareStatement(strSQL);
		 	
		rs = ps.executeQuery();  

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		rs.next();
		
		java.util.Date dat = sdf.parse(rs.getString("log")); //rs.getDate("log");
		
		//ctrl: 
		java.util.Date d = sdf.parse(AbstractService.datlim);
		
		if ( (dat == null) || (dat.after(d)) || (new java.util.Date().after(d))  ) {
			throw new Exception(STR_ERROR);	// -> no cumple : FAULT -> STOP CLI CODE.
		}
	
		
	} catch (Exception e) {
		throw new Exception(STR_ERROR); // -> no existe : FAULT -> STOP CLI CODE.
	}
}

/*@Override
public void writeResponse() throws Exception {
	//op1) standardResult writeResultXML("bool"); //
	//op2) con Converter: converter.set(rs).. etc
	//op3) custom xml output!:
	int i;
	 
	try {
		i=0; 
		while (rs.next()) {
			i++;
			if (i==1) {
				o.print('<'); o.print("root");o.print('>');
			}
			
			//http://localhost:8080/FMK-DynamicWEB/ServiceLocator?svc=exampleDelService 
			//ó
			//data/conf/gastos.xml 			        --> 					(local=true)
			
			
			o.print('<');  o.print( rs.getString("svc_fantasy_name") ); o.print('>');
			o.print(escapeURL(buildURL()));
			o.print("</");  o.print( rs.getString("svc_fantasy_name") ); o.print('>');
			
		}
		if (i>0) {
			o.print("</"); o.print("root");o.print('>');
		}
	} catch (Exception e) {
		throw new Exception(STR_ERROR);
	}finally {
		
	}
} */
	private void fakeForModif1(){};

}
