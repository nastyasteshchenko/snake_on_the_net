package nsu.networks.events;

import nsu.networks.model.GameAnnouncement;
import nsu.networks.model.Model;

public record JoinGameEvent(Model model, GameAnnouncement gameAnnouncement, String nickname, boolean isViewer) {
}
