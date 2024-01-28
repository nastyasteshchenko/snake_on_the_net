package nsu.networks.events.model;

import nsu.networks.model.communication.udp.Socket;

public record HandleErrorMsgEvent(Socket senderSocket, long msgSeq) {
}
