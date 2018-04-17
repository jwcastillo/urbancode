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
def merged;
if (p2 != null && p2.trim().empty) {
	merged = ph.readString(p1);
} else {
	merged = ph.merge(ph.readString(p1), ph.readString(p2))
}
airTool.setOutputProperty('result', ph.getPropertiesString(merged))
airTool.setOutputProperty('definedProperties', ph.getPropertiesString(ph.definedProps(merged)))
airTool.setOutputProperty('undefinedProperties', ph.getPropertiesString(ph.undefinedProps(merged)))

airTool.storeOutputProperties();
