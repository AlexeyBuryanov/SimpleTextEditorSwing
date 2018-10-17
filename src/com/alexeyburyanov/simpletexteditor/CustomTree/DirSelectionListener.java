package com.alexeyburyanov.simpletexteditor.CustomTree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class DirSelectionListener implements TreeSelectionListener {

    private JLabel _display;

    public DirSelectionListener(JLabel display) {
        _display = display;
    }

    public void valueChanged(TreeSelectionEvent event) {
        DefaultMutableTreeNode node = Utils.getTreeNode(event.getPath());
        FileNode fnode = Utils.getFileNode(node);

        if (fnode != null)
            _display.setText(fnode.getFile().getAbsolutePath());
        else
            _display.setText("");
    }
}
