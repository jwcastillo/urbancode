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
def componentId = props['componentId']
def environmentId = props['environmentId']
def toDel = props['toDel'];

// Helper and client initialization
def compEnv = new CompEnvPropsClient(new URI(weburl), udUser, udPass, componentId, environmentId)
def ph = new PropertiesHelper()
def propsToDel
// Logic start
if ("ALL".equals(toDel)) {
	propsToDel = compEnv.getCurrentProperties()
} else {
	propsToDel = ph.readString(toDel)
}

compEnv.deleteProperties(propsToDel);
// Logic end