package com.alexeyburyanov.simpletexteditor.CustomTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

class Utils {

    public final static ImageIcon ICON_FOLDER = new ImageIcon("folder.gif");
    public final static ImageIcon ICON_EXPANDEDFOLDER = new ImageIcon("expandedfolder.gif");

    public static DefaultMutableTreeNode getTreeNode(TreePath path) {
        return (DefaultMutableTreeNode)(path.getLastPathComponent());
    }

    public static FileNode getFileNode(DefaultMutableTreeNode node) {
        if (node == null)
            return null;
        Object obj = node.getUserObject();
        if (obj instanceof IconData)
            obj = ((IconData)obj).getObject();
        if (obj instanceof FileNode)
            return (FileNode)obj;
        else
            return null;
    }
}
