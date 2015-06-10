package buildermaster;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jdt.core.ICompilationUnit;

public class ClientClassLoader { 

private URLClassLoader innerCL; 

	/** an automatic generated delegate method */ 
	public Class loadClass(String name) throws ClassNotFoundException { 
		return innerCL.loadClass(name); 
	} 

	public ClientClassLoader(ICompilationUnit c, ClassLoader parent) { 
		String unitPath = c.getResource().getProject().getLocationURI().toString(); 
		URL binURI = null; 
		try { 
			binURI = new URL(unitPath + "/bin/"); 
		} catch (MalformedURLException e) { 
		// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
		innerCL = new URLClassLoader(new URL[] { binURI }, parent); 
	} 

} 