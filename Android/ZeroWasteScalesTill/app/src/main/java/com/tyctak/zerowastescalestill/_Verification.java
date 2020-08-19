package com.tyctak.zerowastescalestill;

import java.util.ArrayList;

public class _Verification {
    public Integer Build;
    public String ZipFile;
    public String Built;
    public String Version;
    public ArrayList<_File> Fixeds;
    public ArrayList<_File> Files;

    public static class _File {
        public String Name;
        public Boolean IsFixed;
        public String CurrentChecksum;
        public String OriginalChecksum;
        public String Key;
        public Boolean IsMatch;
    }
}