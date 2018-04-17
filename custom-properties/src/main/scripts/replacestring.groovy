import com.urbancode.air.AirPluginTool;
import java.nio.file.*;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

final def input = props['input']
final def output = props['output']
final def value = props['value']

def splitted = input.split('\\n')

for (var in splitted) {
	value = value.replaceAll(var, output)
}
airTool.setOutputProperty('out', value);

airTool.storeOutputProperties();
