package com.heikweber.clipboarder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

/**
 * Object class for entries in copy history
 *
 * @author David
 */

public class HTTPRequestUtil {

	private static String globalURL;

	private static String buildRequest(URL url, Map<String, Object> params, String requestMethod) throws Exception {

		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(requestMethod);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String register(String username, String email, String password) throws Exception {
		URL url = new URL(globalURL + "register.inc.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", password);
		params.put("username", username);
		params.put("email", email);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String activate(String email, String token) throws Exception {
		URL url = new URL(globalURL + "activate.php?email=" + email + "&token=" + token);
		Map<String, Object> params = new LinkedHashMap<>();

		String output = buildRequest(url, params, "GET");

		return output;
	}

	public static String forgotPassword(String email) throws Exception {
		URL url = new URL(globalURL + "forgotPassword.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("email", email);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String resetPassword(String email, String token, String password) throws Exception {
		URL url = new URL(globalURL + "resetPassword.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("email", email);
		params.put("token", token);
		params.put("password", password);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String loginWithPassword(String email, String password, Boolean rememberMe) throws Exception {
		URL url = new URL(globalURL + "login.inc.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", password);
		params.put("email", email);
		params.put("remindme", rememberMe);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String loginWithToken(String email, String token) throws Exception {
		URL url = new URL(globalURL + "login.inc.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String addClipWithPassword(String email, String Password, String clip, String cryptKey)
			throws Exception {

		try {
			clip = Cryptor.encrypt(clip, cryptKey);
		} catch (Exception e1) {
			System.out.println("Could not encrypt text");
		}

		URL url = new URL(globalURL + "createClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("clipboard", clip);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String addClipWithToken(String email, String token, String clip, String cryptKey) throws Exception {

		try {
			clip = Cryptor.encrypt(clip, cryptKey);
		} catch (Exception e1) {
			System.out.println("Could not encrypt text");
		}

		URL url = new URL(globalURL + "createClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);
		params.put("clipboard", clip);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String deleteClipWithPassword(String email, String Password, int clipID) throws Exception {
		URL url = new URL(globalURL + "deleteClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("clipid", clipID);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static String deleteClipWithToken(String email, String token, int clipID) throws Exception {
		URL url = new URL(globalURL + "deleteClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);
		params.put("clipid", clipID);

		String output = buildRequest(url, params, "POST");

		return output;
	}

	public static List<CopyEntry> getClipsWithPassword(String email, String Password, int offset, int number,
			SceneModel model, String cryptKey) throws Exception {
		URL url = new URL(globalURL + "showClips.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("offset", offset);
		params.put("number", number);

		String json = buildRequest(url, params, "POST");

		// create an object via deserialize
		Any obj = JsonIterator.deserialize(json);

		model.setNumberOfClips(obj.get("count").toInt());

		List<CopyEntry> copyEntryList = new ArrayList<CopyEntry>();
		for (Object o : obj.get("data")) {

			String content = ((Any) o).get("Content").toString();

			if (((Any) o).get("ID").toInt() > 0) {
				content = Cryptor.decrypt(((Any) o).get("Content").toString(), cryptKey);
			}

			copyEntryList.add(new CopyEntry(content, ((Any) o).get("ID").toString()));
		}

		return copyEntryList;
	}

	public static List<CopyEntry> getClipsWithToken(String email, String token, int offset, int number,
			SceneModel model, String cryptKey) throws Exception {
		URL url = new URL(globalURL + "showClips.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);
		params.put("offset", offset);
		params.put("number", number);

		String json = buildRequest(url, params, "POST");

		// create an object via deserialize
		Any obj = JsonIterator.deserialize(json);

		model.setNumberOfClips(obj.get("count").toInt());

		List<CopyEntry> copyEntryList = new ArrayList<CopyEntry>();
		for (Object o : obj.get("data")) {

			String content = ((Any) o).get("Content").toString();

			if (((Any) o).get("ID").toInt() > 0) {
				content = Cryptor.decrypt(((Any) o).get("Content").toString(), cryptKey);
			}

			copyEntryList.add(new CopyEntry(content, ((Any) o).get("ID").toString()));
		}

		return copyEntryList;
	}

	public static void setGlobalURL(String globalURL) {
		HTTPRequestUtil.globalURL = globalURL;
	}
}
