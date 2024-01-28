package nsu.networks.events;

import nsu.networks.model.GameAnnouncement;

import java.util.List;

public record UpdateAvailableGamesEvent(List<GameAnnouncement> availableGames) {
}
