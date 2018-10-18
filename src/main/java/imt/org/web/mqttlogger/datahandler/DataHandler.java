package imt.org.web.mqttlogger.datahandler;

import imt.org.web.commonmodel.model.SensorData;
import imt.org.web.mqttlogger.main.Main;

import lombok.AllArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * DataHandler class
 */
@AllArgsConstructor
public class DataHandler implements Runnable {

    private byte[] message;

    /**
     * Handle received message
     * @param message Message
     */
    public void handleMessage(byte[] message) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(message);
            ObjectInputStream ois = new ObjectInputStream(bais);
            SensorData sensorData = (SensorData)ois.readObject();
            logSensorData(sensorData);
        } catch (ClassNotFoundException e) {
            System.out.println("handleMessage() - Cannot found requested class - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("handleMessage() - Unable to deserialize object - " + e.getMessage());
        }
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        handleMessage(message);
    }

    /**
     * Log received SensorData
     * @param sensorData Received SensorData
     */
    public void logSensorData(SensorData sensorData) {
        Main.log.trace( "SensorData(idSensor=" + sensorData.getIdSensor() + ") idCountry=" + sensorData.getIdCountry()
                + "; idCity=" + sensorData.getIdCity() + "; gpsCoordinates=" + sensorData.getGpsCoordinates()
                + "; measureType=" + sensorData.getMeasureType() + "; measureValue=" + sensorData.getMeasureValue()
                + "; date=" + sensorData.getDate() + ";");
    }
}
