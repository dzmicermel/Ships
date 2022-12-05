import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {
    // Метод генерации случайного числа от 0 до `n` включительно
    public static int generateRondomNumberProduct(int rondomNumberProduct) throws IllegalAccessException {
        if (rondomNumberProduct <= 0) {
            throw new IllegalAccessException("rondomNumberProduct must be positive");
        }
        SecureRandom random = new SecureRandom();
        byte[] bytes = random.generateSeed(20);
        random.nextBytes(bytes);
        return random.nextInt(rondomNumberProduct + 1);
    }


    // место в туннеле занято -true, свободно -false
    private static final boolean[] TUNNEL_PLACE = new boolean[6];
    private static final Semaphore SEMAFOR = new Semaphore(5, true);

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {

        // Генерируем корабли. 100шт.
        for (int i = 1; i <= 100; i++) {

            // Генерируем случайное число от 0 до 100. Если оно в диапазоне от 0 до 33,
            // то присваиваем ему значение 10 и т.д, 34 до 66 --- 50,
            // 66 до 100 ---100. Коряво - но примерно справедливо!
            int randomForVolume = (int) (Math.random() * 100);
            if (randomForVolume <= 33) {
                randomForVolume = 10;
            } else if (randomForVolume > 33 && randomForVolume <= 66) {
                randomForVolume = 50;
            } else randomForVolume = 100;

            //генерируем номер продукта от 0 до 3 включительно
            // Пусть product BREAD = 0, BANANA = 1, ОДЕЖДА = 2
            int rondomNumberProduct = 2;
            int r = generateRondomNumberProduct(rondomNumberProduct);
            //int rVolume = generateRondomNumberProduct(rondomNumberProduct);
            switch (r) {
                case 0:
                    new Thread(new Ship(i, "BREAD", randomForVolume)).start();
                    break;
                case 1:
                    new Thread(new Ship(i, "BANANA", randomForVolume)).start();
                    break;
                case 2:
                    new Thread(new Ship(i, "CLOTHES", randomForVolume)).start();
                    break;
            }
            Thread.sleep(100);
        }
    }

    public static class Ship implements Runnable {

        int numberShip;
        String product;
        int volume;

        public Ship(int numberShip, String product, int volume) {
            this.numberShip = numberShip;
            this.product = product;
            this.volume = volume;
        }

        @Override
        public void run() {

            System.out.printf("Ship N%d with the product:%s.  VOLUME %d. Going to tunnel.\n",
                    numberShip, product, volume);
            try {
                SEMAFOR.acquire(); // запрашивает доступ

                int tunnelNumber = -1;

                //ищем свободное место в туннеле
                synchronized (TUNNEL_PLACE) {
                    for (int i = 1; i <= 5; i++) {
                        if (!TUNNEL_PLACE[i]) {
                            TUNNEL_PLACE[i] = true;
                            tunnelNumber = i;
                            System.out.printf("Ship N%d take the place N%d at the tunnel. \n ",
                                    numberShip, tunnelNumber);
                            break;
                        }

                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (TUNNEL_PLACE) {
                    TUNNEL_PLACE[tunnelNumber] = false; //освобождают места в туннеле
                }
                SEMAFOR.release();
                System.out.printf("Ship N%d left place in the tunnel.\n", numberShip);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //распределяем корабли по соответствующим причалам

            if (product == "BREAD") {
                System.out.printf("Ship N%d going in Wharf_BREAD and starts loading BREAD." +
                        "Download duration = %d sec.\n ", numberShip,(volume/10));
                try {
                    Thread.sleep(volume / 10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("Ship N%d. %s. %d. TU-TU.\n",numberShip,product,volume);
            } else if (product == "BANANA") {
                System.out.printf("Ship N%d going in Wharf_BANANA and starts loading BANANA." +
                        "Download duration = %d sec.\n ", numberShip,(volume/10));
                try {
                    Thread.sleep(volume / 10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("Ship N%d. %s. %d. TU-TU.\n",numberShip,product,volume);
            } else {
                System.out.printf("Ship N%d going in Wharf_CLOTHES and starts loading CLOTHES." +
                        "Download duration = %d sec.\n ", numberShip,(volume/10));
                try {
                    Thread.sleep(volume / 10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("Ship N%d. %s. %d. TU-TU.\n",numberShip,product,volume);
            }
        }

    }
}
