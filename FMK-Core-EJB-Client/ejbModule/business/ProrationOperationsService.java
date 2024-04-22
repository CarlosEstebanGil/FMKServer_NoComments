package business;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import Entities.Article;
import Entities.Invoice;
import Entities.Expense;
import Entities.Operation;
import fmk_core_server.AbstractMultiSentenceDBServiceConverter;


	@Stateless(name="ProrationOperationsService", mappedName="ProrationOperationsService")
	@LocalBean
	public class ProrationOperationsService extends AbstractMultiSentenceDBServiceConverter {
		@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;
		
		/**
	  	 * Obtained Connect
	  	 * @return conn
		 * @throws Exception 
	  	 */
	    @Override
	  	public Connection getConnection() throws Exception {
	  		Connection conn = null;
	  		try {
	  			conn = this.ds.getConnection();
	  		} catch(Exception e) {
	  			throw new Exception(e.getMessage());
	  		}
	  		return conn;
	  	}
	    
	    /**
	  	 * Execution algorithm
	  	 * @throws exception
	  	 */
	    @Override 
	  	public void executionAlgorithm() throws Exception {
	    	try { 
	    		//ceg: 20141121
	    		String numops = getStringVarValue("operaciones");
	    		if(	numops==null) {
	    			getProrationOfOperationsCharly(); //TODO Sirve xero igual necesita las ops. hacer una func c/un sql q las traiga.
	    		}else {
	    			prorateOperations();    		 
	    		}
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
	    } 
	    
	    /**
	  	 * Get prorated operations
	  	 * @throws exception
	  	 */
		public void prorateOperations() throws Exception {
		   	try {
				ResultSet rs = getProrationOfOperations(); 			
			   	if(!rs.next()) {
			   		doProrationOfOperations();
			   	}
				rs.beforeFirst();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
		}
		
		/**
	  	 * Do Proration Of Operations
	  	 * @throws exception
	  	 */
		public void doProrationOfOperations() throws Exception {
		   	try {
		   		addOperationsForInvoice();
		   		executeProrationOfOperations();
		   		getProrationOfOperations();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
		}
		
		
	    
		/**
	  	 * Add Operations for the Invoice
	  	 * @throws exception
	  	 */
	    public void addOperationsForInvoice() throws Exception {  	   
			try {
				String idInvoice = getStringVarValue("idfactura");
				String numops = getStringVarValue("operaciones");	
				String[] operations = numops.split(",");			
				for(int i=0; i<operations.length; i++) {
					insertOperation(idInvoice, operations[i]);				
				}			
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}  	
	    }	    
		
	    /**
	  	 * Execute Proration of Operations
	  	 * @throws exception
	  	 */
		public void executeProrationOfOperations() throws Exception {
	   		try {
	   			Invoice invoice = getInvoiceWithOperations();
				executeProration(invoice);
				saveProrationOperation(invoice);
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
		}
		
	 	/**
	  	 * Execute Proration
	  	 * @param invoice
	  	 * @throws exception
	  	 */
		public void executeProration(Invoice invoice) throws Exception {
			ArrayList<Operation> operations = invoice.getOperations();
			double valueOperation = 0;
			for(int i=0; i < operations.size(); i++) {			
				Operation operation = operations.get(i);
				if(invoice.getTypeProration() == 1){
					valueOperation = executeProrationCF(invoice);
				}else{
					valueOperation = executeProrationValue(invoice, operation);
				}
				operation.setAmountProration(valueOperation);			
				doProrationArticles(invoice, operation);			
			}
		}
		
		/**
	  	 * Execute Proration "Por Costo Fijo"
	  	 * @param invoice
	  	 * @return value
	  	 * @throws exception
	  	 */
		public double executeProrationCF(Invoice invoice) throws Exception {
			double averageOperation = getAverage(invoice.getTotalAmountExpense(), invoice.getOperations().size());		
			double value = getAverage(averageOperation, invoice.getQuantityExpenses());
			invoice.setAmountProration(value);	
			return averageOperation;
		}
		
		/**
	  	 * Execute Proration "Por Valor"
	  	 * @param invoice
	  	 * @param operation
	  	 * @return value
	  	 * @throws exception
	  	 */
		public double executeProrationValue(Invoice invoice, Operation operation) throws Exception {
			double percentageOperation = getPercentage(invoice.getTotalOperations(), operation.getTotfob());		
			double value = getPercentageValue(invoice.getTotalAmountExpense(), percentageOperation);
			return value;
		}
	    
		/**
		  * Get Invoice With Expenses
		  * @return invoice
		  * @throws exception
		  */
	    public Invoice getInvoiceWithOperations() throws Exception {
	    	ArrayList<Expense> expenses = new ArrayList<Expense>();
	    	Invoice invoice = new Invoice();    	
			ResultSet rs = getInvoice();
			double amountExpenses = 0D;
	    	
			try {			
				while(rs.next()) {	
					
					amountExpenses  = rs.getDouble("importefactura");
					
					invoice.setId(rs.getInt("id"));
					invoice.setTypeProration(rs.getInt("idtipoprorrateo"));
					invoice.setTotalAmountExpense(amountExpenses);
					
					Expense expense = new Expense();
					expense.setId(rs.getInt("idgasto"));
					expense.setIdInvoiceExpense(rs.getInt("idfacturagasto"));
					expense.setAmountExpense(rs.getDouble("importegasto"));	
					expense.setPorcentageExpense(getPercentage(invoice.getTotalAmountExpense(), expense.getAmountExpense()));
					expenses.add(expense);		
				}	
				
				invoice.setQuantityExpenses(expenses.size());
				getOperations(invoice, expenses, amountExpenses);	

			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}		
			return invoice;	    	
	    }
	
	    /**
		  * Get Operations
		  * @param invoice
		  * @throws exception
		  */
		public void getOperations(Invoice invoice, ArrayList<Expense> expenses, double amountExpenses) throws Exception {
			ArrayList<Operation> operations = new ArrayList<Operation>();
			ResultSet rs = getOperations(); 
			double total = 0;
			
	    	try {  		
	    		while(rs.next()){	
	    			
	    			Operation operation = new Operation();
	    			operation.setId(rs.getInt("id"));
	    			operation.setNumop(rs.getInt("numop"));	 
	    			operation.setTotfob(rs.getDouble("totfob"));
	    			operation.setAmountExpense(amountExpenses);
	    			operation.setExpenses(expenses);
	    			total = total + rs.getDouble("totfob");
	    			
	    			getArticlesOfOperation(operation);
	    			operations.add(operation);
	    		}   
	    		
	    		invoice.setTotalOperations(total);		
	    		invoice.setOperations(operations);	
	    		
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}	
		}		
	    
		/**
		  * Save Proration of Operation
		  * @param invoice
		  * @throws exception
		  */
	    public void saveProrationOperation(Invoice invoice) throws Exception {
	    	try {
		    	ArrayList<Operation> operations = invoice.getOperations(); 
		    	for(int i=0; i < operations.size(); i++) {	 
		    		Operation operation = operations.get(i);  
		    		ArrayList<Expense> expenses = operation.getExpenses();
		    		for(int j=0; j<expenses.size(); j++) {
		    			Expense expense = expenses.get(j);
		    			insertExpenseOperation(invoice, expense, operation);
		    		}
		    	}
		    	markProratedInvoice();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
	    }
		
	    /**
		  * Get Articles of Operation
		  * @param operation
		  * @throws exception
		  */
		public void getArticlesOfOperation(Operation operation) throws Exception {
			ArrayList<Article> articles = new ArrayList<Article>();		
			ResultSet rs = getArticles(operation);	
			double total = 0;
			try {	
				while(rs.next()){			
					Article article = new Article();
					article.setTripli(operation.getNumop());
					article.setArtic(rs.getString("artic"));//Int("artic"));
					article.setFobuni(rs.getDouble("fobuni"));
					total = total + rs.getDouble("fobuni");
					articles.add(article);
				}			
				operation.setTotalArticles(total);
				operation.setArticles(articles);
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
		}	
		
		/**
		  * Do Proration of Articles
		  * @param invoice
		  * @param operation
		  * @throws exception
		  */
		public void doProrationArticles(Invoice invoice, Operation operation) throws Exception {	
			ArrayList<Expense> expenses = operation.getExpenses();	
			ArrayList<Article> articles = operation.getArticles();
			
			for(int j = 0; j < articles.size(); j++){	
				Article article = articles.get(j);	
				for(int i=0; i < expenses.size(); i++){	
					Expense expense = expenses.get(i);
					
					double porcentageArticle = getPercentage(operation.getTotalArticles(), article.getFobuni());		
					double valueArticle = getPercentageValue(operation.getAmountProration(), porcentageArticle);	
					double amountExpense = getPercentageValue(valueArticle, expense.getPorcentageExpense());
					
					expense.setAmountProration(amountExpense);
					article.setExpenses(expenses);
				}
			}
			
			saveProrationArticles(invoice, operation);
		}
		
		/**
		  * Save Proration of Articles
		  * @param invoice
		  * @param operation
		  * @throws exception
		  */
		public void saveProrationArticles(Invoice invoice, Operation operation) throws Exception {
		 	try {
		    	ArrayList<Article> articles = operation.getArticles();
		    	for(int i = 0; i < articles.size(); i++) {
		    		Article article = articles.get(i);
		    		ArrayList<Expense> expenses = article.getExpenses();
		    		for(int j = 0; j < expenses.size(); j++){	
		    			Expense expense = expenses.get(j);
		    			insertExpenseArticle(article, expense);
		    		}
		    	}
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			} 
		}
		
		/**
		  * Get Proration of Operations
		  * @return resultSet
		  * @throws exception
		  */ 
		public ResultSet getProrationOfOperations() throws Exception {
			try {	
				String ids = getStringVarValue("idgastos");
				String numops = getStringVarValue("operaciones");
				String strSQL2 = "SELECT fgo.numop AS numop, fgo.idgasto AS idgasto, g.nombre AS nombre, fgo.importe AS importe " +
						 "FROM 4pl_factura_gasto_operacion fgo " +
						 "LEFT JOIN 4pl_gasto g " + 
						 "ON fgo.idgasto = g.id " +
						 "WHERE idgasto IN (" + ids + ") " +
						 "AND fgo.numop IN (" + numops + ")"; 			
			   	ps = conn.prepareStatement(strSQL2);  
			   	rs = ps.executeQuery(); 		   	
		   	} catch(Exception e) {
		   		throw new Exception (e.getMessage()); 
		   	}
			return rs;
		}	
		
		/**
		  * Get Proration of Operations
		  * @return resultSet
		  * @throws exception
		  */ 
		public ResultSet getProrationOfOperationsCharly() throws Exception {
			try {	
				String idFactura = getStringVarValue("idfactura"); //Obs: idGasto tmb me viene y podria ahorra el 1er select pero queda mas claro asi.
				 
				String strSQL2 = "SELECT fgo.numop AS numop, fgo.idgasto AS idgasto, g.nombre AS nombre, fgo.importe AS importe " +
						"FROM 4pl_factura_gasto_operacion fgo  " +
						 "LEFT JOIN 4pl_gasto g  " +
						" ON fgo.idgasto = g.id  " +
						 "WHERE idgasto IN " + 
							"( SELECT idgasto FROM  4pl_factura_gasto_operacion WHERE idfactura_gasto IN " + 
							"	( SELECT  id FROM 4pl_factura_gasto WHERE idFactura = " +idFactura +" ) ) " +
						" AND fgo.numop IN " +
						"	(SELECT numop FROM  4pl_factura_gasto_operacion WHERE idfactura_gasto IN " + 
							"	( SELECT  id FROM 4pl_factura_gasto WHERE idFactura = " +idFactura +" ) )";
						
			   	ps = conn.prepareStatement(strSQL2);  
			   	rs = ps.executeQuery(); 		   	
		   	} catch(Exception e) {
		   		throw new Exception (e.getMessage()); 
		   	}
			return rs;
		}	
		
		
		/**
	  	 * Get Invoice
	  	 * @return resultSet
	  	 * @throws exception
	  	 */
		public ResultSet getInvoice() throws Exception {
			try {
				final String QRY_SEL_FACTURA_ID = "QRY_SEL_FACTURA_ID";
				String strSQL2 = loadSQLQuery(QRY_SEL_FACTURA_ID);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_SEL_FACTURA_ID);
				rs = ps.executeQuery();  
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
			return rs;
		}
		
		/**
	  	 * Get Operations
	  	 * @return resultSet
	  	 * @throws exception
	  	 */
		public ResultSet getOperations() throws Exception{
			try {
				final String QRY_SEL_OPERACIONES = "QRY_SEL_OPERACIONES";
				String strSQL2 = loadSQLQuery(QRY_SEL_OPERACIONES);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_SEL_OPERACIONES);
				rs = ps.executeQuery();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
			return rs;
		}
		
		/**
	  	 * Get Articles of Operation
	  	 * @param operation
	  	 * @return resultSet
	  	 * @throws exception
	  	 */
		public ResultSet getArticles(Operation operation) throws Exception{
			try {
				final String QRY_SEL_ARTICULOS = "QRY_SEL_ARTICULOS";
				String strSQL2 = loadSQLQuery(QRY_SEL_ARTICULOS);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_SEL_ARTICULOS);
				ps.setInt(1, operation.getNumop()); /* tripli */
				rs = ps.executeQuery();  
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
			return rs;
		}
	    
		/**
	  	 * Insert Operation
	  	 * @param idInvoice
	  	 * @param numop
	  	 * @throws exception
	  	 */
	    public void insertOperation(String idInvoice, String numop) throws Exception {
			try {
				final String QRY_INS_FACTURAOPERACIONES = "QRY_INS_FACTURAOPERACIONES";
				String strSQL2 = loadSQLQuery(QRY_INS_FACTURAOPERACIONES);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_INS_FACTURAOPERACIONES);
				ps.setInt(1, Integer.parseInt(idInvoice));
				ps.setInt(2, Integer.parseInt(numop));
				ps.executeUpdate();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
	    }	    
		
	    /**
	  	 * Insert Expense Operation
	  	 * @param invoice
	  	 * @param expense
	  	 * @param operation
	  	 * @throws exception
	  	 */
	    public void insertExpenseOperation(Invoice invoice, Expense expense, Operation operation) throws Exception {
			try {
				final String QRY_INS_FACTURAGASTO_OP = "QRY_INS_FACTURAGASTO_OP";
				String strSQL2 = loadSQLQuery(QRY_INS_FACTURAGASTO_OP);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_INS_FACTURAGASTO_OP);
				ps.setInt(1, expense.getIdInvoiceExpense());
				ps.setInt(2, operation.getNumop());
				ps.setInt(3, expense.getId());	       	
				if(invoice.getTypeProration() == 1){
					ps.setBigDecimal(4, BigDecimal.valueOf(invoice.getAmountProration()));
				}else{	
					double amountExpense = getPercentageValue(operation.getAmountProration(), expense.getPorcentageExpense());
					ps.setBigDecimal(4, BigDecimal.valueOf(amountExpense));
				}			
				ps.executeUpdate();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
	    }
	    
	    /**
	  	 * Insert Expense Article
	  	 * @param expense
	  	 * @param article
	  	 * @throws exception
	  	 */
	    public void insertExpenseArticle(Article article, Expense expense) throws Exception {		
			try {
				final String QRY_INS_FACTURAGASTO_AR = "QRY_INS_FACTURAGASTO_AR";
				String strSQL2 = loadSQLQuery(QRY_INS_FACTURAGASTO_AR);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_INS_FACTURAGASTO_AR);
				ps.setInt(1, expense.getIdInvoiceExpense());
				ps.setInt(2, article.getTripli());
				ps.setInt(3, expense.getId());
				ps.setString(4, article.getArtic()); //setInt(4, article.getArtic());
				ps.setBigDecimal(5, BigDecimal.valueOf(expense.getAmountProration()));
				ps.executeUpdate();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
	    }
	    
	    /**
	  	 * Mark Prorated Invoice
	  	 * @throws exception
	  	 */
	    public void markProratedInvoice() throws Exception{
	    	try {
				final String QRY_UP_FACTURAPRORRATEADA = "QRY_UP_FACTURAPRORRATEADA";
				String strSQL2 = loadSQLQuery(QRY_UP_FACTURAPRORRATEADA);
				ps = conn.prepareStatement(strSQL2);
				fillService(QRY_UP_FACTURAPRORRATEADA);
				ps.executeUpdate();
			} catch (Exception e) {
				 throw new Exception (e.getMessage()); 
			}
	    }
	    
	    /**
	  	 * Get Average
	  	 * @return average
	  	 */
	    public double getAverage(double total, int amount) {
			double average = 0;
			if(amount != 0) {
				average = total / amount;
			}
			return average;
		}
		
	    /**
	  	 * Get Percentage
	  	 * @return percentage
	  	 */
		public double getPercentage(double total, double value) {	
			double percentage = value*100/total;
			return percentage;
		}
		
		/**
	  	 * Get Percentage Value
	  	 * @return value
	  	 */
		public double getPercentageValue(double total, double percentage) {	
			double value = total*percentage/100;
			return value;
		}	

}