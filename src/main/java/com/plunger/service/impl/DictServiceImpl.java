package com.plunger.service.impl;

import com.plunger.bean.plunger.Dict;
import com.plunger.mapper.plunger.DictMapper;
import com.plunger.service.DictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("dictService")
public class DictServiceImpl implements DictService {

    @Resource
    private DictMapper dictMapper;

    @Override
    public String findValueByTypeAndName(String type, String name) {
        Dict dict = dictMapper.findByTypeAndName(type, name);
        if (dict != null) {
            return dict.getValue();
        }
        return "";
    }
}
