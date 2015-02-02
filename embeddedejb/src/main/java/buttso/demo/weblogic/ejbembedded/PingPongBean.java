package buttso.demo.weblogic.ejbembedded;

import javax.ejb.Stateless;

/**
 *
 * @author sbutton
 */
@Stateless
public class PingPongBean {
    
    public String ping() {
        return String.format("pong");
    }
    
    public String ping(String pinger) {
        return String.format("pong %s", pinger);
    }
}
