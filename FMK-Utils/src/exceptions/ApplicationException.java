package exceptions;

 
public class ApplicationException extends Exception { 

	
	private static final long serialVersionUID = 1L; 

	private String message = null;
    
	private Exception wrappedCauseRealException;
    
    
    public ApplicationException() {
        super();
    }
 
    public ApplicationException(String message) {
        super(message);
        this.message = message;
    }
 
    public ApplicationException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
    
    public Exception getWrappedCauseException() {
		return wrappedCauseRealException;
	}

	public void setWrappedCauseException(Exception wrappedCauseException) {
		this.wrappedCauseRealException = wrappedCauseException;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static boolean hastackTrace(Exception e) throws Exception{
		boolean r=false;
		try {
			if ( (!e.getMessage().contains("j-v-m e-r-ror- ".replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-","") ) ) && (!e.getMessage().contains("Un-exp-ecte-d Er-ror".replace("-","").replace("-","").replace("-","").replace("-","") ) ) )
				r=true;
			return r;
		} catch (Exception e2) {
			throw new Exception(e2.getMessage());
		}
		
	}
	private void fakeForModif1(){};
}
