package com.icecat.cassedro;

import com.icecat.BrandSpecs;
import com.icecat.Scrapper;
import com.icecat.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

/**
 * Created by Sowjanya on 2/28/2017.
 */

//To remember should write class names in spanish only for getting spanish results
public class CassedroLibre extends Scrapper {
    /*private List<String> getCategory(){
        List<String> filter = new LinkedList<>();
        String url = Constants.CATEGORY_URL;
        String html = get_html(url);
        Document document = parse_html(html);
        Element element = document.getElementById("content");
        Elements elements1 = element.getElementsByClass(Constants.FILTER_CLASS);
            for(Element element1 : elements1) {
                String text = element1.text();
                if (text.equals("Filtrar por temática")) {
                    Elements fclass = element.getElementsByTag("a");
                    for(Element element2 : fclass) {
                        String href = element2.attr("href");
                        String filterUrl = Constants.BASE_URL+href;
                            filter.add(filterUrl);
                    }
                }
            }
       //System.out.println(filter);
        return filter;
    }*/
    //ProductUrls
    //Per Page 60 results
    //should manage them till the end of the results.Todo

    /* private List<String> getProductUrl() {
         List<String> productUrl = new LinkedList<>();
         //for (int i = 0; i< getCategory().size();i++) {
             String url = getCategory().get(18);
             String html = get_html(url);
             Document document = parse_html(html);
             Elements h2 = document.getElementsByTag("h2");
             for(Element element : h2){
                 Elements tags = element.getElementsByTag("a");
                 for(Element tag : tags) {
                     String href = tag.attr("href");
                     //System.out.println(href);
                     productUrl.add(Constants.BASE_URL+href);
                 }
             }
         //}
             System.out.println(productUrl);
             System.out.println(productUrl.size());
             return productUrl;
     }*/
    private List<String> getEnglishBooks(){
        List<String> list = new ArrayList<>();
        for(int i = 198; i <= 200; i++) {
            String url = Constants.ENGLISH_BOOKS.replace("%data%",i+"");
            String html = get_html(url);
            Document document = parse_html(html);
            Elements h2 = document.getElementsByTag("h2");
            for(Element element : h2){
                Elements tags = element.getElementsByTag("a");
                for(Element tag : tags) {
                    String href = tag.attr("href");
                    //System.out.println(href);
                    list.add(Constants.BASE_URL+href);
                }
            }
        }
        //System.out.println(list);
        //System.out.println(list.size());
        return list;
    }
    private String getTitle(Document document){
        String[] titleText = null;
        Elements elements = document.getElementsByTag("h1");
        for(Element element : elements) {
            String title = (element.text());
            titleText = title.split("(En papel)");
            //System.out.println(title);
        }      // return titleText[0].replace("(", " ");
        return  titleText[0].replace("(", " ");
    }

    private String getDescription(Document document) {
        /*for(int i = 0; i < getEnglishBooks().size(); i++ ){
            String url = getEnglishBooks().get(i);
            String html = get_html(url);
            Document document = parse_html(html);*/

            Elements elements = document.getElementsByClass("col02");
            if(elements.size() > 0) {
                   String text = elements.get(0).text();
                   if(text.contains("Resumen del libro")) {
                       String[] desc = text.split("Resumen del libro");
                       desc[0] = "Resumen del libro ";
                       String description = desc[1];
                       return description;
                   }else{

                       return text;
                   }

            }else{
                return "null";
            }
        }

        //System.out.println(description);
        //}


    private Map<String,String> details(Document document){
        Map<String,String> details = new HashMap<>();
        Element element = document.getElementsByClass("list07").get(1);
        Elements tags = element.getElementsByTag("li");
        int unknown = 0;
        for(Element tag : tags) {
       /* Elements elements = element.getElementsByClass("first-child");
        for(Element element1 : elements) {
           String text =  element1.text();*/
            //String[] map = text.split(":");
            String text = tag.text();
            String[] map = text.split(":");
            if( map.length > 1) {
                details.put(map[0], map[1]);
            } else {
                details.put("unknown"+unknown, map[0]);
                unknown++;
            }
        }
        //}
       //System.out.println(details);
        return details;
    }
    private String getAuthor(Document document){
        String author = document.getElementsByClass("book-header-2-subtitle-author").get(0).text();
        //System.out.println(author);
        return author;
    }
    private String getAuthorDetails(Document document){
        String author = document.getElementsByClass("book-header-2-subtitle").get(0).getElementsByTag("a").attr("href");
        if(author != null && !author.equals("")) {
            //System.out.println(Constants.BASE_URL+author);
            return Constants.BASE_URL + author;
        }else
            return null;
    }
    private String getAuthorDesc(Document document){
        String url = getAuthorDetails(document);
        if(url != null) {
            String html = get_html(url);
            Document document1 = parse_html(html);
            Elements ps = document1.getElementsByTag("p");
            String text = null;
            for(Element p : ps) {
                if(p.hasClass("mt5")){
                    text = p.text();
                    break;
                }
            }
            //System.out.println(text);
            return text;
        }else
            return "null";
    }
    private String getPublisher(Document document){
        Elements elements = document.getElementsByClass("book-header-2-subtitle-publisher");
              if(elements.size() > 0) {
                  String publisher = elements.get(0).text();
                  return publisher;
              }else{
                  return "null";
              }
        //System.out.println(publisher);

    }
    private List<String> getImageList(Document document){
        List<String> images = new ArrayList<>();
        String img = document.getElementsByClass("img-shadow").get(0).attr("src");
        if(!img.contains("defect")) {
            images.add(img);
            //System.out.println(images);
            return images;
        }else{
            return null;
        }
    }

    public static void writeFile( String filePath){
        CassedroLibre books = new CassedroLibre();

        String headers = "\"Title\"," +
                "\"Author\"," +
                "\"Author Description\"," +
                "\"Publisher\"," +
                "\"Description\"," +
                "\"Image1\"," +
                "\"Image2\"," +
                "\"Image3\"," ;

        List<String> extraHeaders = new ArrayList<>();
        Set<String> extraHeaderSet = new HashSet<>();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            for(int index = 0 ; index < books.getEnglishBooks().size() ;index++) {
                String url = books.getEnglishBooks().get(index);
                //String url = "https://www.casadellibro.com/libro-un-ano-entre-los-persas/mkt0003066624/4306635";
                BrandSpecs brandSpecs = books.brandSpecs(url);
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getName()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getBrand_name()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getAuthorDesc()) + "\",");
                bw.write("\"" + Utils.formatForCSV(brandSpecs.getPublisher()) + "\",");
                String description = brandSpecs.getDescription() != null ? brandSpecs.getDescription() : "null";
                bw.write("\"" + Utils.formatForCSV(description) + "\",");
                int i = 0;
                //pictures 3
                List<String> images = brandSpecs.getImagesList();
                if (images != null)
                    for (String image : images) {
                        bw.write("\"" + Utils.formatForCSV(image) + "\",");
                        i++;
                        if (i == 3) break;
                    }
                while (i < 3) {
                    bw.write(",");
                    i++;
                }
                i = 0;
                Map<String, String> features = brandSpecs.getDetails();
                for(String key: features.keySet()){
                    if( !extraHeaderSet.contains(key) ){
                        extraHeaders.add(key);
                        extraHeaderSet.add(key);
                    }
                }
                for(String key : extraHeaders) {
                    bw.write( "\"" + Utils.formatForCSV(features.get(key)) + "\",");
                }
                bw.newLine();
            }
                bw.flush();
                bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            for(String s : extraHeaders){
                headers += "\"" + s + "\",";
            }
            headers += "\n";

            File mFile = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String result = "";
            String line = "";
            while( (line = br.readLine()) != null){
                result = result + line;
                result += "\n";
            }
            result = headers + result;
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(result.getBytes());
            fos.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private BrandSpecs brandSpecs(String url){
        BrandSpecs brandSpecs = new BrandSpecs();
        Document brandDocument =parse_html(get_html(url));

        String title = getTitle(brandDocument);
        if(title != null )
        brandSpecs.setName(title);

        List<String> images = getImageList(brandDocument);
        if(images != null)
        brandSpecs.setImagesList(images);

        String desc = getDescription(brandDocument);
        if(desc != null)
        brandSpecs.setDescription(desc);

        Map<String, String> details = details(brandDocument);
        if(details!=null)
        brandSpecs.setDetails(details);

        String author = getAuthor(brandDocument);
        if(author != null)
        brandSpecs.setBrand_name(author);

        String authorDesc = getAuthorDesc(brandDocument);
        if(authorDesc != null)
        brandSpecs.setAuthorDesc(authorDesc);

        String publisher = getPublisher(brandDocument);
        if(publisher != null)
        brandSpecs.setPublisher(publisher);

        System.out.println(brandSpecs);
        return brandSpecs;
    }

    public static void main(String[] args) {
        CassedroLibre books = new CassedroLibre();
        books.getEnglishBooks();
        // books.getProductUrl();
        String filePath = "C:\\Users\\Sowjanya\\Documents\\casadellibro";
        // books.brandSpecs(url);
            books.writeFile(filePath + File.separator + "booksp198-200"+".csv");
            //String html = books.get_html(url);
            //Document document = books.parse_html(html);
           //books.getDescription(document);
            //books.details(document);
           // books.getAuthor(document);
       // books.getAuthorDetails(document);
       // books.getAuthorDesc(document);
       // books.getPublisher(document);
       // books.getImageList(document);
     // }
        //books.getTitle();
        //books.getDescription();
    }
}
