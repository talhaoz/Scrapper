package com.icecat;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sowji on 21/01/2017.
 * Utility methods or commonly used methods!
 */
public class Utils {

    public static List<String> getImageUrls(Elements images){
        if(images == null || images.isEmpty()){
            return null;
        }
        List<String> imageUrls = new ArrayList<>();
        for(Element image : images) {
            imageUrls.add( image.attr("src") );
        }

        return imageUrls;
    }

    public static List<List<Specification>> getTableData(Element element) {
        List<List<Specification>> tableData = new ArrayList<>();

        List<String> colNames = new ArrayList<>();
        Elements rows = element.getElementsByTag("tr");
        Element header = rows.get(0);
        Elements columns = header.getElementsByTag("th");
        for(Element th : columns) {
            String name = th.text();
            colNames.add(name);
        }

        for(int i= 1; i< rows.size(); i++ ){
            List<Specification> specList = new ArrayList<>();
            columns = rows.get(i).getElementsByTag("td");
            int j = 0;
            for(Element td : columns) {
                Specification specification = new Specification();
                if(colNames.get(j) == null || colNames.get(j).isEmpty()){
                    j++;
                    continue;
                } else {
                    specification.setName(colNames.get(j));
                    String values = td.text();
                    specification.setValues(values);
                }
                specList.add(specification);
                j++;
            }
            tableData.add(specList);
        }
        return tableData;
    }
}