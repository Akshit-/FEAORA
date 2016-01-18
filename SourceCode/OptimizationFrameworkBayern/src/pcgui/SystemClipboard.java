package pcgui;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class SystemClipboard
{
    public static void copy(String text)
    {
        Clipboard clipboard = getSystemClipboard();

        clipboard.setContents(new StringSelection(text), null);
    }

    public static void paste() throws AWTException
    {
        Robot robot = new Robot();

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
    }

    public static String get() throws HeadlessException,
            UnsupportedFlavorException, IOException
    {
        Clipboard systemClipboard = getSystemClipboard();
        Object text = systemClipboard.getData(DataFlavor.stringFlavor);

        return (String) text;
    }

    private static Clipboard getSystemClipboard()
    {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Clipboard systemClipboard = defaultToolkit.getSystemClipboard();

        return systemClipboard;
    }
}