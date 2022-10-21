package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Singleton
@Slf4j
public class GameObjects {

    @Inject
    Client client;

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    public void add(GameObject gameObject) {
        if (gameObjects.contains(gameObject)) {
            return;
        }

        String[] actions = client.getObjectDefinition(gameObject.getId()).getActions();


        for (String action : actions) {
            if (action != null) {
                //dont add gameobject which are not interactable, to prevent fake bank booths for example
                gameObjects.add(gameObject);
                return;
            }
        }
    }

    public void remove(GameObject gameObject) {
        gameObjects.remove(gameObject);
    }

    public GameObject closest(int... ids) {
        if (gameObjects.isEmpty()) {
            return null;
        }
        if (ids == null) { // seems to happen at startup in render
            return null;
        }
        GameObject closest = null;
        WorldPoint myLocation = client.getLocalPlayer().getWorldLocation();
        for (GameObject gameObject : gameObjects) {
            for (int id : ids) {
                if (id != gameObject.getId()) {
                    continue;
                }

                //sometimes happen but i dont know why
                if(!doesGameObjectActuallyExist(gameObject)){
                    continue;
                }

                if (closest == null) {
                    closest = gameObject;
                    continue;
                }

                double distance = myLocation.distanceTo(gameObject.getWorldLocation());
                if (distance < myLocation.distanceTo(closest.getWorldLocation())) {
                    closest = gameObject;
                }
            }
        }
        return closest;
    }

    public void onGameObjectSpawned(GameObjectSpawned event) {
        add(event.getGameObject());
    }

    public void onGameObjectDespawned(GameObjectDespawned event) {
        remove(event.getGameObject());
    }

    public boolean doesGameObjectActuallyExist(GameObject gameObject) {
        if(gameObject == null){
            return false;
        }
        Point sceneCoords = gameObject.getSceneMinLocation();
        Tile tile = client.getScene().getTiles()[0][sceneCoords.getX()][sceneCoords.getY()];
        for (GameObject go : tile.getGameObjects()){
            if (gameObject.equals(go)){
                return true;
            }
        }
        return false;
    }

    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState().equals(GameState.LOADING)) {
            gameObjects.clear();
        }
    }

    public List<GameObject> filter(Predicate<GameObject> p) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject gameObject : gameObjects) {
            if (gameObject == null) {
                continue;
            }
            if (!p.test(gameObject)) {
                continue;
            }
            result.add(gameObject);
        }
        return result;
    }
}
