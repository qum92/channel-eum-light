package com.eumsystems.channeleumlight.model;

import lombok.Data;

@Data
public class ChannelTransVo {
	private int seqTransNum;
	private String dptpOrgtId;
	private String dstOrgtId;
	private String chnlTdt;
	private String chnlTrnsTm;
	private String chnlTrnsCntn;
}
