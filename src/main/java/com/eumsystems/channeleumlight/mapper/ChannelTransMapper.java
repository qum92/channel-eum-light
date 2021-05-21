package com.eumsystems.channeleumlight.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.eumsystems.channeleumlight.model.ChannelTransVo;

@Mapper
public interface ChannelTransMapper {

	public ChannelTransVo selectSeqNumber();
	public int insertReqTransLog(ChannelTransVo ctv);
	public int insertResTransLog(ChannelTransVo ctv);
}
