package com.littleinferno.flowchart.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.ButtonBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.littleinferno.flowchart.codegen.BaseCodeGenerator;
import com.littleinferno.flowchart.project.Project;

public class UIScene extends Stage {

    private TabbedPane tabbedPane;
    private InputMultiplexer inputMultiplexer;

    private Scene show;

    private static DragAndDrop dragAndDrop;

    public UIScene() {
        super(new ScreenViewport());
    }

    public void init() {

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);

        show = null;

        dragAndDrop = new DragAndDrop();

        VisTable container = new VisTable();
        container.setFillParent(true);

        ButtonBar buttonBar = new ButtonBar();

        VisTextButton run = new VisTextButton("run");

        run.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Project.instance().runProgram();
            }
        });

        VisTextButton save = new VisTextButton("save");

        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Project.instance().save();
            }
        });

        VisTextButton load = new VisTextButton("load");

        load.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Project.loadProject("test", "test/");
            }
        });

        buttonBar.setButton(ButtonBar.ButtonType.LEFT, run);
        buttonBar.setButton(ButtonBar.ButtonType.NEXT, save);
        buttonBar.setButton(ButtonBar.ButtonType.APPLY, load);


        VisTable tmp = new VisTable();
        tmp.add(buttonBar.createTable()).grow().row();
        tmp.addSeparator();
        tmp.setBackground(VisUI.getSkin().getDrawable("window-bg"));

        container.add(tmp).growX().colspan(3).row();

        ControlTable controlTable = new ControlTable(this);

        VisTable activityContainer = new VisTable();

        Table activity = new Table();
        tabbedPane = new TabbedPane();

        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                Table content = tab.getContentTable();

                activity.clearChildren();
                activity.add(content).grow();


                if (show != null) {
                    inputMultiplexer.removeProcessor(show);
                    inputMultiplexer.removeProcessor(show.getGesture());
                }

                show = ((Scene.UiTab) tab).getScene();

                inputMultiplexer.addProcessor(show);
                inputMultiplexer.addProcessor(show.getGesture());
            }
        });

        controlTable.pack();
        container.add(controlTable).growY();

        activityContainer.add(tabbedPane.getTable()).expandX().fillX().row();
        activityContainer.addSeparator();
        activityContainer.add(activity).grow();
        container.addSeparator(true);
        container.add(activityContainer).grow();

        addActor(container);

    }

    public void addDragAndDropTarget(final DragAndDrop.Target target) {
        dragAndDrop.addTarget(target);
    }

    public void addDragAndDropSource(final DragAndDrop.Source source) {
        dragAndDrop.addSource(source);
    }

    public void pinToTabbedPane(Scene.UiTab uiTab) {
        if (tabbedPane.getUIOrderedTabs().contains(uiTab, true))
            tabbedPane.switchTab(uiTab);
        else
            tabbedPane.add(uiTab);

        show = uiTab.getScene();
    }

    public void unpinFromTabbedPane(Scene.UiTab uiTab) {
        tabbedPane.remove(uiTab);
    }

    @Override
    public void draw() {
        show.draw();
        super.draw();
    }

    @Override
    public void act(float delta) {
        show.act(delta);
        super.act(delta);
    }

    public InputMultiplexer getInputMultiplexer() {
        return inputMultiplexer;
    }

    public Scene getShow() {
        return show;
    }

    public interface Begin {
        String gen(BaseCodeGenerator builder);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}