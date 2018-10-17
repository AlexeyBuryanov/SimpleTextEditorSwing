package com.alexeyburyanov.simpletexteditor.CustomTree;

import javax.swing.*;

public class IconData {

    private Icon _icon;
    private Icon _expandedIcon;
    private Object _data;

    public IconData(Icon icon, Object data) {
        _icon = icon;
        _expandedIcon = null;
        _data = data;
    }

    public IconData(Icon icon, Icon expandedIcon, Object data) {
        _icon = icon;
        _expandedIcon = expandedIcon;
        _data = data;
    }

    public Icon getIcon() {
        return _icon;
    }

    public Icon getExpandedIcon() {
        return _expandedIcon != null ? _expandedIcon : _icon;
    }

    public Object getObject() {
        return _data;
    }

    public String toString() {
        return _data.toString();
    }
}
