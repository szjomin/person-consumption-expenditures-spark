package com.jm.controller;

import com.jm.exception.ParameterException;
import com.jm.request.ExpendituresInfoRequest;
import com.jm.result.IResult;
import com.jm.security.signature.AESSignature;
import com.jm.service.ExpendituresInfoService;
import com.jm.utils.JSONUtil;
import com.jm.utils.MyLogger;
import com.jm.utils.SignUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description: $description$
 * @Author: Jomin
 * @Date: $time$ $date$
 */
@Controller
public class ExpendituresInfoController extends BaseController {

    private static final MyLogger LOG = MyLogger.getLogger(ExpendituresInfoController.class);

    private String parivateKey = "538b520311686319";

    @Autowired
    private ExpendituresInfoService expendituresInfoService;

    @RequestMapping(value = "/expenditures", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> gatherInfo(HttpServletRequest request) {

        ExpendituresInfoRequest expendituresInfoRequest = parseEncryptedJsonRequest(request, ExpendituresInfoRequest.class);
        return create4Result(expendituresInfoService.handleExpendituresInfo(expendituresInfoRequest));
    }

    @RequestMapping(value = "/personConsumptionHistory", method = RequestMethod.GET)
    @ResponseBody
    public IResult PersonConsumptionhistory(HttpServletRequest request,
                                            @RequestParam(value = "dateType", required = true) String date,
                                            @RequestParam(value = "userId", required = true) long userId) throws Exception {
        return createResult(expendituresInfoService.getPersonConsumptionStat(date, userId));

    }


    private <T> T parseEncryptedJsonRequest(HttpServletRequest request, Class<T> clz) {
        try {
            T parsedRequest = null;
            String decryptData = null;
            byte[] requestBytes = null;
            try {
                requestBytes = SignUtils.getRequestBytes(request);
                if (ArrayUtils.isEmpty(requestBytes)) {
                    throw new ParameterException("null request bytes");
                }

                String st = new String(requestBytes, "utf-8");
                byte[] realData = SignUtils.resolve(st);
                decryptData = AESSignature.decrypt(realData, parivateKey);

                parsedRequest = JSONUtil.getInstance().json2ObjectIgnoreDifference(decryptData, clz);
            } catch (Exception e) {
                String msg = "Exception happend when creating request." + ", requestBytes: " + Arrays.toString(requestBytes) + ", decryptData:" + decryptData + ", request: " + parsedRequest;
                LOG.warn(msg);
                throw new ParameterException(msg, e);
            }

            if (parsedRequest == null) {
                throw new ParameterException("Null request");
            }
            return parsedRequest;
        } finally {
        }
    }


}
