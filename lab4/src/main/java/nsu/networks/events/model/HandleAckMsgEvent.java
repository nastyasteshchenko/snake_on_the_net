package nsu.networks.events.model;

public record HandleAckMsgEvent(int senderID, long msgSeq) {
}
