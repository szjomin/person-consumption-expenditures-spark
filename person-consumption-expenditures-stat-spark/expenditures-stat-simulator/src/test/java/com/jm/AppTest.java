package com.jm;

import com.jm.model.SimpleConsumptionModel;
import com.jm.model.SimpleCustomerProfileModel;
import com.jm.security.signature.AESSignature;
import com.jm.utils.FormatJsonUtil;
import com.jm.utils.HttpHelper;
import com.jm.utils.JSONUtil;
import com.jm.utils.SignUtils;
import com.jm.vo.PersonConsumptionInfo;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;


import java.io.IOException;
import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    @Test
    public void AppTest() {
   
        String url = "http://CDH01:8080/pce/expenditures";
        System.out.println("junit request url==================" + url);
        PostMethod method = new PostMethod(url);

        PersonConsumptionInfo personConsumptionInfo = new PersonConsumptionInfo();


        SimpleConsumptionModel simpleConsumption1 = new SimpleConsumptionModel();
        SimpleConsumptionModel simpleConsumption2 = new SimpleConsumptionModel();

        simpleConsumption1.setPersonalIdentificationNumber("56000001");
        simpleConsumption1.setConsumptionType("insurance");
        simpleConsumption1.setAmount(1000);

        simpleConsumption1.setPersonalIdentificationNumber("56000001");
        simpleConsumption2.setConsumptionType("loanRepayment");
        simpleConsumption2.setAmount(2000);

        ArrayList<SimpleConsumptionModel> list = new ArrayList<SimpleConsumptionModel>();

        list.add(simpleConsumption1);
        list.add(simpleConsumption2);

        personConsumptionInfo.setSimpleConsumptionList(list);

        try {
            String json = JSONUtil.fromObject(personConsumptionInfo);
            System.out.println("junit request json========" + json);
            FormatJsonUtil.printJson(json);

            // AES加密
            byte[] bytes = AESSignature.encrypt(json, HttpHelper.parivateKey);

            // 格式化成16进制字符串
            String requestData = SignUtils.getFormattedText(bytes);

            method.setRequestBody(requestData);

            HttpHelper.getHttpClient().executeMethod(method);
            String result = HttpHelper.getHttpRequestResult(method);
            System.out.println("junit response json========" + result);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
