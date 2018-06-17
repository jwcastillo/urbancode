package com.ibm.im.custom.props;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.urbancode.commons.util.StringUtil;

public class PropertiesHelper {
	public final static String EMPTY = "";
	public static final String UNDEFINED = "-undefined-";

	public Properties definedProps(Properties props) {
		Properties result = new Properties();
		for (Entry<Object, Object> entry : props.entrySet()) {
			if (!EMPTY.equals(entry.getValue()))
				result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public Properties undefinedProps(Properties props) {
		Properties result = new Properties();
		for (Entry<Object, Object> entry : props.entrySet()) {
			if (EMPTY.equals(entry.getValue()))
				result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public String getPropertiesString(Properties p) throws IOException {
		try (StringWriter sw = new StringWriter()) {
			write(p, sw);
			return sw.toString();
		}
	}
	
	public Properties merge(Properties p1, Properties p2) {
		Properties result = new Properties();
		for (Entry<Object, Object> e : p1.entrySet()) {
			result.put(e.getKey(), e.getValue());
		}
		
		for (Entry<Object, Object> e : p2.entrySet()) {
			result.putIfAbsent(e.getKey(), e.getValue());
		}
		
		return result;
	}
	
	public Properties readFile(String filename, String cs) throws FileNotFoundException, IOException {
		Properties result = new Properties();
		Charset charset = Charset.forName(cs);
		Path path = Paths.get(filename);
		try (BufferedReader br = Files.newBufferedReader(path , charset)) {
			result.load(br);
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
	
	public void writeFile(String filename, Properties p, String charset) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(filename);
			Writer w = new OutputStreamWriter(fos, charset)) {
			write(definedProps(p), w);
		}
	}

	public void write(Properties p, Writer w) throws IOException {
		List<Entry<Object, Object>> l = new ArrayList<>(p.entrySet());
		l.sort(new Comparator<Entry<Object, Object>>() {

			@Override
			public int compare(Entry<Object, Object> o1, Entry<Object, Object> o2) {
				String k1 = (String) o1.getKey();
				String k2 = (String) o2.getKey();
				return k1.compareTo(k2);
			}

		});
		for (Iterator<Entry<Object, Object>> iterator = l.iterator(); iterator.hasNext();) {
			Entry<Object, Object> e = iterator.next();
//			System.out.println(e.getKey() + "=" + e.getValue());
			w.write((String) e.getKey());
			if (!StringUtil.isEmpty((String) e.getValue())) {
				w.write('=');
				w.write((String) e.getValue());
			}
			if (iterator.hasNext())
				w.write("\n");
		}
	}
}
