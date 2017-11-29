import com.ibm.im.custom.props.PropertiesHelper
import com.urbancode.air.AirPluginTool;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

//Load properties
final def p1 = props['property1']
final def p2 = props['property2']

//Logic
def ph = new PropertiesHelper()

//Store properties in result
airTool.setOutputProperty('result', ph.merge(p1, p2));
airTool.storeOutputProperties();
