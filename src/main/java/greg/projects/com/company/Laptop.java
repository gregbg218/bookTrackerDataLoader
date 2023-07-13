package greg.projects.com.company;

class Laptop{

    int laptopId;
    String brand;
    String osType;
    double price;
    int rating;


    Laptop(int laptopIdp,String brand,String osType,double price,int rating){
        this.brand=brand;
        this.osType=osType;
        this.price=price;
        this.rating=rating;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public int getLaptopId() {
        return laptopId;
    }

    public int getRating() {
        return rating;
    }

    public String getOsType() {
        return osType;
    }

}