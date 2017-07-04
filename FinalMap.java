/*
Author: Amit Patni
Created on: 04-07-2017 @6:12pm
*/

/*
Implement a Map, which spills on to disk when it exceeds the heap, or a specified limit.
o Time Complexity should be same as Current NavigableMap implementations (eg:TreeMap)
o Should be capable of handling in a multithreaded environment.
o put() and get() operations, should be Thread Safe.
o Should be Serializable to disk
o Should provide API level Tuning configurations, Where ever applicable.
*/
package mapimplementation;

import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.PrintWriter; 
import java.util.HashMap; 
import java.util.Iterator; 
import java.util.Map; 
import java.util.Scanner; 
import java.util.Set;

public class FinalMap {
    
    public final static int THREAD_POOL_SIZE = 5;
   
    public static synchronized String get(String key,int lineNumber) throws IOException{
	File mapFile = new File("map.txt");
	BufferedReader br = new BufferedReader(new FileReader(mapFile));
	String mapString = "";
	if(mapFile.exists() && mapFile.canRead()){
            for(int i=1; i<=lineNumber; i++){
		mapString = br.readLine();
            }
            int keyIndex = mapString.indexOf(key);
            int colonIndex = mapString.indexOf(":", keyIndex);
            int semicolon = mapString.indexOf(";",colonIndex);
            String value = mapString.substring(colonIndex+1, semicolon);
            System.out.println(value);
            return value;
	}
	return null;
    }

    public static void put(String key,String value,int lineNumber) throws IOException{
	File file = new File("map.txt");
	File file1 = new File("map1.txt");
	if(!file.exists()){
            file.createNewFile();
	}
	BufferedReader br = new BufferedReader(new FileReader(file));
	PrintWriter pw = new PrintWriter(new FileWriter(file1));
	if(file.exists() && file.canRead() && file.canWrite()){
            for(int i=1; i<lineNumber; i++){
		String str = br.readLine();
		if(str == null){
                    pw.println();
		}
                else{
                    pw.println(str);
		}
            }
            String str = br.readLine();
            if(str == null){
		String entry = ((key.concat(":")).concat(value)).concat(";");
		pw.println(entry);
            }
            else if(!str.contains(key)){
		String entry = ((key.concat(":")).concat(value)).concat(";");
		str += entry;
                System.out.println(str);
                pw.println(str);
            }
            else if(str.contains(key)){
                int keyindex = str.indexOf(key);
                int colonindex = str.indexOf(":", keyindex);
                int semicolon;
                semicolon = str.indexOf(";",colonindex);
                String old = str.substring(colonindex+1, semicolon);
                str = str.replace(old, value);
                pw.println(str);
            }
            pw.flush();
            pw.close();
            br.close();
            System.out.println(file.delete());
            System.out.println(file1.renameTo(file));
	}
    }

    public static synchronized int lineNo(int key){
	return ((key%10)+1);
    }

    public static synchronized void overflow(Map<Integer,String> hashMap) throws IOException{
        Iterator ite = hashMap.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<Integer, String> pair=(Map.Entry<Integer, String>)ite.next();
            put(Integer.toString(pair.getKey()), pair.getValue(),lineNo(pair.getKey()));
            ite.remove(); // removes concurrent modification exception
        }
        Set<Integer> keyset;
        keyset = hashMap.keySet();
        keyset.forEach((key) -> {
            hashMap.remove(key);
        });
    }
    
   
    public static void main(String[] args) throws IOException, InterruptedException {
        Map<Integer,String> map = new HashMap<>();
        Scanner io = new Scanner(System.in);
        System.out.println("Enter how many elements wish to insert (Max 5 are allowed in map)");
        int max = io.nextInt();
        for(int i=0; i<max; i++){
            System.out.println("Enter integer key for "+ (i+1) +" th element");
            int key = io.nextInt();
            System.out.println("Enter String value for "+ (i+1) +" th element");
            String value = io.next();
            if(map.size() == 5){
                overflow(map);
            }
            map.put(key, value);
        }
        System.out.println("Enter Key to get Value");
        int key = io.nextInt();
        if(map.containsKey(key)){
            System.out.println("Value is : " + map.get(key));
        }
        else{
            String value=get(Integer.toString(key), lineNo(key));
            System.out.println("Value is : " + value);
        }
    }
}
