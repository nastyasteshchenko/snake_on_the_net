package nsu.networks.events.model;

import nsu.networks.model.GameState;
import nsu.networks.model.communication.gameplayers.GamePlayer;
import nsu.networks.model.communication.udp.Socket;

import java.util.List;

public record HandleGameStateMsgEvent(GameState newGameState, List<GamePlayer> players, Socket senderSocket,
                                      long msgSeq) {
}
