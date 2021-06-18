package ru.mail.huskerdev.gradle.ncompile.core.utils

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinNT

import java.lang.reflect.Field

class ProcessUtils {
    ;
    static long getPid(Process p){
        long pid = -1
        try {
            pid = p.pid()
        } catch (NoSuchMethodError ignored) {
            try {
                if (p.class.name == "java.lang.Win32Process" || p.class.name == "java.lang.ProcessImpl") {
                    Field f = p.class.getDeclaredField("handle")
                    f.accessible = true
                    WinNT.HANDLE hand = new WinNT.HANDLE(Pointer.createConstant(f.getLong(p)))
                    pid = Kernel32.INSTANCE.GetProcessId(hand)
                    f.accessible = false
                } else if (p.class.name == "java.lang.UNIXProcess") {
                    Field f = p.class.getDeclaredField("pid")
                    f.accessible = true
                    pid = f.getLong(p)
                    f.accessible = false
                }
            }
            catch(Exception ignored1) {
                pid = -1
            }
        }
        return pid
    }
}
