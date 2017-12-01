package cck.com.chello.uninstall;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class IO {

// TODO Remove unused code found by UCDetector
// 	public static void delete(File file){
// 		file.delete();
// 	}
// TODO Remove unused code found by UCDetector
// 	public static void delete(String file){
// 		new File(file).delete();
// 	}
// TODO Remove unused code found by UCDetector
// 	public static void mv(String source,String target){
// 		try {
// 			Runtime.getRuntime().exec(String.format("mv %s %s",source,target));
// 		} catch (IOException e) {
// 			// TODO Auto-generated catch block
// 			e.printStackTrace();
// 		}
// 	}

// TODO Remove unused code found by UCDetector
// 	public static String readString(String file) throws IOException{
// 		return readString(new File(file));
// 	}

	public static String readString(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		String str = readString(in);
		in.close();
		return str;
	}
	
	public static byte[] readBytes(InputStream in) throws IOException {
		byte[] buf = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int c = 0;
		while((c = in.read(buf)) > 0){
			out.write(buf,0,c);
		}
		byte[] bytes = out.toByteArray();
		out.close();
		return bytes;
		
	}

	
// TODO Remove unused code found by UCDetector
// 	public static byte[] readBytes(String path) throws IOException{
// 		FileInputStream in = new FileInputStream(path);
// 		byte[] bytes = IO.readBytes(in);
// 		in.close();
// 		return bytes;
// 	}
	
	public static String readString(InputStream in) throws IOException {
		byte[] bytes = readBytes(in);
		return new String(bytes,"UTF-8");
	}
	
// TODO Remove unused code found by UCDetector
// 	public static void writeString(OutputStream out,String str) throws IOException{
// 		out.write(str.getBytes());
// 	}

	
// TODO Remove unused code found by UCDetector
// 	public static void appendString(OutputStream out,String str) throws IOException{
// 		out.write(str.getBytes());
// 	}

	public static void appendString(File file, String str) throws IOException {
		FileOutputStream out = new FileOutputStream(file,true);
		out.write(str.getBytes());
		out.close();
	}
	
// TODO Remove unused code found by UCDetector
// 	public static void appendString(String file,String str) throws IOException{
// 		appendString(new File(file),str);
// 	}
	
	public static void writeString(File file, String str) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(str.getBytes());
		out.close();
	}
	
	public static void writeUTF8String(File file, String str) throws IOException {
		OutputStreamWriter outw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
		outw.write(str);
		outw.close();
	}
	
// TODO Remove unused code found by UCDetector
// 	public static void writeUTF8String(String file,String str) throws IOException{
// 		writeUTF8String(new File(file),str);
// 	}
	
// TODO Remove unused code found by UCDetector
// 	public static void writeString(String file,String str) throws IOException{
// 		writeString(new File(file),str);
// 	}

	public static boolean copy(InputStream in, String target) throws IOException {
		FileOutputStream out = null;
		try{
			out = new FileOutputStream(new File(target));
			byte[] buf = new byte[1024 * 10];
			int c = 0;
			while((c = in.read(buf)) > 0){
				out.write(buf,0,c);
			}
		}finally{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}
	
	public static boolean copy(String source, String target){
		FileInputStream in = null;;
		FileOutputStream out = null;;
		try {
			in = new FileInputStream(new File(source));
			out = new FileOutputStream(new File(target));
			byte[] buf = new byte[1024];
			int c = 0;
			while((c = in.read(buf)) > 0){
				out.write(buf,0,c);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}
	
// TODO Remove unused code found by UCDetector
// 	public static void serialize(Serializable obj,String file) throws FileNotFoundException, IOException{
// 		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
// 		try {
// 			oos.writeObject(obj);
// 			
// 		} catch (IOException e) {
// 			// TODO Auto-generated catch block
// 			e.printStackTrace();
// 			throw e;
// 		}finally{
// 			try {
// 				oos.close();
// 			} catch (IOException e) {
// 				// TODO Auto-generated catch block
// 				e.printStackTrace();
// 			}
// 		}
// 		
// 	}
	
// TODO Remove unused code found by UCDetector
// 	public static Object unserialize(String file) throws Exception{
// 		ObjectInputStream ois = null;
// 		try {
// 			ois = new ObjectInputStream(new FileInputStream(file));
// 			return ois.readObject();
// 		} catch (Exception e) {
// 			return null;
// 		}finally{
// 			try {
// 				if(ois != null)
// 					ois.close();
// 			} catch (IOException e) {
// 				// TODO Auto-generated catch block
// 				e.printStackTrace();
// 			}
// 		}
// 	}
	
	
}
