package com.eumsystems.channeleumlight.service.impl;

import java.io.IOException;

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
import com.eumsystems.channeleumlight.util.Common;
import com.eumsystems.channeleumlight.util.XmlLibrary;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class HttpServiceImpl implements HttpService {
	@Autowired
	private ChannelMapper cm;
	@Autowired
	private XmlLibrary xmlL;
	@Autowired
	private HttpLogService hls;
	@Autowired
	private Common com;

	public String getIsucoInfo(ChannelVo cv) throws IOException {
		ChannelVo chv = cm.getIsucoInfo(cv);
		String body = xmlL.makeBodyByJsonToXml(cv.getJson());
		String xml = xmlL.integrateXml(chv,body);
		String orgIp = com.getClientIpAddressIfServletRequestExist();
		String dstIp = chv.getDstIpVal();
		String dstPort = chv.getDstPortVal();
		String dstUrl = chv.getDstUrlVal();
		hls.insertReqTransLog(orgIp, body, dstIp);
		return httpURLConnection(orgIp, dstIp, dstPort, dstUrl, xml);
	}
	
	private String httpURLConnection(String orgIp, String dstIp, String dstPort, String dstUrl, String xml) {
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
				String content = xmlL.removeXmlHeader(result);
				content = content.replaceAll("<BusinessArea>", "");
				content = content.replaceAll("</BusinessArea>", "");
				hls.insertResTransLog(dstIp, content, orgIp);
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
}
