package com.alexeyburyanov.simpletexteditor.CustomTree;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class DirExpansionListener implements TreeExpansionListener {

    private DefaultTreeModel _model;

    public DirExpansionListener(DefaultTreeModel model) {
        _model = model;
    }

    public void treeExpanded(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = Utils.getTreeNode(event.getPath());
        FileNode fnode = Utils.getFileNode(node);

        // При разворачивании ветки дерева содержимое папки проматривается в отдельном потоке, а обновление дерева
        // происходит в главном потоке
        Thread runner = new Thread(() -> {
            if (fnode != null && fnode.expand(node)) {
                Runnable runnable = () -> _model.reload(node);
                SwingUtilities.invokeLater(runnable);
            }
        });
        runner.start();
    }

    public void treeCollapsed(TreeExpansionEvent event) {}
}