package com.visual;

import javax.swing.*;
import java.util.concurrent.Callable;

public class VarLabel extends JLabel {

    public String label;

    public VarLabel(String label){
        super();
        this.label = label;
    }

    public void update(String value){
        setText(label + ": " + value);
    }
}
