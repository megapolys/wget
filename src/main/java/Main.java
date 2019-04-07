import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static String path = "";
    private static int maxDepth = 1;
    private static String root = "";

    public static void main(String[] args) {

        System.out.println("1st arg - recursion depth \n" +
        "2nd arg - url of page\n" +
                "3rd arg - saving path");


        try {

            maxDepth = Integer.parseInt(args[0]);
            root = args[1];
            path = args[2];
            getHTML(root, 0);

        } catch (Exception e){
            System.out.println("ERROR");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("SUCCESS");
    }

    public static InputStream getRequest(String url) throws Exception{
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }

    static private void getHTML(String url, int depth) throws Exception{

        if (depth > maxDepth) {
            return;
        }

        String[] split = url.split("/+");
        File dir = new File(path);
        dir.mkdir();
        for (int i = 1; i < split.length; i++) {
            dir = new File(dir.getPath() + "\\" + split[i]);
            dir.mkdir();
        }
        FileOutputStream out = new FileOutputStream(dir + "\\index.html");

        Pattern pat = Pattern.compile("href=\"" + root + ".+?\"");

        InputStream request = getRequest(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(request));
        String inputLine;

        HashSet<String> set = new HashSet<String>();

        while ((inputLine = in.readLine()) != null) {
            String str = inputLine + "\n";
            Matcher mat = pat.matcher(str);
            while (mat.find()){
                String group = mat.group();
                set.add(group.substring(6, group.length()-1));
            }

            out.write(str.getBytes());
        }
        in.close();
        out.close();

        for (String str : set) {
            if (str.charAt(str.length() - 1) == '/'){
                getHTML(str, depth + 1);
            } else {
                getOther(str, depth + 1);
            }
        }

    }

    static private void getOther(String url, int depth) throws Exception{

        if (depth > maxDepth) {
            return;
        }

        String[] split = url.split("/+");
        File dir = new File(path);
        dir.mkdir();
        for (int i = 1; i < split.length - 1; i++) {
            dir = new File(dir.getPath() + "\\" + split[i]);
            if (dir.isFile()) {
                dir.delete();
            }
            dir.mkdir();
        }
        File f = new File(dir + "\\" + split[split.length - 1].replaceAll("\\?", "@"));
        System.out.println(f.getPath());
        if (!f.createNewFile()){
            return;
        }
//        System.out.println(f.getPath());
        FileOutputStream out = new FileOutputStream(f);

        InputStream in = getRequest(url);
        byte[] bytes = new byte[512];

        while ((in.read(bytes)) != -1) {
            out.write(bytes);
        }
        in.close();
        out.close();

    }
}
