package com.eumsystems.channeleumlight.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eumsystems.channeleumlight.model.ChannelVo;
import com.google.gson.Gson;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class XmlLibrary {
	@Autowired
	private Common cm;
	
	public String removeXmlHeader(String xml) {
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
		
	public String makeBodyByJsonToXml(String json) {
		log.info(json);
		Gson gson = new Gson();
		Map m = gson.fromJson(json, Map.class);
		StringBuffer sb = new StringBuffer();
		sb.append("<BusinessArea>\r\n");
		
		processMap(m, sb);
		
		sb.append("</BusinessArea>");
		return sb.toString();
	}
	
	public String integrateXml(ChannelVo cv, String xml) throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/template/header.xml");
		String template = IOUtils.toString(is, "UTF-8");
		
		template = template.replaceAll("#hdrInsId#",      cv.getChnlOrgtId());
		template = template.replaceAll("#hdrBizCode#",    cv.getChnlBizId());
		template = template.replaceAll("#hdrSndDate#",    cm.getCurrentTime("YYYYMMDD"));
		template = template.replaceAll("#hdrSndTime#",    cm.getCurrentTime("HHMMSS"));
		template = template.replaceAll("#hdrTxnDate#",    cm.getCurrentTime("YYYYMMDD"));
		
		Pattern p = Pattern.compile(".*?(<BusinessArea>.*?</BusinessArea>)", Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		if (m.find()) {
			template = template.replaceAll("<BusinessArea/>", m.group(1));
		} else {
			template = template.replaceAll("<BusinessArea/>", "");
		}
		return template;
	}
}
