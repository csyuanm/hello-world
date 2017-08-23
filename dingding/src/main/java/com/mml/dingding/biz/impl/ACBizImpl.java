package com.mml.dingding.biz.impl;

import com.mml.dingding.biz.ACBiz;
import com.mml.dingding.model.ACRequest;
import com.mml.dingding.model.ACResponse;
import com.mml.dingding.service.ACService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ACBizImpl implements ACBiz {

    //    biz call service layout
    @Autowired
    ACService acService;

    private static Logger LOGGER = Logger.getLogger(ACBizImpl.class);

    @Override
    public ACResponse addContact(ACRequest acRequest) {

        LOGGER.info("请求入参：" + acRequest.toString());
//        validate parameters first
        try {
            validateParams(acRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return acService.addContact(acRequest);
    }

    public static void validateParams(ACRequest acRequest) throws Exception {
        if (StringUtils.isEmpty(acRequest.getMobile())) {
            throw new Exception("No mobile!!");
        }
        if (StringUtils.isEmpty(acRequest.getStateCode())) {
            throw new Exception("No state code!!");
        }
        if (StringUtils.isEmpty(acRequest.getFollowerUserId())) {
            throw new Exception("No follower id!!");
        }
        if (StringUtils.isEmpty(acRequest.getName())) {
            throw new Exception("No name!!");
        }
//        if (acRequest.getLabelIds() == null || acRequest.getLabelIds().length <= 0) {
//            throw new Exception("No label ids!!");
//        }
    }
}
