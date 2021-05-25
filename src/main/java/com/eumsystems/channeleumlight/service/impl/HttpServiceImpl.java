package com.eumsystems.channeleumlight.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	public String getIsucoInfo(Map<String,Object> req) throws IOException {
		ChannelVo cv = new ChannelVo();
		cv.setChnlBizId((String)req.get("chnlBizId"));
		cv.setChnlOrgtId((String)req.get("chnlOrgtId"));
		cv.setIsucoDvcd((String)req.get("isucoDvcd"));
		ChannelVo chv = cm.getIsucoInfo(cv);
		Map<String,String> map = new HashMap();
		String body = xmlL.makeBodyByJsonToXml(req.get("json").toString());
		String xml = xmlL.integrateXml(chv,body);
		map.put("orgIp", com.getClientIpAddressIfServletRequestExist());
		map.put("dstIp", chv.getDstIpVal());
		map.put("dstPort", chv.getDstPortVal());
		map.put("dstUrl", chv.getDstUrlVal());
		map.put("xml", xml);
		map.put("body", body);
		map.put("type", "req");
		hls.insertTransLog(map);
		return httpURLConnection(map);
	}
	
	private String httpURLConnection(Map<String,String> map) {
		log.info(map);
		try {
			String url = String.format("http://%s:%s%s", map.get("dstIp"), map.get("dstPort"), map.get("dstUrl"));
			log.info(url);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("SOAPAction", "");
			httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10 * 1000)
					.setConnectTimeout(10 * 1000)
					.setSocketTimeout(10 * 1000)
					.build();
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(map.get("xml"), "UTF-8"));
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			CloseableHttpResponse response = httpClient.execute(httpPost);			
			
			if(response.getStatusLine().getStatusCode() == 200) {
				String result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				String content = xmlL.removeXmlHeader(result);
				map.remove("body");
				map.remove("type");
				map.put("body", content);
				hls.insertTransLog(map);
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
