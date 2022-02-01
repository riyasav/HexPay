package io.thoughtbox.hamdan.utls;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DeviceUtils {

    public static boolean isRootGiven() {
        if (isRootAvailable()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = in.readLine();
                if (output != null && output.toLowerCase().contains("uid=0"))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (process != null)
                    process.destroy();
            }
        }

        return false;
    }

    public static boolean isRootAvailable() {
        for (String pathDir : System.getenv("PATH").split(":")){
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }

//    // try executing commands
//    private boolean isrooted2() {
//        return canExecuteCommand("/system/xbin/which su")
//                || canExecuteCommand("/system/bin/which su")
//                || canExecuteCommand("which su");
//    }
//    // executes a command on the system
//    private static boolean canExecuteCommand(String command) {
//        boolean executedSuccesfully;
//        try {
//            Runtime.getRuntime().exec(command);
//            executedSuccesfully = true;
//        } catch (Exception e) {
//            executedSuccesfully = false;
//        }
//
//        return executedSuccesfully;
//    }

    public Boolean isDeviceRooted(Context context) {
        boolean isRooted = isrooted1() || isRootGiven();
        return isRooted;
    }

    private boolean isrooted1() {

        File file = new File("/system/app/Superuser.apk");
        return file.exists();
    }
}