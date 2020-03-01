package com.jm;

import com.jm.model.SimpleConsumptionModel;
import com.jm.security.signature.AESSignature;
import com.jm.utils.*;
import com.jm.vo.PersonConsumptionInfo;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest2 {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void AppTest() {


        PersonConsumptionInfo personConsumptionInfo = new PersonConsumptionInfo();


        String beginDateStr = "2019-08-18 10:00:00";
        Date beginDate = null;
        Long beginVariable = null;
        String beginTimeString = null;
        try {
            beginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beginDateStr);
            beginVariable = beginDate.getTime();
            beginTimeString = DateUtils.getDateStringByMillisecond(DateUtils.HOUR_FORMAT, beginVariable);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("beginDateStr :" + beginDateStr);
        System.out.println("beginDate  :" + beginDate);
        System.out.println("beginVariable  :" + beginVariable);
        System.out.println("beginTimeString :"+ beginTimeString);
        System.out.println("beginTimeString - getDayByHour :"+ DateUtils.getDayByHour(beginTimeString));

    }
}
