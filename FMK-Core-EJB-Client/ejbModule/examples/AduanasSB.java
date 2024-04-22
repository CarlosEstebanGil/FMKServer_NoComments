package examples;

import java.sql.Connection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import fmk_core_server.AbstractDBService;
import Entities.Aduanas;

/**
 *AUTHOR: CGIL
 *
 */

public class AduanasSB  extends AbstractDBService {
	
	protected EntityManager em;

	@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;   
	static final Logger logger = Logger.getLogger(artici_djai_copiar_faltantes.class);
	
    public AduanasSB() {
    	super();
    }

    public EntityManager getEntityManager() {
    	return em;
    	}
    
    public Aduanas createAduana(String codAduParam, String name)  {
    	Aduanas adu = new Aduanas();
    	adu.setCodadu(codAduParam);
    	adu.setNombre(name);
    	getEntityManager().persist(adu);
    	return adu;
    	}
    	
    public void removeEmployee(String codAduParam) {
    	Aduanas adu = findAduana(codAduParam);
    	if (adu != null) {
    		getEntityManager().remove(adu);
    	} 
    }
    	
    public Aduanas findAduana(String codAduParam) {
    	return getEntityManager().find(Aduanas.class, codAduParam);
    }
    
    public List<Aduanas> findAllAduanas() {
    	TypedQuery query = getEntityManager().createQuery("SELECT e FROM Employee e",
    	Aduanas.class);
    	return query.getResultList();
    	}

	@Override
	public void writeResponse() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executionAlgorithm() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Connection getConnection() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
