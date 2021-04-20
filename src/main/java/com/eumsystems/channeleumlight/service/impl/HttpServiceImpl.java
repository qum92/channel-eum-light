package com.eumsystems.channeleumlight.service.impl;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.eumsystems.channeleumlight.service.HttpService;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class HttpServiceImpl implements HttpService {

	@Override
	public String httpURLConnection(String content) {
		String ip = "http://bis.eumsystems.com";
		String port = "80";
		String path = "/soap";
		
		try {
			String url = String.format("%s:%s%s", ip, port, path);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(10 * 1000)
					.setConnectTimeout(10 * 1000)
					.setSocketTimeout(10 * 1000)
					.build();
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new StringEntity(content));
			
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			CloseableHttpResponse response = httpClient.execute(httpPost);			
			
			if(response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity());
				System.out.println(result);
				return result;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
