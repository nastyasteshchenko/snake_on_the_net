package nsu.networks.events.model;

import nsu.networks.model.communication.udp.Socket;

public record HandlePingMsgEvent(Socket senderSocket, long msgSeq) {
}
