package buttso.demo.weblogic.ejbembedded;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sbutton
 */
public class EmbeddedClientTest {

    private EJBContainer ejbContainer;
    private Context ctx;
    
    // Name of PingPongBean in EJBContainer
    private static final String LOOKUP_PINGPONGBEAN = "java:global/classes/PingPongBean";

    @Before
    public void setupTest() {
        try {
            ejbContainer = EJBContainer.createEJBContainer();
            ctx = ejbContainer.getContext();
        } catch (Exception e) {
            System.out.println("Bummer, something wrong with the setup of the EJBContainer: " + e.getMessage());
            throw (e);
        }
    }

    @After
    public void finishTest() {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    @Test
    public void testPingUsingClasses() throws NamingException {
        PingPongBean ppb = (PingPongBean) ctx.lookup(LOOKUP_PINGPONGBEAN);
        assertNotNull(ppb);
        assertNotNull(ppb.ping());
        assertTrue("pong".equals(ppb.ping()));
    }

}
