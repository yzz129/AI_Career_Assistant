package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.AiChatMessage;
import com.internai.career.mapper.AiChatMessageMapper;
import com.internai.career.service.AiChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements AiChatMessageService {
}
