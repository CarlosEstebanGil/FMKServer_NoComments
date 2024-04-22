package exceptions;
//TODO sacar todo el catcheo de Exception, cambiar por catchear las especificas q se que se pueden producir
//x q con exception tmb estoy catcheando las runtime exceptions. o dejar eso como ultimo bloque
//cosa de ahora no tratar igual a las exceptions de negocios (app exceptions) Por lo tanto en resumen:
//TODO Dejar los catch (Exception e) pero meter lo que sean exceptions q me puedo recuperar ej algunas 
// sqlexception del tipo lost connection, server busy o cosas asi q de esas reintentando me recuperaria, y x
// tanto seria app exceptions ( wrappear a eso y relanzar como app exceptions de ultima) y dejar como decia
// los catch exceptions al final xa catchear todo lo fatal sin recuperacion (todas las system exception) ya
// sean runtime exceptions q se den o ya sean checked exceptions (exceptions) pero q no me pueda recuperar
// ahi re wrappear a RuntimeSystemException y wrapear la exception original as cause attrib y relanzar la 
// runtime. asi nadie mas la atrapa o le baja la priori salvo q lo haga a proposito y explicitamente 
// con un catch (runtimeException o mySystemRuntimeException) y asi nadie les baja sin querer la gravedad
// q implica una runtime exception (fatal sin recup ) y no retoma o resume a codigo desp del catch o lo q sea.
// Por lo tanto TODO Dejo los catch Exception al final para eso pero antes meto los catch especializados
// de cada exception q se pueda dar de distintos tipos particulares xa asi tratar c/u como se deba separadamente
// y decidir si la wrapeo a myapp o mysystem runtime exception.
public class SystemException extends RuntimeException { //
	private static final long serialVersionUID = 1L;

		//private static final int APPLICATION_EXCEPTION=1;
		//private static final int SYSTEM_EXCEPTION=1;
		//private int causeExceptionType;

		private String message = null;
	    
		private Throwable wrappedCauseRealException;
	    
	    
	    public SystemException() {
	        super();
	    }
	 
	    public SystemException(String message) {
	        super(message);
	        this.message = message;
	    }
	 
	    public SystemException(Throwable cause) {
	        super(cause);
	    }
	 
	    public SystemException(String message,Throwable cause) {
	        super(message);
	        this.message = message;
	        this.wrappedCauseRealException = cause;
	    }
	    
	    @Override
	    public String toString() {
	        return message;
	    }
	 
	    @Override
	    public String getMessage() {
	        return message;
	    }
	    
	    public Throwable getWrappedCauseException() {
			return wrappedCauseRealException;
		}

		public void setWrappedCauseException(Throwable wrappedCauseException) {
			this.wrappedCauseRealException = wrappedCauseException;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		private void fakeForModif1(){};
	/* En resúmen: By carlitos:

	Osea que todo lo q no se pueda recuperar ni reintentando ni nada se debe relanzar como una runtime exception x todas las capas server xa q nadie menosprecie la gravedad 
	y resuma al codigo quedando el circuito o sistema inconsistente. luego la capa de mas alto nivel q se va a comunicar o dar la Rta al cli será la única que 
	catcheará estas runtime Exceptions armando un msge claro de "error fatal, comuniquese con el proveedor del sistema" y listo!!!! como msge de err xml o como 
	exception para un cli java x ej esta vez si obligandolo a catchearla, no por que no tenga importancia sino por que debe informale que la parte servidora del servicio solicitado
	falló gravemente, luego que el cli decida lo que hace. ( en el caso de un cli xml q no puede manejarse con clases java exception se manejará con xml error=true + errType=system
	ent ya sabe q es fatal y no puede hacer nada xa esa funcionalidad y q debe comunicarse con el proveedor. o si es err de neg x q la logica de neg de la impl del svc en el servidor 
	asi lo indica ent type=app ent le muestra el msge q viene del server y asi el cli sabe que puede reintentar o cambiar la data o lo q le diga la info clara del msge de lo q pasó o 
	generó el error. ambos saltan por onResult pero van al handler func de error por q err=true. Quedando solo el onFault para las exceptions o runtime exceptions que 
	no fueron manejadas x la capa del servidor q le dió la respuesta al cli (wrapeandolas a xml de err o una rta distinto a una exception) sino q relanzaron de una la exception ya
	sea por catchearla pero relanzarla al cli o por ni siquiera catchearla (ej alguna runtime exception de algun nivel bajo q pasó de largo si no se emplea la tecnica de catchear 
	runtimeException gral como para mandar un xml err type=system ) .
	
	Tamb las app except extendiendo exception y no runtime van a usarse xa los errores de negocio en lugar de usar custom and rare not standard business err codes ! 
	*/
}
