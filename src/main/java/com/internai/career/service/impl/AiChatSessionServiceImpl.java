package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.AiChatSession;
import com.internai.career.mapper.AiChatSessionMapper;
import com.internai.career.service.AiChatSessionService;
import org.springframework.stereotype.Service;

@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements AiChatSessionService {
}
