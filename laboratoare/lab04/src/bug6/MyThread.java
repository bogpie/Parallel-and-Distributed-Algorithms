package bug6;

public class MyThread extends Thread {
    @Override
    public void run() {
        synchronized (Main.obj) {
            Singleton instance = Singleton.getInstance();

        }
    }
}
