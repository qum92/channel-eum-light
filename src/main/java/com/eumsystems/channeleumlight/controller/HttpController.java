package com.eumsystems.channeleumlight.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eumsystems.channeleumlight.service.HttpService;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class HttpController {
	@Autowired
	private HttpService hs;
	
	@RequestMapping(value = "/send", method = RequestMethod.POST, produces = "text/xml;charset=UTF-8")
	@PostMapping("/send")
	public @ResponseBody String httpConnection(HttpServletResponse response, @RequestBody String content) throws IOException {
		log.info("content----> "+content);
		return hs.httpURLConnection(content);
	}
}
