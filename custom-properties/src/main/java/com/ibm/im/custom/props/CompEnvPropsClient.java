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
			result.put((String) propObject.get("name"), (String) propObject.get("value"));
		}
		return result;
	}
	
	public String insertProperty(String name, String label, String description, String defaultValue) throws IOException, JSONException {
		String uri = url + "/cli/component/addEnvProp"
                + "?name=" + encodePath(name)
                + "&component=" + encodePath(componentId)
                + "&label=" + encodePath(label)
                + "&default=" + encodePath(defaultValue)
				+ "&description=" + encodePath(description);

        HttpPut method = new HttpPut(uri);
        System.out.println(uri);
        CloseableHttpResponse response = invokeMethod(method);
        return getBody(response);
	}
	
	public String updateProperty(String name, String value) throws IOException {
		String uri = url + "/cli/environment/componentProperties"
                + "?name=" + encodePath(name)
                + "&value=" + encodePath(value)
                + "&component=" + encodePath(componentId)
                + "&environment=" + encodePath(environmentId);

        HttpPut method = new HttpPut(uri);
        System.out.println(uri);
        CloseableHttpResponse response = invokeMethod(method);
        return getBody(response);
	}

	public void insertProperties(Properties props) throws IOException, JSONException {
		Properties existing = getCurrentProperties();
		System.out.println("Inserting properties");
		for (Entry<Object, Object> entry : props.entrySet()) {
			try {
				if (!existing.containsKey(entry.getKey())) {
					System.out.println("Entry: '" + entry.getKey() + "' with value: '" + entry.getValue() + "'");
					insertProperty(toNotNullString(entry.getKey()), toNotNullString(entry.getKey()), toNotNullString(entry.getValue()), PropertiesHelper.UNDEFINED);
					System.out.println("SAVED!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	public void updateProperties(Properties props) {
		for (Entry<Object, Object> entry : props.entrySet()) {
			try {
				System.out.println("Entry: '" + entry.getKey() + "' with value: '" + entry.getValue() + "'");
				updateProperty(toNotNullString(entry.getKey()), toNotNullString(entry.getValue()));
				System.out.println("SAVED!");
			} catch (Exception e) {
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
