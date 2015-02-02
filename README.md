# weblogic-embedded-ejb

*Author: Steve Button*  

*Date: Feb 2015*

## Outline

This project contains a simple Maven project that demonstrates using the WebLogic Server 12.1.3 Embedded EJB Container.

The WebLogic Server 12.1.3 embedded EJB container can be used to deploy and test EJB modules using the java.ejb.* API.  

[https://docs.oracle.com/middleware/1213/wls/EJBAD/embedejb.htm#EJBAD1403](https://docs.oracle.com/middleware/1213/wls/EJBAD/embedejb.htm#EJBAD1403)

Using the EJBContainer with WebLogic Server **requires** a local installation from which the `ORACLE_HOME/wlserver/server/lib/weblogic.jar` file can be specified on the CLASSPATH.  This enables WebLogic Server to create a temporary embedded domain from which the server is started and EJB modules and clients are executed.

This project contains a single `@Stateless` EJB PingPongBean and two EJB clients - a standalone Java client that starts the EJBContainer, looks up the PingPongBean EJB and invokes a method.  The second is a JUnit `@Test` case that does the same.

## Running JUnit Tests

The **EmbeddedClientTest** JUnit test case uses the `java.ejb.*` API to create an instance of an `EJBContainer` from which the desired EJB can be looked up and executed.


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

The **maven-surefire-plugin** is used to execute the test case as part of the maven test phase.  To use WebLogic Server as the provider of the EJBContainer, the following settings need to specified in the plugin configuration section.

    <properties>
      <weblogic.jar.path>${oracle.home}/wlserver/server/lib/weblogic.jar</weblogic.jar.path>
    </properties>

    ....
    
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>2.18</version>
      <configuration>
        <argLine>-Xmx128m</argLine>
        <enableAssertions>false</enableAssertions>
        <classpathDependencyScopeExclude>compile</classpathDependencyScopeExclude>
        <additionalClasspathElements>
          <additionalClasspathElement>${weblogic.jar.path}</additionalClasspathElement>
        </additionalClasspathElements>                            
      </configuration>
    </plugin>

The configuration settings required are:

**Memory setting**

        <argLine>-Xmx128m</argLine>
        
**Disable assertions**

        <enableAssertions>false</enableAssertions>
        
**Remove Java EE APIs dependency from classpath**        
        <classpathDependencyScopeExclude>compile</classpathDependencyScopeExclude>

**Put local weblogic.jar in classpath**

        <additionalClasspathElements>
          <additionalClasspathElement>${weblogic.jar.path}</additionalClasspathElement>
        </additionalClasspathElements>                            


To execute the test case, the **maven test** goal is used which performs the necessary compilation tasks then hands off to the maven-surefire-plugin to invoke the test cases it discovers:

    [sbutton] embeddedejb $ mvn test
    [INFO] Scanning for projects...
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building embeddedejb 1.0
    [INFO] ------------------------------------------------------------------------
    [INFO] 
    [INFO] --- maven-resources-plugin:2.6:resources (default-resources) @     embeddedejb ---
    [INFO] Using 'UTF-8' encoding to copy filtered resources.
    [INFO] Copying 0 resource
    [INFO] 
    [INFO] --- maven-compiler-plugin:3.2:compile (default-compile) @ embeddedejb ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO] 
    [INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @     embeddedejb ---
    [INFO] Using 'UTF-8' encoding to copy filtered resources.
    [INFO] Copying 0 resource
    [INFO] 
    [INFO] --- maven-compiler-plugin:3.2:testCompile (default-testCompile) @ embeddedejb ---
    [INFO] Nothing to compile - all classes are up to date
    [INFO] 
    [INFO] --- maven-surefire-plugin:2.18:test (default-test) @ embeddedejb ---
    [INFO] Surefire report directory: /Users/sbutton/Projects/Java/weblogic-embedded-ejb/embeddedejb/target/surefire-reports

    -------------------------------------------------------
     T E S T S
    -------------------------------------------------------
    Running buttso.demo.weblogic.ejbembedded.EmbeddedClientTest
    <Feb 2, 2015 1:06:24 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded WebLogic Server 12.1.3.0.0  Wed May 21 18:53:34 PDT 2014 1604337 > 
    <Feb 2, 2015 1:06:24 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded domain home /var/folders/m5/vzx5b8xx5cs75n1nt882017m0000gn/T/domain-2015.02.02_13.06.14> 
    <Feb 2, 2015 1:06:24 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Server starting. Waiting for RUNNING Status> 
    <Feb 2, 2015 1:06:24 PM CST> <Error> <EmbeddedServer> <BEA-000000> <FIXME - subject manager initialization invalid in class weblogic.server.embed.internal.ServerRunner> 
    Feb 02, 2015 1:06:28 PM weblogic.wsee.WseeCoreMessages logWseeServiceStarting
    INFO: The Wsee Service is starting
    <Feb 2, 2015 1:06:29 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Server started.> 
    Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 17.694 sec - in buttso.demo.weblogic.ejbembedded.EmbeddedClientTest
    <Feb 2, 2015 1:06:32 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded Server shutting down...> 
    
    Results :
    
    Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 19.590 s
    [INFO] Finished at: 2015-02-02T13:06:32+10:30
    [INFO] Final Memory: 10M/309M
    [INFO] ------------------------------------------------------------------------


The log files from the embedded server can be accessed from the specified **Embedded domain home** noted in the stdout log messages.

## Running the Standalone Client

The project also contains an example of a standalone Java application that uses the EJBContainer API to interact with the `@Stateless PingPongBean`.  This client can be run directly from the command line using the WebLogic Server EJBContainer implementation by simply including **weblogic.jar** on the **CLASSPATH**.

    [sbutton] embeddedejb $ java -classpath /Users/sbutton/Oracle/Middleware/wlserver/server/lib/weblogic.jar:target/classes     buttso.demo.weblogic.ejbembedded.PingPongClient
    
    <Feb 2, 2015 1:17:50 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded WebLogic Server 12.1.3.0.0  Wed May 21 18:53:34 PDT 2014 1604337 > 
    <Feb 2, 2015 1:17:50 PM CST> <Error> <EmbeddedServer> <BEA-000000> <FIXME - subject manager initialization invalid in class weblogic.server.embed.internal.ServerRunner> 
    <Feb 2, 2015 1:17:50 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded domain home /var/folders/m5/vzx5b8xx5cs75n1nt882017m0000gn/T/domain-2015.02.02_13.17.41> 
    <Feb 2, 2015 1:17:50 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Server starting. Waiting for RUNNING Status> 
    Feb 02, 2015 1:17:54 PM weblogic.wsee.WseeCoreMessages logWseeServiceStarting
    INFO: The Wsee Service is starting
    <Feb 2, 2015 1:17:55 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Server started.> 
    Feb 02, 2015 1:17:57 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: ejbContainer: weblogic.ejb.embeddable.EJBContainerImpl@3adeca1f
    Feb 02, 2015 1:17:57 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: Context: EventContext ()
    Feb 02, 2015 1:17:57 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: PingPongBean: buttso.demo.weblogic.ejbembedded.PingPongBean_wfxd6c_NoIntfViewImpl@3538a129
    Feb 02, 2015 1:17:57 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: ping: pong
    Feb 02, 2015 1:17:57 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: ping: pong from PingPongClient
    <Feb 2, 2015 1:17:57 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded Server shutting down...> 

The maven project **pom.xml** also contains an example using the **exec:exec** plugin that is configured to execute the **PingPongClient** using the WebLogic Server EJBContainer implementation in the same manner.

    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>exec-maven-plugin</artifactId>
      <version>1.3.2</version>
      <configuration>
        <executable>java</executable>
        <arguments>
          <argument>-classpath</argument>
          <argument>${weblogic.jar.path}:target/classes</argument>
          <argument>buttso.demo.weblogic.ejbembedded.PingPongClient</argument>
        </arguments>
      </configuration>
    </plugin>      

This is executed from the maven using the **mvn exec:exec** command.

    [sbutton] embeddedejb $ mvn exec:exec
    [INFO] Scanning for projects...
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building embeddedejb 1.0
    [INFO] ------------------------------------------------------------------------
    [INFO] 
    [INFO] --- exec-maven-plugin:1.3.2:exec (default-cli) @ embeddedejb ---
    <Feb 2, 2015 1:26:31 PM CST> <Error> <EmbeddedServer> <BEA-000000> <FIXME - subject manager initialization invalid in class weblogic.server.embed.internal.ServerRunner> 
    <Feb 2, 2015 1:26:31 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded WebLogic Server 12.1.3.0.0  Wed May 21 18:53:34 PDT 2014 1604337 > 
    <Feb 2, 2015 1:26:31 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded domain home /var/folders/m5/vzx5b8xx5cs75n1nt882017m0000gn/T/domain-2015.02.02_13.26.21> 
    <Feb 2, 2015 1:26:31 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Server starting. Waiting for RUNNING Status> 
    Feb 02, 2015 1:26:35 PM weblogic.wsee.WseeCoreMessages logWseeServiceStarting
    INFO: The Wsee Service is starting
    <Feb 2, 2015 1:26:36 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Server started.> 
    Feb 02, 2015 1:26:38 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: ejbContainer: weblogic.ejb.embeddable.EJBContainerImpl@17e8caf2
    Feb 02, 2015 1:26:38 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: Context: EventContext ()
    Feb 02, 2015 1:26:38 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: PingPongBean: buttso.demo.weblogic.ejbembedded.PingPongBean_wfxd6c_NoIntfViewImpl@6f2a1ff0
    Feb 02, 2015 1:26:38 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: ping: pong
    Feb 02, 2015 1:26:38 PM buttso.demo.weblogic.ejbembedded.PingPongClient run
    INFO: ping: pong from PingPongClient
    <Feb 2, 2015 1:26:38 PM CST> <Info> <EmbeddedServer> <BEA-000000> <Embedded Server shutting down...> 
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 18.156 s
    [INFO] Finished at: 2015-02-02T13:26:39+10:30
    [INFO] Final Memory: 10M/309M
    [INFO] ------------------------------------------------------------------------











