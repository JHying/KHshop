package tw.com.huang.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataUtil {

	public static List<String[]> repeatRemove(List<String[]> targetList) {
		Set<String> set = new HashSet<String>();
        for (int i = 0; i < targetList.size(); i++) {
            String[] row = (String[]) targetList.get(i);
            if (set.add(row[0])) {
                //非重複資料則加入
            } else {
            	targetList.remove(i);
            }
        }
		return targetList;
	}
	
}
