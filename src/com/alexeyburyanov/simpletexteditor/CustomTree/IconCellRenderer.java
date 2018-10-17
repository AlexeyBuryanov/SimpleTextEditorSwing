package com.alexeyburyanov.simpletexteditor.CustomTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class IconCellRenderer extends DefaultTreeCellRenderer {

    public IconCellRenderer() {
        setLeafIcon(null);
        setOpenIcon(null);
    }

    // Метод вызывается для каждого узла дерева и возвращает компонент, который будет отображён в качестве узла
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        // Вызвать реализацию по умолчанию
        Component result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        Object obj = node.getUserObject();
        setText(obj.toString());

        if (obj instanceof Boolean)
            setText("Извлечение данных...");

        if (obj instanceof IconData) {
            IconData idata = (IconData)obj;
            if (expanded)
                setIcon(idata.getExpandedIcon());
            else
                setIcon(idata.getIcon());
        } else
            setIcon(null);

        return result;
    }
}
