package greg.projects.com.company;
import java.util.*;
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Laptop[] arr = new Laptop[4];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new Laptop(sc.nextInt(), sc.nextLine(), sc.nextLine(), sc.nextDouble(), sc.nextInt());
            sc.nextLine();



        }
        String brand = sc.nextLine();
        String osType = sc.nextLine();
        int a = countOfLaptop(arr, brand);
        Laptop[] b = search(arr, osType);

        if (a > 0) {
            System.out.println(a);
        } else {
            System.out.println("NOT AVAILABLE");
        }

        if (b != null) {
            for (int i = 0; i < b.length; i++) {
                System.out.println(b[i].getLaptopId() +" "+b[i].getBrand());
            }
        } else {
            System.out.println("NOTINING2");
        }


    }






    public static int countOfLaptop(Laptop[] arr, String brand) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getBrand().equalsIgnoreCase(brand) && arr[i].getRating() > 3) {
                count++;
            }
        }

        return count;
    }

    public static Laptop[] search(Laptop[] arr, String osType) {
        int counter = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getOsType().equalsIgnoreCase(osType)) {
                counter++;
            }
        }
        Laptop[] refine = new Laptop[counter];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getOsType().equalsIgnoreCase(osType)) {
                refine[counter] = arr[i];
                counter--;
            }


        }
        if(counter>0) {
            return refine;
        }
        else {
            return null;
        }

    }
}
