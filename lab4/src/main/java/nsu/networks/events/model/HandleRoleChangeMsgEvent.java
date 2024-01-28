package nsu.networks.events.model;

import nsu.networks.model.communication.NodeRole;
import nsu.networks.model.communication.udp.Socket;

public record HandleRoleChangeMsgEvent(NodeRole senderRole, NodeRole receiverRole, int senderId, int receiverId, Socket senderSocket,
                                       long msgSeq) {
}
