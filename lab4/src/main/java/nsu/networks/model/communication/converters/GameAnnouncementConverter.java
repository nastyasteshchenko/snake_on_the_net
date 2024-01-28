package nsu.networks.model.communication.converters;

import nsu.networks.SnakesProto;
import nsu.networks.model.GameAnnouncement;
import nsu.networks.model.communication.udp.Socket;

public class GameAnnouncementConverter {
    private static final GameAnnouncementConverter INSTANCE = new GameAnnouncementConverter();

    private GameAnnouncementConverter() {
    }

    public static GameAnnouncementConverter getInstance() {
        return INSTANCE;
    }

    public GameAnnouncement snakesProtoToGameAnnouncement(SnakesProto.GameAnnouncement gameAnnouncement,
                                                          Socket senderSocket) {
        return new GameAnnouncement(senderSocket, gameAnnouncement.getGameName(),
                gameAnnouncement.getPlayers().getPlayersCount(),
                GameConfigConverter.getInstance().snakesProtoToGameConfig(gameAnnouncement.getConfig()),
                gameAnnouncement.getCanJoin());
    }
}
