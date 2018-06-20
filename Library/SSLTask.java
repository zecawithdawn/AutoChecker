package com.example.kyh.real.Library;


import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by seoseongho on 15. 1. 20..
 */
public class SSLTask extends AsyncTask<Void, Void, JSONObject> {

    String urlPath;
    JSONObject json;
    TaskListener taskListener;
    ClientConnectionManager ccm;

    public SSLTask(String urlPath, JSONObject json, TaskListener tl) {
        this.urlPath = urlPath;
        this.json = json;
        taskListener = tl;
    }



    @Override
    protected JSONObject doInBackground(Void... params) {

        HttpClient httpClient = getHttpClient();

        try {
            URI url = new URI(urlPath);

            HttpPost httpPost = new HttpPost();
            httpPost.setURI(url);

            String jsonStr = json.toString();

            String key = "365631FA622C7A23";
            String ivStr = "C794A5F23F3519B6";

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            Log.d("JsonDATA", jsonStr);

            byte[] encryptingByteArray = cipher.doFinal(jsonStr.getBytes("UTF8"));
            String encData = Base64.encodeToString(encryptingByteArray, Base64.DEFAULT);

            Log.d("ENCDATA", encData);

            List<BasicNameValuePair> nameValue = new ArrayList<BasicNameValuePair>(2);
            nameValue.add(new BasicNameValuePair("data", encData));
            //웹 접속 - utf-8 방식으로
            httpPost.setEntity(new UrlEncodedFormEntity(nameValue));

            HttpResponse response = httpClient.execute(httpPost);
            String responceString = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

            if(responceString.equals("")){
                ccm.shutdown();
                return null;
            }

            Log.d("RESULT MESSAGE", responceString);

            //AES128 Decrypt
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] encryptedByteArray = Base64.decode(responceString, Base64.DEFAULT);
            byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
            String decryptedData = new String(decryptedByteArray, "UTF-8");

            Log.d("DECTYPTED DATA", decryptedData);

            JSONObject json = new JSONObject(decryptedData);

            ccm.shutdown();

            return json;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ccm.shutdown();

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        taskListener.onReceived(jsonObject);
        super.onPostExecute(jsonObject);
    }

    private HttpClient getHttpClient() {
        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SFSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);

        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }
}
