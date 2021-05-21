package com.eumsystems.channeleumlight.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eumsystems.channeleumlight.mapper.ChannelTransMapper;
import com.eumsystems.channeleumlight.model.ChannelTransVo;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class HttpLogService {
	@Autowired
	private ChannelTransMapper ctm;
	private int seqTransNum;
	
	private void getSeqNum() {
		ChannelTransVo ctv = new ChannelTransVo();
		ctv = ctm.selectSeqNumber();
		log.info(ctv);
		seqTransNum = ctv.getSeqTransNum();
	}
	
	public void insertReqTransLog(String reqIp, String body, String resIp) {
		getSeqNum();
		log.info(seqTransNum);
		ChannelTransVo ctv = new ChannelTransVo();
		ctv.setSeqTransNum(seqTransNum);
		ctv.setDptpOrgtId(reqIp);
		ctv.setDstOrgtId(resIp);
		ctv.setChnlTrnsCntn(body);
		ctm.insertReqTransLog(ctv);
	}
	
	public void insertResTransLog(String reqIp, String body, String resIp) {
		ChannelTransVo ctv = new ChannelTransVo();
		ctv.setSeqTransNum(seqTransNum);
		ctv.setDptpOrgtId(reqIp);
		ctv.setDstOrgtId(resIp);
		ctv.setChnlTrnsCntn(body);
		ctm.insertResTransLog(ctv);
	}
}
