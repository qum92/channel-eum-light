package com.eumsystems.channeleumlight.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.eumsystems.channeleumlight.model.ChannelVo;

public interface HttpService {	
	public String getIsucoInfo(ChannelVo cv, HttpServletRequest request) throws IOException;

}
