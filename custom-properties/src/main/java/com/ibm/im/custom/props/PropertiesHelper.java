package com.ibm.im.custom.props;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesHelper {
	public final static String UNDEFINED = "[UNDEFINED]";

	public Properties definedProps(Properties props) {
		Properties result = new Properties();
		for (Entry<Object, Object> entry : props.entrySet()) {
			if (!UNDEFINED.equals(entry.getValue()))
				result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public Properties undefinedProps(Properties props) {
		Properties result = new Properties();
		for (Entry<Object, Object> entry : props.entrySet()) {
			if (UNDEFINED.equals(entry.getValue()))
				result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public String getPropertiesString(Properties p, String comment) throws IOException {
		try (StringWriter sw = new StringWriter()) {
			write(p, sw);
			return sw.toString();
		}
	}
	
	public Properties merge(Properties p1, Properties p2) {
		Properties result = new Properties(p1);
		
		for (Entry<Object, Object> e : p2.entrySet()) {
			result.putIfAbsent(e.getKey(), e.getValue());
		}
		
		return result;
	}
	
	public Properties readFile(String filename) throws FileNotFoundException, IOException {
		Properties result = new Properties();
		try (FileReader fr = new FileReader(filename)) {
			result.load(fr);
			return result;
		}
	}
	
	public Properties readString(String content) throws FileNotFoundException, IOException {
		Properties result = new Properties();
		try (StringReader fr = new StringReader(content)) {
			result.load(fr);
			return result;
		}
	}
	
	public void writeFile(String filename, Properties p) throws IOException {
		try (PrintWriter pw = new PrintWriter(filename)) {
			write(p, pw);
		}
	}

	public void write(Properties p, Writer w) throws IOException {
		for (Iterator<Entry<Object, Object>> iterator = p.entrySet().iterator(); iterator.hasNext();) {
			Entry<Object, Object> e = iterator.next();
			w.write(e.getKey() + "=" + e.getValue());
			if (iterator.hasNext())
				w.write("\n");
		}
	}
}
