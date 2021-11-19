package philosophersProblem;

public class Philosopher implements Runnable {
    private final Object leftFork;
    private final Object rightFork;
    private final int id;

    public Philosopher(int id, Object leftFork, Object rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.id = id;
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /// Un filozof (primul) ia furculitele in ordine normala (aici L-R)
        /// A lua o furculita <=> a face lock pe acel obiect
        if (id == 0) {
            synchronized (leftFork) {
                sleep(); // delay added to make sure the dead-lock is visible
                synchronized (rightFork) {
                    System.out.println("Philosopher " + id + " is eating");
                }
            }
        } else {
            // Ceilalti au ordinea normala (aici right left)
            // Asteptarea se face asadar evitand deadlock
            synchronized (rightFork) {
                sleep(); // delay added to make sure the dead-lock is visible
                synchronized (leftFork) {
                    System.out.println("Philosopher " + id + " is eating");
                }
            }

        }
    }
}
