package imt.org.web.mqttlogger.main;

import imt.org.web.mqttlogger.subscriber.MQTTSubscriber;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * MqttLogger main class
 */
public class Main {

    public static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        System.out.println("MqttLogger!");
        new MQTTSubscriber();
    }
}
