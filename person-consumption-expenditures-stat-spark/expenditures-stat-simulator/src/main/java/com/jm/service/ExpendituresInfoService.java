package com.jm.service;

import com.jm.common.HBaseClient;
import com.jm.model.SimpleConsumptionModel;
import com.jm.utils.DateUtils;
import com.jm.utils.MyStringUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jm.request.ExpendituresInfoRequest;
import com.jm.result.BaseResult;
import com.jm.result.ExpendituresInfoResult;
import com.jm.utils.JSONUtil;
import com.jm.utils.MyLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: $description$
 * @Author: Jomin
 * @Date: $time$ $date$
 */
@Service
public class ExpendituresInfoService {

    private static final MyLogger LOG = MyLogger.getLogger(ExpendituresInfoService.class);
    private static final MyLogger PERSON_CONSUMPTION_EEXPENDITURES_INFO_LOG = MyLogger.getLogger("PERSON-CONSUMPTION-EEXPENDITURES-INFO-JSON");

    @Autowired
    public HBaseClient hBaseClient;

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

    /*
     * 获取用户某天的应用使用统计
     * */
    public List<SimpleConsumptionModel> getPersonConsumptionStat(String day, long personId) throws Exception
    {
        List<SimpleConsumptionModel> resList = new ArrayList();

        Table table = hBaseClient.getTable("person_consumption_history");
        String rowKey = MyStringUtil.getFixedLengthStr(personId + "", 10) + ":" + DateUtils.getCurrent(DateUtils.YYYYMMDD);

        Get get = new Get(Bytes.toBytes(rowKey));
        Result r = table.get(get);

        for (Cell cell : r.rawCells()) {
            String consumptionType = new String(CellUtil.cloneQualifier(cell));
            long amount = Bytes.toLong(CellUtil.cloneValue(cell));

            SimpleConsumptionModel model = new SimpleConsumptionModel();
            model.setConsumptionType(consumptionType);
            model.setAmount(amount);

            resList.add(model);
        }

        Collections.sort(resList);
        return resList;
    }



}
