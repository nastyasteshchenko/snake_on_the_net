package nsu.networks.controllers;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import nsu.networks.events.ExitAppEvent;
import nsu.networks.events.switching.SwitchToAvailableGamesListEvent;
import nsu.networks.events.switching.SwitchToNewGameConfigEvent;

public class StartPageController {
    private EventBus eventBus;

    @FXML
    private void createNewGame(){
        eventBus.post(new SwitchToNewGameConfigEvent());
    }
    @FXML
    private void joinGame(){
        eventBus.post(new SwitchToAvailableGamesListEvent());
    }
    @FXML
    private void exit(){
        eventBus.post(new ExitAppEvent());
    }

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
