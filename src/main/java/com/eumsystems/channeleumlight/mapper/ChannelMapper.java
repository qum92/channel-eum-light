package com.eumsystems.channeleumlight.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.eumsystems.channeleumlight.model.ChannelVo;

@Mapper
public interface ChannelMapper {

	public ChannelVo getIsucoInfo(ChannelVo cv);
}
