import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttManager(serverUri: String, clientId: String) {
    private val mqttClient = MqttClient(serverUri, clientId, MemoryPersistence())

    fun connect() {
        mqttClient.connect()
    }

    fun publish(topic: String, payload: String) {
        val message = MqttMessage(payload.toByteArray())
        mqttClient.publish(topic, message)
    }

    fun disconnect() {
        mqttClient.disconnect()
    }
}