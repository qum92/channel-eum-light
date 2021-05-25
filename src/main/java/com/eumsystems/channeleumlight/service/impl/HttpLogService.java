package com.eumsystems.channeleumlight.service.impl;

import java.util.Map;

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
	
	public void insertTransLog(Map<String,String> map) {
		ChannelTransVo ctv = new ChannelTransVo();
		if(map.containsKey("type") && "req".equals(map.get("type"))) {
			getSeqNum();
			ctv.setSeqTransNum(seqTransNum);
			ctv.setDptpOrgtId(map.get("orgIp"));
			ctv.setDstOrgtId(map.get("dstIp"));
			ctv.setChnlTrnsCntn(map.get("body"));
			ctm.insertReqTransLog(ctv);
		}else {
			ctv.setSeqTransNum(seqTransNum);
			ctv.setDptpOrgtId(map.get("dstIp"));
			ctv.setDstOrgtId(map.get("orgIp"));
			ctv.setChnlTrnsCntn(map.get("body"));
			ctm.insertResTransLog(ctv);
		}
	}
}
