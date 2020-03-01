package com.jm.service;

import com.jm.common.HBaseClient;
import com.jm.model.SimpleConsumptionModel;
import com.jm.model.SimplePersonConsumptionStatModel;
import com.jm.utils.DateUtils;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Properties;

/**
 * @Description: $description$
 * @Author: Jomin
 * @Date: $time$ $date$
 */
public class StatService {

    private Properties props;
    private static StatService service;

    public static StatService getInstance(Properties props) {
        if (service == null) {
            synchronized (StatService.class) {
                if (service == null) {
                    service = new StatService();
                    service.props = props;
                }
            }
        }
        return service;
    }


    public void addPersonConsumptionHistory(SimplePersonConsumptionStatModel model) {

        System.out.println("====================addPersonConsumptionHistory==========================");

        String tableName = "person_consumption_history";
        Table table = HBaseClient.getInstance(this.props).getTable(tableName);
        String rowKey = model.getPersonId() + ":" + DateUtils.getDayByHour(model.getCreateTime());

        try {
            table.incrementColumnValue(Bytes.toBytes(rowKey), Bytes.toBytes("amount"), Bytes.toBytes(model.getConsumptionType()), model.getAmount());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            HBaseClient.closeTable(table);
        }
    }
}
