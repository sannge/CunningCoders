package Network;

import core.controller.GameController;

public class ModelGateway {

    private static GameController controller;

    public static void setController(GameController c) {
        controller = c;
    }

    public static GameController getController() {
        return controller;
    }
}
