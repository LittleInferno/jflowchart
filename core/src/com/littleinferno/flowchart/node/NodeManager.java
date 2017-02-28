package com.littleinferno.flowchart.node;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.littleinferno.flowchart.gui.Scene;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class NodeManager {

    private List<Node> nodes;

    private Scene scene;

    public NodeManager() {
        nodes = new ArrayList<>();
    }

    public NodeManager(Scene scene) {
        this.scene = scene;
        nodes = new ArrayList<>();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public <T extends Node> T createNode(Class<T> nodeClass, Object... args) {

        T node = null;
        try {
            node = nodeClass.getConstructor().newInstance(args);
            nodes.add(node);
            scene.addActor(node);
            return node;

        } catch (IllegalAccessException |
                InstantiationException |
                NoSuchMethodException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
        return node;
    }

    public void deleteNode(Node node) {
        nodes.remove(node);
        scene.getRoot().removeActor(node);
    }

    public static class NodeManagerSerializer implements Json.Serializer<NodeManager> {
        NodeManager nodeManager = new NodeManager();

        @Override
        public void write(Json json, NodeManager object, Class knownType) {

            json.writeObjectStart();

            try {
                writeNodes(json, object.nodes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            json.writeObjectEnd();
        }

        @Override
        public NodeManager read(Json json, JsonValue jsonData, Class type) {

            JsonValue data = jsonData.child;
            for (JsonValue valueMap = data.child; valueMap != null; valueMap = valueMap.next) {
                try {
                    readObjects(json, ClassReflection.forName(valueMap.name()), valueMap);
                } catch (ReflectionException ex) {
                    throw new SerializationException(ex);
                }
            }
            return nodeManager;
        }

        private void readObjects(Json json, Class type, JsonValue valueMap) {

            Node object = (Node) json.readValue(type, valueMap);
            nodeManager.nodes.add(object);
        }

        private void writeNodes(Json json, List<Node> nodes) throws IOException {
            json.writeObjectStart("nodes");

            for (Node node : nodes) {
                json.getWriter().name(node.getClass().getName());
                json.writeValue(node);
            }
            json.writeObjectEnd();
        }
    }
}
