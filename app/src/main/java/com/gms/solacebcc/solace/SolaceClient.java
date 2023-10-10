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
            mqttClient = new MqttClient("ssl://mr-connection-eiw5cqm9ae9.messaging.solace.cloud:8883", "HelloWorldSub",persistence);
            MqttConnectOptions connectionOptions = new MqttConnectOptions();
            connectionOptions.setUserName("solace-cloud-client");
            connectionOptions.setPassword("9i6bg6dh2n1cvhs5jfii9pau9b".toCharArray());
            mqttClient.connect(connectionOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return mqttClient;
    }
}
