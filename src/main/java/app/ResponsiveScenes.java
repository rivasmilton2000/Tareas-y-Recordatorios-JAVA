package app;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

public final class ResponsiveScenes {
    private static final double DEFAULT_BASE_WIDTH = 1920;
    private static final double DEFAULT_BASE_HEIGHT = 1080;
    private static final double MIN_STAGE_WIDTH = 960;
    private static final double MIN_STAGE_HEIGHT = 640;

    private ResponsiveScenes() {
    }

    public static Scene create(Parent root) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double width = Math.max(MIN_STAGE_WIDTH, Math.min(bounds.getWidth() * 0.92, DEFAULT_BASE_WIDTH));
        double height = Math.max(MIN_STAGE_HEIGHT, Math.min(bounds.getHeight() * 0.92, DEFAULT_BASE_HEIGHT));
        return create(root, width, height);
    }

    public static Scene create(Parent root, double sceneWidth, double sceneHeight) {
        double baseWidth = resolveBaseWidth(root);
        double baseHeight = resolveBaseHeight(root);
        if (root instanceof Region region) {
            region.setMinSize(baseWidth, baseHeight);
            region.setPrefSize(baseWidth, baseHeight);
            region.setMaxSize(baseWidth, baseHeight);
        }

        Group scaledGroup = new Group(root);
        StackPane viewport = new StackPane(scaledGroup);
        viewport.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(viewport, sceneWidth, sceneHeight);
        ChangeListener<Number> scaler = (obs, oldValue, newValue) ->
                applyScale(viewport.getWidth(), viewport.getHeight(), scaledGroup, baseWidth, baseHeight);
        scene.widthProperty().addListener(scaler);
        scene.heightProperty().addListener(scaler);
        applyScale(sceneWidth, sceneHeight, scaledGroup, baseWidth, baseHeight);
        return scene;
    }

    private static void applyScale(double availableWidth, double availableHeight, Group scaledGroup,
                                   double baseWidth, double baseHeight) {
        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }
        double scale = Math.min(availableWidth / baseWidth, availableHeight / baseHeight);
        scaledGroup.setScaleX(scale);
        scaledGroup.setScaleY(scale);
    }

    private static double resolveBaseWidth(Parent root) {
        if (root instanceof Region region) {
            double prefWidth = region.getPrefWidth();
            if (prefWidth > 0) {
                return prefWidth;
            }
            double computed = region.prefWidth(-1);
            if (computed > 0) {
                return computed;
            }
        }
        return DEFAULT_BASE_WIDTH;
    }

    private static double resolveBaseHeight(Parent root) {
        if (root instanceof Region region) {
            double prefHeight = region.getPrefHeight();
            if (prefHeight > 0) {
                return prefHeight;
            }
            double computed = region.prefHeight(-1);
            if (computed > 0) {
                return computed;
            }
        }
        return DEFAULT_BASE_HEIGHT;
    }
}
