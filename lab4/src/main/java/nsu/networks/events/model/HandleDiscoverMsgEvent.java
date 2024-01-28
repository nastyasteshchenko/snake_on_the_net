package nsu.networks.events.model;

import nsu.networks.model.communication.udp.Socket;

public record HandleDiscoverMsgEvent(Socket senderSocket) {
}
