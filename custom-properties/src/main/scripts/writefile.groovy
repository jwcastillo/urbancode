import com.urbancode.air.AirPluginTool;

import java.nio.charset.Charset
import java.nio.file.*;

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

final def filename = props['filename']
final def content = props['content']
final def charset = props['charset']

Files.write(Paths.get(filename), content.getBytes(Charset.forName(charset)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
