/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redkale.util;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;

/**
 * 简单的byte[]操作类。
 *
 * <p>
 * 详情见: http://redkale.org
 *
 * @author zhangjx
 */
public final class ByteArray {

    private byte[] content;

    private int count;

    public ByteArray() {
        this(1024);
    }

    public ByteArray(int size) {
        content = new byte[Math.max(128, size)];
    }

    public void clear() {
        this.count = 0;
    }

    public int find(byte value) {
        return find(0, value);
    }

    public boolean equal(final byte[] bytes) {
        if (bytes == null || count != bytes.length) return false;
        for (int i = 0; i < count; i++) {
            if (content[i] != bytes[i]) return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public byte get(int index) {
        return content[index];
    }

    public byte getLastByte() {
        return content[count - 1];
    }

    public void copyTo(byte[] buf) {
        System.arraycopy(this.content, 0, buf, 0, count);
    }

    public void directFrom(ByteArray array) {
        if (array != null) {
            this.content = array.content;
            this.count = array.count;
        }
    }

    public void directTo(ByteArray array) {
        if (array != null) {
            array.content = this.content;
            array.count = this.count;
        }
    }

    public byte[] directBytes() {
        return content;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(content, count);
    }

    public byte[] getBytesAndClear() {
        byte[] bs = Arrays.copyOf(content, count);
        clear();
        return bs;
    }

    public int find(int offset, char value) {
        return find(offset, (byte) value);
    }

    public int find(int offset, byte value) {
        return find(offset, -1, value);
    }

    public int find(int offset, int limit, char value) {
        return find(offset, limit, (byte) value);
    }

    public int find(int offset, int limit, byte value) {
        byte[] bytes = this.content;
        int end = limit > 0 ? limit : count;
        for (int i = offset; i < end; i++) {
            if (bytes[i] == value) return i;
        }
        return -1;
    }

    public void removeLastByte() {
        if (count > 0) count--;
    }

    public void writeInt(int value) {
        write((byte) (value >> 24 & 0xFF), (byte) (value >> 16 & 0xFF), (byte) (value >> 8 & 0xFF), (byte) (value & 0xFF));
    }

    public void write(byte value) {
        if (count >= content.length - 1) {
            byte[] ns = new byte[content.length + 8];
            System.arraycopy(content, 0, ns, 0, count);
            this.content = ns;
        }
        content[count++] = value;
    }

    public void write(byte... values) {
        if (count >= content.length - values.length) {
            byte[] ns = new byte[content.length + values.length];
            System.arraycopy(content, 0, ns, 0, count);
            this.content = ns;
        }
        System.arraycopy(values, 0, content, count, values.length);
        count += values.length;
    }

    public void write(ByteBuffer buffer, int len) {
        if (len < 1) return;
        if (count >= content.length - len) {
            byte[] ns = new byte[content.length + len];
            System.arraycopy(content, 0, ns, 0, count);
            this.content = ns;
        }
        buffer.get(content, count, len);
        count += len;
    }

    @Override
    public String toString() {
        return new String(content, 0, count);
    }

    public String toString(final Charset charset) {
        return toString(0, count, charset);
    }

    public String toStringAndClear(final Charset charset) {
        String str = toString(0, count, charset);
        clear();
        return str;
    }

    public String toString(final int offset, int len, final Charset charset) {
        if (charset == null) return new String(Utility.decodeUTF8(content, offset, len));
        return new String(content, offset, len, charset);
    }

    public String toDecodeString(final int offset, int len, final Charset charset) {
        int index = offset;
        for (int i = offset; i < (offset + len); i++) {
            switch (content[i]) {
                case '+':
                    content[index] = ' ';
                    break;
                case '%':
                    content[index] = (byte) ((hexBit(content[++i]) * 16 + hexBit(content[++i])));
                    break;
                default:
                    content[index] = content[i];
                    break;
            }
            index++;
        }
        for (int i = index + 1; i < (offset + len); i++) {
            content[i] = ' ';
        }
        len = index - offset;
        if (charset == null) return new String(Utility.decodeUTF8(content, offset, len));
        return new String(content, offset, len, charset);
    }

    private static int hexBit(byte b) {
        if ('0' <= b && '9' >= b) return b - '0';
        if ('a' <= b && 'z' >= b) return b - 'a' + 10;
        if ('A' <= b && 'Z' >= b) return b - 'A' + 10;
        return b;
    }

}
