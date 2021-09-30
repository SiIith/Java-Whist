package Filter;



// a singleton factory for Filters
public class FilterFactory {
    private static FilterFactory filterFactory=null;
    private FilterFactory(){
    }
    public static FilterFactory getInstance(){
        if(filterFactory==null)
            filterFactory=new FilterFactory();
        return filterFactory;
    }

    // returns filter based on selection, default to be naive
    public IFilter getFilter(String type){
        if (type.equals("trump")) return new TrumpSavingIFilter();
        else if(type.equals("naive")) return new NaiveIFilter();
        else return new NaiveIFilter();
    }
}
