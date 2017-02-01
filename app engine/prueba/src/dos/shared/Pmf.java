package dos.shared;

import javax.jdo.*;

//clase que posibilita la persistencia

public final class Pmf {
	
private static final PersistenceManagerFactory pmfInstance =
JDOHelper.getPersistenceManagerFactory("transactions-optional");
private Pmf() {}

public static PersistenceManagerFactory get() {
    return pmfInstance;
}

}