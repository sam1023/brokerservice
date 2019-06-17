package com.ares.utilities.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class TinyEncryption implements Serializable {

    final static public int ARES_CRYPT_MODE_TEA = 1;

    int cryptMode = ARES_CRYPT_MODE_TEA;

    public TinyEncryption() {
        initTransientFields();
    }

    static public String encrypt(String s) {
        TinyEncryption inst = new TinyEncryption();
        return inst.teaEncrypt(s);
    }

    static public String decrypt(String s) {
        TinyEncryption inst = new TinyEncryption();
        return inst.teaDecrypt(s);
    }

    public String teaEncrypt(String s) {
        char ac[] = s.toCharArray();
        byte abyte0[] = new byte[ac.length * 2];
        for (int i = 0; i < ac.length; i++)
            abyte0[i] = (byte) (ac[i] & 255);

        int ai[] = tea.encode(abyte0, abyte0.length);
        String s1 = "";
        for (int j = 0; j < ai.length; j++) {
            String s2 = Integer.toHexString(ai[j]);
            if (s2.length() % 2 != 0)
                s2 = "0" + s2;
            s1 = s1 + s2;
        }

        return s1;
    }

    public String teaDecrypt(String s) {
        int ai[];
        ArrayList<Long> arraylist = new ArrayList<Long>();
        int i = s.length();
        int j;
        for (int k = 0; k < i; k = j) {
            j = k + 8;
            if (j >= i)
                j = i;
            String s1 = s.substring(k, j);
            try {
                arraylist.add(Long.decode("0x" + s1));
            } catch (NumberFormatException numberformatexception) {
                return "";
            }
        }

        ai = new int[arraylist.size()];
        for (int l = 0; l < ai.length; l++)
            ai[l] = ((Long) arraylist.get(l)).intValue();

        char ac[];
        int i1;
        byte abyte0[] = tea.decode(ai);
        ac = new char[abyte0.length];
        i1 = 0;
        for (int j1 = 0; j1 < ac.length; j1++) {
            ac[j1] = (char) abyte0[j1];
            if (ac[j1] == 0)
                i1++;
        }

        return new String(ac, 0, ac.length - i1);
    }

    private void initTransientFields() {
        encKey = (new BigInteger("687d44e4a3a912230909823be3e2455", 16)).toByteArray();
        tea = new Tea(encKey);
    }

    private static final long serialVersionUID = 70L;
    private transient Tea tea;
    private transient byte encKey[];

    public class Tea {
        public Tea(int ai[]) {
            _key = ai;
        }

        public Tea(byte abyte0[]) {
            int i = abyte0.length;
            _key = new int[4];
            if (i != 16)
                throw new ArrayIndexOutOfBoundsException(getClass().getName() + ": Key is not 16 bytes: " + i);
            int k = 0;
            for (int j = 0; j < i;) {
                _key[k] = abyte0[j] << 24 | (abyte0[j + 1] & 255) << 16 | (abyte0[j + 2] & 255) << 8
                        | abyte0[j + 3] & 255;
                j += 4;
                k++;
            }

        }

        int[] encode(byte abyte0[], int i) {
            int l = i;
            byte abyte1[] = abyte0;
            _padding = l % 8;
            if (_padding != 0) {
                _padding = 8 - l % 8;
                abyte1 = new byte[l + _padding];
                System.arraycopy(abyte0, 0, abyte1, 0, l);
                l = abyte1.length;
            }
            int i1 = l / 4;
            int ai[] = new int[2];
            int ai1[] = new int[i1];
            int k = 0;
            for (int j = 0; j < l;) {
                ai[0] = abyte1[j] << 24 | (abyte1[j + 1] & 255) << 16 | (abyte1[j + 2] & 255) << 8
                        | abyte1[j + 3] & 255;
                ai[1] = abyte1[j + 4] << 24 | (abyte1[j + 5] & 255) << 16 | (abyte1[j + 6] & 255) << 8
                        | abyte1[j + 7] & 255;
                encipher(ai);
                ai1[k] = ai[0];
                ai1[k + 1] = ai[1];
                j += 8;
                k += 2;
            }

            return ai1;
        }

        public int[] encipher(int ai[]) {
            int i = ai[0];
            int j = ai[1];
            int k = 0;
            int l = -1640531527;
            int i1 = _key[0];
            int j1 = _key[1];
            int k1 = _key[2];
            int l1 = _key[3];
            for (int i2 = 32; i2-- > 0;) {
                k += l;
                i += (j << 4) + i1 ^ j + k ^ (j >>> 5) + j1;
                j += (i << 4) + k1 ^ i + k ^ (i >>> 5) + l1;
            }

            ai[0] = i;
            ai[1] = j;
            return ai;
        }

        public byte[] decode(int ai[]) {
            int i = ai.length;
            byte abyte0[] = new byte[i * 4];
            int ai1[] = new int[2];
            int k = 0;
            for (int j = 0; j < i;) {
                ai1[0] = ai[j];
                ai1[1] = ai[j + 1];
                decipher(ai1);
                abyte0[k] = (byte) (ai1[0] >>> 24);
                abyte0[k + 1] = (byte) (ai1[0] >>> 16);
                abyte0[k + 2] = (byte) (ai1[0] >>> 8);
                abyte0[k + 3] = (byte) ai1[0];
                abyte0[k + 4] = (byte) (ai1[1] >>> 24);
                abyte0[k + 5] = (byte) (ai1[1] >>> 16);
                abyte0[k + 6] = (byte) (ai1[1] >>> 8);
                abyte0[k + 7] = (byte) ai1[1];
                j += 2;
                k += 8;
            }

            return abyte0;
        }

        public int[] decipher(int ai[]) {
            int i = ai[0];
            int j = ai[1];
            int k = -957401312;
            int l = -1640531527;
            int i1 = _key[0];
            int j1 = _key[1];
            int k1 = _key[2];
            int l1 = _key[3];
            for (int i2 = 32; i2-- > 0;) {
                j -= (i << 4) + k1 ^ i + k ^ (i >>> 5) + l1;
                i -= (j << 4) + i1 ^ j + k ^ (j >>> 5) + j1;
                k -= l;
            }

            ai[0] = i;
            ai[1] = j;
            return ai;
        }

        private int _key[];
        private int _padding;
    }

}