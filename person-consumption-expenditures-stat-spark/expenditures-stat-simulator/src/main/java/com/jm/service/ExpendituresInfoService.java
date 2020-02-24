package com.jm.service;

import org.springframework.stereotype.Service;
import com.jm.request.ExpendituresInfoRequest;
import com.jm.result.BaseResult;
import com.jm.result.ExpendituresInfoResult;
import com.jm.utils.JSONUtil;
import com.jm.utils.MyLogger;

/**
 * @Description: $description$
 * @Author: Jomin
 * @Date: $time$ $date$
 */
@Service
public class ExpendituresInfoService {

    private static final MyLogger LOG = MyLogger.getLogger(ExpendituresInfoService.class);
    private static final MyLogger PERSON_CONSUMPTION_EEXPENDITURES_INFO_LOG = MyLogger.getLogger("PERSON-CONSUMPTION-EEXPENDITURES-INFO-JSON");

    public BaseResult handleExpendituresInfo(ExpendituresInfoRequest expendituresInfoRequest)
    {
        BaseResult result = new ExpendituresInfoResult();

        //还需要根据业务对此对对象做相关操作
        //some handler

        String json = "";

        try {
            json = JSONUtil.fromObject(expendituresInfoRequest);
            PERSON_CONSUMPTION_EEXPENDITURES_INFO_LOG.info(json);
        } catch (Exception e) {
            LOG.error("to json error,json=" + json, e);
        }

        result.setSuccess(true);
        return result;
    }
}
