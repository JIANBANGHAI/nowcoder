package com.nowcoder.community;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}
class Producer implements Runnable{
    public BlockingQueue<Integer> queue;
    Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        for (int i = 0; i <101 ; i++) {
            try {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(System.currentTimeMillis()+"生产:"+queue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable{
    public BlockingQueue<Integer> queue;
    Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
            try {
                while (true){
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                    System.out.println(System.currentTimeMillis()+"消费:"+queue.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
