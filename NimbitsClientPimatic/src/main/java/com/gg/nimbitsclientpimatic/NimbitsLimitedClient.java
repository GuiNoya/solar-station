package com.gg.nimbitsclientpimatic;

import com.google.common.collect.Range;
import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerFactory;
import com.nimbits.client.model.server.apikey.AccessToken;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.PointHelper;
import com.nimbits.io.helper.ValueHelper;
import com.nimbits.io.http.NimbitsClientFactory;
import java.util.Date;
import java.util.List;

public class NimbitsLimitedClient {

    private static final EmailAddress EMAIL_ADDRESS = CommonFactory.createEmailAddress("gui.noya@gmail.com");
    private static final UrlContainer INSTANCE_URL = UrlContainer.getInstance("cloud.nimbits.com");
    private static final AccessToken API_KEY = AccessToken.getInstance("chave");
    private static final Server cloudServer = ServerFactory.getInstance(INSTANCE_URL, EMAIL_ADDRESS, API_KEY);
    private static User user;
    private static NimbitsClient client;

    private static boolean connected = false;

    public static void connect() {
        if (!connected) {
            client = NimbitsClientFactory.getInstance(cloudServer);
            user = client.login();

            connected = true;
            System.out.println("Logged: " + user.getEmail() + " " + user.getToken() + " " + cloudServer.getUrl());
        }
    }

    public static boolean createDataPointIfNotExists(String dataPoint) {
        PointHelper pointHelper = HelperFactory.getPointHelper(cloudServer);
        if (!pointHelper.pointExists(dataPoint)) {
            System.out.println("creating point");
            pointHelper.createPoint(dataPoint, "");
            return true;
        }
        System.out.println("point already created");
        return false;
    }

    public static void insertData(String point, Value value) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        valueHelper.recordValue(point, value);
    }

    public static void insertData(String point, double value) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        valueHelper.recordValue(point, value);
    }

    public static void insertData(String point, double value, Date time) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        valueHelper.recordValue(point, value, time);
    }

    public static void insertData(String point, List<Value> data) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        valueHelper.recordValues(point, data);
    }

    public static void insertData(List<Point> points) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        valueHelper.recordValues(points);
    }

    public static Value getSingleData(String point) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        return valueHelper.getValue(point);
    }

    public static List<Value> getData(String point) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        return valueHelper.getSeries(point);
    }

    public static List<Value> getData(String point, Range<Date> dateRange) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        return valueHelper.getSeries(point, dateRange);
    }

    public static List<Value> getData(String point, int count) {
        if (!connected) {
            connect();
        }
        ValueHelper valueHelper = HelperFactory.getValueHelper(cloudServer);
        return valueHelper.getSeries(point, count);
    }

    public static void setValues(String point, List<Value> values) {
        if (!connected) {
            connect();
        }
        Point p = HelperFactory.getPointHelper(cloudServer).getPoint(point);
        p.setValues(values);
        client.recordSeries(p);
    }
}
