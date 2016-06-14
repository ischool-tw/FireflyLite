package tw.com.ischool.fireflylite.util.http;

import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import tw.com.ischool.fireflylite.util.XmlHelper;
import tw.com.ischool.fireflylite.util.Converter;
import tw.com.ischool.fireflylite.util.StringUtil;


public class HttpUtil {

    public static Element getElement(String urlString) {
        return XmlHelper.parseXml(getInputStream(urlString));
    }

    public static String getString(String urlString, Cancelable cancelable) {
        try {
            InputStream inputStream = getInputStream(urlString);
            return convertToString(inputStream, cancelable);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Getting String from urlString occured a Exception.", e);
        }
    }

    public static InputStream getInputStream(String urlString) {
        try {
            URL url = new URL(urlString);

            if (url.getProtocol().equalsIgnoreCase("https")) {
                SSLContext sc = SSLContext.getInstance("SSL");

                TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertManager()};
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                        .getSocketFactory());
            }
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.connect();

            // code 4xx 表示有錯誤發生
            boolean isError = urlConnection.getResponseCode() >= 400;

            InputStream is = isError ? urlConnection.getErrorStream()
                    : urlConnection.getInputStream();

            return is;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Getting InputStream from URL occured a Exception.", e);
        }
    }

    public static InputStream getInputStreamWithCookie(String urlString, String cookie) {
        try {
            URL url = new URL(urlString);

            if (url.getProtocol().equalsIgnoreCase("https")) {
                SSLContext sc = SSLContext.getInstance("SSL");

                TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertManager()};
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                        .getSocketFactory());
            }
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestProperty("Cookie", cookie);

            urlConnection.connect();

            // code 4xx 表示有錯誤發生
            boolean isError = urlConnection.getResponseCode() >= 400;

            InputStream is = isError ? urlConnection.getErrorStream()
                    : urlConnection.getInputStream();

            return is;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Getting InputStream from URL occured a Exception.", e);
        }
    }

    public static InputStream getInputStream(String urlString, String username, String password) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        if (username == null)
            username = "";

        if (password == null)
            password = "";

        URL url = new URL(urlString);

        if (urlString.toLowerCase(Locale.getDefault()).startsWith("https://")) {
            SSLContext sc = SSLContext.getInstance("SSL");

            TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertManager()};
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        URLConnection uc = url.openConnection();
        if (!username.isEmpty()) {
            String s = username + ":" + password;
            byte[] b = s.getBytes("ASCII");
            String str = Converter.toBase64String(b);

            uc.addRequestProperty("Authorization", "Basic " + str);
        }
        //加gzip處理
        uc.setRequestProperty("Accept-Encoding", "gzip");

        uc.setConnectTimeout(100000);
        uc.setDoOutput(true);

        //處理gzip response
        if ("gzip".equals(uc.getContentEncoding())) {
            return new GZIPInputStream(uc.getInputStream());
        }
        InputStream in = new BufferedInputStream(uc.getInputStream());
        return in;
    }

    public static InputStream postData(URLConnection urlConnection,
                                       String data, int timeoutMillis, AbstractMap.SimpleEntry<String, String>... params) {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            if(params != null) {
                for(AbstractMap.SimpleEntry<String, String> each : params) {
                    httpConnection.setRequestProperty(each.getKey(), each.getValue());
                }
            }
            //加gzip處理
            httpConnection.setRequestProperty("Accept-Encoding", "gzip");

            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setConnectTimeout(timeoutMillis);
            OutputStreamWriter outStream = new OutputStreamWriter(
                    httpConnection.getOutputStream(), "UTF-8");

            outStream.write(data);
            outStream.flush();
            outStream.close();

            //處理gzip response
            if ("gzip".equals(httpConnection.getContentEncoding())) {
                return new GZIPInputStream(httpConnection.getInputStream());
            }

            return httpConnection.getInputStream();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static InputStream postJSON(URLConnection urlConnection,
                                       String data, int timeoutMillis, AbstractMap.SimpleEntry<String, String>... params) {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            if(params != null) {
                for(AbstractMap.SimpleEntry<String, String> each : params) {
                    httpConnection.setRequestProperty(each.getKey(), each.getValue());
                }
            }
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            //加gzip處理
            httpConnection.setRequestProperty("Accept-Encoding", "gzip");

            httpConnection.setDoOutput(true);
            httpConnection.setConnectTimeout(timeoutMillis);
            OutputStreamWriter outStream = new OutputStreamWriter(
                    httpConnection.getOutputStream(), "UTF-8");

            outStream.write(data);
            outStream.flush();
            outStream.close();

            //處理gzip response
            if ("gzip".equals(httpConnection.getContentEncoding())) {
                return new GZIPInputStream(httpConnection.getInputStream());
            }

            return httpConnection.getInputStream();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static InputStream postData(String urlString, String data, int timeoutMillis, AbstractMap.SimpleEntry<String, String>... params) {
        try {
            URL url = new URL(urlString);

            if (urlString.toLowerCase(Locale.getDefault()).startsWith(
                    "https://")) {
                SSLContext sc = SSLContext.getInstance("SSL");

                TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertManager()};
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                        .getSocketFactory());
            }

            URLConnection urlConnection = url.openConnection();

            return postData(urlConnection, data, timeoutMillis, params);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static InputStream postJSON(String urlString, String data, int timeoutMillis, AbstractMap.SimpleEntry<String, String>... params) {
        try {
            URL url = new URL(urlString);

            if (urlString.toLowerCase(Locale.getDefault()).startsWith(
                    "https://")) {
                SSLContext sc = SSLContext.getInstance("SSL");

                TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertManager()};
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc
                        .getSocketFactory());
            }

            URLConnection urlConnection = url.openConnection();

            return postJSON(urlConnection, data, timeoutMillis, params);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String postDataForString(String urlString, String data,
                                           int timeoutMillis, Cancelable cancelable, AbstractMap.SimpleEntry<String, String>... params) {

        try {
            InputStream is = postData(urlString, data, timeoutMillis, params);
            return convertToString(is, cancelable);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String postJSONForString(String urlString, String jsonString, int timeoutMillis, Cancelable cancelable, AbstractMap.SimpleEntry<String, String>... params) {
        try {
            InputStream is = postJSON(urlString, jsonString, timeoutMillis, params);
            return convertToString(is, cancelable);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String putForString(String urlString, String content) throws Exception {

        URL url = new URL(urlString);

        if (url.getProtocol().equalsIgnoreCase("https")) {
            SSLContext sc = SSLContext.getInstance("SSL");

            TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertManager()};
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());
        }

        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("PUT");
        httpCon.setRequestProperty("Content-Type", "application/json");
        httpCon.setRequestProperty("Accept", "application/json");
        //加gzip處理
        httpCon.setRequestProperty("Accept-Encoding", "gzip");

        OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
        out.write(content);
        out.flush();
        out.close();

        //處理gzip response
        if ("gzip".equals(httpCon.getContentEncoding())) {
            InputStream is = new GZIPInputStream(httpCon.getInputStream());
            int code = httpCon.getResponseCode();
            String message = httpCon.getResponseMessage();
            String result = convertToString(is, new Cancelable());
            return result;
        }

        InputStream is = httpCon.getInputStream();
        int code = httpCon.getResponseCode();
        String message = httpCon.getResponseMessage();
        String result = convertToString(is, new Cancelable());
        return result;
    }

    public static Element postDataForElement(String urlString, String data,
                                             int timeoutMilis, AbstractMap.SimpleEntry<String, String>... params) {
        try {
            InputStream is = postData(urlString, data, timeoutMilis, params);
            return XmlHelper.parseXml(is);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String convertToString(InputStream inputStream,
                                          Cancelable cancelable) {

        try {
            String result = StringUtil.EMPTY;
            if (inputStream != null) {
                Writer writer = new StringWriter();

                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(
                            inputStream, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1
                            && (cancelable == null || !cancelable.isCanceled())) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    inputStream.close();
                }
                result = writer.toString();
            }
            return result;
            // InputStreamReader isr = new InputStreamReader(inputStream,
            // "UTF-8");
            // BufferedReader rd = new BufferedReader(isr);
            // StringBuilder sb = new StringBuilder(inputStream.available());
            // String line;
            //
            // while ((line = rd.readLine()) != null
            // && (cancelable == null || !cancelable.isCanceled())) {
            // sb.append(line);
            // }
            // rd.close();
            // return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
