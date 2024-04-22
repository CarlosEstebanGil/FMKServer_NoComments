package configuration;

import java.util.Set;

public interface ISingletonConfiguration {

	public abstract String getPropValue(String strKey); //

	//***************************************U*S*E*R**-**A*P*I*********************************************

	public abstract Set getKeys();

}