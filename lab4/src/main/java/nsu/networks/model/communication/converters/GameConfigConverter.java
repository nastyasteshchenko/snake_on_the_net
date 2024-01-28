package nsu.networks.model.communication.converters;

import nsu.networks.SnakesProto;
import nsu.networks.model.GameConfig;

public class GameConfigConverter {
    private static final GameConfigConverter INSTANCE = new GameConfigConverter();

    private GameConfigConverter() {
    }

    static GameConfigConverter getInstance() {
        return INSTANCE;
    }

    SnakesProto.GameConfig gameConfigToSnakesProto(GameConfig gameConfig) {
        SnakesProto.GameConfig.Builder builder = SnakesProto.GameConfig.newBuilder();
        return builder.setStateDelayMs(gameConfig.delay())
                .setFoodStatic(gameConfig.foodStatic())
                .setWidth(gameConfig.width())
                .setHeight(gameConfig.height())
                .build();
    }

    GameConfig snakesProtoToGameConfig(SnakesProto.GameConfig gameConfig) {
        return new GameConfig(gameConfig.getWidth(), gameConfig.getHeight(),
                gameConfig.getFoodStatic(), gameConfig.getStateDelayMs());
    }
}
