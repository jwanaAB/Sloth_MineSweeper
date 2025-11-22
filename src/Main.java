import controller.MainController;
import javax.swing.SwingUtilities;
import model.SysData;
import view.MainView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SysData model = SysData.getInstance();
            MainView view = new MainView();
            MainController controller = new MainController(model, view);
            controller.init();
        });
    }
}

