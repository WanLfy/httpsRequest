package cn.com.sge.gems.base.web.utils;

import cn.com.sge.gems.base.web.filter.CasTicketValidationFilter;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author qinhao
 * @Date 2017/10/23 16:19
 */
public class HttpsRequest {

    // 接口
    public static final String API_USERINFO = CasTicketValidationFilter.CAS_SERVER_URLPREFIX + "api/user/query";
    public static final String API_USER_PERMISSION = CasTicketValidationFilter.CAS_SERVER_URLPREFIX + "api/user/permission";
    public static final String API_RESOURCE = CasTicketValidationFilter.CAS_SERVER_URLPREFIX + "api/user/resource";
    public static final String API_ROLEINFO = CasTicketValidationFilter.CAS_SERVER_URLPREFIX + "api/role/roleinfo";
    public static final String API_ROLE_PERMISSION = CasTicketValidationFilter.CAS_SERVER_URLPREFIX + "api/role/permission";
    public static final String API_PERMISSION_RESOURCE = CasTicketValidationFilter.CAS_SERVER_URLPREFIX + "api/permission/resource";

    public static final String API_REQUEST_STATUS = "status";
    public static final String API_REQUEST_MESSAGE = "message";
    public static final String API_REQUEST_RESULT = "result";

    // 实现证书信任管理类
    private static TrustManager myX509TrustManager = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    };

    /**
     * @param url   请求的 https api
     * @param param 传递参数
     * @return
     */
    public static JSONObject SendHttpsPOST(String url, List<NameValuePair> param) {
        String result = null;
        JSONObject json = null;
        // 使用此工具可以将键值对编码成"Key=Value&Key2=Value2&Key3=Value3”形式的请求参数
        String requestParam = URLEncodedUtils.format(param, "UTF-8");
        try {
            // 设置SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);

            // 打开连接
            // 要发送的POST请求url?Key=Value&Key2=Value2&Key3=Value3的形式
            URL requestUrl = new URL(url + "?" + requestParam);
            HttpsURLConnection httpsConn = (HttpsURLConnection) requestUrl.openConnection();

            // 设置套接工厂
            httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());

            // 加入数据
            httpsConn.setRequestMethod("POST");
            httpsConn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(
                    httpsConn.getOutputStream());
            /*if (data != null)
                out.writeBytes(data);*/

            out.flush();
            out.close();

            //获取输入流
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
            int code = httpsConn.getResponseCode();
            if (HttpsURLConnection.HTTP_OK == code) {
                String temp = in.readLine();
                /*连接成一个字符串*/
                while (temp != null) {
                    if (result != null)
                        result += temp;
                    else
                        result = temp;
                    temp = in.readLine();
                }
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result != null && result != "") {
            json = JSONObject.parseObject(result);
        }
        return json;
    }
}
