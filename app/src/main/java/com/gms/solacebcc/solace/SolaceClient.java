package com.gms.solacebcc.solace;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public   class SolaceClient {

    public  MqttClient getMqttSolaceClient() {
        MqttClient mqttClient = null;
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient("ssl://mr-connection-d4w63mgopcx.messaging.solace.cloud:8883", "AvDevice",persistence);
            MqttConnectOptions connectionOptions = new MqttConnectOptions();
            connectionOptions.setUserName("solace-cloud-client");
            connectionOptions.setPassword("8e1pta8855kpc5o5k7qksk54il".toCharArray());
            mqttClient.connect(connectionOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return mqttClient;
    }
}
