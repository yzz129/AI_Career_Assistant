package com.internai.career.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internai.career.entity.KnowledgeDoc;
import com.internai.career.mapper.KnowledgeDocMapper;
import com.internai.career.service.KnowledgeDocService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeDocServiceImpl extends ServiceImpl<KnowledgeDocMapper, KnowledgeDoc> implements KnowledgeDocService {
}
