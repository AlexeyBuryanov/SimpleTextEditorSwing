package com.alexeyburyanov.simpletexteditor;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/*
    ✔ Задача №1:
    Разработать программу текстовый редактор, который позволяет загружать
    документы в отдельные вкладки из файлов, имеет меню для команд текстового
    редактора и функционал блокнота (новый документ, открыть, сохранить,
    сохранить как, закрыть и т.д.)

    ✔ Задача №2:
    Добавить в текстовый редактор возможность легко загружать файлы в редактор
    при помощи панели файлов, расположенной слева. В панели файлов отображаются
    файлы из определённой рабочей папки (имя, расширение и размер). При щелчке на
    имени файла в панели файлов он загружается в редактор в отдельной вкладке.
    Также добавить BorderLayout и статус-бар внизу окна для доп. информации
*/
public class Main {

    // Сформировано дизайнером
    private JPanel panelMain;
    private JTabbedPane tabPane;
    private JSplitPane splitPane;
    private JTree treeFiles;
    private JLabel statusLabel;

    // Кастомные надстройки
    private JFrame _frameMain = new JFrame("Простой текстовый редактор");
    private JFileChooser _fileChooser = new JFileChooser();
    private JMenuBar _menuBar = new JMenuBar();
    private Font _fontDefault = new Font("Microsoft Sans Serif", Font.PLAIN, 15);
    private JPanel _plusPanel = new JPanel();
    private DefaultMutableTreeNode _rootNode;

    // Панели вкладок
    private List<JPanel> _tabPanels = new LinkedList<>();
    // Контент (в данном случае просто текст) вкладок
    private List<JTextArea> _textPanels = new LinkedList<>();
    // Скроллер в одной вкладке
    private List<JScrollPane> _scrollPanels = new LinkedList<>();
    // Пути для сохранения
    private List<String> _pathsSave = new LinkedList<>();

    /** Кастомное создание UI-компонентов связанных с дизайнером. Вызывается при запуске. */
    private void createUIComponents() {
        _rootNode = new DefaultMutableTreeNode("Пусто");
        treeFiles = new JTree(_rootNode);
        treeFiles.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeFiles.setEditable(false);
        treeFiles.setCellEditor(getCellEditor());

        treeFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int selRow = treeFiles.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    // Если дабл-клик
                    if (e.getClickCount() == 2) {
                        File node;
                        if (treeFiles.getLastSelectedPathComponent() instanceof File
                                && treeFiles.getLastSelectedPathComponent() != null) {
                            node = (File) treeFiles.getLastSelectedPathComponent();
                        } else return;

                        if (node.isFile()) {
                            // Создаём новый документ
                            createNewDoc();
                            // Грузим файл
                            _pathsSave.set(tabPane.getSelectedIndex(), node.getAbsolutePath());
                            readFile(node.getAbsolutePath());
                            tabPane.setTitleAt(tabPane.getSelectedIndex(), node.getName());
                        } // if
                    } // if
                } // if
            } // mousePressed
        });
    }

    private TreeCellEditor getCellEditor() {
        return new DefaultTreeCellEditor(treeFiles, (DefaultTreeCellRenderer) treeFiles.getCellRenderer()) {
            @Override
            public Object getCellEditorValue() {
                Object value = super.getCellEditorValue();
                JOptionPane.showMessageDialog(_frameMain, value);
                return value;
            }

            @Override
            public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
                                                        boolean leaf, int row) {
                int res = JOptionPane.showConfirmDialog(_frameMain, String.format("Вы хотите переименовать %s ?", value),
                        "Переименование", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {

                }

                System.out.println(value);
                return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
            }
        };
    }

    /** Точка входа приложения */
    public static void main(String[] args) { new Main().setUp(); }

    /**
     * Главный метод по запуску всех установщиков. Вызывается только при старте.
     * */
    private void setUp() {
        splitPane.setContinuousLayout(true);
        setUpMenu();
        setUpTabPane();
        setUpJFrame();
        statusLabel.setText("Добро пожаловать!");
    }

    /**
     * Установка JTabbedPane.
     * */
    private void setUpTabPane() {
        var panelDefault = new JPanel();
        var textDefault = new JTextArea();
        textDefault.setFont(_fontDefault);
        var scrollDefault = new JScrollPane(textDefault);

        tabPane.addTab("Новый", panelDefault);
        _tabPanels.add(panelDefault);
        _textPanels.add(textDefault);
        _scrollPanels.add(scrollDefault);
        _pathsSave.add("");

        _tabPanels.get(0).add(_scrollPanels.get(0));
        _tabPanels.get(0).setLayout(new GridLayout());
        _tabPanels.get(0).setFont(_fontDefault);
        _textPanels.get(0).setFont(_fontDefault);
        tabPane.addTab("Новый", _tabPanels.get(0));

        tabPane.addTab("+", _plusPanel);
        tabPane.addChangeListener(e -> {
            try {
                var index = ((JTabbedPane) e.getSource()).getSelectedIndex();
                if (index == _tabPanels.size() - 1) return;
                var title = ((JTabbedPane) e.getSource()).getTitleAt(index);
                if (title.equals("+")) {
                    createNewDoc();
                }
            } catch (IndexOutOfBoundsException ex) {
                // ignored
            }
        });
    }

    /**
     * Установка меню.
     * */
    private void setUpMenu() {
        var menuFile = new JMenu("Файл");
        var itemNewDoc = new JMenuItem("Новый");
        var itemOpen = new JMenuItem("Открыть...");
        var itemOpenFolder = new JMenuItem("Открыть папку...");
        var itemSave = new JMenuItem("Сохранить");
        var itemSaveAs = new JMenuItem("Сохранить как...");
        var itemClose = new JMenuItem("Закрыть");
        var itemCloseFolder = new JMenuItem("Закрыть папку");
        var itemExit = new JMenuItem("Выход");

        var menuHelp = new JMenu("Справка");
        var itemAbout = new JMenuItem("О программе...");

        // Новый документ
        itemNewDoc.addActionListener(e -> createNewDoc());
        // Открытие файла
        itemOpen.addActionListener(e -> openFile());
        // Открыть папку
        itemOpenFolder.addActionListener(e -> openFolder());
        // Сохранить
        itemSave.addActionListener(e -> save());
        // Сохранить как
        itemSaveAs.addActionListener(e -> openSaveDialog());
        // Закрыть документ
        itemClose.addActionListener(e -> closeDoc());
        // Закрыть папку
        itemCloseFolder.addActionListener(e -> {
            _rootNode = new DefaultMutableTreeNode("Пусто");
            treeFiles.setModel(new DefaultTreeModel(_rootNode));
            statusLabel.setText("Папка закрыта");
        });
        // Выход из приложения
        itemExit.addActionListener(e -> System.exit(0));
        // О программе
        itemAbout.addActionListener(e ->
                JOptionPane.showMessageDialog(_frameMain, "Простой текстовый редактор разработанный " +
                                "с помощью библиотеки Swing.\nCopyright (c) Алексей Бурьянов, 2018.\n ",
                        "О программе", JOptionPane.INFORMATION_MESSAGE, null));

        menuFile.add(itemNewDoc);
        menuFile.add(itemOpen);
        menuFile.add(itemOpenFolder);
        menuFile.addSeparator();
        menuFile.add(itemSave);
        menuFile.add(itemSaveAs);
        menuFile.addSeparator();
        menuFile.add(itemClose);
        menuFile.add(itemCloseFolder);
        menuFile.add(itemExit);

        menuHelp.add(itemAbout);

        _menuBar.add(menuFile);
        _menuBar.add(menuHelp);
    }

    /**
     * Закрыть документ.
     * */
    private void closeDoc() {
        var index = tabPane.getSelectedIndex();
        if (_tabPanels.size() != 1) {
            tabPane.remove(index);
            _tabPanels.remove(index);
            _textPanels.remove(index);
            _scrollPanels.remove(index);
            _pathsSave.remove(index);
            tabPane.setSelectedIndex(_tabPanels.size() - 1);
            statusLabel.setText("Документ закрыт");
        }
    }

    /**
     * Сохранить.
     * */
    private void save() {
        if (_pathsSave.get(tabPane.getSelectedIndex()).equals("")) {
            openSaveDialog();
        } else {
            writeFile();
        }
    }

    /**
     * Открытие папки.
     * */
    private void openFolder() {
        _fileChooser.setDialogTitle("Открыть папку в качестве проекта");
        _fileChooser.setDialogType(JFileChooser.DIRECTORIES_ONLY);
        _fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        var isApprove = _fileChooser.showOpenDialog(panelMain);
        if (isApprove == JFileChooser.APPROVE_OPTION) {
            treeFiles.setModel(new FileTreeModel(_fileChooser.getSelectedFile()));
            treeFiles.setEditable(true);
            statusLabel.setText(String.format("Открыта папка %s", _fileChooser.getSelectedFile().getAbsolutePath()));
        } // if
    }

    /**
     * Открытие файла.
     * */
    private void openFile() {
        _fileChooser.setDialogTitle("Открыть файл");
        _fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        _fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
        var isApprove = _fileChooser.showOpenDialog(panelMain);
        if (isApprove == JFileChooser.APPROVE_OPTION) {
            _pathsSave.set(tabPane.getSelectedIndex(), _fileChooser.getSelectedFile().getAbsolutePath());
            readFile(_fileChooser.getSelectedFile().getAbsolutePath());
            tabPane.setTitleAt(tabPane.getSelectedIndex(), _fileChooser.getSelectedFile().getName());
            statusLabel.setText(String.format("Открыт файл %s", _fileChooser.getSelectedFile().getAbsolutePath()));
        } // if
    }

    /**
     * Установка JFrame.
     * */
    private void setUpJFrame() {
        _frameMain.setContentPane(panelMain);
        _frameMain.setJMenuBar(_menuBar);
        _frameMain.pack();
        _frameMain.setDefaultCloseOperation(_frameMain.EXIT_ON_CLOSE);
        _frameMain.setSize(700, 400);
        _frameMain.setLocationRelativeTo(null);
        String iconPath = System.getProperty("user.dir")+"\\src\\com\\alexeyburyanov\\simpletexteditor\\icons\\favicon.png";
        _frameMain.setIconImage(new ImageIcon(iconPath).getImage());
        _frameMain.setVisible(true);
    }

    /**
     * Чтение файла построчно.
     * */
    private void readFile(String fileName) {
        try (var reader = new FileReader(fileName)) {
            var bufReader = new BufferedReader(reader);
            String line;
            while ((line = bufReader.readLine()) != null) {
                _textPanels.get(tabPane.getSelectedIndex()).append(line + "\n");
            }
            statusLabel.setText(String.format("Открыт файл %s", fileName));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(_frameMain, "Невозможно прочитать файл", "Ошибка чтения",
                    JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("Ошибка чтения");
        }
    }

    /**
     * Запись файла по указанному пути
     * */
    private void writeFile(String fileName) {
        try (var writer = new FileWriter(fileName, false)) {
            writer.write(_textPanels.get(tabPane.getSelectedIndex()).getText());
            writer.flush();
            statusLabel.setText(String.format("Сохранён файл %s", fileName));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(_frameMain, "Невозможно записать файл", "Ошибка записи",
                    JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("Ошибка записи");
        }
    }

    /**
     * Запись файла.
     * */
    private void writeFile() {
        try (var writer = new FileWriter(_pathsSave.get(tabPane.getSelectedIndex()), false)) {
            writer.write(_textPanels.get(tabPane.getSelectedIndex()).getText());
            writer.flush();
            statusLabel.setText("Сохранено");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(_frameMain, "Невозможно записать файл", "Ошибка записи",
                    JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("Ошибка записи");
        }
    }

    /**
     * Создание нового документа.
     * */
    private void createNewDoc() {
        var panelNew = new JPanel();
        var textNew = new JTextArea();
        textNew.setFont(_fontDefault);
        var scrollNew = new JScrollPane(textNew);

        tabPane.addTab("Новый", panelNew);
        _tabPanels.add(panelNew);
        _textPanels.add(textNew);
        _scrollPanels.add(scrollNew);
        _pathsSave.add("");

        _tabPanels.get(_tabPanels.size() - 1).add(_scrollPanels.get(_scrollPanels.size() - 1));
        _tabPanels.get(_tabPanels.size() - 1).setLayout(new GridLayout());
        _tabPanels.get(_tabPanels.size() - 1).setFont(_fontDefault);
        _textPanels.get(_tabPanels.size() - 1).setFont(_fontDefault);

        tabPane.remove(_plusPanel);
        tabPane.addTab("Новый", _tabPanels.get(_tabPanels.size() - 1));
        tabPane.addTab("+", _plusPanel);

        tabPane.setSelectedIndex(_tabPanels.size() - 1);
        statusLabel.setText("Создан новый документ");
    }

    /**
     * Открыть сэйв-диалог.
     * */
    private void openSaveDialog() {
        _fileChooser.setDialogTitle("Сохранить как");
        _fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        _fileChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        var isApprove = _fileChooser.showSaveDialog(panelMain);
        if (isApprove == JFileChooser.APPROVE_OPTION) {
            _pathsSave.set(tabPane.getSelectedIndex(), _fileChooser.getSelectedFile().getAbsolutePath());
            writeFile(_fileChooser.getSelectedFile().getAbsolutePath());
            tabPane.setTitleAt(tabPane.getSelectedIndex(), _fileChooser.getSelectedFile().getName());
            statusLabel.setText(String.format("Сохранено как %s", _fileChooser.getSelectedFile().getAbsolutePath()));
        } // if
    }
}
