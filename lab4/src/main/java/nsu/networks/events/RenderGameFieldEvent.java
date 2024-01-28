package nsu.networks.events;

import nsu.networks.model.GameState;

public record RenderGameFieldEvent(GameState gameState) {
}
