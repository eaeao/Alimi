package com.tktv.alimi.Settings;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.os.StrictMode;
import android.util.Log;

public class xmlParser{
	public static String[] XMLParse(String str_url ,String xmlkey, String xmlkey2){
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		String[] xmlreturn;
		String xml;
		StringBuffer sBuffer = new StringBuffer();
		try{
			URL url = new URL(str_url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if(conn != null){
				conn.setConnectTimeout(20000);
				conn.setUseCaches(false);
				if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
					InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(isr, 8);
					while(true){
						String line = br.readLine();
						if(line==null){
							break;
						}
						sBuffer.append(line);
					}
					br.close();
					conn.disconnect();
				}
			}
			xml = sBuffer.toString();
		}catch (Exception e) {
			Log.i("URL:",""+e.getMessage());
			e.printStackTrace();
			return null;
		}
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			doc = db.parse(is);

			NodeList nodeList = doc.getElementsByTagName(xmlkey);
			xmlreturn = new String[nodeList.getLength()];
			for(int i=0;i<nodeList.getLength();i++)	xmlreturn[i]=((org.w3c.dom.Element)nodeList.item(i)).getAttribute(xmlkey2);
			return xmlreturn;
		}catch (Exception e){
			Log.i("XML:",""+e.getMessage());
			return null;
		}
	}
}