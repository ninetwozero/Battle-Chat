package com.ninetwozero.battlechat.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpHeaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class StorageUtils {
    public static String downloadFile(final Context context, final String url, final Object outname) {
        File outFile;
        if( outname == null ) {
            outFile = new File(context.getExternalFilesDir(null), getFilenameFromUrl(url));
        } else {
            outFile = new File(context.getExternalFilesDir(null), String.valueOf(outname));
        }
        byte[] imageData = null;
        try {
            JSONObject json = BattleChatClient.get(url, HttpHeaders.Get.NORMAL);
            imageData = json.getString("data").getBytes();
        } catch(JSONException ex) {
            Log.d(BattleChat.TAG, ex.getMessage());
            return null;
        }

        if( imageData == null ) {
            return null;
        }

        try {
            FileOutputStream out = new FileOutputStream(outFile);
            out.write(imageData);
            out.close();
        } catch( IOException ex ) {
            Log.d(BattleChat.TAG, ex.getMessage());
            return null;
        }
        return outFile.getAbsolutePath();
    }

    private static String getFilenameFromUrl(final String url) {
        return new File(Uri.parse(url).toString()).getName();
    }

}
