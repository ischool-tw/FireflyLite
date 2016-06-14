package tw.com.ischool.fireflylite.util.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tw.com.ischool.fireflylite.util.Converter;


public class Downloader {
	public void download(String url, String fileName, String username, String password)
			throws IOException {

		HttpURLConnection sourceConnection = null;
		BufferedInputStream inputStream = null;
		FileOutputStream f = null;

		try {
			URL sourceURL = new URL(url);
			try {
				sourceConnection = (HttpURLConnection) sourceURL
						.openConnection();
			} catch (MalformedURLException exc) {
				throw new RuntimeException(
						"Configured URL caused a MalformedURLException: ", exc);
			}
						
			if (!username.isEmpty()) {
				String s = username + ":" + password;
				byte[] b = s.getBytes("ASCII");
				String str = Converter.toBase64String(b);

				sourceConnection.addRequestProperty("Authorization", "Basic " + str);
			}
			
			sourceConnection.setRequestProperty("Accept-Encoding", "zip, jar");
			sourceConnection.connect();
			inputStream = new BufferedInputStream(sourceConnection
					.getInputStream());

			File file = new File(fileName);
			f = new FileOutputStream(file);

			for (int singleByte = inputStream.read(); singleByte != -1; singleByte = inputStream
					.read()) {
				f.write(singleByte);
			}

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (f != null) {
				f.flush();
				f.close();
			}
			if (sourceConnection != null) {
				sourceConnection.disconnect();
			}
		}
	}
}
