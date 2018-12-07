package michal.cardmaker.model;

public class ReverseModel {

    private String name;
    private String address;
    private String fragmentMessage;
    private String path;

    public ReverseModel(String n, String a, String fm, String p){
        this.name = n;
        this.address = a;
        this.fragmentMessage = fm;
        this.path = p;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getFragmentMessage() {
        return fragmentMessage;
    }

    public String getPath() {
        return path;
    }
}
