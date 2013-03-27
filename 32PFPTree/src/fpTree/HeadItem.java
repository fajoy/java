package fpTree;

import java.util.ArrayList;
import java.util.List;

class HeadItem
{
    public String Item;
    public int order = 0;
    public int frequency = 0;
    public HeadItem(String item)
    {
        this.Item = item;
    }
    @Override
    public String toString() {
    	return String.format("%s:%s@%s",Item,frequency,order);
    }
}
