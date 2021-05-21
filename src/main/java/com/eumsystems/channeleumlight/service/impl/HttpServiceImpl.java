package com.eumsystems.channeleumlight.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eumsystems.channeleumlight.mapper.ChannelMapper;
import com.eumsystems.channeleumlight.model.ChannelVo;
import com.eumsystems.channeleumlight.service.HttpService;
import com.google.gson.Gson;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class HttpServiceImpl implements HttpService {
	@Autowired
	private ChannelMapper cm;

	public String getIsucoInfo(ChannelVo cv) throws IOException {
		ChannelVo chv = cm.getIsucoInfo(cv);
		String body = makeBodyByJsonToXml(cv.getJson());
		String xml = integrateXml(chv,body);
		String dstIp = chv.getDstIpVal();
		String dstPort = chv.getDstPortVal();
		String dstUrl = chv.getDstUrlVal();
		return httpURLConnection(dstIp, dstPort, dstUrl, xml);
	}
	
	private String httpURLConnection(String dstIp, String dstPort, String dstUrl, String xml) {
		log.info(xml);
		try {
			String url = String.format("http://%s:%s%s", dstIp, dstPort, dstUrl);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("SOAPAction", "");
			httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10 * 1000)
					.setConnectTimeout(10 * 1000)
					.setSocketTimeout(10 * 1000)
					.build();
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(xml, "UTF-8"));
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			CloseableHttpResponse response = httpClient.execute(httpPost);			
			
			if(response.getStatusLine().getStatusCode() == 200) {
				String result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				String content = removeXmlHeader(result);
				content = content.replaceAll("<BusinessArea>", "");
				content = content.replaceAll("</BusinessArea>", "");
				log.info(content);
				JSONObject json = XML.toJSONObject(content);
				String body = json.toString();
				log.info(body);
				return body;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String removeXmlHeader(String xml) {
		String body = "";
		Pattern p = Pattern.compile(".*?(<BusinessArea>.*?</BusinessArea>)", Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		if (m.find()) {
			body = m.group(1); 
		}
		return body;
	}

	private static void processMap(Map m, StringBuffer sb) {
		Iterator<String> keyIt = m.keySet().iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			Object value = (Object)m.get(key);
			if (value instanceof List) {
				processList((List)value, sb, key);
			}else if (value instanceof Map) {
				sb.append("<").append(key).append(">\r\n");
				processMap((Map)value, sb);
				sb.append("</").append(key).append(">\r\n");
			}else {
				String element = makeElement(key, value);
				sb.append(element);
			}
		}
	}
	
	private static void processList(List list, StringBuffer sb, String key) {
		for (int i=0; i<list.size(); i++) {
			Map m = (Map)list.get(i);
			sb.append("<").append(key).append(">\r\n");
			processMap(m, sb);
			sb.append("</").append(key).append(">\r\n");
		}
	}
	
	private static String makeElement(String key, Object value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<").append(key).append(">").append(value).append("</").append(key).append(">\r\n");
		return sb.toString();
	}
	
	private String integrateXml(ChannelVo cv, String xml) throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/template/header.xml");
		String template = IOUtils.toString(is, "UTF-8");
		
		template = template.replaceAll("#hdrInsId#",      cv.getChnlOrgtId());
		template = template.replaceAll("#hdrBizCode#",    cv.getChnlBizId());
		template = template.replaceAll("#hdrSndDate#",    getCurrentTime("YYYYMMDD"));
		template = template.replaceAll("#hdrSndTime#",    getCurrentTime("HHMMSS"));
		template = template.replaceAll("#hdrTxnDate#",    getCurrentTime("YYYYMMDD"));
		
		Pattern p = Pattern.compile(".*?(<BusinessArea>.*?</BusinessArea>)", Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		if (m.find()) {
			template = template.replaceAll("<BusinessArea/>", m.group(1));
		} else {
			template = template.replaceAll("<BusinessArea/>", "");
		}
		return template;
	}
	
	public static String getCurrentTime(String format) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat(format);
		String dateString = df.format(now);
		return dateString;
	}
	
	private static String makeBodyByJsonToXml(String json) {
		Gson gson = new Gson();
		Map m = gson.fromJson(json, Map.class);
		StringBuffer sb = new StringBuffer();
		sb.append("<BusinessArea>\r\n");
		
		processMap(m, sb);
		
		sb.append("</BusinessArea>");
		return sb.toString();
	}
}
