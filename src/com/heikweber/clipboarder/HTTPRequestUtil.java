package com.heikweber.clipboarder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

public class HTTPRequestUtil {
	private HTTPRequestUtil() {
	}

	public static String register(String Username, String email, String Passwort) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/register.inc.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Passwort);
		params.put("username", Username);
		params.put("email", email);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String activate(String email, String Token) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/activate.php?email=" + email + "&token=" + Token);
		Map<String, Object> params = new LinkedHashMap<>();

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
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String forgotPassword(String email) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/forgotPassword.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("email", email);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String resetPassword(String email, String token, String password) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/resetPassword.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("email", email);
		params.put("token", token);
		params.put("password", password);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String loginWithPassword(String email, String Password, Boolean rememberMe) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/login.inc.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("remindme", rememberMe);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String loginWithToken(String email, String token) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/loginClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String addClipWithPassword(String email, String Password, String clip) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/createClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("clipboard", clip);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String addClipWithToken(String email, String token, String clip) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/createClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);
		params.put("clipboard", clip);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String deleteClipWithPassword(String email, String Password, int clipID) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/deleteClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("clipid", clipID);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static String deleteClipWithToken(String email, String token, int clipID) throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/deleteClip.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);
		params.put("clipid", clipID);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}

	public static List<CopyEntry> getClipsWithPassword(String email, String Password, int offset, int number)
			throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/showClips.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("password", Password);
		params.put("email", email);
		params.put("offset", offset);
		params.put("number", number);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			// JsonIterator.deserialize("qwe").

			// Behandlung der empfangenen Daten des JSON-Objektes
			String json = IOUtils.toString(is, StandardCharsets.UTF_8);

			// Objekt, das das iterierte JSON-Objekt enthält
			Any obj = JsonIterator.deserialize(json);

			// TODO: Repair Login, Whats happens, when no record is set
			// System.out.println("JSON->Data:");
			// System.out.println(obj.get("data").toString());
			// if (obj.get("data").toString() == null ||
			// obj.get("data").toString().isEmpty()) {
			// System.out.println("JSON->Data ist leer");
			// String iniData = "[{\"ID\":\"543\",\"UserID\":\"29\",\"Content\":\"Execute
			// registration\",\"CreateDate\":\"1505031692\"}]";
			// return obj.get("data").asList().stream().map(item -> new CopyEntry("Test
			// Entry", "1"))
			// .collect(Collectors.toList());
			// }
			// System.out.println("JSON->Data:");
			// System.out.println(obj.get("data").toString());
			return obj.get("data").asList().stream()
					.map(item -> new CopyEntry(item.get("Content").toString(), item.get("ID").toString()))
					.collect(Collectors.toList());

		}
	}

	public static List<CopyEntry> getClipsWithToken(String email, String token, int offset, int number)
			throws Exception {
		URL url = new URL("https://notizbuch.online/Clipboarder/showClips.php");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("token", token);
		params.put("usetoken", "1");
		params.put("email", email);
		params.put("offset", offset);
		params.put("number", number);
		System.out.println("HIER " + email);
		System.out.println("HIER " + token);

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
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		try (InputStream is = conn.getInputStream()) {
			// Behandlung der empfangenen Daten des JSON-Objektes
			String json = IOUtils.toString(is, StandardCharsets.UTF_8);
			// Objekt, das das iterierte JSON-Objekt enthält
			Any obj = JsonIterator.deserialize(json);
			return obj.get("data").asList().stream()
					.map(item -> new CopyEntry(item.get("Content").toString(), item.get("ID").toString()))
					.collect(Collectors.toList());
		}
	}
}
