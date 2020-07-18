package cardproject.android.arnab.library;

public class Item {

    private String mtxt1,mtxt2;
    private int mimageres;
    public Item(int imageres,String txt1,String txt2)
    {
        mimageres=imageres;
        mtxt1=txt1;
        mtxt2=txt2;
    }

    public int getMimageres() {
        return mimageres;
    }

    public String getMtxt1() {
        return mtxt1;
    }

    public String getMtxt2() {
        return mtxt2;
    }

}
