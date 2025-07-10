public class ThreadTest {
    
    public static void main(String[] args) {
        MyThread t1 = new MyThread("thread1", 5);
        MyThread t2 = new MyThread("thread2", 3);

        System.out.println("Thread1 Start!");
        t1.start();
        System.out.println("Thread2 Start!");
        t2.start();

    }

    private static class MyThread extends Thread {
        private String name;
        private int count;
        public MyThread(String name) {
            this.name = name;
        }
        public MyThread(String name, int count) {
            this.name = name;
            this.count = count;
        }
        
        @Override
        public void run() {
            for (int i = 1; i <= this.count; i++) {
                System.out.println(name + ":" + i);
            }
        }
    }
}

