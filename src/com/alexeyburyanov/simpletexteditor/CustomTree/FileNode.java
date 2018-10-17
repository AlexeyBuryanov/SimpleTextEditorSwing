package com.alexeyburyanov.simpletexteditor.CustomTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Vector;

public class FileNode {

    private File _file;

    public FileNode(File file) {
        _file = file;
    }

    public File getFile() {
        return _file;
    }

    // Возвращает имя файла или имя папки
    public String toString() {
        return _file.getName().length() > 0 ? _file.getName() : _file.getPath();
    }

    // Alternatively we copied sub-class TreeNode
    // Сканирует файловую систему и заполняет дерево отсутствующими узлами, когда пользователь разворачивает
    // какой-то узел
    public boolean expand(DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode flag = (DefaultMutableTreeNode)parent.getFirstChild();
        if (flag == null)
            return false;

        Object obj = flag.getUserObject();
        if (!(obj instanceof Boolean))
            return false;

        parent.removeAllChildren();

        File[] files = listFiles();
        if (files == null)
            return true;

        Vector v = new Vector();

        for (File f : files) {
            if (!(f.isDirectory()))
                continue;

            FileNode newNode = new FileNode(f);

            boolean isAdded = false;
            for (int i = 0; i < v.size(); i++) {
                FileNode nd = (FileNode) v.elementAt(i);
                if (newNode.compareTo(nd) < 0) {
                    v.insertElementAt(newNode, i);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded)
                v.addElement(newNode);
        }

        for (int i = 0; i < v.size(); i++) {
            FileNode nd = (FileNode)v.elementAt(i);
            IconData idata = new IconData(Utils.ICON_FOLDER, Utils.ICON_EXPANDEDFOLDER, nd);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(idata);
            parent.add(node);

            if (nd.hasSubDirs())
                node.add(new DefaultMutableTreeNode(true));
        }

        return true;
    }

    public boolean hasSubDirs() {
        File[] files = listFiles();
        if (files == null)
            return false;
        for (File file : files) {
            if (file.isDirectory())
                return true;
        }
        return false;
    }

    public int compareTo(FileNode toCompare) {
        return  _file.getName().compareToIgnoreCase(
                toCompare._file.getName() );
    }

    public File[] listFiles() {
        if (!_file.isDirectory())
            return null;
        try {
            return _file.listFiles();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Ошибка чтения каталога"+ _file.getAbsolutePath(),
                    "Простой текстовый редактор", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}
