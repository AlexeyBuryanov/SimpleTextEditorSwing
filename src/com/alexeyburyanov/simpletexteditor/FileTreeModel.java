package com.alexeyburyanov.simpletexteditor;

import javax.swing.tree.TreeModel;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/** Кастомная модель для дерева. */
public class FileTreeModel implements TreeModel {

    private File _root;

    FileTreeModel(File root) {
        _root = root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        File f = (File) parent;
        //System.out.println("Получен дочерний элемент дерева: " + f);
        return Objects.requireNonNull(f.listFiles())[index];
    }

    @Override
    public int getChildCount(Object parent) {
        File f = (File) parent;
        //System.out.println("Получено кол-во дочерних элементов дерева для: " + f);
        if (!f.isDirectory()) {
            return 0;
        } else {
            return Objects.requireNonNull(f.list()).length;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        File par = (File) parent;
        File ch = (File) child;
        return Arrays.asList(Objects.requireNonNull(par.listFiles())).indexOf(ch);
    }

    @Override
    public Object getRoot() {
        return _root;
    }

    @Override
    public boolean isLeaf(Object node) {
        File f = (File) node;
        return !f.isDirectory();
    }

    @Override
    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
        //do nothing
    }

    @Override
    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
        //do nothing
    }

    @Override
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
        //do nothing
    }
}
