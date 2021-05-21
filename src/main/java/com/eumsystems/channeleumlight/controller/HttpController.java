package com.eumsystems.channeleumlight.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eumsystems.channeleumlight.model.ChannelVo;
import com.eumsystems.channeleumlight.service.HttpService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class HttpController {
	@Autowired
	private HttpService hs;
	
	@PostMapping("/channelEum")
	@RequestMapping(value = "/channelEum", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String getChat(@RequestBody ChannelVo cv, HttpServletRequest request) throws IOException {
		log.info(cv);
		log.info(request.getRemoteAddr());
		return hs.getIsucoInfo(cv, request);
	}
}
