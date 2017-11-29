import com.ibm.im.custom.props.CompEnvPropsClient
import com.ibm.im.custom.props.PropertiesHelper
import com.urbancode.air.AirPluginTool;
import com.urbancode.air.CommandHelper
import com.urbancode.ud.client.SystemClient;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()


def udUser = airTool.getAuthTokenUsername()
def udPass = airTool.getAuthToken()
def weburl = System.getenv("AH_WEB_URL")

com.urbancode.air.XTrustProvider.install()

// Load properties
def filename = props['filename']
def componentId = props['componentId']
def environmentId = props['environmentId']

// Helper and client initialization
def ph = new PropertiesHelper()
def compEnv = new CompEnvPropsClient(new URI(weburl), udUser, udPass, componentId, environmentId)

// Logic start
def properties = ph.readFile(filename)
// - insert properties
properties = compEnv.insertProperties(properties)

// - store properties to output
airTool.setOutputProperty('definedProperties', ph.getPropertiesString(ph.definedProps(properties)))
airTool.setOutputProperty('undefinedProperties', ph.getPropertiesString(ph.undefinedProps(properties)))
airTool.setOutputProperty('allProperties', ph.getPropertiesString(properties))

airTool.storeOutputProperties()
// Logic end