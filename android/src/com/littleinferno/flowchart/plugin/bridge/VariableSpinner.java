package com.littleinferno.flowchart.plugin.bridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.annimon.stream.Stream;
import com.littleinferno.flowchart.node.AndroidNode;
import com.littleinferno.flowchart.util.Fun;
import com.littleinferno.flowchart.util.Link;
import com.littleinferno.flowchart.variable.AndroidVariable;
import com.littleinferno.flowchart.variable.AndroidVariableManager;

@SuppressWarnings("unused")
@SuppressLint("ViewConstructor")
public class VariableSpinner extends android.support.v7.widget.AppCompatSpinner {

    private final Link add;
    private final Link remove;

    private VariableSpinner(AndroidVariableManager variableManager, Context context, OnSelected onSelected) {
        super(context);
        Log.e("EROR", "S");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                        Stream.of(variableManager.getVariables())
                                .map(AndroidVariable::getName)
                                .toList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setAdapter(adapter);

        Fun fun = () -> {
            adapter.clear();
            adapter.addAll(Stream.of(variableManager.getVariables())
                    .map(AndroidVariable::getName)
                    .toList());
        };
        add = variableManager.onVariableAdd(fun);
        remove = variableManager.onVariableRemove(fun);

        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onSelected.select(variableManager.getVariable(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                onSelected.select(null);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        add.disconnect();
        remove.disconnect();
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnSelected {
        void select(AndroidVariable object);
    }

    public static VariableSpinner make(AndroidNode node, OnSelected selected) {
        return new VariableSpinner(node.getFunction().getProject().getVariableManager(), node.getContext(), selected);
    }
}