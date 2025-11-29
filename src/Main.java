import controller.MainController;
import javax.swing.SwingUtilities;
import model.SysData;
import view.MainView;


public class Main {
    public static void main(String[] args) {
        // Set up uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Uncaught exception in thread " + thread.getName() + ":");
            exception.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "An error occurred: " + exception.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        });
        
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Initializing application...");
                SysData model = SysData.getInstance();
                System.out.println("SysData instance created");
                MainView view = new MainView();
                System.out.println("MainView created");
                MainController controller = new MainController(model, view);
                System.out.println("MainController created");
                controller.init();
                System.out.println("Controller initialized, GUI should be visible now");
            } catch (Exception e) {
                System.err.println("Error initializing application:");
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}

