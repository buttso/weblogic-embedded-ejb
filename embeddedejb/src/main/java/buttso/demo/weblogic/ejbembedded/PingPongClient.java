package buttso.demo.weblogic.ejbembedded;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 *
 * Standalone EJB Client example:
 *
 * Uses EJBContainer to invoke the powerful PingPongBean to change the world
 *
 * @author sbutton
 *
 */
public class PingPongClient {

    private static final Logger LOG = Logger.getLogger(PingPongClient.class.getName());

    // Name of PingPongBean in EJBContainer
    private static final String LOOKUP_PINGPONGBEAN = "java:global/classes/PingPongBean";

    public static void main(String[] args) {
        PingPongClient client = new PingPongClient();
        client.run();
    }

    /**
     * Use the EJBContainer to lookup and invoke the PingPonBean @Stateless
     */
    private void run() {
        try {
            EJBContainer ejbContainer = EJBContainer.createEJBContainer();
            assert (ejbContainer != null);
            LOG.log(Level.INFO, "ejbContainer: {0}", ejbContainer.toString());
            Context ctx = ejbContainer.getContext();
            assert (ctx != null);
            LOG.log(Level.INFO, "Context: {0}", ctx.toString());
            PingPongBean ppb = (PingPongBean) ctx.lookup(LOOKUP_PINGPONGBEAN);
            assert (ppb != null);
            LOG.log(Level.INFO, "PingPongBean: {0}", ppb.toString());
            String pingReponse = ppb.ping();
            LOG.log(Level.INFO, "ping: {0}", pingReponse);
            pingReponse = ppb.ping("from " + this.getClass().getSimpleName());
            LOG.log(Level.INFO, "ping: {0}", pingReponse);
            ejbContainer.close();
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
