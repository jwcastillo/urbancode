package com.ibm.im.custom.props;

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.urbancode.ud.client.UDRestClient;

public class CompEnvPropsClient extends UDRestClient {

	private String componentId;
	private String environmentId;

	public CompEnvPropsClient(URI url, String clientUser, String clientPassword, String componentId, String environmentId) {
		super(url, clientUser, clientPassword, true);
		this.componentId = componentId;
		this.environmentId = environmentId;
	}

	public Properties getCurrentProperties() throws IOException, JSONException {
		Properties result = new Properties();
		String uri = url + "/cli/environment/componentProperties" 
				+ "?environment=" + encodePath(environmentId)
				+ "&component=" + encodePath(componentId);
		HttpGet method = new HttpGet(uri);

		CloseableHttpResponse response = invokeMethod(method);
		String body = getBody(response);
		JSONArray propsJSON = new JSONArray(body);

		System.out.println("Getting current component environment properties");
		for (int i = 0; i < propsJSON.length(); i++) {
			JSONObject propObject = (JSONObject) propsJSON.get(i);
			String value = (String) propObject.get("value");
			if (!PropertiesHelper.UNDEFINED.equals(value)) {
				result.put((String) propObject.get("name"), value);
			}
		}
		return result;
	}
	
	public String insertProperty(String name, String label, String description) throws IOException, JSONException {
		description = "For example: '" + description +"'";
		String uri = url + "/cli/component/addEnvProp"
                + "?name=" + encodePath(name)
                + "&component=" + encodePath(componentId)
                + "&label=" + encodePath(label)
                + "&default=" + encodePath(PropertiesHelper.UNDEFINED)
				+ "&description=" + encodePath(description);

        HttpPut method = new HttpPut(uri);
        System.out.println(uri);
        CloseableHttpResponse response = invokeMethod(method);
        return getBody(response);
	}
	
	public String updateProperty(String name, String value) throws IOException {
		System.out.println("Entry: '" + name + "' with value: '" + value + "'");
		String uri = url + "/cli/environment/componentProperties"
                + "?name=" + encodePath(name)
                + "&value=" + encodePath(value)
                + "&component=" + encodePath(componentId)
                + "&environment=" + encodePath(environmentId);
		if (name.toUpperCase().contains("PASSWORD"))
			uri += "$isSecure=true";

        HttpPut method = new HttpPut(uri);
        System.out.println(uri);
        CloseableHttpResponse response = invokeMethod(method);
        return getBody(response);
	}

	public Properties insertProperties(Properties props) throws IOException, JSONException {
		Properties existing = getCurrentProperties();
		System.out.println("Existing properties count: " + existing.size());
		System.out.println("Parameter properties count: " + props.size());
		for (Entry<Object, Object> entry : props.entrySet()) {
			try {
				String key = toNotNullString(entry.getKey());
				String description = toNotNullString(entry.getValue());
				if (!existing.containsKey(entry.getKey())) {
					System.out.println("Entry: '" + entry.getKey() + "' with value: '" + entry.getValue() + "'");
					insertProperty(key, key, description);
					System.out.println("SAVED!");
				}
				entry.setValue(existing.getProperty(key, getDefaultValue(description)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return props;
	}
	
	public Properties getModifiedProperties(Properties defProps) throws IOException, JSONException {
		Properties existing = getCurrentProperties();
		Properties result = new Properties();
		for (Entry<Object, Object> entry : defProps.entrySet()) {
			String key = (String) entry.getKey();
			if (existing.containsKey(key)) {
				if (!safeEquals(entry.getValue(), existing.get(key))) {
					result.put(key, existing.getProperty(key));
				}
			}
		}
		return result;
	}
		
	private boolean safeEquals(Object v1, Object v2) {
		if (v1 == null)
			return v2 == null;
		return v1.equals(v2);
	}

	private String getDefaultValue(String description) {
		if (description.isEmpty())
			return PropertiesHelper.EMPTY;
		else
			return description;
	}

	public void updateProperties(Properties changed, Properties defProps) throws IOException, JSONException {
		Properties existing = getCurrentProperties();
		for (Entry<Object, Object> entry : defProps.entrySet()) {
			try {
				String key = (String) entry.getKey();
				String defValue = (String) entry.getValue();
				if (changed.containsKey(key)) {
					String chgValue = (String) changed.get(key);
					if (!defValue.equals(chgValue)) {
						if (existing.containsKey(key)) {
							if (!existing.get(key).equals(chgValue))
								updateProperty(key, chgValue);
						} else {
							try {
								insertProperty(key, key, defValue);
							} catch (Exception e) {
								System.out.println("Property already exist");
							}
							updateProperty(key, chgValue);
						}
					} else {
						if (existing.containsKey(key)) {
							updateProperty(key, PropertiesHelper.UNDEFINED);
						}
					}
				} else {
					if (existing.containsKey(key)) {
						updateProperty(key, PropertiesHelper.UNDEFINED);
					}
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteProperties(Properties props) {
		for (Object key : props.keySet()) {
			try {
				deleteProperty(toNotNullString(key));
				System.out.println("Property: '" + key + "' deleted");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String deleteProperty(String name) throws IOException {
		String uri = url + "/cli/component/removeEnvProp"
                + "?component=" + encodePath(componentId)
                + "&name=" + encodePath(name);

        HttpDelete method = new HttpDelete(uri);
        CloseableHttpResponse response = invokeMethod(method);
        return getBody(response);
	}

	private String toNotNullString(Object o) {
		if (o == null)
			return "";
		return o.toString();
	}
}
