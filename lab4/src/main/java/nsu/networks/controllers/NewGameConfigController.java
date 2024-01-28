package nsu.networks.controllers;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import nsu.networks.controllers.utils.CheckUserInputUtils;
import nsu.networks.controllers.utils.InformUtils;
import nsu.networks.events.*;
import nsu.networks.events.switching.SwitchToGameFieldEvent;
import nsu.networks.events.switching.SwitchToStartPageEvent;
import nsu.networks.exceptions.UserInputException;
import nsu.networks.model.GameConfig;
import nsu.networks.model.GameState;
import nsu.networks.model.communication.gameplayers.PlayerInfo;

public class NewGameConfigController {
    private EventBus eventBus;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField foodStaticField;
    @FXML
    private TextField delayField;
    @FXML
    private TextField gameNameField;
    @FXML
    private TextField nicknameField;
    @FXML
    private void back(){
        eventBus.post(new SwitchToStartPageEvent());
    }

    @FXML
    private void startGame() {
        try {
            String widthStr = widthField.getText();
            CheckUserInputUtils.checkWidth(widthStr);
            int width = Integer.parseInt(widthStr);

            String heightStr = heightField.getText();
            CheckUserInputUtils.checkHeight(heightStr);
            int height = Integer.parseInt(heightStr);

            String foodStaticStr = foodStaticField.getText();
            CheckUserInputUtils.checkFoodStatic(foodStaticStr);
            int foodStatic = Integer.parseInt(foodStaticStr);

            String delayStr = delayField.getText();
            CheckUserInputUtils.checkDelay(delayStr);
            int delay = Integer.parseInt(delayStr);

            String nickname = nicknameField.getText();
            CheckUserInputUtils.checkNickname(nickname);

            String gameName = gameNameField.getText();
            CheckUserInputUtils.checkGameName(gameName);

            GameConfig gameConfig = new GameConfig(width, height, foodStatic, delay);
            GameState gameState = new GameState(gameConfig);

            eventBus.post(new StartNewGameEvent(gameState, new PlayerInfo(nickname), gameName));
            eventBus.post(new SwitchToGameFieldEvent());

        } catch (UserInputException e){
            InformUtils.inform(e.getMessage());
        }
    }

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
