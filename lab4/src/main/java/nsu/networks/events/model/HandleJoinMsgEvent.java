package nsu.networks.events.model;

import nsu.networks.SnakesProto;
import nsu.networks.model.communication.udp.Socket;

public record HandleJoinMsgEvent(SnakesProto.GameMessage.JoinMsg joinMsg, Socket senderSocket, long msgSeq) {
}
