package nsu.networks.model.communication;

import com.google.common.eventbus.EventBus;
import nsu.networks.SnakesProto;
import nsu.networks.events.model.HandleDiscoverMsgEvent;
import nsu.networks.events.model.HandleGameAnnouncementMsgEvent;
import nsu.networks.model.communication.converters.GameAnnouncementConverter;
import nsu.networks.model.communication.udp.Socket;
import nsu.networks.model.communication.udp.UDPMulticastMessageReceiver;

import java.io.IOException;

public class GameAnnouncementMsgHandler extends Thread {
    private final EventBus modelEventBus;

    public GameAnnouncementMsgHandler(EventBus modelEventBus) {
        this.modelEventBus = modelEventBus;
    }

    @Override
    public void run() {
        try {
            UDPMulticastMessageReceiver multicastMessageReceiver = UDPMulticastMessageReceiver.getInstance();
            while (!this.isInterrupted()) {

                Message message = multicastMessageReceiver.receive();
                Socket senderSocket = message.getSocket();
                SnakesProto.GameMessage msg = message.getMessage();

                if (msg.hasAnnouncement() && !this.isInterrupted()) {
                    msg.getAnnouncement().getGamesList().forEach(game ->
                            modelEventBus.post(new HandleGameAnnouncementMsgEvent(GameAnnouncementConverter.getInstance().snakesProtoToGameAnnouncement(game, senderSocket))));
                    continue;
                }

                if (msg.hasDiscover() && !this.isInterrupted()) {
                    modelEventBus.post(new HandleDiscoverMsgEvent(senderSocket));
                }

            }
            multicastMessageReceiver.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
