import java.util.ArrayList;

public class Ex1 {
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);

        ArrayList<Thread> threads = new ArrayList<>();
        for (int idCore = 0; idCore < cores; ++idCore) {
            Thread myRunnable = new Thread(new MyRunnable(idCore));
            threads.add(myRunnable);
            myRunnable.start();
        }

        for(Thread myRunnable : threads){
            try {
                myRunnable.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class MyRunnable implements Runnable {
    private int id;

    MyRunnable(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Hello from thread " + id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
