package tw.com.ischool.fireflylite.util;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Converter {
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";

	public static boolean toBoolean(String str) {
		return toBoolean(str, false);
	}

	public static boolean toBoolean(String str, boolean defaultValue) {
		if (str == null)
			return defaultValue;

		if (str.equals(""))
			return defaultValue;

		try {
			return Boolean.parseBoolean(str);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static int toInteger(String intStr, int defaultInt) {
		try {
			return Integer.parseInt(intStr);
		} catch (Exception ex) {
			return defaultInt;
		}
	}

	public static int toInteger(String intStr) {
		return toInteger(intStr, -1);
	}

	public static Date toDate(String dateString) {
		String formatString = DEFAULT_DATETIME_FORMAT;
		if (dateString.contains("-"))
			formatString = formatString.replace("/", "-");
		return toDate(dateString, formatString);
	}

	public static Date toDate(String dateString, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);

		try {
			return format.parse(dateString);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Date toShortDate(String dateString) {
		return toShortDate(dateString, "1970/01/01");
	}

	public static Date toShortDate(String dateString, String defaultDate) {
		String formatString = DEFAULT_DATE_FORMAT;

		if (dateString == null)
			dateString = defaultDate;
		else if (dateString.isEmpty())
			dateString = defaultDate;

		if (dateString.contains("-"))
			formatString = formatString.replace("/", "-");

		return toDate(dateString, formatString);
	}

	public static String toBase64String(byte[] bytes) {
		// return Base64.encode(bytes);
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	public static byte[] fromBase64String(String base64String) {
		try {
			// return Base64.decode(base64String);
			return Base64.decode(base64String, Base64.DEFAULT);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String toBase64String(Object obj) {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(s);
			out.writeObject(obj);
			out.close();

			byte[] pubBytes = s.toByteArray();
			String str = Converter.toBase64String(pubBytes);
			return str;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromBase64String(String base64String, Class<T> c) {
		try {
			byte[] b = Converter.fromBase64String(base64String);

			ByteArrayInputStream keyStream = new ByteArrayInputStream(b);

			java.io.ObjectInputStream in = new java.io.ObjectInputStream(
					keyStream);
			Object object = in.readObject();
			in.close();

			return c.cast(object);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String toShortDateString(Date date) {
		return toShortDateString(date, "");
	}

	public static String toShortDateString(Date date, String defaultString) {
		return toDateString(date, DEFAULT_DATE_FORMAT, defaultString);
	}

	public static String toDateString(Date date) {
		return toDateString(date, DEFAULT_DATETIME_FORMAT, "");
	}

	public static String toDateString(Date date, String formatString,
			String defaultString) {
		if (date == null)
			return defaultString;

		try {
			SimpleDateFormat format = new SimpleDateFormat(formatString);
			return format.format(date);
		} catch (Exception ex) {
			return defaultString;
		}
	}

	public static String compressString(String source) {
		try {
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			OutputStreamWriter oos = new OutputStreamWriter(gz);

			oos.write(source);
			oos.flush();
			oos.close();
			fos.close();

			byte[] bs = fos.toByteArray();
			return Converter.toBase64String(bs);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String decompressString(String compressed) {
		byte[] bs1 = Converter.fromBase64String(compressed);

		try {
			ByteArrayInputStream fis = new ByteArrayInputStream(bs1);
			GZIPInputStream gs = new GZIPInputStream(fis);

			InputStreamReader reader = new InputStreamReader(gs);

			StringBuilder sb = new StringBuilder();
			char[] cs = new char[1024];
			int d;
			while ((d = reader.read(cs)) != -1) {
				sb.append(cs, 0, d);
			}
			reader.close();
			return sb.toString();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static java.sql.Date toSqlDate(Date d) {
		if (d == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(d);

		java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
		return sqlDate;
	}

	public static Timestamp toSqlTimestamp(String dateString) {
		return toSqlTimestamp(dateString, DEFAULT_DATETIME_FORMAT);
	}

	public static Timestamp toSqlTimestamp(String dateString, String format) {
		Date d = toDate(dateString, format);
		return new Timestamp(d.getTime());
	}

	public static java.sql.Date toSqlDate(String dateString) {
		return toSqlDate(dateString, DEFAULT_DATETIME_FORMAT);
	}

	public static java.sql.Date toSqlDate(String dateString, String formatString) {
		Date d = toDate(dateString, formatString);
		return toSqlDate(d);
	}

	public static Date toDate(java.sql.Date d) {
		if (d == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(d.getTime());

		return cal.getTime();
	}

	public static String toSHA1String(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");

			byte[] result = md.digest(source.getBytes("utf-8"));

			return Converter.toBase64String(result);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String toMD5String(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] result = md.digest(source.getBytes("utf-8"));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toHexString(0xFF & result[i]));
			}
			return sb.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static double toDouble(String valueString) {
		return toDouble(valueString, 0);
	}

	public static double toDouble(String valueString, double defaultValue) {
		try {
			return Double.parseDouble(valueString);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String toString(InputStream in) {
		if (in != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];

			try {
				Reader reader = new BufferedReader(new InputStreamReader(in,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
				return writer.toString();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} finally {
				try {
					in.close();
				} catch (Exception ex) {
				}
			}
		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> c, List<T> events) {
		if (events == null)
			return null;

		T[] array = (T[]) Array.newInstance(c, events.size());
		return events.toArray(array);
	}

	// public static <T> T[] toArray(List<T> list){
	//
	// T[] ls = (T[]) list.toArray();
	// return ls;
	// }
	//
	// public static void main(String[] args) {
	// ArrayList<String> str = new ArrayList<String>();
	// str.add("abc");
	// str.add("1234");
	// String[] s = Converter.toArray(str);
	// System.out.println(s[1]);
	// }
}
