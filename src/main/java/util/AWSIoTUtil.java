package util;

import com.amazonaws.services.iot.client.*;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class AWSIoTUtil {
    private final String basePath;
    private final String osType;
    private final String keystorePath;
    private final String keyStorePassword;
    private final String keyPassword;
    private final KeyStore keyStore;
    private final String clientEndPoint;
    private final String clientId;
    private final AWSIotMqttClient awsIotMqttClient;
    private AWSIotDevice awsIotDevice;
    private final ShadowMessage shadowMessage;

    public AWSIoTUtil() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        basePath = System.getProperty("user.dir");
        osType = System.getProperty("os.name");
        keystorePath = osType.contains("Linux")
                ? basePath + "/my.keystore"
                : basePath + "/src/main/resources/my.keystore";

        keyStorePassword = "#glglgl12";
        keyPassword = "#glglgl12";

        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(keystorePath), keyStorePassword.toCharArray());

        clientEndPoint = "alq09zhsxqhh0-ats.iot.ap-northeast-2.amazonaws.com";
        clientId = "RaspberryPi";

        awsIotMqttClient = new AWSIotMqttClient(clientEndPoint, clientId, keyStore, keyPassword);

        shadowMessage = new ShadowMessage();
    }

    public void setAwsIotDevice(String deviceName) {
        this.awsIotDevice = new AWSIotDevice(deviceName);
    }

    public void connectClient() throws AWSIotException {
        if(this.awsIotDevice != null) {
            awsIotMqttClient.attach(awsIotDevice);
            awsIotMqttClient.connect();
        }
    }

    public void updateTemperatureHumidity(float temperature, float humidity) throws AWSIotException {
        if(awsIotMqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
            JSONObject stateObject = new JSONObject();
            JSONObject reportedObject = new JSONObject();
            JSONObject temperatureHumidityObject = new JSONObject();

            temperatureHumidityObject.put("temperature", temperature);
            temperatureHumidityObject.put("humidity", humidity);

            reportedObject.put("reported", temperatureHumidityObject);
            stateObject.put("state", reportedObject);

            awsIotDevice.update(stateObject.toJSONString());
        }
    }

    public class ShadowMessage extends AWSIotMessage {
        ShadowMessage() {
            super(null,  null);
        }

        @Override
        public void onSuccess() {
            super.onSuccess();

            System.out.println("Update Success!");
            try {
                String updatedState = awsIotDevice.get();
                System.out.println("Updated State : " + updatedState);
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure() {
            super.onFailure();
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
        }
    }
}
