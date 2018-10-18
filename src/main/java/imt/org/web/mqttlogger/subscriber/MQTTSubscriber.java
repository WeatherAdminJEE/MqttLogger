package imt.org.web.mqttlogger.subscriber;

import imt.org.web.mqttlogger.datahandler.DataHandler;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MQTT Publisher class
 */
public class MQTTSubscriber implements MqttCallback {

    private ExecutorService threadPool;
    private MqttClient subscriber;
    private MqttConnectOptions connectOptions;
    private String brokerUrl;
    private String topic;
    private boolean cleanSession;

    // Config file
    public static final ResourceBundle CONFIG = ResourceBundle.getBundle("config");

    public MQTTSubscriber() {
        threadPool = Executors.newFixedThreadPool(10);
        initMQTTSubscriber();

        // Temp directory
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(cleanSession);

            // Construct an MQTT blocking mode client
            subscriber = new MqttClient(this.brokerUrl, "MqttLogger", dataStore);
            subscriber.connect(connectOptions);
            subscriber.setCallback(this);
            subscriber.subscribe(topic);
            System.out.println("MQTT - Connected to " + brokerUrl + " - topic " + topic);
        } catch (MqttException e) {
            System.out.println("MQTTSubscriber() - Unable to set up client : " + e.getMessage());
        }
    }

    /**
     * Init MQTTSubscriber properties
     */
    private void initMQTTSubscriber() {
        String url = CONFIG.getString("MQTTBroker");
        String port = CONFIG.getString("MQTTPort");
        String protocol = "tcp://";
        brokerUrl = protocol + url + ":" + port;
        cleanSession = false;
        topic = CONFIG.getString("MQTTTopic");
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        System.out.println("connectionLost() - Connection lost - " + cause);
        try {
            subscriber.connect(connectOptions);
        } catch (MqttException e) {
            System.out.println("connectionLost() - Unable to reconnect broker - " + e.getMessage());
        }
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Unused - Called when a message has been delivered to the server
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        threadPool.execute(new DataHandler(message.getPayload()));
    }
}
