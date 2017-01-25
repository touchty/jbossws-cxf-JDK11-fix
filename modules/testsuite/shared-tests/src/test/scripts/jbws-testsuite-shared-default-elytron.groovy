def root = new XmlParser().parse(project.properties['inputFile'])

/**
 * Fix logging: optionally remove CONSOLE handler and set a specific log file
 *
 */
def logHandlers = root.profile.subsystem.'root-logger'.handlers[0]
def consoleHandler = logHandlers.find{it.@name == 'CONSOLE'}
if (!project.properties['enableServerLoggingToConsole']) logHandlers.remove(consoleHandler)
def file = root.profile.subsystem.'periodic-rotating-file-handler'.file[0]
file.attributes()['path'] = project.properties['serverLog']


/**
  Elytron security domian
**/
def securityDomains = root.profile.subsystem.'security-domains'[0]
def securityDomain = securityDomains.appendNode('security-domain', ['name':'JBossWS','default-realm':'JBossWS','permission-mapper':'default-permission-mapper'])
def realm = securityDomain.appendNode('realm',['name':'JBossWS','role-decoder':'groups-to-roles'])

def securityDomain2 = securityDomains.appendNode('security-domain', ['name':'handlerauth-security-domain','default-realm':'handlerauth-security-domain','permission-mapper':'default-permission-mapper'])
def realm2 = securityDomain2.appendNode('realm',['name':'handlerauth-security-domain','role-decoder':'groups-to-roles'])

def securityDomain3 = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','default-realm':'JBossWSSecurityDomainPermitAllTest','permission-mapper':'default-permission-mapper'])
def realm3 = securityDomain3.appendNode('realm',['name':'JBossWSSecurityDomainPermitAllTest','role-decoder':'groups-to-roles'])

def securityDomain4 = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainTest','default-realm':'JBossWSSecurityDomainTest','permission-mapper':'default-permission-mapper'])
def realm4 = securityDomain4.appendNode('realm',['name':'JBossWSSecurityDomainTest','role-decoder':'groups-to-roles'])



/**
  Elytron security realm
**/

def securityRealms = root.profile.subsystem.'security-realms'[0]
def propertiesRealm = securityRealms.appendNode('properties-realm', ['name':'JBossWS'])
def usersProperties = propertiesRealm.appendNode('users-properties',['path':project.properties['usersPropFile'], 'plain-text':'true'])
def groupsProperties = propertiesRealm.appendNode('groups-properties',['path':project.properties['rolesPropFile']])


def propertiesRealm2 = securityRealms.appendNode('properties-realm', ['name':'handlerauth-security-domain'])
def usersProperties2 = propertiesRealm2.appendNode('users-properties',['path':project.properties['testResourcesDir'] + '/jaxws/handlerauth/jbossws-users.properties', 'plain-text':'true'])
def groupsProperties2 = propertiesRealm2.appendNode('groups-properties',['path':project.properties['testResourcesDir'] + '/jaxws/handlerauth/jbossws-roles.properties'])


def propertiesRealm3 = securityRealms.appendNode('properties-realm', ['name':'JBossWSSecurityDomainPermitAllTest'])
def usersProperties3 = propertiesRealm3.appendNode('users-properties',['path':project.properties['testResourcesDir'] + '/jaxws/samples/securityDomain/jbossws-users.properties', 'plain-text':'true'])
def groupsProperties3 = propertiesRealm3.appendNode('groups-properties',['path':project.properties['testResourcesDir'] + '/jaxws/samples/securityDomain/jbossws-roles.properties'])

def propertiesRealm4 = securityRealms.appendNode('properties-realm', ['name':'JBossWSSecurityDomainTest'])
def usersProperties4 = propertiesRealm4.appendNode('users-properties',['path':project.properties['testResourcesDir'] + '/jaxws/samples/securityDomain/jbossws-users.properties', 'plain-text':'true'])
def groupsProperties4 = propertiesRealm4.appendNode('groups-properties',['path':project.properties['testResourcesDir'] + '/jaxws/samples/securityDomain/jbossws-roles.properties'])



/**
  HttpAuthentication Factory
**/

def httpAuthen = root.profile.subsystem.'http'[0]

def httpAuthenticationFactory = httpAuthen.appendNode('http-authentication-factory', ['name':'JBossWS','http-server-mechanism-factory':'global', 'security-domain':'JBossWS'])
def mechanismConfiguration = httpAuthenticationFactory.appendNode('mechanism-configuration')
def mechanism = mechanismConfiguration.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm=mechanism.appendNode('mechanism-realm',['realm-name':'JBossWS'])


def httpAuthenticationFactory2 = httpAuthen.appendNode('http-authentication-factory', ['name':'handlerauth-security-domain','http-server-mechanism-factory':'global', 'security-domain':'handlerauth-security-domain'])
def mechanismConfiguration2 = httpAuthenticationFactory2.appendNode('mechanism-configuration')
def mechanism2 = mechanismConfiguration2.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm2=mechanism2.appendNode('mechanism-realm',['realm-name':'handlerauth-security-domain'])

def httpAuthenticationFactory3 = httpAuthen.appendNode('http-authentication-factory', ['name':'JBossWSSecurityDomainPermitAllTest','http-server-mechanism-factory':'global', 'security-domain':'JBossWSSecurityDomainPermitAllTest'])
def mechanismConfiguration3 = httpAuthenticationFactory3.appendNode('mechanism-configuration')
def mechanism3 = mechanismConfiguration3.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm3=mechanism3.appendNode('mechanism-realm',['realm-name':'JBossWSSecurityDomainPermitAllTest'])


def httpAuthenticationFactory4 = httpAuthen.appendNode('http-authentication-factory', ['name':'JBossWSSecurityDomainTest','http-server-mechanism-factory':'global', 'security-domain':'JBossWSSecurityDomainTest'])
def mechanismConfiguration4 = httpAuthenticationFactory4.appendNode('mechanism-configuration')
def mechanism4 = mechanismConfiguration4.appendNode('mechanism',['mechanism-name':'BASIC'])
def mechanismRealm4=mechanism4.appendNode('mechanism-realm',['realm-name':'JBossWSSecurityDomainTest'])


//add this to ejb
def ejbns = new groovy.xml.Namespace('urn:jboss:domain:ejb3:5.0')
def wflyns = new groovy.xml.Namespace('urn:jboss:domain:5.0')
def ejbSubsystem = root[wflyns.profile][ejbns.subsystem][0]


//TODO: is there better create node as sibling in groovy
def ejbChildren = ejbSubsystem.children()
def appSecurityDomains = new groovy.util.Node(null, 'application-security-domains', [])
ejbChildren.add(9, appSecurityDomains)

def ejbSecurityDomain1 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWS','security-domain':'JBossWS'])
def ejbSecurityDomain2 = appSecurityDomains.appendNode('application-security-domain', ['name':'handlerauth-security-domain','security-domain':'handlerauth-security-domain'])
def ejbSecurityDomain3 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','security-domain':'JBossWSSecurityDomainPermitAllTest'])
def ejbSecurityDomain4 = appSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainTest','security-domain':'JBossWSSecurityDomainTest'])

//add to undertow
def undertowns = new groovy.xml.Namespace('urn:jboss:domain:undertow:4.0')
def undertowSubsystem = root[wflyns.profile][undertowns.subsystem][0]

//TODO: is there better create node as sibling in groovy
def undertowChildren = undertowSubsystem.children()
def undertowAppSecurityDomains = new groovy.util.Node(null, 'application-security-domains', [])
undertowChildren.add(5, undertowAppSecurityDomains)

def appSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWS','http-authentication-factory':'JBossWS'])
def basicAppSecurityDomain = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'handlerauth-security-domain','http-authentication-factory':'handlerauth-security-domain'])
def basicAppSecurityDomain2 = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainPermitAllTest','http-authentication-factory':'JBossWSSecurityDomainPermitAllTest'])
def basicAppSecurityDomain3 = undertowAppSecurityDomains.appendNode('application-security-domain', ['name':'JBossWSSecurityDomainTest','http-authentication-factory':'JBossWSSecurityDomainTest'])



/**
 * Add a https connector like this:
 *
 * <security-realm name="jbws-test-https-realm">
 *    <server-identities>
 *        <ssl>
 *             <keystore path="/mnt/ssd/jbossws/stack/cxf/trunk/modules/testsuite/cxf-tests/target/test-classes/test.keystore" keystore-password="changeit" alias="tomcat"/>
 *        </ssl>
 *    </server-identities>
 * </security-realm>
 *
 */

def rootsecurityRealms = root.management.'security-realms'[0]
def rootsecurityRealm = rootsecurityRealms.appendNode('security-realm', ['name':'jbws-test-https-realm'])
def serverIdentities = rootsecurityRealm.appendNode('server-identities')
def ssl = serverIdentities.appendNode('ssl')
ssl.appendNode('keystore', ['path':project.properties['keystorePath'],'keystore-password':'changeit','alias':'tomcat'])

def server = root.profile.subsystem.server[0]
def curHttpsListener = server.'https-listener'[0]
if (curHttpsListener != null) server.remove(curHttpsListener)
server.appendNode('https-listener', ['name':'jbws-test-https-listener','socket-binding':'https','security-realm':'jbws-test-https-realm'])

/**
 * Save the configuration to a new file
 */

def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(project.properties['outputFile'])
f.write(writer.toString())