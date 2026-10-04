package bf.memoire;
import android.content.*;
import android.database.*;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import java.io.*;
import java.util.*;

public class AttachmentProvider extends ContentProvider {
    public boolean onCreate(){return true;}
    private File file(Uri uri)throws FileNotFoundException {
        String id=uri.getPathSegments().isEmpty()?"":uri.getPathSegments().get(0);
        if(!id.matches("[a-fA-F0-9-]{36}"))throw new FileNotFoundException();
        File file=new File(getContext().getFilesDir(),"attachments/"+id);
        if(!file.isFile())throw new FileNotFoundException();return file;
    }
    public String getType(Uri uri){String name=uri.getLastPathSegment();int dot=name.lastIndexOf('.');String type=dot<0?null:MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substring(dot+1).toLowerCase(Locale.ROOT));return type==null?"application/octet-stream":type;}
    public Cursor query(Uri uri,String[] projection,String selection,String[] args,String sort){
        try{File f=file(uri);String[] columns=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection;MatrixCursor c=new MatrixCursor(columns);Object[] row=new Object[columns.length];for(int i=0;i<columns.length;i++){if(columns[i].equals(OpenableColumns.DISPLAY_NAME))row[i]=uri.getLastPathSegment();else if(columns[i].equals(OpenableColumns.SIZE))row[i]=f.length();}c.addRow(row);return c;}catch(FileNotFoundException e){return null;}
    }
    public ParcelFileDescriptor openFile(Uri uri,String mode)throws FileNotFoundException{if(mode.equals("r"))return ParcelFileDescriptor.open(file(uri),ParcelFileDescriptor.MODE_READ_ONLY);if(mode.equals("w")||mode.equals("rw"))return ParcelFileDescriptor.open(file(uri),ParcelFileDescriptor.MODE_READ_WRITE|ParcelFileDescriptor.MODE_TRUNCATE);throw new FileNotFoundException("Mode interdit");}
    public Uri insert(Uri uri,ContentValues values){throw new UnsupportedOperationException();}
    public int update(Uri uri,ContentValues values,String where,String[] args){throw new UnsupportedOperationException();}
    public int delete(Uri uri,String where,String[] args){throw new UnsupportedOperationException();}
}
