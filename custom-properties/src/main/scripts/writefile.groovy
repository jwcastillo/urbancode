import com.urbancode.air.AirPluginTool;
import java.nio.file.*;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

final def filename = props['filename']
final def content = props['content']

Files.write(Paths.get(filename), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING)
