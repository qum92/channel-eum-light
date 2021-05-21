package com.eumsystems.channeleumlight.service;

import java.io.IOException;

import com.eumsystems.channeleumlight.model.ChannelVo;

public interface HttpService {	
	public String getIsucoInfo(ChannelVo cv) throws IOException;

}
