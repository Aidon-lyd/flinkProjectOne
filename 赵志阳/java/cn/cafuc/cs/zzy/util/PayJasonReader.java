package cn.cafuc.cs.zzy.util;

import cn.cafuc.cs.zzy.model.PayData;
import com.alibaba.fastjson.JSON;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @Author : zzy
 * @Date : 2019/12/21
 * @Verson : 1.0
 */
public class PayJasonReader implements SourceFunction<PayData> {
    private final String dataFilePath;
    private transient BufferedReader reader;

    public PayJasonReader(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    @Override
    public void run(SourceContext<PayData> sourceContext) throws Exception {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFilePath), "UTF-8"));
        String jsonLine = new String();
        while (reader.ready() && (jsonLine = reader.readLine()) != null) {
            PayData payData = JSON.parseObject(jsonLine, PayData.class);
            sourceContext.collect(payData);
            Thread.sleep(500);
        }
        reader.close();
    }

    @Override
    public void cancel() {

    }
}
