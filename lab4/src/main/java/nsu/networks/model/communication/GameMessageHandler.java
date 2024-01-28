package nsu.networks.model.communication;

import com.google.common.eventbus.EventBus;
import nsu.networks.SnakesProto;
import nsu.networks.events.model.*;
import nsu.networks.model.GameConfig;
import nsu.networks.model.GameState;
import nsu.networks.model.communication.converters.DirectionConverter;
import nsu.networks.model.communication.converters.GamePlayersConverter;
import nsu.networks.model.communication.converters.GameStateConverter;
import nsu.networks.model.communication.converters.NodeRoleConverter;
import nsu.networks.model.communication.gameplayers.GamePlayer;
import nsu.networks.model.communication.udp.Socket;
import nsu.networks.model.communication.udp.UDPMessageReceiverAndSender;
import nsu.networks.model.snake.Direction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameMessageHandler extends Thread{
        private final EventBus modelEventBus;
        private final GameConfig gameConfig;
        public GameMessageHandler(EventBus modelEventBus, GameConfig gameConfig){
            this.gameConfig = gameConfig;
            this.modelEventBus = modelEventBus;
        }

        public void run(){

            try {
                UDPMessageReceiverAndSender messageReceiverAndSender = UDPMessageReceiverAndSender.getInstance();

                while (!this.isInterrupted()) {

                    Message message = messageReceiverAndSender.receive();

                    SnakesProto.GameMessage gameMessage = message.getMessage();

                    Socket senderSocket =  message.getSocket();

                    if (gameMessage.hasSteer() && !this.isInterrupted()) {
                        SnakesProto.GameMessage.SteerMsg steer = gameMessage.getSteer();
                        Direction newDirection = DirectionConverter.getInstance().snakesProtoToDirection(steer.getDirection());
                        CompletableFuture.runAsync(() -> modelEventBus.post(new HandleSteerMsgEvent(newDirection, senderSocket, gameMessage.getMsgSeq())));
                    continue;
                    }

                    if (gameMessage.hasState() && !this.isInterrupted()) {
                        SnakesProto.GameState snakesProtoState = gameMessage.getState().getState();
                        GameState newState = GameStateConverter.getInstance().snakesProtoToGameState(snakesProtoState, gameConfig);
                        List<GamePlayer> players = GamePlayersConverter.getInstance().snakesProtoToGamePlayers(snakesProtoState.getPlayers());

                        CompletableFuture.runAsync(() -> modelEventBus.post(new HandleGameStateMsgEvent(newState, players, senderSocket, gameMessage.getMsgSeq())));
                        continue;
                    }

                    if (gameMessage.hasJoin() && !this.isInterrupted()) {
                        SnakesProto.GameMessage.JoinMsg joinMsg = gameMessage.getJoin();
                        CompletableFuture.runAsync(() ->modelEventBus.post(new HandleJoinMsgEvent(joinMsg, senderSocket, gameMessage.getMsgSeq())));
                        continue;
                    }

                    if (gameMessage.hasPing() && !this.isInterrupted()) {
                        CompletableFuture.runAsync(() ->modelEventBus.post(new HandlePingMsgEvent(senderSocket, gameMessage.getMsgSeq())));
                        continue;
                    }

                    if (gameMessage.hasAck() && !this.isInterrupted()) {
                        CompletableFuture.runAsync(() ->modelEventBus.post(new HandleAckMsgEvent(gameMessage.getSenderId(), gameMessage.getMsgSeq())));
                        continue;
                    }

                    if (gameMessage.hasError() && !this.isInterrupted()) {
                        CompletableFuture.runAsync(() ->modelEventBus.post(new HandleErrorMsgEvent(senderSocket, gameMessage.getMsgSeq())));
                        continue;
                    }

                    if (gameMessage.hasRoleChange() && !this.isInterrupted()) {
                        SnakesProto.GameMessage.RoleChangeMsg roleChangeMsg = gameMessage.getRoleChange();
                        NodeRole senderRole = NodeRoleConverter.getInstance().snakesProtoToNodeRole(roleChangeMsg.getSenderRole());
                        NodeRole receiverRole = NodeRoleConverter.getInstance().snakesProtoToNodeRole(roleChangeMsg.getReceiverRole());
                        CompletableFuture.runAsync(() ->modelEventBus.post(new HandleRoleChangeMsgEvent(senderRole, receiverRole, gameMessage.getSenderId(), gameMessage.getReceiverId(), senderSocket, gameMessage.getMsgSeq())));
                    }
                }
            } catch (IOException ignored){

            }
        }

}
