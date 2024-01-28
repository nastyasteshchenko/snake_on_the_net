package nsu.networks.events;

import nsu.networks.model.GameState;
import nsu.networks.model.communication.gameplayers.PlayerInfo;

public record StartNewGameEvent(GameState gameState, PlayerInfo playerInfo, String gameName) {
}
