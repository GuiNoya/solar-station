package com.gg.nimbitsclientpimatic;

import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NimbitsClientPimatic {

    public static void updateNimbitsData(String device, String attributeName) {
        updateNimbitsData(device, attributeName, false);
    }

    public static void updateNimbitsData(String device, String attributeName, boolean dataIsString) {
        Database.open();
        NimbitsLimitedClient.connect();

        String dataPoint = device + " - " + attributeName;

        boolean created = NimbitsLimitedClient.createDataPointIfNotExists(dataPoint);
        Date lastDate;
        if (created) {
            lastDate = new Date(0);
        } else {
            lastDate = NimbitsLimitedClient.getSingleData(dataPoint).getTimestamp();
        }

        List<Tuple> dataList = Database.getData(device, attributeName, lastDate, dataIsString);
        System.out.println("New values since last read: " + dataList.size());
        Database.close();

        List<Value> values = new ArrayList<>();
        if (dataIsString) {
            for (Tuple<Long, String> tuple : dataList) {
                System.out.println("Adding new value (string): " + tuple);
                Value value = ValueFactory.createValueModel(new Date(tuple.x), tuple.y);
                values.add(value);
            }
        } else {
            for (Tuple<Long, Double> tuple : dataList) {
                System.out.println("Adding new value (number): " + tuple);
                Value value = ValueFactory.createValueModel(tuple.y, new Date(tuple.x));
                values.add(value);
            }
        }

        if (!values.isEmpty()) {
            System.out.println("Sending data to Nimbits...");
            if (created) {
                NimbitsLimitedClient.setValues(dataPoint, values);
            } else {
                NimbitsLimitedClient.insertData(dataPoint, values);
            }
            System.out.println("Data sent.");
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            while (true) {
                updateNimbitsData("DHT11", "temperature");
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            System.out.println("Exiting by: " + e.toString());
        }

        // Ver valores do ponto
        List<Value> values = NimbitsLimitedClient.getData("DHT11 - temperature");
        for (Value v : values) {
            System.out.println(v.getDoubleValue() + " - " + v.getTimestamp().toString());
        }
    }
}
