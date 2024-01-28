package nsu.networks.events.model;

import nsu.networks.model.communication.udp.Socket;
import nsu.networks.model.snake.Direction;

public record HandleSteerMsgEvent(Direction newDirection, Socket senderSocket, long msgSeq) {
}
