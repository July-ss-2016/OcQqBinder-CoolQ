package vip.ourcraft.coolqplugins.ocqqbinder.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by July_ on 2017/10/14.
 */
public class HttpUtil {
    public static String sendGet(final String url, final String cookie) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("cookie", cookie);
            connection.connect();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result.toString();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return result.toString();
    }
}
