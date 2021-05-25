package com.eumsystems.channeleumlight.service;

import java.io.IOException;
import java.util.Map;

public interface HttpService {	
	public String getIsucoInfo(Map<String,Object> req) throws IOException;

}
