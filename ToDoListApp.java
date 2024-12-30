
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class ToDoListApp {

    public ToDoListApp() {
        ToDoListFrame frame = new ToDoListFrame("To-Do-List");
        frame.setSize(700, 450);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ToDoListApp todo = new ToDoListApp();
    }
}

interface TaskOperations {

    void addTask(String task, String category);

    void removeTask(String task, String category);

    JPanel getTaskPanel(String category);
}

abstract class AbstractFrame extends JFrame {

    protected JTextField taskField;

    public AbstractFrame() {
        taskField = new JTextField();
        taskField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    }

    protected JPanel createInputPanel(JButton addButton) {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(20, 30, 10, 30));
        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        return inputPanel;
    }
}

class ToDoListFrame extends AbstractFrame {

    private TaskOperations taskManager;
    private JButton addTaskButton;

    public ToDoListFrame(String title) {
        setTitle(title);
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(94, 156, 255));
        JLabel headerLabel = new JLabel("Taskify ^.^", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        taskManager = new TaskManager();

        JPanel taskPanels = new JPanel(new GridLayout(1, 3, 10, 10));
        taskPanels.add(new JScrollPane(taskManager.getTaskPanel("Not Started")));
        taskPanels.add(new JScrollPane(taskManager.getTaskPanel("In Progress")));
        taskPanels.add(new JScrollPane(taskManager.getTaskPanel("Finished")));
        backgroundPanel.add(taskPanels, BorderLayout.CENTER);

        addTaskButton = new JButton("Add Task");
        addTaskButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel inputPanel = createInputPanel(addTaskButton);
        backgroundPanel.add(inputPanel, BorderLayout.SOUTH);

        addTaskButton.addActionListener(e -> addToNotStarted());
    }

    private void addToNotStarted() {
        String task = taskField.getText().trim();
        if (!task.isEmpty()) {
            taskManager.addTask(task, "Not Started");
            taskField.setText("");
        }
    }
}

class TaskManager implements TaskOperations {

    private JPanel notStartedPanel;
    private JPanel inProgressPanel;
    private JPanel finishedPanel;

    private DefaultListModel<String> notStartedData;
    private DefaultListModel<String> inProgressData;
    private DefaultListModel<String> finishedData;

    public TaskManager() {
        notStartedPanel = createTaskPanel("Not Started");
        inProgressPanel = createTaskPanel("In Progress");
        finishedPanel = createTaskPanel("Finished");

        notStartedData = new DefaultListModel<>();
        inProgressData = new DefaultListModel<>();
        finishedData = new DefaultListModel<>();
    }

    public JPanel getTaskPanel(String category) {
        switch (category) {
            case "Not Started":
                return notStartedPanel;
            case "In Progress":
                return inProgressPanel;
            case "Finished":
                return finishedPanel;
            default:
                return new JPanel();
        }
    }

    public void addTask(String task, String category) {
        switch (category) {
            case "Not Started":
                notStartedData.addElement(task);
                break;
            case "In Progress":
                inProgressData.addElement(task);
                break;
            case "Finished":
                finishedData.addElement(task);
                break;
        }
        refreshTaskPanel(category);
    }

    public void removeTask(String task, String category) {
        switch (category) {
            case "Not Started":
                notStartedData.removeElement(task);
                break;
            case "In Progress":
                inProgressData.removeElement(task);
                break;
            case "Finished":
                finishedData.removeElement(task);
                break;
        }
        refreshTaskPanel(category);
    }

    private JPanel createTaskPanel(String category) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(category));
        return panel;
    }

    private void refreshTaskPanel(String category) {
        JPanel panel = getTaskPanel(category);
        DefaultListModel<String> model = getCategoryModel(category);

        panel.removeAll();
        for (int i = 0; i < model.size(); i++) {
            String task = model.getElementAt(i);

            JPanel taskItemPanel = new JPanel(new BorderLayout());
            JButton taskButton = new JButton(task);
            taskButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            if (category.equals("Finished")) {
                taskButton.setFont(taskButton.getFont().deriveFont(Font.PLAIN));
                taskButton.setForeground(Color.GRAY);
            }

            taskButton.addActionListener(e -> showTaskOptions(task, category));

            JButton removeButton = new JButton("X");
            removeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            removeButton.setBackground(new Color(244, 67, 54));
            removeButton.setForeground(Color.WHITE);
            removeButton.addActionListener(e -> removeTask(task, category));

            taskItemPanel.add(taskButton, BorderLayout.CENTER);
            taskItemPanel.add(removeButton, BorderLayout.EAST);
            taskItemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            panel.add(taskItemPanel);
        }

        panel.revalidate();
        panel.repaint();
    }

    private DefaultListModel<String> getCategoryModel(String category) {
        switch (category) {
            case "Not Started":
                return notStartedData;
            case "In Progress":
                return inProgressData;
            case "Finished":
                return finishedData;
            default:
                return new DefaultListModel<>();
        }
    }

    private void showTaskOptions(String task, String currentCategory) {
        String[] categories = {"Not Started", "In Progress", "Finished"};
        String newCategory = (String) JOptionPane.showInputDialog(
                null,
                "Move task to:",
                "Task Options",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categories,
                currentCategory
        );

        if (newCategory != null && !newCategory.equals(currentCategory)) {
            removeTask(task, currentCategory);
            addTask(task, newCategory);
        }
    }
}
