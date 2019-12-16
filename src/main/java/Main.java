import dht11.DHT11;
import util.AWSIoTUtil;

public class Main {

    public static void main(String[] args) {
        DHT11 dht11 = new DHT11();
        AWSIoTUtil awsIoTUtil = null;

        try {
            awsIoTUtil = new AWSIoTUtil();
            awsIoTUtil.setAwsIotDevice("RaspberryPi");
            awsIoTUtil.connectClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(awsIoTUtil != null) {
            while (true) {
                try {
                    Thread.sleep(3000);
                    float[] temperatureHumidity = dht11.getTemperatureHumidity();

                    if(temperatureHumidity[0] != 0.0f && temperatureHumidity[1] != 0.0f) {
                        awsIoTUtil.updateTemperatureHumidity(temperatureHumidity[0], temperatureHumidity[1]);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
